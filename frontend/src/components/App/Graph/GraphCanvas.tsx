import React, { useRef, useEffect, useCallback, RefObject, JSX } from "react"; // Import React and relevant hooks for state, side-effects, and references
import styles from "../../../styles/modules/graphCanvas.module.css"; // Import CSS module for styling

/**
 * Offset interface for translation of the view window in the graph.
 */
interface Offset {
    x: number; // Horizontal offset in world coordinates
    y: number; // Vertical offset in world coordinates
}

/**
 * ViewWindow interface represents the current visible region of the graph in world coordinates.
 */
interface ViewWindow {
    origin: number; // Leftmost visible x value
    bound: number;  // Rightmost visible x value
    bottom: number; // Lowest visible y value
    top: number;    // Highest visible y value
}

/**
 * DrawingSet interface contains an array of points and a color for each curve to be drawn.
 */
interface DrawingSet {
    points: Array<{ x: number; y: number }>; // Points to be drawn for the curve
    color: string; // Color of the curve
}

/**
 * Props for the GraphCanvas component.
 * @property {DrawingSet[]} drawingSets - Array of sets of points to be drawn.
 * @property {function} [onViewChange] - Optional callback when the view window changes.
 * @property {RefObject<HTMLCanvasElement | null>} canvasRef - Ref to the canvas element.
 */
interface GraphCanvasProps {
    drawingSets: DrawingSet[];
    onViewChange?: (vw: ViewWindow) => void;
    canvasRef: RefObject<HTMLCanvasElement | null>;
}

/**
 * Utility to safely get a CSS variable value, with a fallback if not defined.
 * @param {string} name - CSS variable name.
 * @param {string} fallback - Fallback value if variable is not found.
 * @returns {string} The value of the CSS variable or the fallback.
 */
function getCSSVar(name: string, fallback: string) {
    return (
        getComputedStyle(document.documentElement).getPropertyValue(name).trim() ||
        fallback
    );
}

/**
 * Computes the step size for grid lines in world coordinates, given a desired pixel spacing.
 * Rounds to a "nice" number for readability (1, 2, 5, or 10 * 10^n).
 * @param {number} desiredPxBetween - Desired pixels between grid lines.
 * @param {number} scale - Current pixels-per-unit scale.
 * @returns {number} Grid step size in world coordinates.
 */
function computeGridStep(desiredPxBetween: number, scale: number): number {
    const rawStep = desiredPxBetween / scale;
    const exponent = Math.floor(Math.log10(rawStep));
    const base = Math.pow(10, exponent);
    const mantissa = rawStep / base;

    if (mantissa <= 1) return base;
    if (mantissa <= 2) return 2 * base;
    if (mantissa <= 5) return 5 * base;
    return 10 * base;
}

/**
 * Formats a number for axis/grid labels, using exponential if very large/small, otherwise with up to 3 decimals.
 * @param {number} value - The value to format.
 * @returns {string} The formatted string.
 */
function formatLabel(value: number): string {
    const absVal = Math.abs(value);
    if (absVal >= 10000 || (absVal <= 0.001 && absVal !== 0)) {
        return value.toExponential(2);
    }
    if (Number.isInteger(value)) {
        return value.toString();
    }
    const decimals = Math.max(0, 3 - Math.floor(Math.log10(absVal)));
    return value
        .toFixed(decimals)
        .replace(/\.?0+$/, "");
}

// Default offset for the graph view (centered at origin)
const DEFAULT_OFFSET = { x: 0, y: 0 };
// Default scale (pixels per world unit)
const DEFAULT_SCALE = 40;

/**
 * GraphCanvas component.
 * Renders a zoomable, pannable graph canvas with grid, axes, and one or more curves.
 * @param {GraphCanvasProps} props - The props for the component.
 * @returns {JSX.Element} The rendered graph canvas and controls.
 */
export default function GraphCanvas({
                                        drawingSets,         // Array of DrawingSet objects to render
                                        onViewChange,        // Optional callback when the view window changes
                                        canvasRef,           // Ref to the canvas element (external or internal)
                                    }: GraphCanvasProps): JSX.Element {
    // Local fallback ref in case canvasRef is not provided
    const localCanvasRef = useRef<HTMLCanvasElement>(null);
    // The actual ref to use (prefer external, fallback to local)
    const actualRef = canvasRef ?? localCanvasRef;
    // Ref to track the last mouse position for panning
    const lastMousePos = useRef<{ x: number; y: number } | null>(null);

    // State: current offset (translation) of the view in world coordinates
    const [offset, setOffset] = React.useState<Offset>(DEFAULT_OFFSET);
    // State: current scale (pixels per world unit)
    const [scale, setScale] = React.useState<number>(DEFAULT_SCALE);
    // Ref for debouncing view change events
    const debounceTimer = useRef<number | null>(null);

    /**
     * Converts world coordinates to canvas coordinates.
     * @param {number} wx - X in world coordinates.
     * @param {number} wy - Y in world coordinates.
     * @param {number} cw - Canvas width in px.
     * @param {number} ch - Canvas height in px.
     * @returns {{cx: number, cy: number}} Canvas coordinates.
     */
    const worldToCanvas = useCallback(
        (wx: number, wy: number, cw: number, ch: number) => {
            return {
                cx: cw / 2 + (wx + offset.x) * scale,
                cy: ch / 2 - (wy + offset.y) * scale,
            };
        },
        [offset, scale]
    );

    /**
     * Converts canvas coordinates to world coordinates.
     * @param {number} cx - X in canvas coordinates.
     * @param {number} cy - Y in canvas coordinates.
     * @param {number} cw - Canvas width in px.
     * @param {number} ch - Canvas height in px.
     * @returns {{x: number, y: number}} World coordinates.
     */
    const canvasToWorld = useCallback(
        (cx: number, cy: number, cw: number, ch: number) => {
            return {
                x: (cx - cw / 2) / scale - offset.x,
                y: (ch / 2 - cy) / scale - offset.y,
            };
        },
        [offset, scale]
    );

    /**
     * Draws the X and Y axes on the canvas using the current view transform.
     * @param {CanvasRenderingContext2D} ctx - 2D canvas context.
     * @param {number} cw - Canvas width.
     * @param {number} ch - Canvas height.
     */
    const drawAxes = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const { cx: x0 } = worldToCanvas(0, 0, cw, ch);
            const { cy: y0 } = worldToCanvas(0, 0, cw, ch);

            ctx.beginPath();
            ctx.strokeStyle = getCSSVar("--gc-axis", "#444"); // Use CSS variable for axis color
            ctx.lineWidth = 2;
            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, ch);
            ctx.moveTo(0, y0);
            ctx.lineTo(cw, y0);
            ctx.stroke();
            ctx.closePath();
        },
        [worldToCanvas]
    );

    /**
     * Draws the grid lines (major and minor) and axis labels on the canvas.
     * All grid colors and label styles use CSS variables.
     * @param {CanvasRenderingContext2D} ctx - 2D canvas context.
     * @param {number} cw - Canvas width.
     * @param {number} ch - Canvas height.
     */
    const drawGrid = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const gridStepWorld = computeGridStep(80, scale); // Major grid step in world units
            const minorStepWorld = gridStepWorld / 5; // Minor grid step (finer lines)

            // Compute the visible world bounds for grid drawing
            const left = canvasToWorld(0, ch / 2, cw, ch).x;
            const right = canvasToWorld(cw, ch / 2, cw, ch).x;
            const top = canvasToWorld(cw / 2, 0, cw, ch).y;
            const bottom = canvasToWorld(cw / 2, ch, cw, ch).y;

            // Compute where the axes cross the canvas
            const zeroPos = worldToCanvas(0, 0, cw, ch);
            const zeroX = zeroPos.cx;
            const zeroY = zeroPos.cy;

            // Flags: is the x or y axis visible within the current view?
            const axisVisibleX = zeroY >= 0 && zeroY <= ch;
            const axisVisibleY = zeroX >= 0 && zeroX <= cw;

            // --- Minor grid lines ---
            ctx.beginPath();
            ctx.strokeStyle = getCSSVar("--gc-grid-minor", "#e0e0e0"); // Minor grid line color
            ctx.lineWidth = 1;

            // Vertical minor grid lines
            let x = Math.floor(left / minorStepWorld) * minorStepWorld;
            while (x <= right) {
                const { cx } = worldToCanvas(x, 0, cw, ch);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, ch);
                x += minorStepWorld;
            }

            // Horizontal minor grid lines
            let y = Math.floor(bottom / minorStepWorld) * minorStepWorld;
            while (y <= top) {
                const { cy } = worldToCanvas(0, y, cw, ch);
                ctx.moveTo(0, cy);
                ctx.lineTo(cw, cy);
                y += minorStepWorld;
            }

            ctx.stroke();
            ctx.closePath();

            // --- Major grid lines and labels ---
            ctx.beginPath();
            ctx.strokeStyle = getCSSVar("--gc-grid-major", "#cccccc"); // Major grid line color
            ctx.lineWidth = 1.5;
            ctx.fillStyle = getCSSVar("--gc-grid-label", "#333"); // Label color
            ctx.font = "12px sans-serif";

            // Draw vertical major lines and X labels
            const iMinX = Math.ceil(left / gridStepWorld);
            const iMaxX = Math.floor(right / gridStepWorld);
            for (let i = iMinX; i <= iMaxX; i++) {
                const xVal = i * gridStepWorld;
                const { cx } = worldToCanvas(xVal, 0, cw, ch);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, ch);

                const label = formatLabel(xVal);
                const vOffset = Math.abs(xVal) < gridStepWorld ? 16 : 4;
                let labelY: number;
                if (axisVisibleX) {
                    labelY = zeroY - vOffset;
                } else if (zeroY < 0) {
                    labelY = 12;
                } else {
                    labelY = ch - 4;
                }
                // Do not draw "0" label on the axis if axis is visible (handled separately)
                if (i !== 0 || !axisVisibleX) {
                    ctx.fillText(label, cx + 2, labelY);
                }
            }

            // Draw horizontal major lines and Y labels
            const iMinY = Math.ceil(bottom / gridStepWorld);
            const iMaxY = Math.floor(top / gridStepWorld);
            for (let j = iMinY; j <= iMaxY; j++) {
                const yVal = j * gridStepWorld;
                const { cy } = worldToCanvas(0, yVal, cw, ch);
                ctx.moveTo(0, cy);
                ctx.lineTo(cw, cy);

                const label = formatLabel(yVal);
                const hOffset = Math.abs(yVal) < gridStepWorld ? 16 : 4;
                let labelX: number;
                if (axisVisibleY) {
                    labelX = zeroX + hOffset;
                } else if (zeroX < 0) {
                    labelX = 4;
                } else {
                    labelX = cw - label.length * 7 - 4;
                }
                // Do not draw "0" label on the axis if axis is visible (handled separately)
                if (j !== 0 || !axisVisibleY) {
                    ctx.fillText(label, labelX, cy - 2);
                }
            }

            ctx.stroke();
            ctx.closePath();

            // Draw the "0" label at the intersection of axes, if both axes are visible
            if (axisVisibleX && axisVisibleY) {
                ctx.fillStyle = getCSSVar("--gc-grid-label", "#333");
                ctx.font = "12px sans-serif";
                ctx.fillText("0", zeroX + 4, zeroY - 4);
            }
        },
        [canvasToWorld, scale, worldToCanvas]
    );

    /**
     * Draws all the curves provided in drawingSets on the canvas.
     * Each curve can be a different color.
     * @param {CanvasRenderingContext2D} ctx - 2D canvas context.
     * @param {number} cw - Canvas width.
     * @param {number} ch - Canvas height.
     */
    const drawAllCurves = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            // Compute visible world bounds (for clipping)
            const left = canvasToWorld(0, ch / 2, cw, ch).x;
            const right = canvasToWorld(cw, ch / 2, cw, ch).x;
            const top = canvasToWorld(cw / 2, 0, cw, ch).y;
            const bottom = canvasToWorld(cw / 2, ch, cw, ch).y;

            drawingSets.forEach((set) => {
                const points = set.points;
                const color = set.color;
                if (points.length < 2) return;

                ctx.beginPath();
                ctx.strokeStyle = color;
                ctx.lineWidth = 2;

                let started = false;
                for (let i = 0; i < points.length - 1; i++) {
                    const p1 = points[i];
                    const p2 = points[i + 1];

                    // Skip segments that are entirely outside the visible area
                    if (
                        (p1.x < left && p2.x < left) ||
                        (p1.x > right && p2.x > right) ||
                        (p1.y < bottom && p2.y < bottom) ||
                        (p1.y > top && p2.y > top)
                    ) {
                        continue;
                    }

                    const c1 = worldToCanvas(p1.x, p1.y, cw, ch);
                    const c2 = worldToCanvas(p2.x, p2.y, cw, ch);

                    if (!started) {
                        ctx.moveTo(c1.cx, c1.cy);
                        started = true;
                    }
                    ctx.lineTo(c2.cx, c2.cy);
                }

                ctx.stroke();
                ctx.closePath();
            });
        },
        [drawingSets, canvasToWorld]
    );

    /**
     * Redraws everything on the canvas: grid, axes, and all curves.
     * Should be called whenever the view or data changes.
     */
    const redrawAll = useCallback(() => {
        const canvas = actualRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;
        const { width: cw, height: ch } = canvas;
        ctx.clearRect(0, 0, cw, ch); // Clear previous drawing
        ctx.fillStyle = getCSSVar("--gc-background", "#fff"); // Background color
        ctx.fillRect(0, 0, cw, ch);

        drawGrid(ctx, cw, ch);        // Draw grid first (under everything)
        drawAxes(ctx, cw, ch);        // Draw axes next
        drawAllCurves(ctx, cw, ch);   // Draw all function curves on top
    }, [drawAxes, drawGrid, drawAllCurves, actualRef]);

    /**
     * Effect: when the view window changes, call the onViewChange callback (debounced).
     * Provides the current visible world region (origin, bound, bottom, top), rounded to 6 decimals.
     */
    useEffect(() => {
        if (!onViewChange) return;
        const canvas = actualRef.current;
        if (!canvas) return;

        if (debounceTimer.current !== null) {
            clearTimeout(debounceTimer.current);
        }

        debounceTimer.current = window.setTimeout(() => {
            const { width: cw, height: ch } = canvas;
            let left = canvasToWorld(0, ch / 2, cw, ch).x;
            let right = canvasToWorld(cw, ch / 2, cw, ch).x;
            let top = canvasToWorld(cw / 2, 0, cw, ch).y;
            let bottom = canvasToWorld(cw / 2, ch, cw, ch).y;

            const decimals = 6;
            left = Number(left.toFixed(decimals));
            right = Number(right.toFixed(decimals));
            top = Number(top.toFixed(decimals));
            bottom = Number(bottom.toFixed(decimals));

            onViewChange({ origin: left, bound: right, bottom, top });
            debounceTimer.current = null;
        }, 50);

        return () => {
            if (debounceTimer.current !== null) {
                clearTimeout(debounceTimer.current);
                debounceTimer.current = null;
            }
        };
    }, [offset, scale, canvasToWorld, onViewChange, actualRef]);

    /**
     * Effect: Resizes the canvas to match its parent container whenever the parent resizes.
     * Uses ResizeObserver for responsive sizing. Calls redrawAll after resizing.
     */
    useEffect(() => {
        const canvas = actualRef.current;
        if (!canvas) return;
        const resizeCanvas = () => {
            const parent = canvas.parentElement;
            if (!parent) return;
            canvas.width = parent.clientWidth;
            canvas.height = parent.clientHeight;
            redrawAll();
        };
        const observer = new ResizeObserver(resizeCanvas);
        observer.observe(canvas.parentElement!);
        resizeCanvas();
        return () => observer.disconnect();
    }, [redrawAll, actualRef]);

    /**
     * Handles the mouse wheel event for zooming in/out on the canvas.
     * Zooms about the mouse location, preserving the world coord under the pointer.
     */
    const handleWheel = useCallback(
        (e: WheelEvent) => {
            e.preventDefault();
            const canvas = actualRef.current;
            if (!canvas) return;
            const rect = canvas.getBoundingClientRect();
            const canvasX = e.clientX - rect.left;
            const canvasY = e.clientY - rect.top;
            const { x: wxBefore, y: wyBefore } = canvasToWorld(
                canvasX,
                canvasY,
                canvas.width,
                canvas.height
            );
            const delta = e.deltaY;
            const TOUCHPAD_THRESHOLD = 4;
            const isPixelMode = e.deltaMode === 0;
            const isTouchpad =
                (isPixelMode && Math.abs(delta) < TOUCHPAD_THRESHOLD) ||
                Math.abs(delta) < TOUCHPAD_THRESHOLD;
            const baseZoom = 0.1;
            const zoomIntensity = isTouchpad ? baseZoom * 0.2 : baseZoom;
            const factor = delta < 0 ? 1 + zoomIntensity : 1 - zoomIntensity;
            const newScale = scale * factor;
            const wxAfter = (canvasX - canvas.width / 2) / newScale - offset.x;
            const wyAfter = (canvas.height / 2 - canvasY) / newScale - offset.y;
            setOffset({
                x: offset.x - (wxBefore - wxAfter),
                y: offset.y - (wyBefore - wyAfter),
            });
            setScale(newScale);
        },
        [canvasToWorld, offset, scale, actualRef]
    );

    /**
     * Handler for mouse down event (start panning).
     */
    const handleMouseDown = useCallback((e: React.MouseEvent) => {
        e.preventDefault();
        lastMousePos.current = { x: e.clientX, y: e.clientY };
    }, []);

    /**
     * Handler for mouse move event (panning).
     * Only triggers if mouse was previously pressed.
     */
    const handleMouseMove = useCallback(
        (e: React.MouseEvent) => {
            if (!lastMousePos.current) return;
            e.preventDefault();
            const dx = e.clientX - lastMousePos.current.x;
            const dy = e.clientY - lastMousePos.current.y;
            lastMousePos.current = { x: e.clientX, y: e.clientY };
            setOffset((prev) => ({
                x: prev.x + dx / scale,
                y: prev.y - dy / scale,
            }));
        },
        [scale]
    );

    /**
     * Handler for mouse up event (end panning).
     */
    const handleMouseUp = useCallback(() => {
        lastMousePos.current = null;
    }, []);

    /**
     * Effect: attaches and detaches the wheel event handler for zooming on the canvas.
     */
    useEffect(() => {
        const canvas = actualRef.current;
        if (!canvas) return;
        canvas.addEventListener("wheel", handleWheel, { passive: false });
        return () => {
            canvas.removeEventListener("wheel", handleWheel);
        };
    }, [handleWheel, actualRef]);

    /**
     * Effect: redraws the canvas whenever the offset or scale changes.
     */
    useEffect(() => {
        redrawAll();
    }, [offset, scale, redrawAll]);

    // --- BUTTONS LOGIC ---

    /**
     * Handler for zoom-in button. Increases the scale by 20%.
     */
    const handleZoomIn = () => setScale((prev) => prev * 1.2);

    /**
     * Handler for zoom-out button. Decreases the scale by ~16.7%.
     */
    const handleZoomOut = () => setScale((prev) => prev / 1.2);

    /**
     * Handler for reset view button. Restores the scale and offset to their defaults.
     */
    const handleResetView = () => {
        setScale(DEFAULT_SCALE);
        setOffset(DEFAULT_OFFSET);
    };

    // Render the graph canvas and zoom controls
    return (
        <div className={styles.canvasContainer}>
            <canvas
                ref={actualRef}
                className={styles.canvas}
                onMouseDown={handleMouseDown} // Start panning
                onMouseMove={handleMouseMove} // Pan view
                onMouseUp={handleMouseUp}     // End panning
                onMouseLeave={handleMouseUp}  // End panning if mouse leaves canvas
            />
            <div className={styles.zoomControls}>
                <button
                    className={styles.zoomBtn}
                    aria-label="Zoom In"
                    onClick={handleZoomIn}
                    tabIndex={0}
                    type="button"
                >
                    {/* SVG Plus icon */}
                    <svg width="22" height="22" viewBox="0 0 22 22" className={styles.iconSvg}>
                        <rect x="9" y="4" width="4" height="14" rx="2" fill="white"/>
                        <rect x="4" y="9" width="14" height="4" rx="2" fill="white"/>
                    </svg>
                </button>
                <button
                    className={styles.zoomBtn}
                    aria-label="Zoom Out"
                    onClick={handleZoomOut}
                    tabIndex={0}
                    type="button"
                >
                    {/* SVG Minus icon */}
                    <svg width="22" height="22" viewBox="0 0 22 22" className={styles.iconSvg}>
                        <rect x="4" y="9" width="14" height="4" rx="2" fill="white"/>
                    </svg>
                </button>
                <button
                    className={styles.zoomBtn}
                    aria-label="Reset View"
                    onClick={handleResetView}
                    tabIndex={0}
                    type="button"
                >
                    <svg className={styles.resetIcon} width="18" height="18" viewBox="0 0 18 18">
                        <circle cx="9" cy="9" r="7" stroke="white" strokeWidth="1.5" fill="none" />
                        <path d="M12.75 9A3.75 3.75 0 1 1 9 5.25" stroke="white" strokeWidth="1.5" fill="none" />
                        <circle cx="9" cy="9" r="1.25" fill="white"/>
                    </svg>
                </button>
            </div>
        </div>
    );
}
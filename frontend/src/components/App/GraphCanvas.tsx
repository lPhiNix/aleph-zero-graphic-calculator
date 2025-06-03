// src/components/App/GraphCanvas.tsx

import React, {useEffect, useRef, useState, useCallback, JSX} from 'react';
import styles from '../../styles/modules/graphCanvas.module.css';

/** Props interface for GraphCanvas component */
interface GraphCanvasProps {
    expressions: string[]; // Currently unused, reserved for future analytical graphing
}

/**
 * Interactive graphing canvas component with zoom/pan functionality
 * @param {GraphCanvasProps} props - Component properties
 * @returns {JSX.Element} Canvas element
 */
export default function GraphCanvas({ expressions }: GraphCanvasProps): JSX.Element {
    // Reference to the canvas DOM element
    const canvasRef = useRef<HTMLCanvasElement>(null);

    // Current translation offset (world space)
    const [offset, setOffset] = useState<{ x: number; y: number }>({ x: 0, y: 0 });

    // Current zoom scale (pixels per world unit)
    const [scale, setScale] = useState<number>(40);

    // Last mouse position for drag tracking
    const lastMouse = useRef<{ x: number; y: number } | null>(null);

    // -------------------- Coordinate Transformations --------------------

    /**
     * Converts world coordinates to canvas pixel coordinates
     * @param {number} x - World X coordinate
     * @param {number} y - World Y coordinate
     * @param {number} width - Canvas width
     * @param {number} height - Canvas height
     * @returns {Object} Canvas coordinates {cx, cy}
     */
    const worldToCanvas = useCallback(
        (x: number, y: number, width: number, height: number) => {
            return {
                cx: width / 2 + (x + offset.x) * scale, // Center X + scaled offset
                cy: height / 2 - (y + offset.y) * scale // Center Y (inverted)
            };
        },
        [offset, scale]
    );

    /**
     * Converts canvas pixel coordinates to world coordinates
     * @param {number} cx - Canvas X pixel
     * @param {number} cy - Canvas Y pixel
     * @param {number} width - Canvas width
     * @param {number} height - Canvas height
     * @returns {Object} World coordinates {x, y}
     */
    const canvasToWorld = useCallback(
        (cx: number, cy: number, width: number, height: number) => {
            return {
                x: (cx - width / 2) / scale - offset.x, // Inverse scaling + offset
                y: (height / 2 - cy) / scale - offset.y // Inverse scaling + offset (inverted)
            };
        },
        [offset, scale]
    );

    // -------------------- Grid Calculation --------------------

    /**
     * Calculates optimal grid step based on current zoom level
     * @param {number} scaleValue - Current pixels per world unit
     * @returns {number} Optimal grid step in world units
     */
    const getStepFromScale = useCallback((scaleValue: number) => {
        const desiredPxBetween = 80; // Target pixels between grid lines
        const raw = desiredPxBetween / scaleValue; // Raw world unit step

        // Calculate logarithmic scale
        const exponent = Math.floor(Math.log10(raw));
        const base = Math.pow(10, exponent);
        const mantissa = raw / base;

        // Round to nearest nice increment (1, 2, 5, 10)
        let nice: number;
        if (mantissa <= 1) {
            nice = base;
        } else if (mantissa <= 2) {
            nice = 2 * base;
        } else if (mantissa <= 5) {
            nice = 5 * base;
        } else {
            nice = 10 * base;
        }

        return nice;
    }, []);

    // -------------------- Drawing Operations --------------------

    /**
     * Draws coordinate grid with major/minor lines
     * @param {CanvasRenderingContext2D} ctx - Canvas context
     * @param {number} width - Canvas width
     * @param {number} height - Canvas height
     */
    const drawGrid = useCallback(
        (ctx: CanvasRenderingContext2D, width: number, height: number) => {
            const step = getStepFromScale(scale); // Major grid step
            const minorStep = step / 5; // Minor grid step

            // Get visible world boundaries
            const leftWorld = canvasToWorld(0, 0, width, height).x;
            const rightWorld = canvasToWorld(width, 0, width, height).x;
            const bottomWorld = canvasToWorld(0, height, width, height).y;
            const topWorld = canvasToWorld(0, 0, width, height).y;

            // Draw minor grid lines
            ctx.beginPath();
            ctx.strokeStyle = '#e0e0e0'; // Light gray
            ctx.lineWidth = 1;

            // Vertical minor lines
            let startXMinor = Math.floor(leftWorld / minorStep) * minorStep;
            for (let x = startXMinor; x <= rightWorld; x += minorStep) {
                const { cx } = worldToCanvas(x, 0, width, height);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, height);
            }

            // Horizontal minor lines
            let startYMinor = Math.floor(bottomWorld / minorStep) * minorStep;
            for (let y = startYMinor; y <= topWorld; y += minorStep) {
                const { cy } = worldToCanvas(0, y, width, height);
                ctx.moveTo(0, cy);
                ctx.lineTo(width, cy);
            }
            ctx.stroke();
            ctx.closePath();

            // Draw major grid lines and labels
            ctx.beginPath();
            ctx.strokeStyle = '#cccccc'; // Medium gray
            ctx.lineWidth = 1.5;
            ctx.fillStyle = '#333'; // Dark text
            ctx.font = '12px sans-serif';

            // Vertical major lines
            let startXMajor = Math.floor(leftWorld / step) * step;
            const { cy: y0 } = worldToCanvas(0, 0, width, height); // Origin Y
            for (let x = startXMajor; x <= rightWorld; x += step) {
                const { cx } = worldToCanvas(x, 0, width, height);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, height);

                // Label non-zero lines
                if (Math.abs(x) > 1e-6) {
                    ctx.fillText(x.toString(), cx + 2, y0 - 4);
                }
            }

            // Horizontal major lines
            let startYMajor = Math.floor(bottomWorld / step) * step;
            const { cx: x0 } = worldToCanvas(0, 0, width, height); // Origin X
            for (let y = startYMajor; y <= topWorld; y += step) {
                const { cy } = worldToCanvas(0, y, width, height);
                ctx.moveTo(0, cy);
                ctx.lineTo(width, cy);

                // Label non-zero lines
                if (Math.abs(y) > 1e-6) {
                    ctx.fillText(y.toString(), x0 + 4, cy - 2);
                }
            }
            ctx.stroke();
            ctx.closePath();
        },
        [canvasToWorld, getStepFromScale, scale, worldToCanvas]
    );

    /**
     * Draws X and Y axes
     * @param {CanvasRenderingContext2D} ctx - Canvas context
     * @param {number} width - Canvas width
     * @param {number} height - Canvas height
     */
    const drawAxes = useCallback(
        (ctx: CanvasRenderingContext2D, width: number, height: number) => {
            ctx.beginPath();
            ctx.strokeStyle = '#444444'; // Dark gray
            ctx.lineWidth = 2;

            // Draw Y axis
            const { cx: x0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, height);

            // Draw X axis
            const { cy: y0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(0, y0);
            ctx.lineTo(width, y0);

            ctx.stroke();
            ctx.closePath();
        },
        [worldToCanvas]
    );

    /**
     * Main drawing function (clears and redraws entire canvas)
     */
    const draw = useCallback(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const { width, height } = canvas;

        // Clear canvas
        ctx.clearRect(0, 0, width, height);

        // Draw white background
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);

        // Draw coordinate system
        drawAxes(ctx, width, height);
        drawGrid(ctx, width, height);

        // (Future) Draw analytical expressions here using `expressions` prop
    }, [drawAxes, drawGrid, expressions]);

    // -------------------- Canvas Resizing --------------------

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        /** Resizes canvas to fill parent container */
        const resizeCanvas = () => {
            const parent = canvas.parentElement;
            if (!parent) return;

            // Match canvas dimensions to container
            canvas.width = parent.clientWidth;
            canvas.height = parent.clientHeight;

            // Redraw content
            draw();
        };

        // Set up resize observer
        const resizeObserver = new ResizeObserver(resizeCanvas);
        resizeObserver.observe(canvas.parentElement!);

        // Initial resize
        resizeCanvas();

        // Cleanup observer
        return () => resizeObserver.disconnect();
    }, [draw]);

    // -------------------- Event Handlers --------------------

    /** Handles zoom via mouse wheel */
    const handleWheel = useCallback(
        (e: React.WheelEvent) => {
            e.preventDefault();
            const canvas = canvasRef.current;
            if (!canvas) return;

            // Get mouse position relative to canvas
            const rect = canvas.getBoundingClientRect();
            const cx = e.clientX - rect.left;
            const cy = e.clientY - rect.top;

            // Get current world position under cursor
            const { x: worldXBefore, y: worldYBefore } = canvasToWorld(
                cx,
                cy,
                canvas.width,
                canvas.height
            );

            // Calculate zoom factor
            const zoomIntensity = 0.1;
            const factor = e.deltaY < 0 ? 1 + zoomIntensity : 1 - zoomIntensity;
            const newScale = scale * factor;

            // Calculate new world position under cursor
            const worldXAfter = (cx - canvas.width / 2) / newScale - offset.x;
            const worldYAfter = (canvas.height / 2 - cy) / newScale - offset.y;

            // Adjust offset to maintain cursor position
            setOffset((prev) => ({
                x: prev.x - (worldXBefore - worldXAfter),
                y: prev.y - (worldYBefore - worldYAfter)
            }));
            setScale(newScale);
        },
        [canvasToWorld, offset, scale]
    );

    /** Initiates panning on mouse down */
    const handleMouseDown = useCallback((e: React.MouseEvent) => {
        e.preventDefault();
        // Store initial mouse position
        lastMouse.current = { x: e.clientX, y: e.clientY };
    }, []);

    /** Handles panning during mouse move */
    const handleMouseMove = useCallback(
        (e: React.MouseEvent) => {
            if (!lastMouse.current) return;
            e.preventDefault();

            // Calculate mouse movement delta
            const dx = e.clientX - lastMouse.current.x;
            const dy = e.clientY - lastMouse.current.y;

            // Update stored position
            lastMouse.current = { x: e.clientX, y: e.clientY };

            // Adjust offset based on movement
            setOffset((prev) => ({
                x: prev.x + dx / scale, // Convert pixels to world units
                y: prev.y - dy / scale  // Invert Y direction
            }));
        },
        [scale]
    );

    /** Ends panning operation */
    const handleMouseUp = useCallback(() => {
        lastMouse.current = null; // Clear drag tracking
    }, []);

    // -------------------- Render --------------------

    return (
        <canvas
            ref={canvasRef}
            className={styles.canvas}
            onWheel={handleWheel}
            onMouseDown={handleMouseDown}
            onMouseMove={handleMouseMove}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp} // Cancel drag on exit
        />
    );
}
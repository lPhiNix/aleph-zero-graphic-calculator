// src/components/App/Graph/GraphCanvas.tsx

import React, { JSX, useCallback, useEffect, useRef, useState } from 'react'; // Import React and necessary hooks/types for component and state management
import styles from '../../../styles/modules/graphCanvas.module.css'; // Import CSS module to style the canvas element

/**
 * Represents a 2D offset in world coordinates.
 */
interface Offset {
    x: number; // Horizontal offset in world units
    y: number; // Vertical offset in world units
}

/**
 * Props for GraphCanvas component.
 * @param expressions - Array of expression strings to plot (currently unused, reserved for future use).
 */
interface GraphCanvasProps {
    expressions: string[]; // List of expressions; kept for future extension to plot expressions
}

// ─── UTILITIES ────────────────────────────────────────────────────────────

/**
 * Compute a "nice" grid step in world units such that grid lines are approximately
 * the desired number of pixels apart on screen.
 *
 * @param desiredPxBetween - Target pixel distance between major grid lines.
 * @param scale - Current scale factor (pixels per world unit).
 * @returns World-unit step size for major grid lines.
 */
function computeGridStep(desiredPxBetween: number, scale: number): number {
    const rawStep = desiredPxBetween / scale; // Compute raw world-unit distance to achieve desired pixel spacing
    const exponent = Math.floor(Math.log10(rawStep)); // Determine exponent of rawStep in scientific notation
    const base = Math.pow(10, exponent); // Compute base = 10^exponent
    const mantissa = rawStep / base; // Determine mantissa between 1 and 10

    if (mantissa <= 1) return base; // If mantissa ≤ 1, choose base
    if (mantissa <= 2) return 2 * base; // If mantissa ≤ 2, choose 2×base
    if (mantissa <= 5) return 5 * base; // If mantissa ≤ 5, choose 5×base
    return 10 * base; // Otherwise, choose 10×base
}

/**
 * Format a numeric label for axis/grid. Uses exponential notation if value is very large
 * or very small; otherwise uses up to 3 significant digits.
 *
 * @param value - Numeric value to format.
 * @returns Formatted string representation.
 */
function formatLabel(value: number): string {
    const absVal = Math.abs(value); // Compute absolute value for threshold checks
    if ((absVal >= 10000 || (absVal <= 0.001 && absVal !== 0))) {
        return value.toExponential(2); // Use exponential with 2 decimals if very large or small
    }
    if (Number.isInteger(value)) {
        return value.toString(); // If integer, return as-is
    }
    const decimals = Math.max(0, 3 - Math.floor(Math.log10(absVal))); // Determine number of decimals to show up to 3 significant figures
    return value
        .toFixed(decimals) // Convert to string with computed decimals
        .replace(/\.?0+$/, ''); // Remove trailing zeros and optional decimal point
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────

/**
 * GraphCanvas component renders a 2D coordinate grid with pan and zoom functionality.
 *
 * @param expressions - Array of mathematical expression strings (reserved for future plotting).
 * @returns JSX element containing the canvas.
 */
export default function GraphCanvas({ expressions }: GraphCanvasProps): JSX.Element {
    const canvasRef = useRef<HTMLCanvasElement>(null); // Reference to the <canvas> element for drawing
    const lastMousePos = useRef<{ x: number; y: number } | null>(null); // Tracks last mouse position for panning

    // Offset state in world units (initially centered at 0,0)
    const [offset, setOffset] = useState<Offset>({ x: 0, y: 0 });
    // Scale state: how many pixels represent one world unit (initially 40px per unit)
    const [scale, setScale] = useState<number>(40);

    /**
     * Convert world coordinates (wx, wy) to canvas pixel coordinates.
     *
     * @param wx - World x-coordinate.
     * @param wy - World y-coordinate.
     * @param cw - Canvas width in pixels.
     * @param ch - Canvas height in pixels.
     * @returns Object with cx and cy: pixel coordinates on canvas.
     */
    const worldToCanvas = useCallback(
        (wx: number, wy: number, cw: number, ch: number) => {
            return {
                cx: cw / 2 + (wx + offset.x) * scale, // Translate world x by offset, scale to pixels, then center horizontally
                cy: ch / 2 - (wy + offset.y) * scale, // Translate world y by offset, scale to pixels, then invert and center vertically
            };
        },
        [offset, scale] // Recompute whenever offset or scale changes
    );

    /**
     * Convert canvas pixel coordinates to world coordinates.
     *
     * @param cx - Canvas x-coordinate in pixels.
     * @param cy - Canvas y-coordinate in pixels.
     * @param cw - Canvas width in pixels.
     * @param ch - Canvas height in pixels.
     * @returns Object with x and y: world coordinates.
     */
    const canvasToWorld = useCallback(
        (cx: number, cy: number, cw: number, ch: number) => {
            return {
                x: (cx - cw / 2) / scale - offset.x, // Reverse centering and scaling, then subtract offset for world x
                y: (ch / 2 - cy) / scale - offset.y, // Reverse centering and scaling, then subtract offset for world y
            };
        },
        [offset, scale] // Recompute whenever offset or scale changes
    );

    /**
     * Draw the X and Y axes on the canvas, passing through the world origin.
     *
     * @param ctx - 2D rendering context.
     * @param cw - Canvas width in pixels.
     * @param ch - Canvas height in pixels.
     */
    const drawAxes = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const { cx: x0 } = worldToCanvas(0, 0, cw, ch); // Compute pixel x for world x=0,y=0
            const { cy: y0 } = worldToCanvas(0, 0, cw, ch); // Compute pixel y for world x=0,y=0

            ctx.beginPath(); // Begin path for drawing axes
            ctx.strokeStyle = '#444'; // Dark gray color for axes
            ctx.lineWidth = 2; // Thicker line width for axes

            // Draw Y-axis: vertical line at x0 from top to bottom
            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, ch);

            // Draw X-axis: horizontal line at y0 from left to right
            ctx.moveTo(0, y0);
            ctx.lineTo(cw, y0);

            ctx.stroke(); // Actually draw the lines
            ctx.closePath(); // Close the path
        },
        [worldToCanvas] // Redraw axes whenever transformation changes
    );

    /**
     * Draw the grid lines (major and minor) and numeric labels on the canvas.
     *
     * @param ctx - 2D rendering context.
     * @param cw - Canvas width in pixels.
     * @param ch - Canvas height in pixels.
     */
    const drawGrid = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const gridStepWorld = computeGridStep(80, scale); // Compute major grid spacing in world units
            const minorStepWorld = gridStepWorld / 5; // Minor grid lines at 1/5th of major step

            // Determine world bounds currently visible on canvas edges
            const left = canvasToWorld(0, 0, cw, ch).x; // World x at left edge
            const right = canvasToWorld(cw, 0, cw, ch).x; // World x at right edge
            const top = canvasToWorld(0, 0, cw, ch).y; // World y at top edge
            const bottom = canvasToWorld(0, ch, cw, ch).y; // World y at bottom edge

            // ─── Draw minor grid lines ─────────────────────────────────────────
            ctx.beginPath(); // Begin path for minor grid
            ctx.strokeStyle = '#e0e0e0'; // Light gray color for minor lines
            ctx.lineWidth = 1; // Thin lines for minor grid

            // Draw vertical minor lines: iterate from first multiple ≥ left to right
            let x = Math.floor(left / minorStepWorld) * minorStepWorld; // First minor x-grid line at or before left bound
            while (x <= right) {
                const { cx } = worldToCanvas(x, 0, cw, ch); // Pixel x-coordinate for this minor line
                ctx.moveTo(cx, 0); // Start at top of canvas
                ctx.lineTo(cx, ch); // Draw line down to bottom
                x += minorStepWorld; // Move to next minor x-grid line
            }

            // Draw horizontal minor lines: iterate from first multiple ≥ bottom to top
            let y = Math.floor(bottom / minorStepWorld) * minorStepWorld; // First minor y-grid line at or below bottom bound
            while (y <= top) {
                const { cy } = worldToCanvas(0, y, cw, ch); // Pixel y-coordinate for this minor line
                ctx.moveTo(0, cy); // Start at left of canvas
                ctx.lineTo(cw, cy); // Draw line to right edge
                y += minorStepWorld; // Move to next minor y-grid line
            }

            ctx.stroke(); // Render minor grid lines
            ctx.closePath(); // Close minor grid path

            // ─── Draw major grid lines & labels ───────────────────────────────
            ctx.beginPath(); // Begin path for major grid
            ctx.strokeStyle = '#cccccc'; // Slightly darker gray for major lines
            ctx.lineWidth = 1.5; // Slightly thicker than minor lines
            ctx.fillStyle = '#333'; // Dark color for text labels
            ctx.font = '12px sans-serif'; // Font for labels

            const iMinX = Math.ceil(left / gridStepWorld); // First integer grid index ≥ left bound
            const iMaxX = Math.floor(right / gridStepWorld); // Last integer grid index ≤ right bound
            const zeroY = worldToCanvas(0, 0, cw, ch).cy; // Pixel y-coordinate of world y=0 for label positioning

            // Draw vertical major lines and numeric labels
            for (let i = iMinX; i <= iMaxX; i++) {
                const xVal = i * gridStepWorld; // Compute actual world x for this grid line
                const { cx } = worldToCanvas(xVal, 0, cw, ch); // Convert to pixel x
                ctx.moveTo(cx, 0); // Start at top
                ctx.lineTo(cx, ch); // Draw to bottom
                if (i !== 0) {
                    const label = formatLabel(xVal); // Format numeric label for xVal
                    const vOffset = Math.abs(xVal) < gridStepWorld ? 16 : 4; // Decide if label needs extra offset near origin
                    ctx.fillText(label, cx + 2, zeroY - vOffset); // Draw label near axis
                }
            }

            const iMinY = Math.ceil(bottom / gridStepWorld); // First integer grid index ≥ bottom
            const iMaxY = Math.floor(top / gridStepWorld); // Last integer grid index ≤ top bound
            const zeroX = worldToCanvas(0, 0, cw, ch).cx; // Pixel x-coordinate of world x=0 for label positioning

            // Draw horizontal major lines and numeric labels
            for (let j = iMinY; j <= iMaxY; j++) {
                const yVal = j * gridStepWorld; // Compute actual world y for this grid line
                const { cy } = worldToCanvas(0, yVal, cw, ch); // Convert to pixel y
                ctx.moveTo(0, cy); // Start at left
                ctx.lineTo(cw, cy); // Draw to right
                if (j !== 0) {
                    const label = formatLabel(yVal); // Format numeric label for yVal
                    const hOffset = Math.abs(yVal) < gridStepWorld ? 16 : 4; // Extra offset near origin if needed
                    ctx.fillText(label, zeroX + hOffset, cy - 2); // Draw label next to axis
                }
            }

            ctx.stroke(); // Render major grid lines
            ctx.closePath(); // Close major grid path

            // ─── Draw "0" at origin ────────────────────────────────────────────
            ctx.fillStyle = '#333'; // Ensure fill color still dark for "0"
            ctx.font = '12px sans-serif'; // Ensure font remains consistent
            const origin = worldToCanvas(0, 0, cw, ch); // Compute pixel coordinates for world origin
            ctx.fillText('0', origin.cx + 4, origin.cy - 4); // Draw "0" label slightly offset from origin
        },
        [canvasToWorld, scale, worldToCanvas] // Redraw grid whenever transformations or scale change
    );

    /**
     * Clear the entire canvas and redraw the background, axes, and grid.
     */
    const redrawAll = useCallback(() => {
        const canvas = canvasRef.current; // Get the current canvas element
        if (!canvas) return; // If no canvas reference, bail out

        const ctx = canvas.getContext('2d'); // Get 2D drawing context
        if (!ctx) return; // If context not available, bail out

        const { width: cw, height: ch } = canvas; // Destructure canvas width/height
        ctx.clearRect(0, 0, cw, ch); // Clear the entire canvas area
        ctx.fillStyle = '#fff'; // Set fill to white for background
        ctx.fillRect(0, 0, cw, ch); // Fill the background with white

        drawAxes(ctx, cw, ch); // Draw coordinate axes
        drawGrid(ctx, cw, ch); // Draw grid lines and labels

        // Placeholder: future implementation to plot mathematical expressions
        // e.g., plotExpressions(ctx, expressions, offset, scale);
    }, [drawAxes, drawGrid, expressions]);

    // ─── RESIZE HANDLING ────────────────────────────────────────────────────

    useEffect(() => {
        const canvas = canvasRef.current; // Get reference to canvas
        if (!canvas) return; // If no canvas, do nothing

        const resizeCanvas = () => {
            const parent = canvas.parentElement; // Find parent element to match size
            if (!parent) return; // If no parent, bail out
            canvas.width = parent.clientWidth; // Set canvas width to parent width
            canvas.height = parent.clientHeight; // Set canvas height to parent height
            redrawAll(); // Redraw everything after resizing
        };

        const observer = new ResizeObserver(resizeCanvas); // Create observer to watch parent size changes
        observer.observe(canvas.parentElement!); // Start observing parent's size
        resizeCanvas(); // Immediately size and draw canvas on mount

        return () => observer.disconnect(); // Clean up observer on component unmount
    }, [redrawAll]); // Re-run only if redrawAll changes (i.e., offsets or scale)

    // ─── ZOOM HANDLER ───────────────────────────────────────────────────────

    /**
     * Handle wheel events (mouse wheel or touchpad scroll) to zoom in/out.
     *
     * @param e - WheelEvent triggered on canvas.
     */
    const handleWheel = useCallback(
        (e: WheelEvent) => {
            e.preventDefault(); // Prevent default scrolling behavior
            const canvas = canvasRef.current; // Retrieve canvas element
            if (!canvas) return; // If missing, bail out

            const rect = canvas.getBoundingClientRect(); // Get canvas position for correct mouse coordinates
            const canvasX = e.clientX - rect.left; // Calculate mouse x relative to canvas
            const canvasY = e.clientY - rect.top; // Calculate mouse y relative to canvas

            // Compute world coordinates under cursor before zoom
            const { x: wxBefore, y: wyBefore } = canvasToWorld(canvasX, canvasY, canvas.width, canvas.height);

            const delta = e.deltaY; // Amount of wheel movement
            const TOUCHPAD_THRESHOLD = 4; // Pixel threshold to distinguish touchpad vs wheel
            const isPixelMode = e.deltaMode === 0; // deltaMode 0 usually means pixel values
            const isTouchpad = (isPixelMode && Math.abs(delta) < TOUCHPAD_THRESHOLD) || Math.abs(delta) < TOUCHPAD_THRESHOLD; // Heuristic for touchpad
            const baseZoom = 0.1; // Base zoom increment
            const zoomIntensity = isTouchpad ? baseZoom * 0.2 : baseZoom; // Reduce zoom speed for touchpad
            const factor = delta < 0 ? 1 + zoomIntensity : 1 - zoomIntensity; // Determine zoom in/out factor
            const newScale = scale * factor; // Compute new scale

            // Compute world coordinates that will correspond to same cursor position after zoom (without changing offset)
            const wxAfter = (canvasX - canvas.width / 2) / newScale - offset.x;
            const wyAfter = (canvas.height / 2 - canvasY) / newScale - offset.y;

            // Adjust offset so that point under cursor remains stable in view
            setOffset({
                x: offset.x - (wxBefore - wxAfter), // Shift offset in x
                y: offset.y - (wyBefore - wyAfter), // Shift offset in y
            });
            setScale(newScale); // Update scale state
        },
        [canvasToWorld, offset, scale] // Dependencies: canvasToWorld, current offset, current scale
    );

    // ─── MOUSE PAN HANDLERS ─────────────────────────────────────────────────

    /**
     * Handle mouse down event to initiate panning.
     *
     * @param e - React mouse event.
     */
    const handleMouseDown = useCallback((e: React.MouseEvent) => {
        e.preventDefault(); // Prevent any default browser drag behavior
        lastMousePos.current = { x: e.clientX, y: e.clientY }; // Store initial mouse position
    }, []);

    /**
     * Handle mouse move event to pan canvas when dragging.
     *
     * @param e - React mouse event.
     */
    const handleMouseMove = useCallback(
        (e: React.MouseEvent) => {
            if (!lastMousePos.current) return; // If no previous mouse recorded, do nothing
            e.preventDefault(); // Prevent unwanted text selection or dragging
            const dx = e.clientX - lastMousePos.current.x; // Compute horizontal delta since last event
            const dy = e.clientY - lastMousePos.current.y; // Compute vertical delta since last event
            lastMousePos.current = { x: e.clientX, y: e.clientY }; // Update last mouse position

            setOffset(prev => ({
                x: prev.x + dx / scale, // Shift world x offset proportional to pixel movement
                y: prev.y - dy / scale, // Shift world y offset (invert y direction)
            }));
        },
        [scale] // Only re-create handler if scale changes
    );

    /**
     * Handle mouse up or leave to end panning.
     */
    const handleMouseUp = useCallback(() => {
        lastMousePos.current = null; // Clear last mouse position to stop panning
    }, []);

    // ─── EVENT LISTENERS REGISTRATION ───────────────────────────────────────

    useEffect(() => {
        const canvas = canvasRef.current; // Get reference to canvas
        if (!canvas) return; // If no canvas, do nothing

        canvas.addEventListener('wheel', handleWheel, { passive: false }); // Attach wheel handler with passive: false to allow preventDefault
        return () => {
            canvas.removeEventListener('wheel', handleWheel); // Clean up listener on unmount
        };
    }, [handleWheel]); // Reattach listener only if handleWheel function identity changes

    // Redraw entire canvas when offset or scale changes
    useEffect(() => {
        redrawAll(); // Trigger redraw to reflect new offset or zoom scale
    }, [offset, scale, redrawAll]); // Dependencies: offset, scale, and redraw function

    return (
        <canvas
            ref={canvasRef} // Assign ref for drawing operations
            className={styles.canvas} // Apply CSS module class for styling (e.g., full width/height)
            style={{ touchAction: 'none' }} // Disable default touch actions (e.g., panning) on mobile/touch devices
            onMouseDown={handleMouseDown} // Listen for mouse down to start panning
            onMouseMove={handleMouseMove} // Listen for mouse move to pan
            onMouseUp={handleMouseUp} // Listen for mouse up to end panning
            onMouseLeave={handleMouseUp} // Treat leaving canvas as ending panning
        />
    );
}
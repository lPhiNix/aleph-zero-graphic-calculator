import React, { JSX, useCallback, useEffect, useRef, useState } from 'react';
import styles from '../../../styles/modules/graphCanvas.module.css';

interface Offset {
    x: number;
    y: number;
}

interface ViewWindow {
    origin: number;
    bound: number;
    bottom: number;
    top: number;
}

interface GraphCanvasProps {
    drawingSets: Array<Array<{ x: number; y: number }>>;
    /** Callback para notificar al padre el “rango” visible (origin, bound, bottom, top) */
    onViewChange?: (vw: ViewWindow) => void;
}

/** computeGridStep y formatLabel idénticos a antes */
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
        .replace(/\.?0+$/, '');
}

export default function GraphCanvas({
                                        drawingSets,
                                        onViewChange,
                                    }: GraphCanvasProps): JSX.Element {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const lastMousePos = useRef<{ x: number; y: number } | null>(null);

    const [offset, setOffset] = useState<Offset>({ x: 0, y: 0 });
    const [scale, setScale] = useState<number>(40);

    // Ref para almacenar el ID del timeout de debounce
    const debounceTimer = useRef<number | null>(null);

    const worldToCanvas = useCallback(
        (wx: number, wy: number, cw: number, ch: number) => {
            return {
                cx: cw / 2 + (wx + offset.x) * scale,
                cy: ch / 2 - (wy + offset.y) * scale,
            };
        },
        [offset, scale]
    );

    const canvasToWorld = useCallback(
        (cx: number, cy: number, cw: number, ch: number) => {
            return {
                x: (cx - cw / 2) / scale - offset.x,
                y: (ch / 2 - cy) / scale - offset.y,
            };
        },
        [offset, scale]
    );

    const drawAxes = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const { cx: x0 } = worldToCanvas(0, 0, cw, ch);
            const { cy: y0 } = worldToCanvas(0, 0, cw, ch);

            ctx.beginPath();
            ctx.strokeStyle = '#444';
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

    const drawGrid = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const gridStepWorld = computeGridStep(80, scale);
            const minorStepWorld = gridStepWorld / 5;

            const left = canvasToWorld(0, ch / 2, cw, ch).x;
            const right = canvasToWorld(cw, ch / 2, cw, ch).x;
            const top = canvasToWorld(cw / 2, 0, cw, ch).y;
            const bottom = canvasToWorld(cw / 2, ch, cw, ch).y;

            const zeroPos = worldToCanvas(0, 0, cw, ch);
            const zeroX = zeroPos.cx;
            const zeroY = zeroPos.cy;

            const axisVisibleX = zeroY >= 0 && zeroY <= ch;
            const axisVisibleY = zeroX >= 0 && zeroX <= cw;

            // ─── Líneas menores ─────────────────────────────────────────────
            ctx.beginPath();
            ctx.strokeStyle = '#e0e0e0';
            ctx.lineWidth = 1;

            let x = Math.floor(left / minorStepWorld) * minorStepWorld;
            while (x <= right) {
                const { cx } = worldToCanvas(x, 0, cw, ch);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, ch);
                x += minorStepWorld;
            }

            let y = Math.floor(bottom / minorStepWorld) * minorStepWorld;
            while (y <= top) {
                const { cy } = worldToCanvas(0, y, cw, ch);
                ctx.moveTo(0, cy);
                ctx.lineTo(cw, cy);
                y += minorStepWorld;
            }

            ctx.stroke();
            ctx.closePath();

            // ─── Líneas mayores y etiquetas ──────────────────────────────────
            ctx.beginPath();
            ctx.strokeStyle = '#cccccc';
            ctx.lineWidth = 1.5;
            ctx.fillStyle = '#333';
            ctx.font = '12px sans-serif';

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
                if (i !== 0 || !axisVisibleX) {
                    ctx.fillText(label, cx + 2, labelY);
                }
            }

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
                if (j !== 0 || !axisVisibleY) {
                    ctx.fillText(label, labelX, cy - 2);
                }
            }

            ctx.stroke();
            ctx.closePath();

            if (axisVisibleX && axisVisibleY) {
                ctx.fillStyle = '#333';
                ctx.font = '12px sans-serif';
                ctx.fillText('0', zeroX + 4, zeroY - 4);
            }
        },
        [canvasToWorld, scale, worldToCanvas]
    );

    /**
     * Dibuja cada conjunto de puntos (drawingSets) como trazado continuo,
     * pero solo los segmentos que intersectan con la vista actual.
     */
    const drawAllCurves = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            // 1. Calculamos límites en coordenadas “mundo” para hacer clipping:
            const left = canvasToWorld(0, ch / 2, cw, ch).x;
            const right = canvasToWorld(cw, ch / 2, cw, ch).x;
            const top = canvasToWorld(cw / 2, 0, cw, ch).y;
            const bottom = canvasToWorld(cw / 2, ch, cw, ch).y;

            drawingSets.forEach((points, idx) => {
                if (points.length < 2) return;
                ctx.beginPath();
                ctx.strokeStyle = `hsl(${(idx * 60) % 360}, 70%, 40%)`;
                ctx.lineWidth = 2;

                // Recorremos cada par de puntos consecutivos:
                for (let i = 0; i < points.length - 1; i++) {
                    const p1 = points[i];
                    const p2 = points[i + 1];

                    // Clipping en “mundo”: si ambos puntos están completamente fuera
                    // (p1.x,p1.y) y (p2.x,p2.y) en el mismo lado, saltamos.
                    if (
                        (p1.x < left && p2.x < left) ||
                        (p1.x > right && p2.x > right) ||
                        (p1.y < bottom && p2.y < bottom) ||
                        (p1.y > top && p2.y > top)
                    ) {
                        continue;
                    }

                    // Si al menos un extremo intersecta la vista, dibujamos:
                    const c1 = worldToCanvas(p1.x, p1.y, cw, ch);
                    const c2 = worldToCanvas(p2.x, p2.y, cw, ch);
                    if (i === 0) {
                        ctx.moveTo(c1.cx, c1.cy);
                    }
                    ctx.lineTo(c2.cx, c2.cy);
                }

                ctx.stroke();
                ctx.closePath();
            });
        },
        [drawingSets, canvasToWorld, worldToCanvas]
    );

    const redrawAll = useCallback(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;
        const { width: cw, height: ch } = canvas;
        ctx.clearRect(0, 0, cw, ch);
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, cw, ch);

        drawAxes(ctx, cw, ch);
        drawGrid(ctx, cw, ch);
        drawAllCurves(ctx, cw, ch);
    }, [drawAxes, drawGrid, drawAllCurves]);

    /** Cuando cambian offset/scale, notificamos al padre el nuevo “viewWindow” con debounce */
    useEffect(() => {
        if (!onViewChange) return;
        const canvas = canvasRef.current;
        if (!canvas) return;

        // Si el usuario sigue interactuando, reiniciamos el timeout
        if (debounceTimer.current !== null) {
            clearTimeout(debounceTimer.current);
        }

        debounceTimer.current = window.setTimeout(() => {
            const { width: cw, height: ch } = canvas;
            const left = canvasToWorld(0, ch / 2, cw, ch).x;
            const right = canvasToWorld(cw, ch / 2, cw, ch).x;
            const top = canvasToWorld(cw / 2, 0, cw, ch).y;
            const bottom = canvasToWorld(cw / 2, ch, cw, ch).y;
            onViewChange({ origin: left, bound: right, bottom, top });
            debounceTimer.current = null;
        }, 500); // Ahora 500 ms en lugar de 1000 para ser un poco más responsivo

        return () => {
            if (debounceTimer.current !== null) {
                clearTimeout(debounceTimer.current);
                debounceTimer.current = null;
            }
        };
    }, [offset, scale, canvasToWorld, onViewChange]);

    /** Observamos cambios de tamaño para redibujar */
    useEffect(() => {
        const canvas = canvasRef.current;
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
    }, [redrawAll]);

    const handleWheel = useCallback(
        (e: WheelEvent) => {
            e.preventDefault();
            const canvas = canvasRef.current;
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
        [canvasToWorld, offset, scale]
    );

    const handleMouseDown = useCallback((e: React.MouseEvent) => {
        e.preventDefault();
        lastMousePos.current = { x: e.clientX, y: e.clientY };
    }, []);

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

    const handleMouseUp = useCallback(() => {
        lastMousePos.current = null;
    }, []);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        canvas.addEventListener('wheel', handleWheel, { passive: false });
        return () => {
            canvas.removeEventListener('wheel', handleWheel);
        };
    }, [handleWheel]);

    useEffect(() => {
        redrawAll();
    }, [offset, scale, redrawAll]);

    return (
        <canvas
            ref={canvasRef}
            className={styles.canvas}
            style={{ touchAction: 'none' }}
            onMouseDown={handleMouseDown}
            onMouseMove={handleMouseMove}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
        />
    );
}

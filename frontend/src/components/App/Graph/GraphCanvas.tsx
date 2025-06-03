// src/components/App/Graph/GraphCanvas.tsx

import React, { useEffect, useRef, useState, useCallback, JSX } from 'react';
import styles from '../../../styles/modules/graphCanvas.module.css';

interface Offset {
    x: number;
    y: number;
}

interface GraphCanvasProps {
    expressions: string[]; // Actualmente sin uso para graficar expresiones
}

export default function GraphCanvas({ expressions }: GraphCanvasProps): JSX.Element {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const lastMouse = useRef<{ x: number; y: number } | null>(null);

    const [offset, setOffset] = useState<Offset>({ x: 0, y: 0 });
    const [scale, setScale] = useState<number>(40);

    // ─── UTILIDADES DE TRANSFORMACIÓN ────────────────────────────────────────

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

    const getGridStep = useCallback((currentScale: number) => {
        const desiredPxBetween = 80;
        const rawStep = desiredPxBetween / currentScale;
        const exponent = Math.floor(Math.log10(rawStep));
        const base = Math.pow(10, exponent);
        const mantissa = rawStep / base;

        if (mantissa <= 1) return base;
        if (mantissa <= 2) return 2 * base;
        if (mantissa <= 5) return 5 * base;
        return 10 * base;
    }, []);

    const formatLabel = useCallback((value: number): string => {
        const absVal = Math.abs(value);
        if ((absVal >= 10000 || (absVal <= 0.001 && absVal !== 0))) {
            return value.toExponential(2);
        }
        if (Number.isInteger(value)) {
            return value.toString();
        }
        const decimals = Math.max(0, 3 - Math.floor(Math.log10(absVal)));
        return value.toFixed(decimals).replace(/\.?0+$/, '');
    }, []);

    // ─── DIBUJO DE GRILLA Y EJES ────────────────────────────────────────────

    const drawGrid = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            const step = getGridStep(scale);
            const minorStep = step / 5;

            // Límites del mundo visibles
            const { x: left }   = canvasToWorld(0, 0, cw, ch);
            const { x: right }  = canvasToWorld(cw, 0, cw, ch);
            const { y: top }    = canvasToWorld(0, 0, cw, ch);
            const { y: bottom } = canvasToWorld(0, ch, cw, ch);

            // Líneas menores
            ctx.beginPath();
            ctx.strokeStyle = '#e0e0e0';
            ctx.lineWidth = 1;

            let xMinor = Math.floor(left / minorStep) * minorStep;
            for (; xMinor <= right; xMinor += minorStep) {
                const { cx } = worldToCanvas(xMinor, 0, cw, ch);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, ch);
            }

            let yMinor = Math.floor(bottom / minorStep) * minorStep;
            for (; yMinor <= top; yMinor += minorStep) {
                const { cy } = worldToCanvas(0, yMinor, cw, ch);
                ctx.moveTo(0, cy);
                ctx.lineTo(cw, cy);
            }

            ctx.stroke();
            ctx.closePath();

            // Líneas mayores y etiquetas
            ctx.beginPath();
            ctx.strokeStyle = '#cccccc';
            ctx.lineWidth = 1.5;
            ctx.fillStyle = '#333';
            ctx.font = '12px sans-serif';

            const iMinX = Math.ceil(left / step);
            const iMaxX = Math.floor(right / step);
            const { cy: zeroY } = worldToCanvas(0, 0, cw, ch);

            for (let i = iMinX; i <= iMaxX; i++) {
                const xVal = i * step;
                const { cx } = worldToCanvas(xVal, 0, cw, ch);
                ctx.moveTo(cx, 0);
                ctx.lineTo(cx, ch);

                if (i !== 0) {
                    const label = formatLabel(xVal);
                    const vOffset = Math.abs(xVal) < step ? 16 : 4;
                    ctx.fillText(label, cx + 2, zeroY - vOffset);
                }
            }

            const iMinY = Math.ceil(bottom / step);
            const iMaxY = Math.floor(top / step);
            const { cx: zeroX } = worldToCanvas(0, 0, cw, ch);

            for (let j = iMinY; j <= iMaxY; j++) {
                const yVal = j * step;
                const { cy } = worldToCanvas(0, yVal, cw, ch);
                ctx.moveTo(0, cy);
                ctx.lineTo(cw, cy);

                if (j !== 0) {
                    const label = formatLabel(yVal);
                    const hOffset = Math.abs(yVal) < step ? 16 : 4;
                    ctx.fillText(label, zeroX + hOffset, cy - 2);
                }
            }

            ctx.stroke();
            ctx.closePath();

            // Etiqueta "0" en el origen
            const { cx: originX, cy: originY } = worldToCanvas(0, 0, cw, ch);
            ctx.fillStyle = '#333';
            ctx.font = '12px sans-serif';
            ctx.fillText('0', originX + 4, originY - 4);
        },
        [canvasToWorld, formatLabel, getGridStep, scale, worldToCanvas]
    );

    const drawAxes = useCallback(
        (ctx: CanvasRenderingContext2D, cw: number, ch: number) => {
            ctx.beginPath();
            ctx.strokeStyle = '#444';
            ctx.lineWidth = 2;
            const { cx: x0 } = worldToCanvas(0, 0, cw, ch);
            const { cy: y0 } = worldToCanvas(0, 0, cw, ch);

            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, ch);
            ctx.moveTo(0, y0);
            ctx.lineTo(cw, y0);
            ctx.stroke();
            ctx.closePath();
        },
        [worldToCanvas]
    );

    const clearAndDraw = useCallback(() => {
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
        // Aquí se podrían dibujar las expresiones pasadas en `expressions`
    }, [drawAxes, drawGrid, expressions]);

    // ─── REDIMENSIONAR EL CANVAS AL TAMAÑO DEL PADRE ───────────────────────

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const resizeCanvas = () => {
            const parent = canvas.parentElement;
            if (!parent) return;
            canvas.width = parent.clientWidth;
            canvas.height = parent.clientHeight;
            clearAndDraw();
        };

        const observer = new ResizeObserver(resizeCanvas);
        observer.observe(canvas.parentElement!);
        resizeCanvas();

        return () => {
            observer.disconnect();
        };
    }, [clearAndDraw]);

    // ─── EVENTOS DE ZOOM Y PAN ─────────────────────────────────────────────

    const handleWheel = useCallback(
        (e: WheelEvent) => {
            e.preventDefault();
            const canvas = canvasRef.current;
            if (!canvas) return;

            const rect = canvas.getBoundingClientRect();
            const cx = e.clientX - rect.left;
            const cy = e.clientY - rect.top;
            const { x: wxBefore, y: wyBefore } = canvasToWorld(cx, cy, canvas.width, canvas.height);

            const zoomIntensity = 0.05;
            const factor = e.deltaY < 0 ? 1 + zoomIntensity : 1 - zoomIntensity;
            const newScale = scale * factor;

            const wxAfter = (cx - canvas.width / 2) / newScale - offset.x;
            const wyAfter = (canvas.height / 2 - cy) / newScale - offset.y;

            setOffset(prev => ({
                x: prev.x - (wxBefore - wxAfter),
                y: prev.y - (wyBefore - wyAfter),
            }));
            setScale(newScale);
        },
        [canvasToWorld, offset, scale]
    );

    const handleMouseDown = useCallback((e: React.MouseEvent) => {
        e.preventDefault();
        lastMouse.current = { x: e.clientX, y: e.clientY };
    }, []);

    const handleMouseMove = useCallback(
        (e: React.MouseEvent) => {
            if (!lastMouse.current) return;
            e.preventDefault();
            const dx = e.clientX - lastMouse.current.x;
            const dy = e.clientY - lastMouse.current.y;
            lastMouse.current = { x: e.clientX, y: e.clientY };

            setOffset(prev => ({
                x: prev.x + dx / scale,
                y: prev.y - dy / scale,
            }));
        },
        [scale]
    );

    const handleMouseUp = useCallback(() => {
        lastMouse.current = null;
    }, []);

    // Registrar listener de rueda con passive: false
    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        canvas.addEventListener('wheel', handleWheel, { passive: false });
        return () => {
            canvas.removeEventListener('wheel', handleWheel);
        };
    }, [handleWheel]);

    // Redibujar cuando cambian offset o scale
    useEffect(() => {
        clearAndDraw();
    }, [offset, scale, clearAndDraw]);

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
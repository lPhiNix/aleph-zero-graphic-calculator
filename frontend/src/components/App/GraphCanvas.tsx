// src/components/Graph/GraphCanvas.tsx
import React, { useRef, useEffect, useState } from 'react';
import styles from '../../styles/modules/graphCanvas.module.css';

interface GraphCanvasProps {
    expressions: string[];
}

export default function GraphCanvas({ expressions }: GraphCanvasProps) {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const [offset, setOffset] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
    const [scale, setScale] = useState<number>(40);
    const lastMouse = useRef<{ x: number; y: number } | null>(null);

    const worldToCanvas = (x: number, y: number, width: number, height: number) => {
        return {
            cx: width / 2 + (x + offset.x) * scale,
            cy: height / 2 - (y + offset.y) * scale,
        };
    };

    const getStepFromScale = (scale: number) => {
        if (scale < 10) return 50;
        if (scale < 20) return 20;
        if (scale < 40) return 10;
        if (scale < 80) return 5;
        if (scale < 160) return 2;
        return 1;
    };

    const canvasToWorld = (cx: number, cy: number, width: number, height: number) => {
        return {
            x: (cx - width / 2) / scale - offset.x,
            y: (height / 2 - cy) / scale - offset.y,
        };
    };

    const draw = () => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const { width, height } = canvas;
        ctx.clearRect(0, 0, width, height);

        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);

        ctx.beginPath();
        ctx.strokeStyle = '#e0e0e0';
        ctx.lineWidth = 1;

        const leftWorld = canvasToWorld(0, 0, width, height).x;
        const rightWorld = canvasToWorld(width, 0, width, height).x;
        const bottomWorld = canvasToWorld(0, height, width, height).y;
        const topWorld = canvasToWorld(0, 0, width, height).y;

        const minX = Math.floor(leftWorld) - 1;
        const maxX = Math.ceil(rightWorld) + 1;
        const minY = Math.floor(bottomWorld) - 1;
        const maxY = Math.ceil(topWorld) + 1;

        for (let i = minX; i <= maxX; i++) {
            const { cx } = worldToCanvas(i, 0, width, height);
            ctx.moveTo(cx, 0);
            ctx.lineTo(cx, height);
        }

        for (let j = minY; j <= maxY; j++) {
            const { cy } = worldToCanvas(0, j, width, height);
            ctx.moveTo(0, cy);
            ctx.lineTo(width, cy);
        }

        ctx.stroke();
        ctx.closePath();

        ctx.beginPath();
        ctx.strokeStyle = '#444444';
        ctx.lineWidth = 2;

        {
            const { cx: x0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, height);
        }

        {
            const { cy: y0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(0, y0);
            ctx.lineTo(width, y0);
        }

        ctx.stroke();
        ctx.closePath();

        drawGridNumbersAndEnhancedLines(ctx, width, height);
    };

    const drawGridNumbersAndEnhancedLines = (ctx: CanvasRenderingContext2D, width: number, height: number) => {
        const step = getStepFromScale(scale);

        const leftWorld = canvasToWorld(0, 0, width, height).x;
        const rightWorld = canvasToWorld(width, 0, width, height).x;
        const bottomWorld = canvasToWorld(0, height, width, height).y;
        const topWorld = canvasToWorld(0, 0, width, height).y;

        ctx.beginPath();
        for (let x = Math.floor(leftWorld / step) * step; x <= rightWorld; x += step) {
            const { cx } = worldToCanvas(x, 0, width, height);
            ctx.strokeStyle = '#cccccc';
            ctx.lineWidth = 1.5;
            ctx.moveTo(cx, 0);
            ctx.lineTo(cx, height);

            const { cy: y0 } = worldToCanvas(0, 0, width, height);
            ctx.fillStyle = '#333';
            ctx.font = '12px sans-serif';
            ctx.fillText(x.toString(), cx + 2, y0 - 4);
        }

        for (let y = Math.floor(bottomWorld / step) * step; y <= topWorld; y += step) {
            const { cy } = worldToCanvas(0, y, width, height);
            ctx.strokeStyle = '#cccccc';
            ctx.lineWidth = 1.5;
            ctx.moveTo(0, cy);
            ctx.lineTo(width, cy);

            const { cx: x0 } = worldToCanvas(0, 0, width, height);
            ctx.fillStyle = '#333';
            ctx.font = '12px sans-serif';
            ctx.fillText(y.toString(), x0 + 4, cy - 2);
        }
        ctx.stroke();
        ctx.closePath();
    };

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const resizeObserver = new ResizeObserver(() => {
            const parent = canvas.parentElement;
            if (!parent) return;
            canvas.width = parent.clientWidth;
            canvas.height = parent.clientHeight;
            draw();
        });
        resizeObserver.observe(canvas.parentElement!);
        draw();

        // 🔒 Previene scroll de página al hacer zoom con la rueda o touchpad
        const handleWheelManual = (e: WheelEvent) => {
            e.preventDefault();
            const rect = canvas.getBoundingClientRect();
            const cx = e.clientX - rect.left;
            const cy = e.clientY - rect.top;

            const { x: worldXBefore, y: worldYBefore } = canvasToWorld(cx, cy, canvas.width, canvas.height);

            const zoomIntensity = 0.1;
            const newScale = scale * (e.deltaY < 0 ? (1 + zoomIntensity) : (1 - zoomIntensity));
            const clampedScale = Math.max(0.5, Math.min(newScale, 10000));
            setScale(clampedScale);

            const { x: worldXAfter, y: worldYAfter } = canvasToWorld(cx, cy, canvas.width, canvas.height);
            setOffset(prev => ({
                x: prev.x + (worldXBefore - worldXAfter),
                y: prev.y + (worldYBefore - worldYAfter),
            }));
        };

        canvas.addEventListener('wheel', handleWheelManual, { passive: false });

        return () => {
            resizeObserver.disconnect();
            canvas.removeEventListener('wheel', handleWheelManual);
        };
    }, [offset, scale, expressions]);


    const handleWheel = (e: React.WheelEvent) => {
        e.preventDefault();
        const canvas = canvasRef.current;
        if (!canvas) return;
        const { left, top, width, height } = canvas.getBoundingClientRect();
        const cx = e.clientX - left;
        const cy = e.clientY - top;
        const { x: worldXBefore, y: worldYBefore } = canvasToWorld(cx, cy, width, height);

        const zoomIntensity = 0.1;
        const newScale = scale * (e.deltaY < 0 ? (1 + zoomIntensity) : (1 - zoomIntensity));
        setScale(Math.max(10, Math.min(newScale, 500)));

        const { x: worldXAfter, y: worldYAfter } = canvasToWorld(cx, cy, width, height);
        setOffset(prev => ({
            x: prev.x + (worldXBefore - worldXAfter),
            y: prev.y + (worldYBefore - worldYAfter),
        }));
    };

    const handleMouseDown = (e: React.MouseEvent) => {
        e.preventDefault();
        lastMouse.current = { x: e.clientX, y: e.clientY };
    };

    const handleMouseMove = (e: React.MouseEvent) => {
        if (!lastMouse.current) return;
        e.preventDefault();
        const dx = e.clientX - lastMouse.current.x;
        const dy = e.clientY - lastMouse.current.y;
        lastMouse.current = { x: e.clientX, y: e.clientY };

        setOffset(prev => ({
            x: prev.x + dx / scale,
            y: prev.y - dy / scale,
        }));
    };

    const handleMouseUp = () => {
        lastMouse.current = null;
    };

    return (
        <canvas
            ref={canvasRef}
            className={styles.canvas}
            onWheel={handleWheel}
            onMouseDown={handleMouseDown}
            onMouseMove={handleMouseMove}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
        />
    );
}

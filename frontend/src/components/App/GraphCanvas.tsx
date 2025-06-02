// src/components/Graph/GraphCanvas.tsx
import React, { useRef, useEffect, useState } from 'react';
import styles from '../../styles/modules/graphCanvas.module.css';

interface GraphCanvasProps {
    expressions: string[];
}

export default function GraphCanvas({ expressions }: GraphCanvasProps) {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    // Center en coordenadas del mundo. Inicialmente centrado en (0,0)
    const [offset, setOffset] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
    // Escala = píxeles por unidad de coordenada
    const [scale, setScale] = useState<number>(40); // 40px = 1 unidad del eje

    // Para pan con mouse:
    const lastMouse = useRef<{ x: number; y: number } | null>(null);

    // Función para convertir coordenada “mundo” (x, y) a píxeles en canvas:
    const worldToCanvas = (x: number, y: number, width: number, height: number) => {
        return {
            cx: width / 2 + (x + offset.x) * scale,
            cy: height / 2 - (y + offset.y) * scale,
        };
    };

    // Función para convertir coordenadas de canvas (px) a “mundo” (x, y):
    const canvasToWorld = (cx: number, cy: number, width: number, height: number) => {
        return {
            x: (cx - width / 2) / scale - offset.x,
            y: (height / 2 - cy) / scale - offset.y,
        };
    };

    // Dibuja la cuadricula y las funciones
    const draw = () => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const { width, height } = canvas;
        // Limpio todo
        ctx.clearRect(0, 0, width, height);

        // Dibujar fondo blanco:
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);

        // Dibujar cuadricula: líneas cada 1 unidad
        ctx.beginPath();
        ctx.strokeStyle = '#e0e0e0';
        ctx.lineWidth = 1;

        // Líneas verticales
        // Encontrar “x” más a la izquierda en coordenadas del mundo:
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

        // Líneas horizontales
        for (let j = minY; j <= maxY; j++) {
            const { cy } = worldToCanvas(0, j, width, height);
            ctx.moveTo(0, cy);
            ctx.lineTo(width, cy);
        }

        ctx.stroke();
        ctx.closePath();

        // Dibujar ejes X e Y (en color más oscuro)
        ctx.beginPath();
        ctx.strokeStyle = '#444444';
        ctx.lineWidth = 2;

        // Eje Y (x=0)
        {
            const { cx: x0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(x0, 0);
            ctx.lineTo(x0, height);
        }
        // Eje X (y=0)
        {
            const { cy: y0 } = worldToCanvas(0, 0, width, height);
            ctx.moveTo(0, y0);
            ctx.lineTo(width, y0);
        }

        ctx.stroke();
        ctx.closePath();

        // Dibujar cada expresión en un color distinto
        expressions.forEach((expr, idx) => {
            if (expr.trim() === '') return; // salto líneas vacías

            // Intentar parsear la función:
            // En producción mejor usar una librería que maneje errores y validaciones.
            let fn: ((x: number) => number) | null = null;
            try {
                // “Function” se crea con el cuerpo: return <expr>;
                // Ej: expr = "Math.sin(x)" o "x*x + 2"
                fn = new Function('x', 'return ' + expr) as any;
            } catch {
                fn = null;
            }
            if (!fn) return;

            // Define color para cada curva
            const colors = ['#FF5722', '#3F51B5', '#009688', '#E91E63', '#FF9800'];
            const color = colors[idx % colors.length];

            ctx.beginPath();
            ctx.strokeStyle = color;
            ctx.lineWidth = 2;

            // Recorremos pixel a pixel en el ancho del canvas y calculamos y = f(x_world)
            const samplePixels = 1; // cada 1 px muestro un punto
            let firstPoint = true;

            for (let cx = 0; cx <= width; cx += samplePixels) {
                const { x: worldX } = canvasToWorld(cx, 0, width, height);
                let worldY: number;
                try {
                    worldY = fn(worldX);
                    if (typeof worldY !== 'number' || isNaN(worldY) || !isFinite(worldY)) {
                        firstPoint = true;
                        continue;
                    }
                } catch {
                    firstPoint = true;
                    continue;
                }

                const { cx: px, cy: py } = worldToCanvas(worldX, worldY, width, height);
                if (firstPoint) {
                    ctx.moveTo(px, py);
                    firstPoint = false;
                } else {
                    ctx.lineTo(px, py);
                }
            }
            ctx.stroke();
        });
    };

    // Este efecto se encarga de redimensionar el canvas al tamaño del contenedor y de dibujar
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

        // dibujo inicial
        draw();

        return () => resizeObserver.disconnect();
    }, [offset, scale, expressions]);

    // Manejadores de mouse para zoom y pan:
    const handleWheel = (e: React.WheelEvent) => {
        e.preventDefault();
        const canvas = canvasRef.current;
        if (!canvas) return;
        const { left, top, width, height } = canvas.getBoundingClientRect();
        // Posición del cursor en coordenadas de canvas:
        const cx = e.clientX - left;
        const cy = e.clientY - top;
        const { x: worldXBefore, y: worldYBefore } = canvasToWorld(cx, cy, width, height);

        // Zoom in/out
        const zoomIntensity = 0.1;
        const newScale = scale * (e.deltaY < 0 ? (1 + zoomIntensity) : (1 - zoomIntensity));
        setScale(Math.max(10, Math.min(newScale, 500))); // límite entre 10px/1u y 500px/1u

        // Queremos que el punto bajo el cursor se mantenga fijo, así que ajustamos offset:
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

        // Movemos el offset en “unidades del mundo”:
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

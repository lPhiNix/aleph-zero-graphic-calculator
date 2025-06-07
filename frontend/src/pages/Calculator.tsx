// src/pages/Calculator.tsx
import { useState, useEffect, useCallback, useRef } from 'react';
import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas';
import MathKeyboard from '../components/App/MathKeyboard';
import styles from '../styles/modules/graphCanvas.module.css';
import {
    evaluateSingleExpression,
    evaluateBatchExpressions,
} from '../services/mathService';
import type { ExpressionResult } from '../types/math';

interface ViewWindow {
    origin: number;
    bound: number;
    bottom: number;
    top: number;
}

type IntervalData = {
    from: number;
    to: number;
    points: Array<{ x: number; y: number }>;
};

export default function Calculator() {
    // 1. Estado de las expresiones actuales
    const [expressions, setExpressions] = useState<string[]>(['']);

    // 2. Estado de resultados: un objeto por cada línea de expresión
    const [results, setResults] = useState<ExpressionResult[]>(() =>
        expressions.map(() => ({}))
    );

    // 3. Estado de colores: un color hex por cada expresión
    const [colors, setColors] = useState<string[]>(['#ff0000']);

    // 3b. Estado de “disabledFlags”: indica si cada fila está deshabilitada
    const [disabledFlags, setDisabledFlags] = useState<boolean[]>([false]);

    // 4. Estado de la ventana actual de dibujo
    const [viewWindow, setViewWindow] = useState<ViewWindow>({
        origin: -10,
        bound: 10,
        bottom: -10,
        top: 10,
    });

    // Ref para detectar el primer render y evitar doble llamada
    const isFirstRender = useRef(true);

    // Ref para almacenar, por cada índice de expresión, los intervalos cacheados
    const cacheRef = useRef<Record<number, IntervalData[]>>({});

    // Sincronizar arrays cuando cambie el número de expresiones
    useEffect(() => {
        setResults(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push({});
            upd.length = expressions.length;
            return upd;
        });
        setColors(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push('#000000');
            upd.length = expressions.length;
            return upd;
        });
        setDisabledFlags(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push(false);
            upd.length = expressions.length;
            return upd;
        });
        expressions.forEach((_, i) => {
            if (!cacheRef.current[i]) cacheRef.current[i] = [];
        });
    }, [expressions]);

    // Util para missing intervals (igual que antes)
    const getMissingIntervals = useCallback(
        (from: number, to: number, existing: IntervalData[]) => {
            if (from >= to) return [];
            let intervals: Array<[number, number]> = [[from, to]];
            existing
                .sort((a, b) => a.from - b.from)
                .forEach(({ from: f, to: t }) => {
                    const next: Array<[number, number]> = [];
                    intervals.forEach(([a, b]) => {
                        if (t <= a || f >= b) next.push([a, b]);
                        else {
                            if (a < f) next.push([a, f]);
                            if (b > t) next.push([t, b]);
                        }
                    });
                    intervals = next;
                });
            return intervals;
        },
        []
    );

    // Fetch parcial al cambiar viewWindow (sin cambios en batching)
    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        let mounted = true;
        const { origin, bound } = viewWindow;
        const dec = '50';

        expressions.forEach((expr, idx) => {
            if (disabledFlags[idx] || expr.trim() === '') {
                if (!mounted) return;
                cacheRef.current[idx] = [];
                setResults(prev => {
                    const copy = [...prev];
                    copy[idx] = { exprType: prev[idx]?.exprType };
                    return copy;
                });
                return;
            }
            const existing = cacheRef.current[idx] || [];
            const missing = getMissingIntervals(origin, bound, existing);
            if (missing.length === 0) {
                const pts: Array<{ x: number; y: number }> = [];
                existing
                    .filter(iv => iv.to > origin && iv.from < bound)
                    .forEach(iv =>
                        iv.points.forEach(pt => {
                            if (pt.x >= origin && pt.x <= bound) pts.push(pt);
                        })
                    );
                pts.sort((a, b) => a.x - b.x);
                setResults(prev => {
                    if (!mounted) return prev;
                    const u = prev.map(r => ({ ...r }));
                    u[idx] = { ...u[idx], drawingPoints: pts, exprType: u[idx]?.exprType };
                    return u;
                });
                return;
            }
            (async () => {
                for (const [f, t] of missing) {
                    try {
                        const res = await evaluateSingleExpression(
                            expr,
                            dec,
                            f.toString(),
                            t.toString()
                        );
                        if (!mounted) return;
                        cacheRef.current[idx].push({ from: f, to: t, points: res.drawingPoints || [] });
                        const merged: Array<{ x: number; y: number }> = [];
                        cacheRef.current[idx]
                            .filter(iv => iv.to > origin && iv.from < bound)
                            .forEach(iv =>
                                iv.points.forEach(pt => {
                                    if (pt.x >= origin && pt.x <= bound) merged.push(pt);
                                })
                            );
                        merged.sort((a, b) => a.x - b.x);
                        setResults(prev => {
                            if (!mounted) return prev;
                            const u = prev.map(r => ({ ...r }));
                            u[idx] = { ...u[idx], drawingPoints: merged, exprType: u[idx]?.exprType };
                            return u;
                        });
                    } catch {
                        if (!mounted) return;
                        setResults(prev => {
                            const u = prev.map(r => ({ ...r }));
                            u[idx] = {
                                ...u[idx],
                                errors: [...(u[idx].errors || []), `Error intervalo [${f},${t}]`],
                                exprType: u[idx]?.exprType,
                            };
                            return u;
                        });
                    }
                }
            })();
        });

        return () => {
            mounted = false;
        };
    }, [viewWindow, expressions, disabledFlags, getMissingIntervals]);

    // Nuevo handle: evalúa lote que incluye asignaciones anteriores + expr actual
    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            if (disabledFlags[index] || expr.trim() === '') return;

            // Encontrar todas las asignaciones anteriores a `index`
            const batchIndices = expressions
                .slice(0, index + 1)
                .map((e, i) => ({ expr: e, i }))
                .filter(({i }) => results[i]?.exprType === 'ASSIGNMENT')
                .map(({ i }) => i);

            // siempre incluimos la expresión actual
            batchIndices.push(index);

            // construir array de expresiones por posición
            const batchExprs = batchIndices.map(i => expressions[i]);

            const dec = '50';
            const origin = viewWindow.origin.toString();
            const bound = viewWindow.bound.toString();

            try {
                const batchResults = await evaluateBatchExpressions(
                    batchExprs,
                    dec,
                    origin,
                    bound
                );
                // actualizar cada índice
                setResults(prev => {
                    const u = [...prev];
                    batchIndices.forEach((origIdx, idxInBatch) => {
                        const res = batchResults[idxInBatch];
                        u[origIdx] = {
                            ...res,
                            // conservar exprType si manque
                            exprType: res.exprType || prev[origIdx]?.exprType,
                        };
                        // recargar caché sólo para dibujo del actual
                        if (origIdx === index) {
                            cacheRef.current[origIdx] = [
                                {
                                    from: viewWindow.origin,
                                    to: viewWindow.bound,
                                    points: res.drawingPoints || [],
                                },
                            ];
                        }
                    });
                    return u;
                });
            } catch (err) {
                console.error('Error batch evaluation:', err);
            }
        },
        [expressions, results, disabledFlags, viewWindow]
    );

    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    const handleColorChange = useCallback((i: number, color: string) => {
        setColors(prev => {
            const u = [...prev];
            u[i] = color;
            return u;
        });
    }, []);

    const handleToggleDisabled = useCallback((i: number) => {
        setDisabledFlags(prev => {
            const u = [...prev];
            u[i] = !u[i];
            return u;
        });
    }, []);

    const insertIntoExpression = (v: string) =>
        setExpressions(prev => {
            const u = [...prev];
            u[u.length - 1] += v;
            return u;
        });

    const backspace = () =>
        setExpressions(prev => {
            const u = [...prev];
            const li = u.length - 1;
            u[li] = u[li].slice(0, -1);
            return u;
        });

    const clearAll = () => {
        setExpressions(['']);
        setResults([{}]);
        cacheRef.current = {};
        setColors(['#000000']);
        setDisabledFlags([false]);
    };

    const evaluateExpression = () => {
        const li = expressions.length - 1;
        if (expressions[li].trim() !== '' && !disabledFlags[li]) {
            handleExpressionBlur(li, expressions[li]);
        }
    };

    const allDrawingSets = expressions.map((_, i) =>
        disabledFlags[i]
            ? { points: [], color: '#666666' }
            : { points: results[i]?.drawingPoints || [], color: colors[i] }
    );

    // ──────── DEFINICIÓN DEL TECLADO ────────
    const teclas = [
        { label: '2nd', onClick: () => insertIntoExpression('2nd') },
        { label: 'π', onClick: () => insertIntoExpression('π') },
        { label: 'e', onClick: () => insertIntoExpression('e') },
        { label: 'C', onClick: clearAll },
        { label: '⌫', onClick: backspace },
        { label: 'x²', onClick: () => insertIntoExpression('^2') },
        { label: '1/x', onClick: () => insertIntoExpression('1/') },
        { label: '|x|', onClick: () => insertIntoExpression('| |') },
        { label: 'x', onClick: () => insertIntoExpression('x') },
        { label: 'y', onClick: () => insertIntoExpression('y') },
        { label: '√', onClick: () => insertIntoExpression('√(') },
        { label: '(', onClick: () => insertIntoExpression('(') },
        { label: ')', onClick: () => insertIntoExpression(')') },
        { label: '=', onClick: () => insertIntoExpression('=') },
        { label: '÷', onClick: () => insertIntoExpression('/') },
        { label: 'xʸ', onClick: () => insertIntoExpression('^') },
        { label: '7', onClick: () => insertIntoExpression('7') },
        { label: '8', onClick: () => insertIntoExpression('8') },
        { label: '9', onClick: () => insertIntoExpression('9') },
        { label: '×', onClick: () => insertIntoExpression('*') },
        { label: '10ˣ', onClick: () => insertIntoExpression('10^') },
        { label: '4', onClick: () => insertIntoExpression('4') },
        { label: '5', onClick: () => insertIntoExpression('5') },
        { label: '6', onClick: () => insertIntoExpression('6') },
        { label: '−', onClick: () => insertIntoExpression('-') },
        { label: 'log', onClick: () => insertIntoExpression('log(') },
        { label: '1', onClick: () => insertIntoExpression('1') },
        { label: '2', onClick: () => insertIntoExpression('2') },
        { label: '3', onClick: () => insertIntoExpression('3') },
        { label: '+', onClick: () => insertIntoExpression('+') },
        { label: 'ln', onClick: () => insertIntoExpression('ln(') },
        { label: '⟲', onClick: () => console.log('Undo pressed') },
        { label: '0', onClick: () => insertIntoExpression('0') },
        { label: ',', onClick: () => insertIntoExpression(',') },
        { label: '↵', onClick: evaluateExpression, className: 'wideKey' },
    ];

    return (
        <div className={styles.pageContainer}>
            <Header title="Calculadora con Colores" />
            <div className={styles.mainArea}>
                {/* ─── IZQUIERDA: CANVAS ───────────────────────────────────────────────────── */}
                <div className={styles.canvasWrapper}>
                    <GraphCanvas
                        drawingSets={allDrawingSets}
                        onViewChange={handleViewChange}
                    />
                </div>

                {/* ─── DERECHA: EXPRESSION LIST + TECLADO ─────────────────────────────────── */}
                <div className={styles.expressionsWrapper}>
                    <ExpressionList
                        expressions={expressions}
                        onExpressionsChange={setExpressions}
                        onExpressionBlur={handleExpressionBlur}
                        colors={colors}
                        onColorChange={handleColorChange}
                        disabledFlags={disabledFlags}
                        onToggleDisabled={handleToggleDisabled}
                        expressionTypes={results.map((r) => r.exprType)}
                        results={results}
                    />

                    {/* ─── TECLADO matemático ────────────────────────────────────────────────── */}
                    <MathKeyboard keys={teclas} />
                </div>
            </div>
        </div>
    );
}

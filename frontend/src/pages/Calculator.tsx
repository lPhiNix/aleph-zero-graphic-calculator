import { useState, useEffect, useCallback, useRef } from 'react';
import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas';
import MathKeyboard from '../components/App/MathKeyboard';
import styles from '../styles/modules/graphCanvas.module.css';
import { evaluateSingleExpression } from '../services/mathService';
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
    const [colors, setColors] = useState<string[]>(['#ff0000']); // inicializamos con un color por defecto

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

    // ──────── SINCRONIZAR `results`, `colors` y `disabledFlags` cuando cambie `expressions.length` ────────
    useEffect(() => {
        // 1) Ajustar longitud de `results`
        setResults((prev) => {
            const updated = [...prev];
            while (updated.length < expressions.length) {
                updated.push({});
            }
            updated.length = expressions.length;
            return updated;
        });

        // 2) Ajustar longitud de `colors`
        setColors((prev) => {
            const updated = [...prev];
            while (updated.length < expressions.length) {
                updated.push('#000000');
            }
            updated.length = expressions.length;
            return updated;
        });

        // 3) Ajustar longitud de `disabledFlags`
        setDisabledFlags((prev) => {
            const updated = [...prev];
            while (updated.length < expressions.length) {
                // por defecto, recién creada, está habilitada → false
                updated.push(false);
            }
            updated.length = expressions.length;
            return updated;
        });

        // 4) Inicializar caché para índices nuevos
        expressions.forEach((_, idx) => {
            if (!cacheRef.current[idx]) {
                cacheRef.current[idx] = [];
            }
        });
    }, [expressions]);

    /** Dada la ventana deseada [from, to] y los intervalos `existing`, devuelve qué sub-intervalos faltan */
    const getMissingIntervals = (
        desiredFrom: number,
        desiredTo: number,
        existing: IntervalData[]
    ): Array<[number, number]> => {
        if (desiredFrom >= desiredTo) return [];
        const sortedExisting = [...existing].sort((a, b) => a.from - b.from);
        let toCheck: Array<[number, number]> = [[desiredFrom, desiredTo]];

        sortedExisting.forEach(({ from, to }) => {
            const nextCheck: Array<[number, number]> = [];
            toCheck.forEach(([a, b]) => {
                if (to <= a || from >= b) {
                    nextCheck.push([a, b]);
                } else {
                    if (a < from) {
                        nextCheck.push([a, from]);
                    }
                    if (b > to) {
                        nextCheck.push([to, b]);
                    }
                }
            });
            toCheck = nextCheck;
        });

        return toCheck;
    };

    // ──────── FETCH PARCIAL AL CAMBIAR `viewWindow` ────────
    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        let isMounted = true;
        const { origin, bound } = viewWindow;
        const strDecimals = '50';

        expressions.forEach((expr, idx) => {
            // Si la fila está deshabilitada, omitimos completamente
            if (disabledFlags[idx]) {
                if (!isMounted) return;
                cacheRef.current[idx] = [];
                setResults((prev) => {
                    const copy = [...prev];
                    // Conservamos exprType pero borramos dibujo y errores
                    copy[idx] = { exprType: prev[idx]?.exprType };
                    return copy;
                });
                return;
            }

            // Si la expresión está vacía, limpiamos
            if (expr.trim() === '') {
                if (!isMounted) return;
                cacheRef.current[idx] = [];
                setResults((prev) => {
                    const copy = [...prev];
                    copy[idx] = {};
                    return copy;
                });
                return;
            }

            const existing = cacheRef.current[idx] || [];
            const missing = getMissingIntervals(origin, bound, existing);

            // Si no hay intervalos faltantes, reconstruimos dibujo desde caché
            if (missing.length === 0) {
                const combined: Array<{ x: number; y: number }> = [];
                existing
                    .filter((iv) => iv.to > origin && iv.from < bound)
                    .forEach((iv) =>
                        iv.points.forEach((pt) => {
                            if (pt.x >= origin && pt.x <= bound) {
                                combined.push(pt);
                            }
                        })
                    );
                combined.sort((a, b) => a.x - b.x);

                setResults((prev) => {
                    if (!isMounted) return prev;
                    const updated = prev.map((r) => ({ ...r }));
                    updated[idx] = {
                        ...updated[idx],
                        drawingPoints: combined,
                        exprType: updated[idx]?.exprType,
                    };
                    return updated;
                });
                return;
            }

            // Para cada sub-intervalo faltante, hacemos fetch asíncrono
            (async () => {
                for (const [f, t] of missing) {
                    try {
                        const res = await evaluateSingleExpression(
                            expr,
                            strDecimals,
                            f.toString(),
                            t.toString()
                        );
                        if (!isMounted) return;
                        const newPts = res.drawingPoints || [];

                        // Insertamos este fragmento en caché
                        cacheRef.current[idx].push({ from: f, to: t, points: newPts });

                        // Reconstruimos merged solo con los puntos que caen en [origin, bound]
                        const merged: Array<{ x: number; y: number }> = [];
                        cacheRef.current[idx]
                            .filter((iv) => iv.to > origin && iv.from < bound)
                            .forEach((iv) =>
                                iv.points.forEach((pt) => {
                                    if (pt.x >= origin && pt.x <= bound) {
                                        merged.push(pt);
                                    }
                                })
                            );
                        merged.sort((a, b) => a.x - b.x);

                        setResults((prev) => {
                            if (!isMounted) return prev;
                            const updated = prev.map((r) => ({ ...r }));
                            updated[idx] = {
                                ...updated[idx],
                                drawingPoints: merged,
                                exprType: updated[idx]?.exprType || res.exprType,
                            };
                            return updated;
                        });
                    } catch {
                        if (!isMounted) return;
                        setResults((prev) => {
                            const updated = prev.map((r) => ({ ...r }));
                            updated[idx] = {
                                ...updated[idx],
                                errors: [
                                    ...(updated[idx].errors || []),
                                    `Error al cargar intervalo [${f}, ${t}]`,
                                ],
                                exprType: updated[idx]?.exprType,
                            };
                            return updated;
                        });
                    }
                }
            })();
        });

        return () => {
            isMounted = false;
        };
    }, [viewWindow, expressions, disabledFlags]);

    // ──────── MANEJO DE “blur” EN CADA EXPRESIÓN ────────
    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            // Si la fila está deshabilitada, no evaluamos nada
            if (disabledFlags[index]) {
                return;
            }

            if (expr.trim() === '') {
                cacheRef.current[index] = [];
                setResults((prev) => {
                    const copy = [...prev];
                    copy[index] = {};
                    return copy;
                });
                return;
            }

            try {
                const decimals = '50';
                const origin = viewWindow.origin.toString();
                const bound = viewWindow.bound.toString();

                const res = await evaluateSingleExpression(expr, decimals, origin, bound);

                // 1) Guardamos evaluación/calculation/errors en results[index]
                setResults((prev) => {
                    const updated = [...prev];
                    updated[index] = {
                        ...res,
                        // Si res.exprType viene undefined, conservamos el anterior
                        exprType: res.exprType || prev[index]?.exprType,
                    };
                    return updated;
                });

                // 2) Reemplazamos la caché de esa línea con el intervalo completo
                const fromNum = viewWindow.origin;
                const toNum = viewWindow.bound;
                const drawingPts = res.drawingPoints || [];

                cacheRef.current[index] = [
                    {
                        from: fromNum,
                        to: toNum,
                        points: drawingPts,
                    },
                ];
            } catch (err) {
                cacheRef.current[index] = [];
                setResults((prev) => {
                    const updated = [...prev];
                    updated[index] = {
                        errors: ['Error al evaluar la expresión'],
                        exprType: updated[index]?.exprType,
                    };
                    return updated;
                });
                console.error('[handleExpressionBlur] Error al evaluar:', err);
            }
        },
        [viewWindow, disabledFlags]
    );

    // ──────── CALLBACK PARA CAMBIO DE VIEW WINDOW ────────
    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    // ──────── CALLBACK PARA CAMBIO DE COLOR ────────
    const handleColorChange = useCallback((index: number, newColor: string) => {
        setColors((prev) => {
            const updated = [...prev];
            updated[index] = newColor;
            return updated;
        });
    }, []);

    // ──────── CALLBACK PARA TOGGLE DISABLED ────────
    const handleToggleDisabled = useCallback(
        (index: number) => {
            setDisabledFlags((prev) => {
                const updated = [...prev];
                updated[index] = !updated[index];
                return updated;
            });
            // No se limpia exprType ni evaluation: se conserva el tipo
        },
        []
    );

    // ──────── TECLADO MATEMÁTICO ────────
    const insertIntoExpression = (value: string) => {
        setExpressions((prev) => {
            const newArr = [...prev];
            const lastIndex = newArr.length - 1;
            newArr[lastIndex] = newArr[lastIndex] + value;
            return newArr;
        });
    };

    const backspace = () => {
        setExpressions((prev) => {
            const newArr = [...prev];
            const lastIndex = newArr.length - 1;
            newArr[lastIndex] = newArr[lastIndex].slice(0, -1);
            return newArr;
        });
    };

    const clearAll = () => {
        setExpressions(['']);
        setResults([{}]);
        cacheRef.current = {};
        setColors(['#000000']);
        setDisabledFlags([false]);
    };

    const evaluateExpression = () => {
        const lastIndex = expressions.length - 1;
        const lastExpr = expressions[lastIndex];
        if (lastExpr.trim() !== '' && !disabledFlags[lastIndex]) {
            handleExpressionBlur(lastIndex, lastExpr);
        }
    };

    // ──────── CONSTRUIMOS `drawingSets` PARA GraphCanvas ────────
    /**
     * Ahora cada elemento es { points, color } en el mismo orden que `expressions`.
     * Si la fila está deshabilitada o no tiene puntos, ponemos points: [].
     */
    const allDrawingSets = expressions.map((_, idx) => {
        if (disabledFlags[idx]) {
            return {
                points: [] as Array<{ x: number; y: number }>,
                color: '#666666', // color de placeholder gris
            };
        }
        return {
            points: results[idx]?.drawingPoints || [],
            color: colors[idx] || '#000000',
        };
    });

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

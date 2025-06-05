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

/**
 * Para cada expresión, almacenamos un array de intervalos “cacheados”:
 *   {
 *     from: number;
 *     to: number;
 *     points: Array<{ x: number; y: number }>
 *   }
 * Luego, siempre que cambie la viewWindow, calculamos qué sub-intervalos FALTAN
 * a partir de la unión de los que ya tenemos. Solo pedimos esos.
 */
type IntervalData = {
    from: number;
    to: number;
    points: Array<{ x: number; y: number }>;
};

export default function Calculator() {
    // 1. Estado de las expresiones actuales
    const [expressions, setExpressions] = useState<string[]>(['']);

    // 2. Estado de resultados: un objeto por cada línea de expresión
    //    (solo para evaluation/calculation/errors; drawingPoints lo sacamos de la caché)
    const [results, setResults] = useState<ExpressionResult[]>(
        () => expressions.map(() => ({}))
    );

    // 3. Estado de la ventana actual de dibujo (origin, bound, bottom, top)
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

    // Sincronizar la longitud de `results` con la de `expressions`
    useEffect(() => {
        setResults((prev) => {
            const updated = [...prev];
            while (updated.length < expressions.length) {
                updated.push({});
            }
            updated.length = expressions.length;
            return updated;
        });
        // A su vez, si se ha agregado una expresión nueva, inicializamos su caché vacía
        setTimeout(() => {
            if (!cacheRef.current) cacheRef.current = {};
            expressions.forEach((_, idx) => {
                if (!cacheRef.current[idx]) {
                    cacheRef.current[idx] = [];
                }
            });
        }, 0);
    }, [expressions.length]);

    /**
     * Dada un intervalo deseado [from, to] y un array de intervalos ya cacheados,
     * devuelve un array de sub-intervalos “faltantes” que no están cubiertos aún.
     */
    const getMissingIntervals = (
        desiredFrom: number,
        desiredTo: number,
        existing: IntervalData[]
    ): Array<[number, number]> => {
        if (desiredFrom >= desiredTo) return [];
        // Construimos una lista de intervalos inicial: [[desiredFrom, desiredTo]]
        let toCheck: Array<[number, number]> = [[desiredFrom, desiredTo]];

        existing.forEach(({ from, to }) => {
            const nextCheck: Array<[number, number]> = [];
            toCheck.forEach(([a, b]) => {
                // Si no se solapan, dejamos tal cual
                if (to <= a || from >= b) {
                    nextCheck.push([a, b]);
                } else {
                    // Hay solapamiento: recortamos
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

        return toCheck; // es el array de sub-intervalos que faltan
    };

    //--------------------------------------------------------------------
    // Cuando cambie viewWindow (zoom o pan) y NO sea el primer render,
    // generamos las peticiones PARCIALES (solo para los missing intervals).
    //--------------------------------------------------------------------
    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        const { origin, bound } = viewWindow;
        const strDecimals = '50';

        // Para cada expresión no vacía:
        expressions.forEach((expr, idx) => {
            if (expr.trim() === '') {
                // Si está vacía, limpiamos su caché y resultado
                cacheRef.current[idx] = [];
                setResults((prev) => {
                    const copy = [...prev];
                    copy[idx] = {};
                    return copy;
                });
                return;
            }

            // 1. Obtenemos los intervalos ya cacheados para esta expresión:
            const existing = cacheRef.current[idx] || [];

            // 2. Detectar sub-intervalos faltantes dentro de [origin, bound]:
            const missing = getMissingIntervals(origin, bound, existing);

            // 3. Si no falta nada, reconstruimos dibujo a partir de la caché y salimos:
            if (missing.length === 0) {
                // Unimos todos los puntos de todos los intervalos que intersectan [origin, bound]
                const combined: Array<{ x: number; y: number }> = [];
                existing
                    .filter((iv) => iv.to > origin && iv.from < bound)
                    .forEach((iv) => {
                        // Tomamos solo los puntos dentro de [origin, bound]
                        iv.points.forEach((pt) => {
                            if (pt.x >= origin && pt.x <= bound) {
                                combined.push(pt);
                            }
                        });
                    });
                // Ordenamos por x
                combined.sort((a, b) => a.x - b.x);

                // Actualizamos solo el campo drawingPoints en results
                setResults((prev) => {
                    const updated = prev.map((r) => ({ ...r }));
                    updated[idx] = {
                        ...updated[idx],
                        drawingPoints: combined,
                    };
                    return updated;
                });
                return;
            }

            // 4. Para cada tramo faltante, hacemos 1 fetch y luego agregamos a la caché
            missing.forEach(async ([f, t]) => {
                try {
                    const res = await evaluateSingleExpression(expr, strDecimals, f.toString(), t.toString());
                    const newPts = res.drawingPoints || [];

                    // 5. Insertamos este nuevo intervalo en la caché (y lo mantenemos ordenado)
                    cacheRef.current[idx].push({ from: f, to: t, points: newPts });
                    // Posibilidad de que haya superposición:
                    //  -> NO la consolidamos aquí (para simplicidad), pero en un futuro se pueden fusionar.
                    // Ahora reconstruimos todos los puntos para [origin, bound]:
                    const merged: Array<{ x: number; y: number }> = [];
                    cacheRef.current[idx]
                        .filter((iv) => iv.to > origin && iv.from < bound)
                        .forEach((iv) => {
                            iv.points.forEach((pt) => {
                                if (pt.x >= origin && pt.x <= bound) {
                                    merged.push(pt);
                                }
                            });
                        });
                    merged.sort((a, b) => a.x - b.x);

                    setResults((prev) => {
                        const updated = prev.map((r) => ({ ...r }));
                        updated[idx] = {
                            ...updated[idx],
                            drawingPoints: merged,
                        };
                        return updated;
                    });
                } catch {
                    // En caso de error en esta sub-petición, dejamos dibujo en vacío
                    setResults((prev) => {
                        const updated = prev.map((r) => ({ ...r }));
                        updated[idx] = {
                            ...updated[idx],
                            drawingPoints: [],
                        };
                        return updated;
                    });
                }
            });
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [viewWindow, expressions]);

    //--------------------------------------------------------------------
    // Callback que dispara la petición al backend al hacer blur en un input
    // (para evaluation / calculation)
    //--------------------------------------------------------------------
    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            // Si la cadena está vacía, limpiamos el resultado completo y la caché
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
                // Usamos el viewWindow actual para origin y bound
                const decimals = '50';
                const origin = viewWindow.origin.toString();
                const bound = viewWindow.bound.toString();

                const res = await evaluateSingleExpression(expr, decimals, origin, bound);

                // 1. Guardamos evaluación/calculation/errors:
                setResults((prev) => {
                    const updated = [...prev];
                    updated[index] = res;
                    return updated;
                });

                // 2. Actualizamos la caché: limpiamos la entrada anterior y pedimos TODO el tramo
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
                    updated[index] = { errors: ['Error al evaluar la expresión'] };
                    return updated;
                });
                console.error('[handleExpressionBlur] Error al evaluar:', err);
            }
        },
        [viewWindow]
    );

    //--------------------------------------------------------------------
    // Cuando cambie la ventana de visualización (zoom / pan), este callback
    // será invocado desde GraphCanvas. Solo actualiza viewWindow.
    //--------------------------------------------------------------------
    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    //--------------------------------------------------------------------
    // Métodos para el teclado matemático
    //--------------------------------------------------------------------
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
        // Limpiamos caché completa
        cacheRef.current = {};
    };

    const evaluateExpression = () => {
        const lastExpr = expressions[expressions.length - 1];
        if (lastExpr.trim() !== '') {
            // Trigger blur “manual” si el usuario presiona ↵ en el teclado
            handleExpressionBlur(expressions.length - 1, lastExpr);
        }
    };

    //--------------------------------------------------------------------
    // Construimos un array de todos los conjuntos de puntos que hayan llegado
    //--------------------------------------------------------------------
    // Solo pasamos los “drawingPoints” a GraphCanvas, porque ahí dibujamos
    const allDrawingSets = results
        .map((r) => r.drawingPoints)
        .filter((dp): dp is Array<{ x: number; y: number }> =>
            Array.isArray(dp)
        );

    //--------------------------------------------------------------------
    // Definición del teclado matemático (igual que antes)
    //--------------------------------------------------------------------
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
            <Header title="Placeholder" />
            <div className={styles.mainArea}>
                {/* ─── LADO IZQUIERDO: CANVAS ─────────────────────────────────── */}
                <div className={styles.canvasWrapper}>
                    <GraphCanvas
                        drawingSets={allDrawingSets}
                        onViewChange={handleViewChange}
                    />
                </div>

                {/* ─── LADO DERECHO: EXPRESSION LIST + TECLADO ───────────────── */}
                <div className={styles.expressionsWrapper}>
                    <ExpressionList
                        expressions={expressions}
                        onExpressionsChange={setExpressions}
                        onExpressionBlur={handleExpressionBlur}
                    />

                    {/*  Mostramos debajo de cada input sus resultados (evaluation/calculation/errors) */}
                    <div className={styles.resultsContainer}>
                        {expressions.map((_expr, idx) => {
                            const r = results[idx] || {};
                            return (
                                <div key={idx} className={styles.singleResultBlock}>
                                    {r.evaluation && (
                                        <div className={styles.resultLine}>
                                            <strong>Evaluación:</strong> {r.evaluation}
                                        </div>
                                    )}
                                    {r.calculation && (
                                        <div className={styles.resultLine}>
                                            <strong>Cálculo:</strong> {r.calculation}
                                        </div>
                                    )}
                                    {r.errors &&
                                        r.errors.map((err, i) => (
                                            <div key={i} className={styles.errorLine}>
                                                ⚠ {err}
                                            </div>
                                        ))}
                                </div>
                            );
                        })}
                    </div>

                    {/* ─── TECLADO matemático ───────────────────────────────────── */}
                    <MathKeyboard keys={teclas} />
                </div>
            </div>
        </div>
    );
}

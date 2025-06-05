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

export default function Calculator() {
    // 1. Estado de las expresiones actuales
    const [expressions, setExpressions] = useState<string[]>(['']);

    // 2. Estado de resultados: un objeto por cada línea de expresión
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

    // Sincronizar la longitud de `results` con la de `expressions`
    useEffect(() => {
        setResults((prev) => {
            const updated = [...prev];
            // Si hay más expresiones, agregamos objetos vacíos
            while (updated.length < expressions.length) {
                updated.push({});
            }
            // Si hay menos, recortamos
            updated.length = expressions.length;
            return updated;
        });
    }, [expressions.length]);

    //--------------------------------------------------------------------
    // Cuando cambie viewWindow (p.ej. zoom o pan) y NO sea el primer render,
    // volvemos a pedir al backend los nuevos puntos “drawingPoints” para cada expresión no vacía.
    //--------------------------------------------------------------------
    useEffect(() => {
        if (isFirstRender.current) {
            // En el primer render no forzamos una recarga de gráfico
            isFirstRender.current = false;
            return;
        }
        // Para cada expresión no vacía, solicitamos dibujo con los nuevos origin/bound
        (async () => {
            const { origin, bound } = viewWindow;
            const strOrigin = origin.toString();
            const strBound = bound.toString();
            // Generamos un array de promesas: índice + resultado
            const promises = expressions.map(async (expr, idx) => {
                if (expr.trim() === '') {
                    return { idx, newDrawing: undefined };
                }
                try {
                    // Llamamos al servicio con los nuevos límites
                    const res = await evaluateSingleExpression(
                        expr,
                        '50',
                        strOrigin,
                        strBound
                    );
                    return { idx, newDrawing: res.drawingPoints || [] };
                } catch {
                    return { idx, newDrawing: [] };
                }
            });

            const all = await Promise.all(promises);

            // Actualizamos únicamente el campo drawingPoints en cada resultado
            setResults((prev) => {
                const updated = prev.map((r) => ({ ...r })); // Clonamos
                all.forEach(({ idx, newDrawing }) => {
                    if (newDrawing !== undefined) {
                        updated[idx] = {
                            ...updated[idx],
                            drawingPoints: newDrawing,
                        };
                    } else {
                        // Si la expresión está vacía, limpiamos dibujo
                        updated[idx] = {
                            ...updated[idx],
                            drawingPoints: undefined,
                        };
                    }
                });
                return updated;
            });
        })();
    }, [viewWindow, expressions]);

    //--------------------------------------------------------------------
    // Callback que dispara la petición al backend al hacer blur en un input
    //--------------------------------------------------------------------
    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            // Si la cadena está vacía, limpiamos el resultado completo
            if (expr.trim() === '') {
                setResults((prev) => {
                    const copy = [...prev];
                    copy[index] = {};
                    return copy;
                });
                return;
            }

            try {
                // Usamos el viewWindow actual para origen y límite
                const decimals = '50';
                const origin = viewWindow.origin.toString();
                const bound = viewWindow.bound.toString();

                const res = await evaluateSingleExpression(
                    expr,
                    decimals,
                    origin,
                    bound
                );

                setResults((prev) => {
                    const updated = [...prev];
                    updated[index] = res;
                    return updated;
                });
            } catch (err) {
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
    // será invocado desde GraphCanvas. Sólo actualiza viewWindow.
    //--------------------------------------------------------------------
    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    //--------------------------------------------------------------------
    // Método para insertar texto desde el teclado matemático en la última expresión
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
    // Sólo pasamos los “drawingPoints” a GraphCanvas, porque ahí dibujamos
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

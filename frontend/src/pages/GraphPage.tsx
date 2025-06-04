// src/pages/GraphPage.tsx
import { useState, useEffect } from 'react';
import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas';
import MathKeyboard from '../components/App/MathKeyboard';
import styles from '../styles/modules/graphCanvas.module.css';
import { evaluateSingleExpression } from '../services/mathService';
import type { ExpressionResult } from '../types/math';

export default function GraphPage() {
    // 1. Estado de las expresiones actuales
    const [expressions, setExpressions] = useState<string[]>(['']);

    // 2. Estado de resultados: un objeto por cada línea de expresión
    const [results, setResults] = useState<ExpressionResult[]>([{ }]);

    // Cuando la longitud de `expressions` cambie, ajustamos `results` para coincidir
    useEffect(() => {
        setResults((prev) => {
            const newArr = [...prev];
            // Si hay más expresiones, agregamos índices vacíos
            while (newArr.length < expressions.length) {
                newArr.push({});
            }
            // Si hay menos, recortamos
            if (newArr.length > expressions.length) {
                newArr.length = expressions.length;
            }
            return newArr;
        });
    }, [expressions]);

    // Callback que dispara la petición al backend al hacer blur en un input
    const handleExpressionBlur = async (index: number, expr: string) => {
        // Si la cadena está vacía, simplemente limpiamos cualquier resultado anterior
        if (expr.trim() === '') {
            setResults((prev) => {
                const copy = [...prev];
                copy[index] = {};
                return copy;
            });
            return;
        }

        // Llamamos al servicio; puedes ajustar decimals/origin/bound según UX (aquí fijos por ejemplo)
        const decimals = '50';
        const origin = '-10';
        const bound = '10';

        const res = await evaluateSingleExpression(expr, decimals, origin, bound);
        setResults((prev) => {
            const updated = [...prev];
            updated[index] = res;
            return updated;
        });
    };

    // Teclado matemático:
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
        console.log('Evaluar: ', expressions[expressions.length - 1]);
    };

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

    // Construimos un array de todos los conjuntos de puntos que hayan llegado
    // en results[i].drawingPoints
    const allDrawingSets = results
        .filter((r) => Array.isArray(r.drawingPoints))
        .map((r) => r.drawingPoints!) as Array<Array<{ x: number; y: number }>>;

    return (
        <div className={styles.pageContainer}>
            <Header title="Placeholder" />
            <div className={styles.mainArea}>
                {/* ─── LADO IZQUIERDO: CANVAS ─────────────────────────────────── */}
                <div className={styles.canvasWrapper}>
                    <GraphCanvas expressions={expressions} drawingSets={allDrawingSets} />
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

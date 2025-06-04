import { useState } from 'react';
import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas';
import MathKeyboard from '../components/App/MathKeyboard';
import styles from '../styles/modules/graphCanvas.module.css';

/**
 * GraphPage:
 * - Lado izquierdo: cuadricula (GraphCanvas).
 * - Lado derecho: primero ExpressionList, luego el MathKeyboard que definimos.
 */
export default function GraphPage() {
    // Estado que contiene todas las expresiones que ingresa el usuario.
    const [expressions, setExpressions] = useState<string[]>(['']);

    // Inserta texto (value) en la última línea de la lista de expresiones:
    const insertIntoExpression = (value: string) => {
        setExpressions((prev) => {
            const newArr = [...prev];
            const lastIndex = newArr.length - 1;
            newArr[lastIndex] = newArr[lastIndex] + value;
            return newArr;
        });
    };

    // Borra el último carácter de la última expresión
    const backspace = () => {
        setExpressions((prev) => {
            const newArr = [...prev];
            const lastIndex = newArr.length - 1;
            newArr[lastIndex] = newArr[lastIndex].slice(0, -1);
            return newArr;
        });
    };

    // Limpia toda la lista de expresiones y deja una sola línea vacía
    const clearAll = () => {
        setExpressions(['']);
    };

    // (Opcional) “Evaluar” la expresión: aquí podrías parsear y graficar, etc.
    const evaluateExpression = () => {
        // Por ahora no hace nada, pero puedes poner tu lógica aquí.
        console.log('Evaluar: ', expressions[expressions.length - 1]);
    };

    /**
     * Now: definimos exactamente las 35 teclas, en orden de aparición fila×columna.
     * Cada objeto tiene:
     *  - label: el texto que se ve en pantalla
     *  - onClick: la acción que queremos ejecutar
     *  - className: opcional (“wideKey” si queremos que ocupe 2 columnas)
     */
    const teclas = [
        // ─── FILA 1 ─────────────────────────────────────────────────────────────
        { label: '2nd', onClick: () => insertIntoExpression('2nd') },
        { label: 'π',   onClick: () => insertIntoExpression('π') },
        { label: 'e',   onClick: () => insertIntoExpression('e') },
        { label: 'C',   onClick: clearAll },
        { label: '⌫',   onClick: backspace },

        // ─── FILA 2 ─────────────────────────────────────────────────────────────
        { label: 'x²',  onClick: () => insertIntoExpression('^2') },
        { label: '1/x', onClick: () => insertIntoExpression('1/') },
        { label: '|x|', onClick: () => insertIntoExpression('| |') },
        { label: 'x',   onClick: () => insertIntoExpression('x') },
        { label: 'y',   onClick: () => insertIntoExpression('y') },

        // ─── FILA 3 ─────────────────────────────────────────────────────────────
        { label: '√',   onClick: () => insertIntoExpression('√(') },
        { label: '(',   onClick: () => insertIntoExpression('(') },
        { label: ')',   onClick: () => insertIntoExpression(')') },
        { label: '=',   onClick: () => insertIntoExpression('=') },
        { label: '÷',   onClick: () => insertIntoExpression('/') },

        // ─── FILA 4 ─────────────────────────────────────────────────────────────
        { label: 'xʸ',  onClick: () => insertIntoExpression('^') },
        { label: '7',   onClick: () => insertIntoExpression('7') },
        { label: '8',   onClick: () => insertIntoExpression('8') },
        { label: '9',   onClick: () => insertIntoExpression('9') },
        { label: '×',   onClick: () => insertIntoExpression('*') },

        // ─── FILA 5 ─────────────────────────────────────────────────────────────
        { label: '10ˣ', onClick: () => insertIntoExpression('10^') },
        { label: '4',   onClick: () => insertIntoExpression('4') },
        { label: '5',   onClick: () => insertIntoExpression('5') },
        { label: '6',   onClick: () => insertIntoExpression('6') },
        { label: '−',   onClick: () => insertIntoExpression('-') },

        // ─── FILA 6 ─────────────────────────────────────────────────────────────
        { label: 'log', onClick: () => insertIntoExpression('log(') },
        { label: '1',   onClick: () => insertIntoExpression('1') },
        { label: '2',   onClick: () => insertIntoExpression('2') },
        { label: '3',   onClick: () => insertIntoExpression('3') },
        { label: '+',   onClick: () => insertIntoExpression('+') },

        // ─── FILA 7 ─────────────────────────────────────────────────────────────
        { label: 'ln',  onClick: () => insertIntoExpression('ln(') },
        // Aquí usaremos “⟲” como ejemplo de undo; podría ser cualquier función
        { label: '⟲',   onClick: () => console.log('Undo pressed') },
        { label: '0',   onClick: () => insertIntoExpression('0') },
        { label: ',',   onClick: () => insertIntoExpression(',') },
        // Tecla “Enter” ocupa 2 columnas seguidas ⇒ className: 'wideKey'
        { label: '↵',   onClick: evaluateExpression, className: 'wideKey' },
    ];

    return (
        <div className={styles.pageContainer}>
            <Header title="Placeholder" />
            <div className={styles.mainArea}>
                {/* ─── LADO IZQUIERDO: CANVAS ─────────────────────────────────── */}
                <div className={styles.canvasWrapper}>
                    <GraphCanvas expressions={expressions} />
                </div>

                {/* ─── LADO DERECHO: EXPRESSION LIST + TECLADO ───────────────── */}
                <div className={styles.expressionsWrapper}>
                    {/* 1) Lista de expresiones (con scroll si crece demasiado) */}
                    <ExpressionList
                        expressions={expressions}
                        onExpressionsChange={setExpressions}
                    />

                    {/* 2) Teclado matemático tipo “científico” */}
                    <MathKeyboard keys={teclas} />
                </div>
            </div>
        </div>
    );
}

// src/components/App/ExpressionList.tsx
import { useEffect, useRef, useState } from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (updater: (prev: string[]) => string[]) => void;
    onExpressionBlur: (index: number, expr: string) => void;
    colors: string[];
    onColorChange: (index: number, newColor: string) => void;
    disabledFlags: boolean[];
    onToggleDisabled: (index: number) => void;
    expressionTypes: Array<string | undefined>;
    results: Array<{
        evaluation?: string;
        calculation?: string;
        errors?: string[];
    }>;
}

export default function ExpressionList({
                                           expressions,
                                           onExpressionsChange,
                                           onExpressionBlur,
                                           colors,
                                           onColorChange,
                                           disabledFlags,
                                           onToggleDisabled,
                                           expressionTypes,
                                           results,
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);
    const colorInputRefs = useRef<Array<HTMLInputElement | null>>([]);

    const [focusedIndex, setFocusedIndex] = useState<number | null>(null);
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    const handleChange = (index: number, value: string) => {
        onExpressionsChange(prev => {
            const updated = [...prev];
            updated[index] = value;
            if (index === prev.length - 1 && value.trim() !== '') {
                updated.push('');
            }
            return updated;
        });
    };

    const handleBlur = (index: number) => {
        const value = expressions[index] ?? '';
        onExpressionBlur(index, value);
        const isLast = index === expressions.length - 1;
        if (!isLast) return;
        onExpressionsChange(prev =>
            value.trim() !== '' && prev[prev.length - 1].trim() !== ''
                ? [...prev, '']
                : prev
        );
    };

    const handleClearRow = (index: number) => {
        onExpressionsChange(prev => {
            const updated = [...prev];
            updated[index] = '';
            return updated;
        });
        const inputEl = inputRefs.current[index];
        if (inputEl) {
            inputEl.focus();
            inputEl.select();
        }
    };

    const handleDeleteRow = (index: number) => {
        onExpressionsChange(prev => {
            if (prev.length <= 1) return [''];
            const updated = [...prev];
            updated.splice(index, 1);
            return updated;
        });
    };

    inputRefs.current = [];
    colorInputRefs.current = [];

    return (
        <div className={styles.listContainer} ref={containerRef}>
            {expressions.map((expr, idx) => {
                const isLastGap = idx === expressions.length - 1 && expr.trim() === '';
                const tipo = expressionTypes[idx];
                let etiqueta: string;
                console.log(tipo)
                switch (tipo) {
                    case 'FUNCTION':
                        etiqueta = 'f';
                        break;
                    case 'ASSIGNMENT':
                        etiqueta = 'a';
                        break;
                    case 'NUMERIC':
                        etiqueta = 'N';
                        break;
                    case 'EQUATION':
                        etiqueta = 'Eq';
                        break;
                    case 'MATRIX':
                        etiqueta = 'm';
                        break;
                    case 'BOOLEAN':
                        etiqueta = 'b';
                        break;
                    case 'VECTOR':
                        etiqueta = 'v';
                        break;
                    default:
                        etiqueta = 'Ex';
                }
                const originalColor = isLastGap ? '#666666' : colors[idx] || '#666666';
                const functionColor = disabledFlags[idx] ? '#666666' : originalColor;

                const countSameTypeBefore = expressionTypes
                    .slice(0, idx)
                    .filter(t => t === tipo).length;
                const subIndex = isLastGap ? '' : String(countSameTypeBefore + 1);

                const { evaluation, calculation, errors } = results[idx] || {};

                // Filtrado de líneas repetidas:
                const lines: string[] = [];
                if (evaluation && evaluation !== expr) lines.push(`= ${evaluation}`);
                if (calculation && calculation !== expr && calculation !== evaluation)
                    lines.push(`= ${calculation}`);

                return (
                    <div
                        key={idx}
                        className={`${styles.inputWrapper} ${
                            (hoveredIndex === idx || focusedIndex === idx) && !disabledFlags[idx]
                                ? styles.rowHighlighted
                                : ''
                        }`}
                        style={{ '--highlight-border': functionColor } as React.CSSProperties}
                        onMouseEnter={() => setHoveredIndex(idx)}
                        onMouseLeave={() => setHoveredIndex(prev => (prev === idx ? null : prev))}
                    >
                        <div className={styles.inputRow}>
                            <div
                                className={styles.functionLabel}
                                style={{
                                    backgroundColor: functionColor,
                                    borderColor:
                                        (hoveredIndex === idx || focusedIndex === idx) && !disabledFlags[idx]
                                            ? functionColor
                                            : 'transparent',
                                    color: functionColor,
                                    cursor: isLastGap ? 'default' : 'pointer',
                                }}
                                onClick={() => !isLastGap && onToggleDisabled(idx)}
                            >
                                <span className={styles.fLetter}><em>{etiqueta}</em></span>
                                {!isLastGap && <sub className={styles.fSubscript}>{subIndex}</sub>}
                            </div>

                            <input
                                type="text"
                                className={styles.exprInput}
                                placeholder={isLastGap ? 'Escribir una función' : ''}
                                value={expr}
                                ref={el => { inputRefs.current[idx] = el }}
                                onChange={e => handleChange(idx, e.target.value)}
                                onFocus={() => setFocusedIndex(idx)}
                                onBlur={() => { handleBlur(idx); setFocusedIndex(null) }}
                                aria-label={`Expresión ${idx + 1}`}
                            />

                            {hoveredIndex === idx && focusedIndex !== idx && !isLastGap && (
                                <div className={styles.buttonsContainer}>
                                    {errors && errors.length > 0 && (
                                        <span
                                            className={styles.errorIcon}
                                            title={errors.join('\n')}
                                            aria-label="Errores"
                                        >
                      ⚠️
                    </span>
                                    )}

                                    {!disabledFlags[idx] && (
                                        <button
                                            type="button"
                                            aria-label={`Evaluar fila ${idx + 1}`}
                                            className={styles.iconButton}
                                            style={{ '--button-color': functionColor } as React.CSSProperties}
                                            onMouseDown={e => { e.preventDefault(); onExpressionBlur(idx, expr) }}
                                        >
                                            ⚡
                                        </button>
                                    )}

                                    <button
                                        type="button"
                                        aria-label={`Cambiar color fila ${idx + 1}`}
                                        className={styles.iconButton}
                                        style={{ '--button-color': functionColor } as React.CSSProperties}
                                        onClick={() => colorInputRefs.current[idx]?.click()}
                                    >
                                        🎨
                                    </button>

                                    <button
                                        type="button"
                                        aria-label={`Borrar fila ${idx + 1}`}
                                        className={styles.iconButton}
                                        style={{ '--button-color': functionColor } as React.CSSProperties}
                                        onClick={e => { e.preventDefault(); handleDeleteRow(idx) }}
                                    >
                                        🗑️
                                    </button>
                                </div>
                            )}

                            {focusedIndex === idx && expr.trim() !== '' && (
                                <button
                                    type="button"
                                    aria-label={`Limpiar fila ${idx + 1}`}
                                    className={styles.clearButton}
                                    onMouseDown={e => { e.preventDefault(); handleClearRow(idx) }}
                                >
                                    ×
                                </button>
                            )}

                            <input
                                type="color"
                                ref={el => { colorInputRefs.current[idx] = el }}
                                value={functionColor}
                                onChange={e => onColorChange(idx, e.target.value)}
                                style={{
                                    position: 'fixed',
                                    width: '1px',
                                    height: '1px',
                                    opacity: 0,
                                    pointerEvents: 'none',
                                }}
                            />
                        </div>

                        <div className={styles.resultLines}>
                            {lines.map((line, i) => (
                                <div key={i} className={styles.resultLine}>
                                    {line}
                                </div>
                            ))}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

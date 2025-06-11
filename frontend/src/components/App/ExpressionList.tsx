import React, {useEffect, useRef, useState} from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (updater: (prev: string[]) => string[]) => void;
    onExpressionBlur: (index: number, expr: string) => void;
    colors: string[];
    onColorChange: (index: number, newColor: string) => void;
    disabledFlags: boolean[];
    onToggleDisabled: (index: number) => void;
    onDeleteRow: (index: number) => void;
    expressionTypes: Array<string | undefined>;
    results: Array<{
        evaluation?: string;
        calculation?: string;
        errors?: string[];
        warnings?: string[];
    }>;
    focusedIndex: number | null;
    setFocusedIndex: (i: number | null) => void;
    caretPosition: number;
    setCaretPosition: (pos: number) => void;
    selectionLength: number;
    setSelectionLength: (len: number) => void;
}

const MAX_ROWS = 10;

export default function ExpressionList({
                                           expressions,
                                           onExpressionsChange,
                                           onExpressionBlur,
                                           colors,
                                           onColorChange,
                                           disabledFlags,
                                           onToggleDisabled,
                                           onDeleteRow,
                                           expressionTypes,
                                           results,
                                           focusedIndex,
                                           setFocusedIndex,
                                           caretPosition,
                                           setCaretPosition,
                                           selectionLength,
                                           setSelectionLength,
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);
    const colorInputRefs = useRef<Array<HTMLInputElement | null>>([]);

    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);
    const [sliderConfigs, setSliderConfigs] = useState<{ min: number; max: number; step: number }[]>([]);

    // Count only non-empty lines (excluding the last gap row)
    const usableRows = expressions.filter((expr, idx) => idx !== expressions.length - 1 && expr.trim() !== '').length;

    useEffect(() => {
        setSliderConfigs(prev => {
            const updated = [...prev];
            while (updated.length < expressions.length) {
                updated.push({min: 0, max: 1, step: 0.1});
            }
            updated.length = expressions.length;
            return updated;
        });
    }, [expressions]);

    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    useEffect(() => {
        if (
            focusedIndex !== null &&
            inputRefs.current[focusedIndex]
        ) {
            const input = inputRefs.current[focusedIndex]!;
            input.focus();
            const start = caretPosition;
            const end = caretPosition + selectionLength;
            input.setSelectionRange(start, end);
        }
    }, [focusedIndex, caretPosition, selectionLength, expressions]);

    useEffect(() => {
        // Only allow adding a new row if the usableRows is less than MAX_ROWS
        if (
            (expressions.length === 0 || expressions[expressions.length - 1].trim() !== '') &&
            usableRows < MAX_ROWS
        ) {
            onExpressionsChange(prev => {
                // Only add blank if not already blank and not exceeding max usable rows
                const usable = prev.filter((expr, idx) => idx !== prev.length - 1 && expr.trim() !== '').length;
                if ((prev.length === 0 || prev[prev.length - 1].trim() !== '') && usable < MAX_ROWS) {
                    return [...prev, ''];
                }
                return prev;
            });
        }
        // Remove extra blank rows if necessary
        else if (
            expressions.length > 1 &&
            expressions[expressions.length - 2].trim() === '' &&
            expressions[expressions.length - 1].trim() === ''
        ) {
            onExpressionsChange(prev => {
                let copy = [...prev];
                while (
                    copy.length > 1 &&
                    copy[copy.length - 2].trim() === '' &&
                    copy[copy.length - 1].trim() === ''
                    ) {
                    copy.pop();
                }
                return copy;
            });
        }
        // Only rerun this if expressions or onExpressionsChange changes
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [expressions, onExpressionsChange, usableRows]);

    const handleChange = (index: number, value: string) => {
        // Prevent adding new content if max rows reached and the row is the last blank
        if (usableRows >= MAX_ROWS &&
            index === expressions.length - 1 &&
            expressions[index].trim() === '' &&
            value.trim() !== ''
        ) {
            return; // Ignore typing into the last gap if at max rows
        }
        onExpressionsChange(prev => {
            const updated = [...prev];
            updated[index] = value;
            return updated;
        });
    };

    return (
        <div className={styles.listContainer} ref={containerRef}>
            {expressions.map((expr, idx) => {
                const isLastGap = idx === expressions.length - 1 && expr.trim() === '';
                const tipo = isLastGap ? undefined : expressionTypes[idx];
                let etiqueta: string;
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
                const isDisabled = disabledFlags[idx];
                const originalColor = isLastGap ? 'var(--expr-first-color)' : colors[idx] || 'var(--expr-first-color)';
                const functionColor = isDisabled ? 'var(--expr-disabled-color)' : originalColor;

                const countSameTypeBefore = expressionTypes.slice(0, idx).filter(t => t === tipo).length;
                const subIndex = isLastGap ? '' : String(countSameTypeBefore + 1);

                const {evaluation, calculation, errors} = results[idx] || {};
                const lines: string[] = [];
                if (evaluation && evaluation !== expr) lines.push(`= ${evaluation}`);
                if (calculation && calculation !== expr && calculation !== evaluation)
                    lines.push(`= ${calculation}`);

                const assignMatch = expr.match(/^\s*([a-zA-Z]+)\s*=\s*([-+]?\d+(?:\.\d+)?)\s*$/);
                const isNumericAssign = !!assignMatch;
                const varName = assignMatch?.[1] || '';
                const currentValue = assignMatch ? parseFloat(assignMatch[2]) : 0;
                const {min, max, step} = sliderConfigs[idx] || {min: 0, max: 1, step: 0.1};

                const iconCount =
                    (errors && errors.length > 0 ? 1 : 0) +
                    (results[idx]?.warnings?.length ? 1 : 0) +
                    (!isDisabled ? 1 : 0) +
                    1 +
                    1;

                const dynamicPadding = hoveredIndex === idx && focusedIndex !== idx && !isLastGap
                    ? `calc(2.35rem + ${iconCount} * 2.35rem)`
                    : '2.5rem';

                // Prevent rendering the last gap row if max usable rows reached
                if (isLastGap && usableRows >= MAX_ROWS) {
                    return null;
                }

                return (
                    <div
                        key={idx}
                        className={`${styles.inputWrapper} ${(hoveredIndex === idx || focusedIndex === idx) && !isDisabled ? styles.rowHighlighted : ''}`}
                        style={{'--highlight-border': functionColor} as React.CSSProperties}
                        onMouseEnter={() => setHoveredIndex(idx)}
                        onMouseLeave={() => setHoveredIndex(prev => (prev === idx ? null : prev))}
                    >
                        <div className={styles.inputRow}>
                            <div
                                className={styles.functionLabel}
                                style={{
                                    backgroundColor: functionColor,
                                    borderColor: (hoveredIndex === idx || focusedIndex === idx) && !isDisabled ? functionColor : 'transparent',
                                    color: isDisabled ? 'var(--expr-label)' : functionColor,
                                    cursor: isLastGap ? 'default' : 'pointer'
                                }}
                                onClick={() => !isLastGap && onToggleDisabled(idx)}
                            >
                                <span className={styles.fLetter}><em>{etiqueta}</em></span>
                                {!isLastGap && <sub className={styles.fSubscript}>{subIndex}</sub>}
                            </div>

                            <input
                                type="text"
                                style={{ paddingRight: dynamicPadding }}
                                className={`${styles.exprInput} ${(hoveredIndex === idx && focusedIndex !== idx && !isLastGap) ? styles.hasButtons : ''}`}
                                placeholder={isLastGap ? (usableRows >= MAX_ROWS ? 'Máximo 10 filas' : 'Escribir una función') : ''}
                                value={expr}
                                ref={el => {
                                    inputRefs.current[idx] = el
                                }}
                                onChange={e => {
                                    handleChange(idx, e.target.value);
                                    setCaretPosition(e.target.selectionStart ?? 0);
                                    setSelectionLength(
                                        (e.target.selectionEnd ?? 0) - (e.target.selectionStart ?? 0)
                                    );
                                }}
                                onFocus={e => {
                                    setFocusedIndex(idx);
                                    setCaretPosition(e.target.selectionStart ?? 0);
                                    setSelectionLength(
                                        (e.target.selectionEnd ?? 0) - (e.target.selectionStart ?? 0)
                                    );
                                }}
                                onClick={e => {
                                    setFocusedIndex(idx);
                                    setCaretPosition(e.currentTarget.selectionStart ?? 0);
                                    setSelectionLength(
                                        (e.currentTarget.selectionEnd ?? 0) - (e.currentTarget.selectionStart ?? 0)
                                    );
                                }}
                                onSelect={e => {
                                    setFocusedIndex(idx);
                                    setCaretPosition(e.currentTarget.selectionStart ?? 0);
                                    setSelectionLength(
                                        (e.currentTarget.selectionEnd ?? 0) - (e.currentTarget.selectionStart ?? 0)
                                    );
                                }}
                                onBlur={e => {
                                    if (
                                        e.relatedTarget &&
                                        (e.relatedTarget as HTMLElement).dataset &&
                                        (e.relatedTarget as HTMLElement).dataset.virtualkey === "true"
                                    ) {
                                        setTimeout(() => {
                                            inputRefs.current[idx]?.focus();
                                            const selStart = caretPosition;
                                            const selEnd = caretPosition + selectionLength;
                                            inputRefs.current[idx]?.setSelectionRange(selStart, selEnd);
                                        }, 0);
                                        return;
                                    }
                                    setFocusedIndex(null);
                                    setCaretPosition(0);
                                    setSelectionLength(0);
                                    onExpressionBlur(idx, expr);
                                }}
                                aria-label={`Expresión ${idx + 1}`}
                                disabled={isLastGap && usableRows >= MAX_ROWS}
                            />

                            {hoveredIndex === idx && focusedIndex !== idx && !isLastGap && (
                                <div className={styles.buttonsContainer}>
                                    {errors && errors.length > 0 &&
                                        <span className={styles.errorIcon} title={errors.join('\n')}
                                              aria-label="Errores">❌</span>}
                                    {results[idx]?.warnings && results[idx].warnings.length > 0 &&
                                        <span className={styles.warningIcon} title={results[idx].warnings.join('\n')}
                                              aria-label="Advertencias">⚠️</span>}
                                    {!isDisabled && <button type="button" aria-label={`Evaluar fila ${idx + 1}`}
                                                            className={styles.iconButton}
                                                            style={{'--button-color': functionColor} as React.CSSProperties}
                                                            onMouseDown={e => {
                                                                e.preventDefault();
                                                                onExpressionBlur(idx, expr);
                                                            }}>⚡</button>}
                                    <button type="button" aria-label={`Cambiar color fila ${idx + 1}`}
                                            className={styles.iconButton}
                                            style={{'--button-color': functionColor} as React.CSSProperties}
                                            onClick={() => colorInputRefs.current[idx]?.click()}>🎨
                                    </button>
                                    <button type="button" aria-label={`Borrar fila ${idx + 1}`}
                                            className={styles.iconButton}
                                            style={{'--button-color': functionColor} as React.CSSProperties}
                                            onClick={e => {
                                                e.preventDefault();
                                                setSliderConfigs(prev => {
                                                    const updated = [...prev];
                                                    updated.splice(idx, 1);
                                                    return updated;
                                                });
                                                onDeleteRow(idx);
                                            }}>
                                        🗑️
                                    </button>
                                </div>
                            )}

                            {focusedIndex === idx && expr.trim() !== '' && (
                                <button type="button" aria-label={`Limpiar fila ${idx + 1}`}
                                        className={styles.clearButton} onMouseDown={e => {
                                    e.preventDefault();
                                    onExpressionsChange(prev => {
                                        const u = [...prev];
                                        u[idx] = '';
                                        return u;
                                    });
                                    setCaretPosition(0);
                                    setSelectionLength(0);
                                }}>×</button>
                            )}

                            <input type="color" ref={el => {
                                colorInputRefs.current[idx] = el
                            }} value={functionColor} onChange={e => onColorChange(idx, e.target.value)} className={styles.colorInput}/>
                        </div>

                        {isNumericAssign && (
                            <div className={styles.sliderRow}>
                                <input
                                    type="range"
                                    min={min}
                                    max={max}
                                    step={step}
                                    value={currentValue}
                                    onChange={e => {
                                        const newVal = e.target.value;
                                        onExpressionsChange(prev => {
                                            const u = [...prev];
                                            u[idx] = `${varName} = ${newVal}`;
                                            return u;
                                        });
                                    }}
                                />
                                <div className={styles.sliderControls}>
                                    <label>Min <input type="number" value={min} onChange={e => {
                                        const v = parseFloat(e.target.value);
                                        setSliderConfigs(cfg => {
                                            const c = [...cfg];
                                            c[idx] = {...c[idx], min: v};
                                            return c;
                                        });
                                    }}/></label>
                                    <label>Step <input type="number" value={step} onChange={e => {
                                        const v = parseFloat(e.target.value);
                                        setSliderConfigs(cfg => {
                                            const c = [...cfg];
                                            c[idx] = {...c[idx], step: v};
                                            return c;
                                        });
                                    }}/></label>
                                    <label>Max <input type="number" value={max} onChange={e => {
                                        const v = parseFloat(e.target.value);
                                        setSliderConfigs(cfg => {
                                            const c = [...cfg];
                                            c[idx] = {...c[idx], max: v};
                                            return c;
                                        });
                                    }}/></label>
                                </div>
                            </div>
                        )}

                        <div className={styles.resultLines}>
                            {lines.map((line, i) => (
                                <div key={i} className={styles.resultLine}>{line}</div>
                            ))}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}
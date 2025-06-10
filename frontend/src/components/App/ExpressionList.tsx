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
        // Si hay un input enfocado, pon el foco y la selección correctamente
        if (
            focusedIndex !== null &&
            inputRefs.current[focusedIndex]
        ) {
            const input = inputRefs.current[focusedIndex]!;
            input.focus();
            // Selecciona si hay selectionLength distinto de 0, si no solo mueve el caret
            const start = caretPosition;
            const end = caretPosition + selectionLength;
            input.setSelectionRange(start, end);
        }
    }, [focusedIndex, caretPosition, selectionLength, expressions]);

    // Lógica para asegurar que siempre hay una fila vacía al final
    useEffect(() => {
        if (expressions.length === 0 || expressions[expressions.length - 1].trim() !== '') {
            onExpressionsChange(prev => {
                if (prev.length === 0 || prev[prev.length - 1].trim() !== '') {
                    return [...prev, ''];
                }
                return prev;
            });
        }
        // Opcional: eliminar filas vacías duplicadas al final
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
    }, [expressions, onExpressionsChange]);

    const handleChange = (index: number, value: string) => {
        onExpressionsChange(prev => {
            const updated = [...prev];
            updated[index] = value;
            return updated;
        });
        // Limpiar resultados si lo necesitas
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
                const originalColor = isLastGap ? '#666666' : colors[idx] || '#666666';
                const functionColor = disabledFlags[idx] ? '#666666' : originalColor;

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
                    (!disabledFlags[idx] ? 1 : 0) + // ⚡
                    1 + // 🎨
                    1;  // 🗑️

                const dynamicPadding = hoveredIndex === idx && focusedIndex !== idx && !isLastGap
                    ? `${2.35 + iconCount * 2.35}rem`
                    : '2.5rem';

                return (
                    <div
                        key={idx}
                        className={`${styles.inputWrapper} ${(hoveredIndex === idx || focusedIndex === idx) && !disabledFlags[idx] ? styles.rowHighlighted : ''}`}
                        style={{'--highlight-border': functionColor} as React.CSSProperties}
                        onMouseEnter={() => setHoveredIndex(idx)}
                        onMouseLeave={() => setHoveredIndex(prev => (prev === idx ? null : prev))}
                    >
                        <div className={styles.inputRow}>
                            <div
                                className={styles.functionLabel}
                                style={{
                                    backgroundColor: functionColor,
                                    borderColor: (hoveredIndex === idx || focusedIndex === idx) && !disabledFlags[idx] ? functionColor : 'transparent',
                                    color: functionColor,
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
                                placeholder={isLastGap ? 'Escribir una función' : ''}
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
                                // Cambiado: NO limpiar el foco al hacer blur si el blur viene de click del teclado virtual
                                onBlur={e => {
                                    // Si el blur fue provocado por un botón del teclado virtual, no pierdas el foco
                                    if (
                                        e.relatedTarget &&
                                        (e.relatedTarget as HTMLElement).dataset &&
                                        (e.relatedTarget as HTMLElement).dataset.virtualkey === "true"
                                    ) {
                                        // Vuelve a enfocar el input al siguiente tick
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
                            />

                            {hoveredIndex === idx && focusedIndex !== idx && !isLastGap && (
                                <div className={styles.buttonsContainer}>
                                    {errors && errors.length > 0 &&
                                        <span className={styles.errorIcon} title={errors.join('\n')}
                                              aria-label="Errores">❌</span>}
                                    {results[idx]?.warnings && results[idx].warnings.length > 0 &&
                                        <span className={styles.warningIcon} title={results[idx].warnings.join('\n')}
                                              aria-label="Advertencias">⚠️</span>}
                                    {!disabledFlags[idx] && <button type="button" aria-label={`Evaluar fila ${idx + 1}`}
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
                            }} value={functionColor} onChange={e => onColorChange(idx, e.target.value)} style={{
                                position: 'fixed',
                                width: '1px',
                                height: '1px',
                                opacity: 0,
                                pointerEvents: 'none'
                            }}/>
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
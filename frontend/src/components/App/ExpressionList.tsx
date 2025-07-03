import React, {useEffect, useRef, useState} from 'react'; // Import React and React hooks for side effects, refs, and state management
import styles from '../../styles/modules/expressionList.module.css'; // Import styles from CSS module for the expression list

/**
 * Interface defining the props expected by the ExpressionList component.
 * @property {string[]} expressions - Array of user expressions (each row in the list).
 * @property {(updater: (prev: string[]) => string[]) => void} onExpressionsChange - Callback to update expressions.
 * @property {(index: number, expr: string) => void} onExpressionBlur - Callback for blur event on row input.
 * @property {string[]} colors - Array of color strings for each expression.
 * @property {(index: number, newColor: string) => void} onColorChange - Callback to update the color of a row.
 * @property {boolean[]} disabledFlags - Boolean flags for row enabled/disabled state.
 * @property {(index: number) => void} onToggleDisabled - Callback to toggle row enabled/disabled.
 * @property {(index: number) => void} onDeleteRow - Callback to delete a row.
 * @property {Array<string | undefined>} expressionTypes - Array of types, one for each row.
 * @property {Array<{evaluation?: string; calculation?: string; errors?: string[]; warnings?: string[]}>} results - Result objects for each row.
 * @property {number | null} focusedIndex - Index of the currently focused row, or null if none.
 * @property {(i: number | null) => void} setFocusedIndex - Setter for focusedIndex.
 * @property {number} caretPosition - Current caret position within the input.
 * @property {(pos: number) => void} setCaretPosition - Setter for caretPosition.
 * @property {number} selectionLength - Current selection length in the input.
 * @property {(len: number) => void} setSelectionLength - Setter for selectionLength.
 */
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

/**
 * The maximum number of usable rows allowed in the list.
 * Any more are prevented from being added.
 */
const MAX_ROWS = 10;

/**
 * ExpressionList component renders a dynamic list of editable expressions for the user.
 * It supports per-row color selection, enable/disable, evaluation, sliders for numeric assignments, and error/warning display.
 * @param {ExpressionListProps} props - All props required for controlling the list and its state.
 * @returns {JSX.Element} The rendered list of expression input rows and controls.
 */
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
    /**
     * Ref to the main container div, used for scrolling to bottom on changes.
     */
    const containerRef = useRef<HTMLDivElement>(null);

    /**
     * Ref array to each input element for focus and selection manipulation.
     */
    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);

    /**
     * Ref array to each color input element for programmatic color chooser opening.
     */
    const colorInputRefs = useRef<Array<HTMLInputElement | null>>([]);

    /**
     * State for the index of the row currently hovered by the mouse.
     */
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    /**
     * State for slider configuration (min, max, step) per row.
     * Used for numeric assignment rows.
     */
    const [sliderConfigs, setSliderConfigs] = useState<{ min: number; max: number; step: number }[]>([]);

    /**
     * Computes the number of non-empty, usable rows in the list (excluding the last empty gap row).
     * This is used to enforce the maximum row count.
     */
    const usableRows = expressions.filter(
        (expr, idx) => idx !== expressions.length - 1 && expr.trim() !== ''
    ).length;

    /**
     * Ensures the sliderConfigs array has the same length as the expressions array.
     * Adds default config objects as needed or trims if necessary.
     * Runs on every change to expressions.
     */
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

    /**
     * Scrolls the container to the bottom every time the expressions array changes.
     * Ensures the user always sees the most recently added/modified row.
     */
    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    /**
     * Focuses the correct input and restores the caret and selection when focusedIndex/caretPosition/selectionLength changes.
     * This allows for seamless keyboard/mouse navigation and virtual keyboard integration.
     */
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

    /**
     * Adds a new blank row when appropriate:
     * - If the last row is not blank, and
     * - Usable rows are less than the maximum.
     * Does not remove empty rows automatically.
     * Runs on changes to expressions, onExpressionsChange, or usableRows.
     */
    useEffect(() => {
        if (
            (expressions.length === 0 || expressions[expressions.length - 1].trim() !== '') &&
            usableRows < MAX_ROWS
        ) {
            onExpressionsChange(prev => {
                const usable = prev.filter((expr, idx) => idx !== prev.length - 1 && expr.trim() !== '').length;
                if ((prev.length === 0 || prev[prev.length - 1].trim() !== '') && usable < MAX_ROWS) {
                    return [...prev, ''];
                }
                return prev;
            });
        }
        // No longer automatically removing empty rows here.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [expressions, onExpressionsChange, usableRows]);

    /**
     * Handles input changes for any row.
     * Prevents typing in the last gap row if at max rows.
     * Calls the expressions updater with the new value.
     * @param {number} index - Row index being changed.
     * @param {string} value - New value for the row.
     */
    const handleChange = (index: number, value: string) => {
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

    // Render the main container and each row in the expressions list
    return (
        <div className={styles.listContainer} ref={containerRef}>
            {/* Map each expression to a row of UI controls */}
            {expressions.map((expr, idx) => {
                /**
                 * Whether this row is the last row and is blank (the "gap" row for appending).
                 */
                const isLastGap = idx === expressions.length - 1 && expr.trim() === '';

                /**
                 * Type of the expression in this row, undefined for last gap.
                 */
                const tipo = isLastGap ? undefined : expressionTypes[idx];

                /**
                 * Label for the function type, based on expression type.
                 * All labels are single-letter or short English abbreviations.
                 */
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

                /**
                 * Whether the row is disabled.
                 */
                const isDisabled = disabledFlags[idx];

                /**
                 * The color to use for the row, defaulting to the first color if none set or if last gap.
                 */
                const originalColor = isLastGap ? 'var(--expr-first-color)' : colors[idx] || 'var(--expr-first-color)';

                /**
                 * The effective color for the row, using the disabled color if disabled.
                 */
                const functionColor = isDisabled ? 'var(--expr-disabled-color)' : originalColor;

                /**
                 * Subindex for the label, counting same-type rows before this one.
                 */
                const countSameTypeBefore = expressionTypes.slice(0, idx).filter(t => t === tipo).length;
                const subIndex = isLastGap ? '' : String(countSameTypeBefore + 1);

                /**
                 * Extract evaluation/calculation/errors for this row from results.
                 */
                const {evaluation, calculation, errors} = results[idx] || {};

                /**
                 * Lines to display as results below the input.
                 */
                const lines: string[] = [];
                if (evaluation && evaluation !== expr) lines.push(`= ${evaluation}`);
                if (calculation && calculation !== expr && calculation !== evaluation)
                    lines.push(`= ${calculation}`);

                /**
                 * Detect if the row is a numeric assignment, e.g., x = 2.1
                 * Used for showing a slider.
                 */
                const assignMatch = expr.match(/^\s*([a-zA-Z]+)\s*=\s*([-+]?\d+(?:\.\d+)?)\s*$/);
                const isNumericAssign = !!assignMatch;
                const varName = assignMatch?.[1] || '';
                const currentValue = assignMatch ? parseFloat(assignMatch[2]) : 0;
                const {min, max, step} = sliderConfigs[idx] || {min: 0, max: 1, step: 0.1};

                /**
                 * Number of icons/buttons in the row, used for dynamic padding.
                 */
                const iconCount =
                    (errors && errors.length > 0 ? 1 : 0) +
                    (results[idx]?.warnings?.length ? 1 : 0) +
                    (!isDisabled ? 1 : 0) +
                    1 +
                    1;

                /**
                 * Dynamic padding for the input, increased if row is hovered and not focused or last gap.
                 */
                const dynamicPadding = hoveredIndex === idx && focusedIndex !== idx && !isLastGap
                    ? `calc(2.35rem + ${iconCount} * 2.35rem)`
                    : '2.5rem';

                // Prevent rendering the last gap row if max usable rows reached
                if (isLastGap && usableRows >= MAX_ROWS) {
                    return null;
                }

                // Render all controls for this row
                return (
                    <div
                        key={idx}
                        className={`${styles.inputWrapper} ${(hoveredIndex === idx || focusedIndex === idx) && !isDisabled ? styles.rowHighlighted : ''}`}
                        style={{'--highlight-border': functionColor} as React.CSSProperties}
                        onMouseEnter={() => setHoveredIndex(idx)}
                        onMouseLeave={() => setHoveredIndex(prev => (prev === idx ? null : prev))}
                    >
                        <div className={styles.inputRow}>
                            {/* Function type label with enable/disable toggle */}
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

                            {/* Input for editing the expression */}
                            <input
                                type="text"
                                style={{ paddingRight: dynamicPadding }}
                                className={`${styles.exprInput} ${(hoveredIndex === idx && focusedIndex !== idx && !isLastGap) ? styles.hasButtons : ''}`}
                                placeholder={isLastGap ? (usableRows >= MAX_ROWS ? 'Max 10 rows' : 'Write an expression') : ''}
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
                                    // If blur is to a virtual keyboard button, refocus input
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
                                aria-label={`Expression ${idx + 1}`}
                                disabled={isLastGap && usableRows >= MAX_ROWS}
                            />

                            {/* Row action buttons, shown on hover if not focused or last gap */}
                            {hoveredIndex === idx && focusedIndex !== idx && !isLastGap && (
                                <div className={styles.buttonsContainer}>
                                    {/* Error icon if there are errors */}
                                    {errors && errors.length > 0 &&
                                        <span className={styles.errorIcon} title={errors.join('\n')}
                                              aria-label="Errors">❌</span>}
                                    {/* Warning icon if there are warnings */}
                                    {results[idx]?.warnings && results[idx].warnings.length > 0 &&
                                        <span className={styles.warningIcon} title={results[idx].warnings.join('\n')}
                                              aria-label="Warnings">⚠️</span>}
                                    {/* Evaluate icon if row is enabled */}
                                    {!isDisabled && <button type="button" aria-label={`Evaluate row ${idx + 1}`}
                                                            className={styles.iconButton}
                                                            style={{'--button-color': functionColor} as React.CSSProperties}
                                                            onMouseDown={e => {
                                                                e.preventDefault();
                                                                onExpressionBlur(idx, expr);
                                                            }}>⚡</button>}
                                    {/* Color picker button */}
                                    <button type="button" aria-label={`Change row color ${idx + 1}`}
                                            className={styles.iconButton}
                                            style={{'--button-color': functionColor} as React.CSSProperties}
                                            onClick={() => colorInputRefs.current[idx]?.click()}>🎨
                                    </button>
                                    {/* Delete row button */}
                                    <button type="button" aria-label={`Delete row ${idx + 1}`}
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

                            {/* Clear button for focused, non-empty row */}
                            {focusedIndex === idx && expr.trim() !== '' && (
                                <button type="button" aria-label={`Clear row ${idx + 1}`}
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

                            {/* Hidden input for color selection, opened programmatically */}
                            <input type="color" ref={el => {
                                colorInputRefs.current[idx] = el
                            }} value={functionColor} onChange={e => onColorChange(idx, e.target.value)} className={styles.colorInput}/>
                        </div>

                        {/* Slider for rows that are numeric assignments */}
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

                        {/* Render evaluation/calculation result lines if any */}
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
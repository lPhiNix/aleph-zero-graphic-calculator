// src/components/App/ExpressionList.tsx
import { useEffect, useRef, useState } from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (updater: (prev: string[]) => string[]) => void;
    /** Se dispara cuando un <input> pierde el foco: índice + contenido */
    onExpressionBlur: (index: number, expr: string) => void;

    /** Arreglo de colores hex ("#rrggbb") por cada expresión */
    colors: string[];
    /** Callback que notifica al padre que la fila `index` cambió a `newColor` */
    onColorChange: (index: number, newColor: string) => void;

    /** Arreglo de flags indicando qué fila está deshabilitada */
    disabledFlags: boolean[];
    /** Callback para alternar (enable/disable) una fila dado su índice */
    onToggleDisabled: (index: number) => void;

    /** Arreglo de tipos de expresión devueltos por el backend (puede ser undefined) */
    expressionTypes: Array<string | undefined>;
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
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);
    const colorInputRefs = useRef<Array<HTMLInputElement | null>>([]);

    // Para saber qué fila está enfocada y cuál está “hovered”
    const [focusedIndex, setFocusedIndex] = useState<number | null>(null);
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    const handleChange = (index: number, value: string) => {
        onExpressionsChange((prev) => {
            const updated = [...prev];
            updated[index] = value;

            // Si estamos en la última fila y escribimos algo, añadimos una nueva vacía
            if (index === prev.length - 1 && value.trim() !== '') {
                updated.push('');
            }
            return updated;
        });
    };

    const handleBlur = (index: number) => {
        const value = expressions[index] ?? '';
        onExpressionBlur(index, value);

        // Si estamos en la última fila y quedó con texto, añadimos otra vacía
        const isLast = index === expressions.length - 1;
        if (!isLast) return;

        onExpressionsChange((prev) => {
            if (value.trim() !== '' && prev[prev.length - 1].trim() !== '') {
                return [...prev, ''];
            }
            return prev;
        });
    };

    const handleClearRow = (index: number) => {
        onExpressionsChange((prev) => {
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
        onExpressionsChange((prev) => {
            // Si sólo queda una fila, mantenemos al menos una vacía
            if (prev.length <= 1) return [''];
            const updated = [...prev];
            updated.splice(index, 1);
            return updated;
        });
    };

    // Antes de renderizar, reseteamos las referencias para sincronizar índices
    inputRefs.current = [];
    colorInputRefs.current = [];

    return (
        <div className={styles.listContainer} ref={containerRef}>
            {expressions.map((expr, idx) => {
                // Detectar si es la última fila vacía (placeholder)
                const isLastGap = idx === expressions.length - 1 && expr.trim() === '';

                // Determinar el tipo de etiqueta según `expressionTypes[idx]`
                const tipo = expressionTypes[idx];
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
                    default:
                        etiqueta = 'Ex';
                }

                // Color original de la función (puede venir de padre)
                const originalColor = isLastGap ? '#666666' : colors[idx] || '#666666';
                // Si la fila está deshabilitada, forzamos gris
                const functionColor = disabledFlags[idx] ? '#666666' : originalColor;

                // Contar cuántas veces apareció este mismo tipo antes de idx
                const countSameTypeBefore = expressionTypes
                    .slice(0, idx)
                    .filter((t) => t === tipo)
                    .length;
                // El número que ponemos como subíndice es countSameTypeBefore + 1,
                // a menos que sea la última fila vacía (no mostramos subíndice).
                const subIndex = isLastGap ? '' : String(countSameTypeBefore + 1);

                return (
                    <div
                        key={idx}
                        className={`${styles.inputWrapper} ${
                            (hoveredIndex === idx || focusedIndex === idx) &&
                            !disabledFlags[idx]
                                ? styles.rowHighlighted
                                : ''
                        }`}
                        style={{
                            '--highlight-border': functionColor,
                        } as React.CSSProperties}
                        onMouseEnter={() => setHoveredIndex(idx)}
                        onMouseLeave={() =>
                            setHoveredIndex((prev) => (prev === idx ? null : prev))
                        }
                    >
                        {/* ─── CUADRADO “tipoᵢ” A LA IZQUIERDA ─── */}
                        <div
                            className={styles.functionLabel}
                            style={{
                                backgroundColor: functionColor,
                                borderColor:
                                    (hoveredIndex === idx || focusedIndex === idx) &&
                                    !disabledFlags[idx]
                                        ? functionColor
                                        : 'transparent',
                                color: functionColor, // para que currentColor coincida
                                cursor: isLastGap ? 'default' : 'pointer', // última fila no es clicable
                            }}
                            onClick={() => {
                                if (!isLastGap) {
                                    onToggleDisabled(idx);
                                }
                            }}
                        >
              <span className={styles.fLetter}>
                <em>{etiqueta}</em>
              </span>
                            {!isLastGap && (
                                <sub className={styles.fSubscript}>{subIndex}</sub>
                            )}
                        </div>

                        {/* ─── INPUT DE TEXTO ─── */}
                        <input
                            type="text"
                            className={styles.exprInput}
                            placeholder={isLastGap ? 'Escribir una función' : ''}
                            value={expr}
                            ref={(el) => {
                                inputRefs.current[idx] = el;
                            }}
                            onChange={(e) => handleChange(idx, e.target.value)}
                            onFocus={() => setFocusedIndex(idx)}
                            onBlur={() => {
                                handleBlur(idx);
                                setFocusedIndex(null);
                            }}
                            aria-label={`Expresión ${idx + 1}`}
                        />

                        {/* ─── BOTÓN “×” (clear) sólo si está enfocado y no vacío ─── */}
                        {focusedIndex === idx && expr.trim() !== '' && (
                            <button
                                type="button"
                                aria-label={`Limpiar fila ${idx + 1}`}
                                className={styles.clearButton}
                                onMouseDown={(e) => {
                                    // Evitamos que el blur suceda antes de limpiar
                                    e.preventDefault();
                                    handleClearRow(idx);
                                }}
                            >
                                <span aria-hidden="true">×</span>
                            </button>
                        )}

                        {/*
              ─── LOS TRES ICON-BOTONES (⚡ 🎨 🗑️)
                  Solo si:
                    1) el ratón está “hover” sobre la fila
                    2) el input NO está enfocado
                    3) NO es la última fila vacía
              Dentro del contenedor, ocultamos ⚡ si está deshabilitada la fila,
              pero mostramos 🎨 y 🗑️ siempre.
            */}
                        {hoveredIndex === idx && focusedIndex !== idx && !isLastGap && (
                            <div className={styles.buttonsContainer}>
                                {/* 1) ⚡ Evaluar ahora (no se muestra si está deshabilitada) */}
                                {!disabledFlags[idx] && (
                                    <button
                                        type="button"
                                        aria-label={`Evaluar fila ${idx + 1}`}
                                        className={styles.iconButton}
                                        style={{
                                            '--button-color': functionColor,
                                        } as React.CSSProperties}
                                        onMouseDown={(e) => {
                                            // Evitamos que el input pierda focus antes de evaluar
                                            e.preventDefault();
                                            onExpressionBlur(idx, expr);
                                        }}
                                    >
                                        <span className={styles.iconLightning}>⚡</span>
                                    </button>
                                )}

                                {/* 2) 🎨 Cambiar color (siempre se muestra) */}
                                <button
                                    type="button"
                                    aria-label={`Cambiar color fila ${idx + 1}`}
                                    className={styles.iconButton}
                                    style={{
                                        '--button-color': functionColor,
                                    } as React.CSSProperties}
                                    onClick={() => {
                                        const colorInput = colorInputRefs.current[idx];
                                        if (colorInput) colorInput.click();
                                    }}
                                >
                                    <span className={styles.iconPalette}>🎨</span>
                                </button>

                                {/* 3) 🗑️ Borrar fila (siempre se muestra) */}
                                <button
                                    type="button"
                                    aria-label={`Borrar fila ${idx + 1}`}
                                    className={styles.iconButton}
                                    style={{
                                        '--button-color': functionColor,
                                    } as React.CSSProperties}
                                    onClick={(e) => {
                                        e.preventDefault();
                                        handleDeleteRow(idx);
                                    }}
                                >
                                    <span className={styles.iconTrash}>🗑️</span>
                                </button>
                            </div>
                        )}

                        {/* ─── INPUT[type="color"] OCULTO PARA CADA FILA ─── */}
                        <input
                            type="color"
                            ref={(el) => {
                                colorInputRefs.current[idx] = el;
                            }}
                            value={functionColor}
                            onChange={(e) => {
                                onColorChange(idx, e.target.value);
                            }}
                            style={{
                                position: 'fixed',
                                width: '1px',
                                height: '1px',
                                opacity: 0,
                                pointerEvents: 'none',
                            }}
                        />
                    </div>
                );
            })}
        </div>
    );
}
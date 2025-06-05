import { useEffect, useRef } from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (updater: (prev: string[]) => string[]) => void;
    /** Se dispara cuando un <input> pierde el foco: index + contenido */
    onExpressionBlur: (index: number, expr: string) => void;
}

export default function ExpressionList({
                                           expressions,
                                           onExpressionsChange,
                                           onExpressionBlur,
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);

    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    const handleChange = (index: number, value: string) => {
        onExpressionsChange((prev) => {
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
        const isLast = index === expressions.length - 1;

        // Llamamos siempre al callback padre
        onExpressionBlur(index, value);

        if (!isLast) return;

        onExpressionsChange((prev) => {
            if (value.trim() !== '') {
                if (prev[prev.length - 1].trim() !== '') {
                    return [...prev, ''];
                }
                return prev;
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
            if (prev.length <= 1) return [''];
            const updated = [...prev];
            updated.splice(index, 1);
            return updated;
        });
    };

    return (
        <div className={styles.listContainer} ref={containerRef}>
            {expressions.map((expr, idx) => (
                <div key={idx} className={styles.inputWrapper}>
                    <input
                        type="text"
                        className={styles.exprInput}
                        placeholder={idx === expressions.length - 1 ? 'Escribir una expresión' : ''}
                        value={expr}
                        ref={(el) => {
                            inputRefs.current[idx] = el;
                        }}
                        onChange={(e) => handleChange(idx, e.target.value)}
                        onBlur={() => handleBlur(idx)}
                        onFocus={() => {
                            /* Con CSS:focus-within se mostrará el botón de limpiar */
                        }}
                    />
                    {expr.trim() !== '' && (
                        <button
                            type="button"
                            className={styles.deleteButton}
                            onClick={() => handleClearRow(idx)}
                        >
                            <span className={styles.deleteIcon}>×</span>
                        </button>
                    )}
                    {idx !== expressions.length - 1 && (
                        <button
                            type="button"
                            className={styles.deleteRowButton}
                            onClick={() => handleDeleteRow(idx)}
                        >
                            <span className={styles.trashIcon}>🗑️</span>
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
}

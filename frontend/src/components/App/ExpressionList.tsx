import {useEffect, useRef} from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (updater: (prev: string[]) => string[]) => void;
}

export default function ExpressionList({
                                           expressions,
                                           onExpressionsChange,
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);

    // Cuando expressions cambia, hago scroll al final:
    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    /**
     * Se dispara cada vez que cambias el contenido de un input.
     * Actualiza la fila [index] con el nuevo value. Además, si era
     * la última fila y value NO está vacío, le agrega una ficha vacía.
     */
    const handleChange = (index: number, value: string) => {
        onExpressionsChange((prev) => {
            const updated = [...prev];
            updated[index] = value;

            // Si este era el último renglón Y ahora tiene algo de texto, agrego otro vacío
            if (index === prev.length - 1 && value.trim() !== '') {
                updated.push('');
            }

            return updated;
        });
    };

    /**
     * Se dispara cuando un input pierde el foco (onBlur).
     * - Si era la última fila y NO está vacía ➔ agrego una nueva fila vacía.
     * - Si era la última fila y está VACÍA ➔ la elimino (siempre que quede al menos UNA fila).
     */
    const handleBlur = (index: number) => {
        const value = expressions[index] ?? '';
        const isLast = index === expressions.length - 1;

        if (!isLast) return;

        onExpressionsChange((prev) => {
            // Caso 1: última fila NO vacía ➔ agrego otra fila vacía
            if (value.trim() !== '') {
                // Pero aseguro que no duplique si ya existe una fila vacía al final
                if (prev[prev.length - 1].trim() !== '') {
                    return [...prev, ''];
                }
                return prev;
            }

            // Caso: si solo hay 1 fila en blanco, la dejo
            return prev;
        });
    };

    /**
     * Borra el contenido del renglón [index] (lo deja como cadena vacía).
     * No elimina la fila: solo limpia su texto.
     */
    const handleClearRow = (index: number) => {
        onExpressionsChange((prev) => {
            const updated = [...prev];
            updated[index] = '';
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
                        placeholder={
                            idx === expressions.length - 1 ? 'Escribir una expresión' : ''
                        }
                        value={expr}
                        onChange={(e) => handleChange(idx, e.target.value)}
                        onBlur={() => handleBlur(idx)}
                        onFocus={() => {
                            /* Con CSS :focus-within se mostrará el botón */
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
                </div>
            ))}
        </div>
    );
}

// src/components/Graph/ExpressionList.tsx
import { useEffect, useRef } from 'react';
import styles from '../../styles/modules/expressionList.module.css';

interface ExpressionListProps {
    expressions: string[];
    onExpressionsChange: (newExpressions: (prev: any) => any[]) => void;
}

export default function ExpressionList({
                                           expressions,
                                           onExpressionsChange
                                       }: ExpressionListProps) {
    const containerRef = useRef<HTMLDivElement>(null);

    // Cuando expressions cambia, hago scroll al final:
    useEffect(() => {
        const el = containerRef.current;
        if (el) el.scrollTop = el.scrollHeight;
    }, [expressions]);

    const handleChange = (index: number, value: string) => {
        onExpressionsChange(prev => {
            const updated = [...prev];
            updated[index] = value;
            // Si era el último renglón y escribo algo, agrego uno nuevo
            if (index === prev.length - 1 && value.trim() !== '') {
                updated.push('');
            }
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
                        placeholder={idx === 0 ? 'f(x) =' : ''}
                        value={expr}
                        onChange={(e) => handleChange(idx, e.target.value)}
                    />
                </div>
            ))}
        </div>
    );
}

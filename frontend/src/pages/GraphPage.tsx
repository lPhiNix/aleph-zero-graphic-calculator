// src/pages/GraphPage.tsx
import { useState } from 'react';
import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas.tsx';
import styles from '../styles/modules/graphCanvas.module.css';

export default function GraphPage() {
    // Estado que contendrá todas las expresiones que ingresó el usuario.
    const [expressions, setExpressions] = useState<string[]>(['']);

    return (
        <div className={styles.pageContainer}>
            <Header title="Placeholder" />
            <div className={styles.mainArea}>
                <div className={styles.canvasWrapper}>
                    <GraphCanvas expressions={expressions} />
                </div>
                <div className={styles.expressionsWrapper}>
                    {/* Le paso el estado y el setter a ExpressionList */}
                    <ExpressionList
                        expressions={expressions}
                        onExpressionsChange={setExpressions}
                    />
                </div>
            </div>
        </div>
    );
}

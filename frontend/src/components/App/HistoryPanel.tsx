import React, { useEffect, useState } from "react";
import styles from "../../styles/modules/historyPanel.module.css";
import AxiosConfig from "../../services/axiosService.ts";

interface SimpleUserHistoryDto {
    id: number;
    createdAt: string;
    updatedAt: string;
    snapshot: string;
    description: string;
}

interface MathExpressionPreferences {
    color: string;
    xprType: string;
}

interface MathExpressionCreationDto {
    expression: string;
    orderIndex: number;
    points: string;
    evaluation: string;
    calculation: string;
    preferences: MathExpressionPreferences;
}

interface UserHistoryCreationDto {
    snapshot: string;
    mathExpressions: MathExpressionCreationDto[];
}

interface HistoryPanelProps {
    expressions: string[];
    results: any[];
    colors: string[];
    types: string[];
    graphCanvasRef: React.RefObject<HTMLCanvasElement | null>;
    refreshExpressions: (exprs: string[], colors: string[], types: string[]) => void;
}

export default function HistoryPanel({
                                         expressions,
                                         results,
                                         colors,
                                         types,
                                         graphCanvasRef,
                                         refreshExpressions,
                                     }: HistoryPanelProps) {
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [history, setHistory] = useState<SimpleUserHistoryDto[]>([]);

    useEffect(() => {
        if (!open) return;
        setLoading(true);
        AxiosConfig.getInstance()
            .get("/api/v1/math/history/summary")
            .then((res) => setHistory(res.data.content))
            .finally(() => setLoading(false));
    }, [open]);

    const handleSave = async () => {
        if (!graphCanvasRef.current) return;
        const rawSnapshot = graphCanvasRef.current.toDataURL("image/jpeg");
        const snapshot = rawSnapshot.replace(/^data:image\/jpeg;base64,/, "");

        const mathExpressions = expressions
            .map((expr, idx) => ({
                expression: expr,
                orderIndex: idx,
                points: results[idx]?.drawingPoints ? JSON.stringify(results[idx].drawingPoints) : "",
                evaluation: results[idx]?.evaluation || "",
                calculation: results[idx]?.calculation || "",
                preferences: {
                    color: colors[idx] || "#ebcecb",
                    xprType: types[idx] || "UNKNOWN",
                },
            }))
            .filter((e) => e.expression.trim() !== "");
        const payload: UserHistoryCreationDto = { snapshot, mathExpressions };
        setLoading(true);
        try {
            await AxiosConfig.getInstance().post("/api/v1/math/history", payload);
            const res = await AxiosConfig.getInstance().get("/api/v1/math/history/summary");
            setHistory(res.data.content);
        } catch (e) {}
        setLoading(false);
    };

    const handleDelete = async (id: number) => {
        setLoading(true);
        try {
            await AxiosConfig.getInstance().delete(`/api/v1/math/history/${id}`);
            setHistory((h) => h.filter((r) => r.id !== id));
        } catch (e) {}
        setLoading(false);
    };

    const handleLoad = async (id: number) => {
        setLoading(true);
        try {
            const res = await AxiosConfig.getInstance().get(`/api/v1/math/history/${id}`);
            // ADAPTACIÓN: usa 'expressions' del backend
            const exprs = Array.from(res.data.content.expressions)
                .sort((a: any, b: any) => a.indexOrder - b.indexOrder);
            const exp = exprs.map((e: any) => e.mathExpression.expression);
            const cols = exprs.map((e: any) => e.mathExpression.preferences.color);
            const tys = exprs.map((e: any) => e.mathExpression.preferences.xprType);
            refreshExpressions(exp, cols, tys);
            setOpen(false);
        } catch (e) {}
        setLoading(false);
    };

    return (
        <div className={styles.historyPanelWrapper}>
            <button
                className={styles.historyTab}
                onClick={() => setOpen((v) => !v)}
                aria-label="Mostrar historial"
            >
                🕑
            </button>
            {open && (
                <div className={styles.historyPanel}>
                    <div className={styles.historyPanelHeader}>
                        <span>Historial</span>
                        <button className={styles.saveBtn} onClick={handleSave} disabled={loading}>
                            + Guardar lista actual
                        </button>
                    </div>
                    <div className={styles.historyList}>
                        {loading && <div style={{ textAlign: "center", color: "#888" }}>Cargando...</div>}
                        {!loading &&
                            history.map((h) => (
                                <div key={h.id} className={styles.historyRow}>
                                    <img
                                        src={h.snapshot}
                                        alt="snapshot"
                                        className={styles.snapshotThumb}
                                        onClick={() => handleLoad(h.id)}
                                    />
                                    <div className={styles.historyMeta}>
                                        <span>
                                            {new Date(h.createdAt).toLocaleString()}
                                        </span>
                                        <button
                                            className={styles.deleteBtn}
                                            title="Eliminar"
                                            onClick={() => handleDelete(h.id)}
                                            disabled={loading}
                                        >
                                            🗑️
                                        </button>
                                    </div>
                                </div>
                            ))}
                    </div>
                </div>
            )}
        </div>
    );
}
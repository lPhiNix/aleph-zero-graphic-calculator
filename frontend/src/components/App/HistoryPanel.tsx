import React, { useEffect, useRef, useState } from "react";
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
    const panelRef = useRef<HTMLDivElement>(null);
    const [panelHeight, setPanelHeight] = useState<number>(0);
    const [panelTop, setPanelTop] = useState<number>(0);

    // Ajusta el tamaño y posición según el canvas.
    useEffect(() => {
        const resize = () => {
            if (graphCanvasRef.current && panelRef.current) {
                const canvasRect = graphCanvasRef.current.getBoundingClientRect();
                setPanelHeight(canvasRect.height);
                setPanelTop(canvasRect.top);
            }
        };
        resize();
        window.addEventListener("resize", resize);
        return () => window.removeEventListener("resize", resize);
    }, [graphCanvasRef, open]);

    useEffect(() => {
        if (!open) return;
        setLoading(true);
        AxiosConfig.getInstance()
            .get("/api/v1/math/history/summary")
            .then((res) => setHistory(res.data.content))
            .finally(() => setLoading(false));
    }, [open]);

    // Nueva función para saber si hay expresiones no vacías
    const hasNonEmptyExpressions = expressions.some(expr => expr.trim() !== "");

    const handleSave = async () => {
        if (!graphCanvasRef.current) return;

        // Si no hay expresiones válidas, no guardar (seguridad extra)
        if (!hasNonEmptyExpressions) return;

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
        const payload: UserHistoryCreationDto = {snapshot, mathExpressions};
        setLoading(true);
        try {
            await AxiosConfig.getInstance().post("/api/v1/math/history", payload);
            const res = await AxiosConfig.getInstance().get("/api/v1/math/history/summary");
            setHistory(res.data.content);
        } catch (e) {
        }
        setLoading(false);
    };

    const handleDelete = async (id: number) => {
        setLoading(true);
        try {
            await AxiosConfig.getInstance().delete(`/api/v1/math/history/${id}`);
            setHistory((h) => h.filter((r) => r.id !== id));
        } catch (e) {
        }
        setLoading(false);
    };

    const handleLoad = async (id: number) => {
        setLoading(true);
        try {
            const res = await AxiosConfig.getInstance().get(`/api/v1/math/history/${id}`);
            const exprs = Array.from(res.data.content.expressions)
                .sort((a: any, b: any) => a.indexOrder - b.indexOrder);
            const exp = exprs.map((e: any) => e.mathExpression.expression);
            const cols = exprs.map((e: any) => e.mathExpression.preferences.color);
            const tys = exprs.map((e: any) => e.mathExpression.preferences.xprType);
            refreshExpressions(exp, cols, tys);
            setOpen(false);
        } catch (e) {
        }
        setLoading(false);
    };

    // Animación y visibilidad
    const panelClass = [
        styles.historyPanel,
        open ? styles.historyPanelOpen : styles.historyPanelClosed,
    ].join(" ");

    return (
        <div
            className={styles.historyPanelWrapper}
            style={{
                pointerEvents: "none",
                height: panelHeight ? `${panelHeight}px` : undefined,
                top: panelTop ? `${panelTop}px` : undefined,
            }}
        >
            <div
                ref={panelRef}
                className={panelClass}
                style={{
                    pointerEvents: open ? "auto" : "none",
                    height: panelHeight ? `${panelHeight}px` : undefined,
                    top: 0,
                    left: 0,
                }}
            >
                <div className={styles.historyContent}>
                    <div className={styles.historyPanelHeader}>
                    <span className={styles.historyTitle}>
                        🕓 Historial
                    </span>
                        <button
                            className={styles.actionBtn}
                            onClick={handleSave}
                            disabled={loading || !hasNonEmptyExpressions}
                        >
                            💾 Guardar
                        </button>
                    </div>
                    <div className={styles.historyList}>
                        {loading && (
                            <div style={{textAlign: "center", color: "#888"}}>
                                Cargando...
                            </div>
                        )}
                        {!loading &&
                            history.map((h) => (
                                <div key={h.id} className={styles.historyRow}>
                                    <img
                                        src={h.snapshot}
                                        alt="snapshot"
                                        className={styles.snapshotThumb}
                                        draggable={false}
                                    />
                                    <div className={styles.historyMeta}>
                                        {h.description && (
                                            <div className={styles.description}>{h.description}</div>
                                        )}
                                        <div className={styles.updatedAt}>
                                            Modificada: {new Date(h.updatedAt).toLocaleString()}
                                        </div>
                                        <div className={styles.buttonRow}>
                                            <button
                                                className={`${styles.actionBtn} ${styles.loadBtn}`}
                                                onClick={() => handleLoad(h.id)}
                                                disabled={loading}
                                            >
                                                🗂️ Cargar
                                            </button>
                                            <button
                                                className={`${styles.actionBtn} ${styles.deleteBtn}`}
                                                onClick={() => handleDelete(h.id)}
                                                disabled={loading}
                                            >
                                                🗑️ Eliminar
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                    </div>
                </div>
                <button
                    className={styles.historyTab}
                    onClick={() => setOpen((v) => !v)}
                    aria-label={open ? "Cerrar historial" : "Mostrar historial"}
                    tabIndex={0}
                >
                    <div className={styles.tabLines}>
                        <div/>
                        <div/>
                    </div>
                </button>
            </div>
        </div>
    );
}
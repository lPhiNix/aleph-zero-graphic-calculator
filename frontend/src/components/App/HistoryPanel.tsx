import React, { useEffect, useRef, useState } from "react"; // Import React and hooks for state, refs, and effects
import styles from "../../styles/modules/historyPanel.module.css"; // Import CSS module for styling the panel
import AxiosConfig from "../../services/axiosService.ts"; // Import Axios singleton for API requests

/**
 * Data Transfer Object for a single history entry, simplified for listing.
 */
interface SimpleUserHistoryDto {
    id: number; // Unique identifier for the history entry
    createdAt: string; // Creation timestamp
    updatedAt: string; // Last update timestamp
    snapshot: string; // Snapshot image as a data URL (base64)
    description: string; // Optional textual description
}

/**
 * Preferences for a math expression, like color and type.
 */
interface MathExpressionPreferences {
    color: string; // Color string (e.g. hex code)
    xprType: string; // Expression type (e.g. "FUNCTION", "ASSIGNMENT")
}

/**
 * Data Transfer Object for creating a math expression in history.
 */
interface MathExpressionCreationDto {
    expression: string; // The expression string
    orderIndex: number; // The order of the expression in the list
    points: string; // Serialized drawing points as string
    evaluation: string; // Evaluated result
    calculation: string; // Calculation details
    preferences: MathExpressionPreferences; // Color and type
}

/**
 * Data Transfer Object for creating a new user history record.
 */
interface UserHistoryCreationDto {
    snapshot: string; // Snapshot image as base64 (no data URL header)
    mathExpressions: MathExpressionCreationDto[]; // Array of expressions in this snapshot
}

/**
 * Props for the HistoryPanel component.
 * @property {string[]} expressions - Current expressions in the editor
 * @property {any[]} results - Results for each expression (evaluation, calculation, etc)
 * @property {string[]} colors - Array of colors for each expression
 * @property {string[]} types - Array of types for each expression
 * @property {React.RefObject<HTMLCanvasElement | null>} graphCanvasRef - Ref to the graph canvas
 * @property {(exprs: string[], colors: string[], types: string[]) => void} refreshExpressions - Callback to update the current expressions/colors/types in the main app
 */
interface HistoryPanelProps {
    expressions: string[];
    results: any[];
    colors: string[];
    types: string[];
    graphCanvasRef: React.RefObject<HTMLCanvasElement | null>;
    refreshExpressions: (exprs: string[], colors: string[], types: string[]) => void;
}

/**
 * HistoryPanel component.
 * Shows a side panel with saved history entries (snapshots of expressions and calculations).
 * Allows saving, deleting, and loading entries.
 * @param {HistoryPanelProps} props - Panel props for controlling expressions, results, colors, etc.
 * @returns {JSX.Element} The rendered panel.
 */
export default function HistoryPanel({
                                         expressions,
                                         results,
                                         colors,
                                         types,
                                         graphCanvasRef,
                                         refreshExpressions,
                                     }: HistoryPanelProps) {
    /**
     * State for open/closed state of history panel.
     */
    const [open, setOpen] = useState(false);

    /**
     * State for loading spinner (while saving, deleting, or loading).
     */
    const [loading, setLoading] = useState(false);

    /**
     * State for list of saved history records fetched from backend.
     */
    const [history, setHistory] = useState<SimpleUserHistoryDto[]>([]);

    /**
     * Ref for the panel div (used for resizing and animation).
     */
    const panelRef = useRef<HTMLDivElement>(null);

    /**
     * State for dynamically calculated height of the panel (matches canvas).
     */
    const [panelHeight, setPanelHeight] = useState<number>(0);

    /**
     * State for dynamically calculated top offset of the panel (matches canvas).
     */
    const [panelTop, setPanelTop] = useState<number>(0);

    /**
     * Adjusts the panel's size and position to match the graph canvas.
     * Runs on mount and whenever the canvas ref or open state changes.
     */
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

    /**
     * Loads the user's history entries whenever the panel is opened.
     * Sets loading spinner and fetches the summary from backend API.
     */
    useEffect(() => {
        if (!open) return;
        setLoading(true);
        AxiosConfig.getInstance()
            .get("/api/v1/math/history/summary")
            .then((res) => setHistory(res.data.content))
            .finally(() => setLoading(false));
    }, [open]);

    /**
     * Checks if there is at least one non-empty expression in the list.
     * Used to enable/disable the save button.
     */
    const hasNonEmptyExpressions = expressions.some(expr => expr.trim() !== "");

    /**
     * Handles saving the current state as a new history entry.
     * Takes a snapshot of the canvas, serializes expressions, and posts to backend.
     * Updates the history list after saving.
     */
    const handleSave = async () => {
        if (!graphCanvasRef.current) return;
        // Extra guard: do not save if all expressions are empty.
        if (!hasNonEmptyExpressions) return;

        // Get canvas as base64 string (remove the data URL header)
        const rawSnapshot = graphCanvasRef.current.toDataURL("image/jpeg");
        const snapshot = rawSnapshot.replace(/^data:image\/jpeg;base64,/, "");

        // Serialize all non-empty expressions with their info
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
            // Optionally handle error here (e.g. show notification)
        }
        setLoading(false);
    };

    /**
     * Handles deleting a history entry by ID.
     * Removes from backend and updates local state.
     * @param {number} id - The ID of the entry to delete.
     */
    const handleDelete = async (id: number) => {
        setLoading(true);
        try {
            await AxiosConfig.getInstance().delete(`/api/v1/math/history/${id}`);
            setHistory((h) => h.filter((r) => r.id !== id));
        } catch (e) {
            // Optionally handle error here
        }
        setLoading(false);
    };

    /**
     * Handles loading a history entry by ID.
     * Fetches the expressions/colors/types and loads them into the main app.
     * @param {number} id - The ID of the entry to load.
     */
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
            // Optionally handle error here
        }
        setLoading(false);
    };

    /**
     * CSS class for the panel, based on open/closed state, to trigger animations.
     */
    const panelClass = [
        styles.historyPanel,
        open ? styles.historyPanelOpen : styles.historyPanelClosed,
    ].join(" ");

    // Render the panel and its controls
    return (
        <div
            className={styles.historyPanelWrapper}
            style={{
                pointerEvents: "none", // Prevents interaction unless panel is open
                height: panelHeight ? `${panelHeight}px` : undefined, // Dynamic height
                top: panelTop ? `${panelTop}px` : undefined, // Dynamic top position
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
                    {/* Header row with title and save button */}
                    <div className={styles.historyPanelHeader}>
                        <span className={styles.historyTitle}>
                            🕓 History
                        </span>
                        <button
                            className={styles.actionBtn}
                            onClick={handleSave}
                            disabled={loading || !hasNonEmptyExpressions}
                        >
                            💾 Save
                        </button>
                    </div>
                    {/* List of history entries and loading spinner */}
                    <div className={styles.historyList}>
                        {loading && (
                            <div style={{textAlign: "center", color: "#888"}}>
                                Loading...
                            </div>
                        )}
                        {!loading &&
                            history.map((h) => (
                                <div key={h.id} className={styles.historyRow}>
                                    {/* Thumbnail of the snapshot */}
                                    <img
                                        src={h.snapshot}
                                        alt="snapshot"
                                        className={styles.snapshotThumb}
                                        draggable={false}
                                    />
                                    <div className={styles.historyMeta}>
                                        {/* Optional description */}
                                        {h.description && (
                                            <div className={styles.description}>{h.description}</div>
                                        )}
                                        {/* Last updated date */}
                                        <div className={styles.updatedAt}>
                                            Modified: {new Date(h.updatedAt).toLocaleString()}
                                        </div>
                                        {/* Action buttons for load and delete */}
                                        <div className={styles.buttonRow}>
                                            <button
                                                className={`${styles.actionBtn} ${styles.loadBtn}`}
                                                onClick={() => handleLoad(h.id)}
                                                disabled={loading}
                                            >
                                                🗂️ Load
                                            </button>
                                            <button
                                                className={`${styles.actionBtn} ${styles.deleteBtn}`}
                                                onClick={() => handleDelete(h.id)}
                                                disabled={loading}
                                            >
                                                🗑️ Delete
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                    </div>
                </div>
                {/* Tab button to open/close the panel */}
                <button
                    className={styles.historyTab}
                    onClick={() => setOpen((v) => !v)}
                    aria-label={open ? "Close history" : "Show history"}
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
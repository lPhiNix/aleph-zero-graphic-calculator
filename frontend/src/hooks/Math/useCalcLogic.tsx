import {useRef, useCallback, useEffect, useState} from 'react';
import { useExpressionsState } from './useExpressionState.tsx'; // Custom hook to manage expressions
import { useCaretSelection } from './useCaretSelection.tsx';   // Custom hook to manage caret/selection for inputs
import {
    evaluateSingleExpression,
    evaluateBatchExpressions,
} from '../../services/mathService.ts'; // Math evaluation service

/**
 * Interface for the visible window in world coordinates for the graph.
 */
interface ViewWindow {
    origin: number;
    bound: number;
    bottom: number;
    top: number;
}

/**
 * Represents a cached interval of computed points for efficient re-use.
 */
type IntervalData = {
    from: number; // Start of interval
    to: number;   // End of interval
    points: Array<{ x: number; y: number }>; // Computed points in [from, to]
};

/**
 * Options for inserting text into an expression.
 */
type InsertOptions = {
    deltaCaret?: number;    // Caret movement after insert
    selectLength?: number;  // Length of selection after insert
};

/**
 * Main hook for calculator logic.
 * Manages expressions, results, drawing, caret/selection, and graph view window.
 * Handles evaluation and caching.
 * @returns {object} All state and logic for calculator panel.
 */
export function useCalculatorLogic() {
    // --- Expressions and Results State ---
    const {
        expressions,
        setExpressions,
        results,
        setResults,
        colors,
        setColors,
        disabledFlags,
        setDisabledFlags,
        cacheRef,
        deleteRow,
    } = useExpressionsState();

    // --- Caret and Selection for Text Input ---
    const {
        focusedIndex,
        setFocusedIndex,
        caretPosition,
        setCaretPosition,
        selectionLength,
        setSelectionLength
    } = useCaretSelection();

    // --- Graph View Window State ---
    const [viewWindow, setViewWindow] = useState<ViewWindow>({
        origin: -10,
        bound: 10,
        bottom: -10,
        top: 10,
    });

    // Used to skip effect on initial render
    const isFirstRender = useRef(true);

    /**
     * Given a range [from, to], returns subintervals missing from the existing cache.
     * Used to minimize API calls for points.
     */
    const getMissingIntervals = useCallback(
        (from: number, to: number, existing: IntervalData[]) => {
            if (from >= to) return [];
            let intervals: Array<[number, number]> = [[from, to]];
            existing
                .sort((a, b) => a.from - b.from)
                .forEach(({ from: f, to: t }) => {
                    const next: Array<[number, number]> = [];
                    intervals.forEach(([a, b]) => {
                        if (t <= a || f >= b) next.push([a, b]);
                        else {
                            if (a < f) next.push([a, f]);
                            if (b > t) next.push([t, b]);
                        }
                    });
                    intervals = next;
                });
            return intervals;
        },
        []
    );

    /**
     * Effect: Watch view window or expressions, and update points by evaluating missing intervals.
     * - For each expression, if not disabled/empty, fetch missing point intervals.
     * - Update results and cache.
     */
    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        let mounted = true;
        const { origin, bound } = viewWindow;
        const dec = '50'; // Number of points per interval

        expressions.forEach((expr, idx) => {
            if (disabledFlags[idx] || expr.trim() === '') {
                if (!mounted) return;
                cacheRef.current[idx] = [];
                setResults(prev => {
                    const copy = [...prev];
                    copy[idx] = { exprType: prev[idx]?.exprType };
                    return copy;
                });
                return;
            }
            const existing = cacheRef.current[idx] || [];
            const missing = getMissingIntervals(origin, bound, existing);
            if (missing.length === 0) {
                // All points already cached for this window
                const pts: Array<{ x: number; y: number }> = [];
                existing
                    .filter(iv => iv.to > origin && iv.from < bound)
                    .forEach(iv =>
                        iv.points.forEach(pt => {
                            if (pt.x >= origin && pt.x <= bound) pts.push(pt);
                        })
                    );
                pts.sort((a, b) => a.x - b.x);
                setResults(prev => {
                    if (!mounted) return prev;
                    const u = prev.map(r => ({ ...r }));
                    u[idx] = { ...u[idx], drawingPoints: pts, exprType: u[idx]?.exprType };
                    return u;
                });
                return;
            }
            // Fetch missing intervals asynchronously
            (async () => {
                for (const [f, t] of missing) {
                    try {
                        const res = await evaluateSingleExpression(
                            expr,
                            dec,
                            f.toString(),
                            t.toString()
                        );
                        if (!mounted) return;
                        cacheRef.current[idx].push({ from: f, to: t, points: res.drawingPoints || [] });
                        const merged: Array<{ x: number; y: number }> = [];
                        cacheRef.current[idx]
                            .filter(iv => iv.to > origin && iv.from < bound)
                            .forEach(iv =>
                                iv.points.forEach(pt => {
                                    if (pt.x >= origin && pt.x <= bound) merged.push(pt);
                                })
                            );
                        merged.sort((a, b) => a.x - b.x);
                        setResults(prev => {
                            if (!mounted) return prev;
                            const u = prev.map(r => ({ ...r }));
                            u[idx] = { ...u[idx], drawingPoints: merged, exprType: u[idx]?.exprType };
                            return u;
                        });
                    } catch {
                        if (!mounted) return;
                        setResults(prev => {
                            const u = prev.map(r => ({ ...r }));
                            u[idx] = {
                                ...u[idx],
                                errors: [...(u[idx].errors || []), `Error intervalo [${f},${t}]`],
                                exprType: u[idx]?.exprType,
                            };
                            return u;
                        });
                    }
                }
            })();
        });

        return () => {
            mounted = false;
        };
    }, [viewWindow, expressions, disabledFlags, getMissingIntervals, setResults, cacheRef]);

    /**
     * Handles blur (focus loss) on an expression input.
     * Evaluates the expression and updates results.
     * Batch-evaluates assignments up to and including this index.
     */
    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            if (disabledFlags[index] || expr.trim() === '') return;

            // Find batch indices for assignment expressions up to this index
            const batchIndices = expressions
                .slice(0, index + 1)
                .map((e, i) => ({ expr: e, i }))
                .filter(({ i }) => results[i]?.exprType === 'ASSIGNMENT')
                .map(({ i }) => i);

            batchIndices.push(index);

            const batchExprs = batchIndices.map(i => expressions[i]);
            const dec = '50';
            const origin = viewWindow.origin.toString();
            const bound = viewWindow.bound.toString();

            try {
                const batchResults = await evaluateBatchExpressions(
                    batchExprs,
                    dec,
                    origin,
                    bound
                );
                setResults(prev => {
                    const u = [...prev];
                    batchIndices.forEach((origIdx, idxInBatch) => {
                        const res = batchResults[idxInBatch];
                        u[origIdx] = {
                            ...res,
                            exprType: res.exprType || prev[origIdx]?.exprType,
                        };
                        if (origIdx === index) {
                            cacheRef.current[origIdx] = [
                                {
                                    from: viewWindow.origin,
                                    to: viewWindow.bound,
                                    points: res.drawingPoints || [],
                                },
                            ];
                        }
                    });
                    return u;
                });
            } catch (err) {
                console.error('Error batch evaluation:', err);
            }
        },
        [expressions, results, disabledFlags, viewWindow, setResults, cacheRef]
    );

    /**
     * Handler for when the graph view window changes.
     * Updates the state so re-evaluation can occur.
     * @param {ViewWindow} vw - The new view window.
     */
    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    /**
     * Changes the color of an expression row.
     * @param {number} i - Row index
     * @param {string} color - New color hex
     */
    const handleColorChange = useCallback((i: number, color: string) => {
        setColors(prev => {
            const u = [...prev];
            u[i] = color;
            return u;
        });
    }, []);

    /**
     * Toggles whether an expression row is enabled/disabled.
     * @param {number} i - Row index
     */
    const handleToggleDisabled = useCallback((i: number) => {
        setDisabledFlags(prev => {
            const u = [...prev];
            u[i] = !u[i];
            return u;
        });
    }, []);

    /**
     * Deletes an expression row.
     * @param {number} idx - Row index
     */
    const handleDeleteRow = useCallback((idx: number) => {
        deleteRow(idx);
    }, [deleteRow]);

    /**
     * Inserts a string into the currently focused expression at the caret position.
     * Optionally selects text or moves caret afterwards.
     * @param {string} v - Value to insert
     * @param {InsertOptions} [options] - Caret movement/selection options
     */
    const insertIntoExpression = (v: string, options?: InsertOptions) => {
        if (focusedIndex === null) return;
        setExpressions(prev => {
            const u = [...prev];
            const idx = focusedIndex;
            const expr = u[idx] ?? "";
            const selectionStart = caretPosition;
            const selectionEnd = caretPosition + selectionLength;
            const from = Math.min(selectionStart, selectionEnd);
            const to = Math.max(selectionStart, selectionEnd);
            u[idx] = expr.slice(0, from) + v + expr.slice(to);
            return u;
        });
        if (options?.selectLength) {
            setCaretPosition(() => {
                const selectionStart = caretPosition;
                const selectionEnd = caretPosition + selectionLength;
                const from = Math.min(selectionStart, selectionEnd);
                return from + (options?.deltaCaret ?? v.length);
            });
            setSelectionLength(options.selectLength);
        } else {
            setCaretPosition(() => {
                const selectionStart = caretPosition;
                const selectionEnd = caretPosition + selectionLength;
                const from = Math.min(selectionStart, selectionEnd);
                return from + (options?.deltaCaret ?? v.length);
            });
            setSelectionLength(0);
        }
    };

    /**
     * Backspace/delete handler for the currently focused expression.
     * Handles selection deletion or single character deletion.
     */
    const backspace = () => {
        if (focusedIndex === null) return;
        setExpressions(prev => {
            const u = [...prev];
            const idx = focusedIndex;
            const expr = u[idx] ?? "";
            const selectionStart = caretPosition;
            const selectionEnd = caretPosition + selectionLength;
            if (selectionLength !== 0) {
                const from = Math.min(selectionStart, selectionEnd);
                const to = Math.max(selectionStart, selectionEnd);
                u[idx] = expr.slice(0, from) + expr.slice(to);
            } else if (selectionStart > 0) {
                u[idx] = expr.slice(0, selectionStart - 1) + expr.slice(selectionStart);
            }
            return u;
        });
        if (selectionLength !== 0) {
            setCaretPosition(() => {
                const selectionStart = caretPosition;
                const selectionEnd = caretPosition + selectionLength;
                return Math.min(selectionStart, selectionEnd);
            });
            setSelectionLength(0);
        } else {
            setCaretPosition(pos => (pos > 0 ? pos - 1 : 0));
        }
    };

    /**
     * Clears all expressions, results, colors, disables, caret, and cache.
     */
    const clearAll = () => {
        setExpressions(['']);
        setResults([{}]);
        cacheRef.current = {};
        setColors(['#000000']);
        setDisabledFlags([false]);
        setFocusedIndex(null);
        setCaretPosition(0);
        setSelectionLength(0);
    };

    /**
     * Triggers evaluation of the currently focused or last non-empty expression.
     */
    const evaluateExpression = () => {
        const li = focusedIndex !== null ? focusedIndex : expressions.length - 1;
        if (expressions[li].trim() !== '' && !disabledFlags[li]) {
            handleExpressionBlur(li, expressions[li]).then(() => { });
        }
    };

    /**
     * Combines expression drawing points and color for all rows, for graph rendering.
     * Disabled rows are gray, enabled rows use assigned color.
     */
    const allDrawingSets = expressions.map((_, i) =>
        disabledFlags[i]
            ? { points: [], color: '#666666' }
            : { points: results[i]?.drawingPoints || [], color: colors[i] }
    );

    // Return all logic and state for calculator
    return {
        expressions,
        setExpressions,
        results,
        setResults,
        colors,
        setColors,
        disabledFlags,
        setDisabledFlags,
        viewWindow,
        setViewWindow,
        focusedIndex,
        setFocusedIndex,
        caretPosition,
        setCaretPosition,
        selectionLength,
        setSelectionLength,
        handleExpressionBlur,
        handleViewChange,
        handleColorChange,
        handleToggleDisabled,
        handleDeleteRow,
        insertIntoExpression,
        backspace,
        clearAll,
        evaluateExpression,
        allDrawingSets
    };
}
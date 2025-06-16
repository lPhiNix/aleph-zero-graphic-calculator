import {useRef, useCallback, useEffect, useState} from 'react';
import { useExpressionsState } from './useExpressionState.tsx';
import { useCaretSelection } from './useCaretSelection.tsx';
import {
    evaluateSingleExpression,
    evaluateBatchExpressions,
} from '../../services/mathService.ts';

interface ViewWindow {
    origin: number;
    bound: number;
    bottom: number;
    top: number;
}

type IntervalData = {
    from: number;
    to: number;
    points: Array<{ x: number; y: number }>;
};

type InsertOptions = {
    deltaCaret?: number;
    selectLength?: number;
};

export function useCalculatorLogic() {
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

    const {
        focusedIndex,
        setFocusedIndex,
        caretPosition,
        setCaretPosition,
        selectionLength,
        setSelectionLength
    } = useCaretSelection();

    const [viewWindow, setViewWindow] = useState<ViewWindow>({
        origin: -10,
        bound: 10,
        bottom: -10,
        top: 10,
    });

    const isFirstRender = useRef(true);

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

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        let mounted = true;
        const { origin, bound } = viewWindow;
        const dec = '50';

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

    const handleExpressionBlur = useCallback(
        async (index: number, expr: string) => {
            if (disabledFlags[index] || expr.trim() === '') return;

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

    const handleViewChange = useCallback((vw: ViewWindow) => {
        setViewWindow(vw);
    }, []);

    const handleColorChange = useCallback((i: number, color: string) => {
        setColors(prev => {
            const u = [...prev];
            u[i] = color;
            return u;
        });
    }, []);


    const handleToggleDisabled = useCallback((i: number) => {
        setDisabledFlags(prev => {
            const u = [...prev];
            u[i] = !u[i];
            return u;
        });
    }, []);

    const handleDeleteRow = useCallback((idx: number) => {
        deleteRow(idx);
    }, [deleteRow]);

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

    const evaluateExpression = () => {
        const li = focusedIndex !== null ? focusedIndex : expressions.length - 1;
        if (expressions[li].trim() !== '' && !disabledFlags[li]) {
            handleExpressionBlur(li, expressions[li]).then(() => { });
        }
    };

    const allDrawingSets = expressions.map((_, i) =>
        disabledFlags[i]
            ? { points: [], color: '#666666' }
            : { points: results[i]?.drawingPoints || [], color: colors[i] }
    );

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
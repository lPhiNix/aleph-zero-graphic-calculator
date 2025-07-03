import { useState, useEffect, useRef } from 'react';
import type { ExpressionResult } from '../../types/math';

// Your pastel color palette as CSS variables
const DEFAULT_COLORS = [
    'var(--color-blue-pastel)',
    'var(--color-yellow-pastel)',
    'var(--color-purple-pastel)',
    'var(--color-pink-pastel)',
    'var(--color-orange-pastel)',
    'var(--color-cyan-pastel)',
    'var(--color-lime-pastel)',
    'var(--color-indigo-pastel)',
    'var(--color-beige-pastel)',
];

// Resolves a CSS color variable to its actual value (hex/rgb)
function resolveCSSColor(cssVar: string): string {
    // If already a hex/rgb color, return directly
    if (/^#([0-9a-f]{3,8})$/i.test(cssVar) || cssVar.startsWith('rgb')) return cssVar;
    // If it's a CSS variable like var(--xxx)
    if (cssVar.startsWith('var(')) {
        const match = cssVar.match(/^var\((--[a-zA-Z0-9\-]+)\)/);
        if (match) {
            const varName = match[1];
            const value = getComputedStyle(document.documentElement).getPropertyValue(varName).trim();
            // If variable not found, fall back to black
            return value || '#000000';
        }
    }
    // Default fallback is black
    return '#000000';
}

type IntervalData = {
    from: number;
    to: number;
    points: Array<{ x: number; y: number }>;
};

/**
 * Hook to manage the state of expressions, results, colors, disables, and drawing cache.
 * Used in calculator panels and math input areas.
 */
export function useExpressionsState() {
    const [expressions, setExpressions] = useState<string[]>(['']);
    const [results, setResults] = useState<ExpressionResult[]>(() =>
        expressions.map(() => ({}))
    );
    // Colors: initialize after first render to access CSS
    const [colors, setColors] = useState<string[]>([]);

    const [disabledFlags, setDisabledFlags] = useState<boolean[]>([false]);
    const cacheRef = useRef<Record<number, IntervalData[]>>({});

    // Initialize colors after first render (when CSS is available)
    useEffect(() => {
        if (colors.length === 0) {
            setColors([
                resolveCSSColor(DEFAULT_COLORS[0])
            ]);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Whenever expressions change, ensure parallel arrays (results, colors, disables, cache) are in sync
    useEffect(() => {
        setResults(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push({});
            upd.length = expressions.length;
            return upd;
        });

        setColors(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) {
                const cssVar = DEFAULT_COLORS[upd.length % DEFAULT_COLORS.length];
                const resolved = resolveCSSColor(cssVar) || '#000000';
                upd.push(resolved);
            }
            upd.length = expressions.length;
            return upd;
        });

        setDisabledFlags(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push(false);
            upd.length = expressions.length;
            return upd;
        });

        expressions.forEach((_, i) => {
            if (!cacheRef.current[i]) cacheRef.current[i] = [];
        });
    }, [expressions]);

    /**
     * Removes a row (expression, result, color, disables) and shifts cache.
     * Ensures there's always at least one row.
     * @param {number} idx - Index to delete
     */
    const deleteRow = (idx: number) => {
        setExpressions(prev => {
            const u = [...prev];
            u.splice(idx, 1);
            return u.length > 0 ? u : [''];
        });
        setColors(prev => {
            const u = [...prev];
            u.splice(idx, 1);
            return u.length > 0 ? u : [resolveCSSColor(DEFAULT_COLORS[0])];
        });
        setDisabledFlags(prev => {
            const u = [...prev];
            u.splice(idx, 1);
            return u.length > 0 ? u : [false];
        });
        setResults(prev => {
            const u = [...prev];
            u.splice(idx, 1);
            return u.length > 0 ? u : [{}];
        });
        // Rebuild cacheRef, shifting keys as needed
        const newCache: typeof cacheRef.current = {};
        Object.entries(cacheRef.current).forEach(([key, val]) => {
            const k = Number(key);
            if (k === idx) return;
            newCache[k > idx ? k - 1 : k] = val;
        });
        cacheRef.current = newCache;
    };

    // Return all state and setters for external use
    return {
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
    };
}
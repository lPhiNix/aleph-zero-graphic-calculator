import { useState, useEffect, useRef } from 'react';
import type { ExpressionResult } from '../../types/math';

// Tu paleta de colores pastel como variables CSS
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

// Función para resolver la variable CSS a su valor real (hex/rgb)
function resolveCSSColor(cssVar: string): string {
    // Si es hex/rgb, devuélvelo directo
    if (/^#([0-9a-f]{3,8})$/i.test(cssVar) || cssVar.startsWith('rgb')) return cssVar;
    // Si es variable css tipo var(--xxx)
    if (cssVar.startsWith('var(')) {
        const match = cssVar.match(/^var\((--[a-zA-Z0-9\-]+)\)/);
        if (match) {
            const varName = match[1];
            const value = getComputedStyle(document.documentElement).getPropertyValue(varName).trim();
            // Si por cualquier motivo la variable no existe, retorna negro
            return value || '#000000';
        }
    }
    // Si todo falla, negro
    return '#000000';
}

type IntervalData = {
    from: number;
    to: number;
    points: Array<{ x: number; y: number }>;
};

export function useExpressionsState() {
    const [expressions, setExpressions] = useState<string[]>(['']);
    const [results, setResults] = useState<ExpressionResult[]>(() =>
        expressions.map(() => ({}))
    );
    // Colores: inicializa tras primer render para que haya acceso a CSS
    const [colors, setColors] = useState<string[]>([]);

    const [disabledFlags, setDisabledFlags] = useState<boolean[]>([false]);
    const cacheRef = useRef<Record<number, IntervalData[]>>({});

    // Inicializa los colores tras el primer render (cuando ya se puede leer CSS)
    useEffect(() => {
        if (colors.length === 0) {
            setColors([
                resolveCSSColor(DEFAULT_COLORS[0])
            ]);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

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
        const newCache: typeof cacheRef.current = {};
        Object.entries(cacheRef.current).forEach(([key, val]) => {
            const k = Number(key);
            if (k === idx) return;
            newCache[k > idx ? k - 1 : k] = val;
        });
        cacheRef.current = newCache;
    };

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
import { useState, useEffect, useRef } from 'react';
import type { ExpressionResult } from '../types/math';

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
    const [colors, setColors] = useState<string[]>(['#ff0000']);
    const [disabledFlags, setDisabledFlags] = useState<boolean[]>([false]);
    const cacheRef = useRef<Record<number, IntervalData[]>>({});

    useEffect(() => {
        setResults(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push({});
            upd.length = expressions.length;
            return upd;
        });
        setColors(prev => {
            const upd = [...prev];
            while (upd.length < expressions.length) upd.push('#000000');
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
            return u.length > 0 ? u : ['#000000'];
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
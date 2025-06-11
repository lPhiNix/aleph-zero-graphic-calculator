import React, { useState, useRef, useEffect } from 'react';
import styles from '../../styles/modules/mathkeyboard.module.css';

interface KeyButton {
    label: string;
    onClick: () => void;
    className?: string;
    dataVirtualKey?: boolean;
    category?: string;
}

interface CategoryConfig {
    name: string;
    columns: number;
}

interface MathKeyboardProps {
    keys: KeyButton[];
    symjaKeys: KeyButton[];
    mathFuncKeys: KeyButton[];
    constantKeys: KeyButton[];
    mathCategoryConfig?: CategoryConfig[];
    symjaCategoryConfig?: CategoryConfig[];
    constantCategoryConfig?: CategoryConfig[];
    onAnyKeyPress?: (label: string) => void;
}

function CategoryGrid({
                          items,
                          categories,
                          onAnyKeyPress,
                      }: {
    items: KeyButton[];
    categories?: CategoryConfig[];
    onAnyKeyPress?: (label: string) => void;
}) {
    const grouped: Record<string, KeyButton[]> = {};
    for (const k of items) {
        const cat = k.category || 'Sin categoría';
        if (!grouped[cat]) grouped[cat] = [];
        grouped[cat].push(k);
    }

    return (
        <div>
            {Object.entries(grouped).map(([cat, btns]) => {
                const col = categories?.find(c => c.name === cat)?.columns ?? 5;
                return (
                    <div key={cat} className={styles.categoryWrapper}>
                        <div className={styles.categoryHeader}>
                            {cat}
                        </div>
                        <div
                            className={styles.grid}
                            style={{ gridTemplateColumns: `repeat(${col}, 1fr)` }}
                        >
                            {btns.map((key) => (
                                <button
                                    key={key.label}
                                    type="button"
                                    className={`${styles.keyButton} ${key.className || ''}`}
                                    onClick={() => {
                                        key.onClick();
                                        if (onAnyKeyPress) onAnyKeyPress(key.label);
                                    }}
                                    data-virtualkey={key.dataVirtualKey ? "true" : undefined}
                                    tabIndex={0}
                                >
                                    {key.label}
                                </button>
                            ))}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

function Submenu({
                     items,
                     categories,
                     submenuRef,
                     children,
                 }: {
    items: KeyButton[];
    categories?: CategoryConfig[];
    submenuRef: React.RefObject<HTMLDivElement | null>; // 🔧 modificado aquí
    children?: React.ReactNode;
}) {
    return (
        <div
            ref={submenuRef}
            className={styles.categorySubmenu}
        >
            <CategoryGrid items={items} categories={categories} />
            {children}
        </div>
    );
}

export default function MathKeyboard({
                                         keys,
                                         symjaKeys,
                                         mathFuncKeys,
                                         constantKeys,
                                         mathCategoryConfig,
                                         symjaCategoryConfig,
                                         constantCategoryConfig,
                                         onAnyKeyPress
                                     }: MathKeyboardProps) {
    const [openMenu, setOpenMenu] = useState<null | 'math' | 'symja' | 'constants'>(null);

    const toolbarRef = useRef<HTMLDivElement | null>(null);
    const submenuRef = useRef<HTMLDivElement | null>(null);


    function getClassNames(...classes: (string | undefined)[]) {
        // Filtra las clases no definidas y las busca en styles (CSS Modules)
        return classes
            .filter(Boolean)
            .map((cls) => styles[cls!])
            .join(' ');
    }

    useEffect(() => {
        if (!openMenu) return;
        function handleClick(e: MouseEvent) {
            if (
                toolbarRef.current &&
                !toolbarRef.current.contains(e.target as Node) &&
                submenuRef.current &&
                !submenuRef.current.contains(e.target as Node)
            ) {
                setOpenMenu(null);
            }
        }
        window.addEventListener('mousedown', handleClick);
        return () => window.removeEventListener('mousedown', handleClick);
    }, [openMenu]);

    return (
        <div className={styles.keyboardContainer}>
            <div
                className={styles.toolbar}
                ref={toolbarRef}
            >
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'math' ? null : 'math')}
                    data-virtualkey="true"
                >
                    📐 Funciones Matemáticas
                </button>
                {openMenu === 'math' && (
                    <Submenu
                        items={mathFuncKeys}
                        categories={mathCategoryConfig}
                        submenuRef={submenuRef}
                    />
                )}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'symja' ? null : 'symja')}
                    data-virtualkey="true"
                >
                    🧠 Symja
                </button>
                {openMenu === 'symja' && (
                    <Submenu
                        items={symjaKeys}
                        categories={symjaCategoryConfig}
                        submenuRef={submenuRef}
                    />
                )}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'constants' ? null : 'constants')}
                    data-virtualkey="true"
                >
                    📏 Constantes

                </button>
                {openMenu === 'constants' && (
                    <Submenu
                        items={constantKeys}
                        categories={constantCategoryConfig}
                        submenuRef={submenuRef}
                    />
                )}
            </div>
            <div className={styles.grid}>
                {keys.map((key, idx) => (
                    <button
                        key={idx}
                        type="button"
                        className={getClassNames('keyButton', ...(key.className?.split(' ') || []))}
                        onClick={() => {
                            key.onClick();
                            if (onAnyKeyPress) onAnyKeyPress(key.label);
                        }}
                        data-virtualkey={key.dataVirtualKey ? "true" : undefined}
                        tabIndex={0}
                    >
                        {key.label}
                    </button>
                ))}
            </div>
        </div>
    );
}
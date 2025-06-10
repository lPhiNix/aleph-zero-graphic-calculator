import { useState } from 'react';
import styles from '../../styles/modules/mathKeyboard.module.css';

interface KeyButton {
    label: string;
    onClick: () => void;
    className?: string;
    dataVirtualKey?: boolean;
    category?: string;
}

// Nuevo tipo para la configuración de categorías
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

// Helper para grid por categoría
function CategoryGrid({
                          items,
                          categories,
                          onAnyKeyPress,
                      }: {
    items: KeyButton[];
    categories?: CategoryConfig[];
    onAnyKeyPress?: (label: string) => void;
}) {
    // Agrupa por categoría
    const grouped: Record<string, KeyButton[]> = {};
    for (const k of items) {
        const cat = k.category || 'Sin categoría';
        if (!grouped[cat]) grouped[cat] = [];
        grouped[cat].push(k);
    }

    return (
        <div>
            {Object.entries(grouped).map(([cat, btns]) => {
                // Busca config de columnas
                const col = categories?.find(c => c.name === cat)?.columns ?? 5;
                return (
                    <div key={cat} style={{ marginBottom: "1.5rem" }}>
                        <div style={{ fontWeight: "bold", fontSize: ".95rem", margin: ".5rem 0", color: "#6ad" }}>
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

    function Submenu({
                         items,
                         categories,
                         onClose,
                     }: {
        items: KeyButton[];
        categories?: CategoryConfig[];
        onClose: () => void;
    }) {
        return (
            <div
                style={{
                    position: 'absolute',
                    left: 0,
                    top: '2.5rem',
                    background: '#222',
                    color: '#fff',
                    border: '1px solid #444',
                    minWidth: '16rem',
                    zIndex: 20,
                    borderRadius: '0 0 6px 6px',
                    boxShadow: '0 3px 12px 0 #000c',
                    padding: '0.5rem 0.7rem'
                }}
                onMouseLeave={onClose}
            >
                <CategoryGrid items={items} categories={categories} onAnyKeyPress={onAnyKeyPress} />
            </div>
        );
    }

    return (
        <div className={styles.keyboardContainer}>
            <div className={styles.toolbar} style={{ position: 'relative', display: 'flex', gap: 8 }}>
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'math' ? null : 'math')}
                    data-virtualkey="true"
                >
                    Funciones Matemáticas ▾
                </button>
                {openMenu === 'math' && (
                    <Submenu
                        items={mathFuncKeys}
                        categories={mathCategoryConfig}
                        onClose={() => setOpenMenu(null)}
                    />
                )}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'symja' ? null : 'symja')}
                    data-virtualkey="true"
                >
                    Funciones Symja ▾
                </button>
                {openMenu === 'symja' && (
                    <Submenu
                        items={symjaKeys}
                        categories={symjaCategoryConfig}
                        onClose={() => setOpenMenu(null)}
                    />
                )}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'constants' ? null : 'constants')}
                    data-virtualkey="true"
                >
                    Constantes ▾
                </button>
                {openMenu === 'constants' && (
                    <Submenu
                        items={constantKeys}
                        categories={constantCategoryConfig}
                        onClose={() => setOpenMenu(null)}
                    />
                )}
            </div>
            {/* ─── GRID DE TECLAS (7 × 5) ────────────────────────────────── */}
            <div className={styles.grid}>
                {keys.map((key, idx) => (
                    <button
                        key={idx}
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
}
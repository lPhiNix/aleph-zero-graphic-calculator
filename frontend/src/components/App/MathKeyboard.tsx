import React, { useState, useRef, useEffect } from 'react'; // Import React and hooks for state, refs, and effects
import styles from '../../styles/modules/mathkeyboard.module.css'; // Import CSS module for keyboard styling

/**
 * Interface representing a button on the math keyboard.
 * @property {string} label - Text or symbol shown on the button.
 * @property {() => void} onClick - Function to call when button is clicked.
 * @property {string} [className] - Optional custom class for the button.
 * @property {boolean} [dataVirtualKey] - Indicates if the key is virtual (for accessibility).
 * @property {string} [category] - Optional category name for grouping.
 */
interface KeyButton {
    label: string;
    onClick: () => void;
    className?: string;
    dataVirtualKey?: boolean;
    category?: string;
}

/**
 * Interface representing a category grouping for keyboard keys.
 * @property {string} name - Category name.
 * @property {number} columns - Number of columns in the grid for this category.
 */
interface CategoryConfig {
    name: string;
    columns: number;
}

/**
 * Props for the MathKeyboard component.
 * @property {KeyButton[]} keys - Main row of keys.
 * @property {KeyButton[]} symjaKeys - Keys for the "Symja" submenu.
 * @property {KeyButton[]} mathFuncKeys - Keys for the "Math Functions" submenu.
 * @property {KeyButton[]} constantKeys - Keys for the "Constants" submenu.
 * @property {CategoryConfig[]} [mathCategoryConfig] - Category config for math function submenu.
 * @property {CategoryConfig[]} [symjaCategoryConfig] - Category config for symja submenu.
 * @property {CategoryConfig[]} [constantCategoryConfig] - Category config for constants submenu.
 * @property {(label: string) => void} [onAnyKeyPress] - Optional callback on any key press.
 */
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

/**
 * Renders a grid of buttons grouped by category.
 * @param {KeyButton[]} items - Keys to display.
 * @param {CategoryConfig[]} [categories] - Category configuration.
 * @param {(label: string) => void} [onAnyKeyPress] - Callback for key press.
 * @returns {JSX.Element}
 */
function CategoryGrid({
                          items,
                          categories,
                          onAnyKeyPress,
                      }: {
    items: KeyButton[];
    categories?: CategoryConfig[];
    onAnyKeyPress?: (label: string) => void;
}) {
    /**
     * Group the items by their category. Defaults to "Uncategorized" if none is provided.
     */
    const grouped: Record<string, KeyButton[]> = {};
    for (const k of items) {
        // Label "Sin categoría" changed to "Uncategorized" for English consistency
        const cat = k.category || 'Uncategorized';
        if (!grouped[cat]) grouped[cat] = [];
        grouped[cat].push(k);
    }

    return (
        <div>
            {/* Render a grid for each category */}
            {Object.entries(grouped).map(([cat, btns]) => {
                // Determine column count for this category, defaulting to 5
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

/**
 * Renders a submenu panel for a set of category keys.
 * @param {KeyButton[]} items - Keys to display in the submenu.
 * @param {CategoryConfig[]} [categories] - Optional category config.
 * @param {React.RefObject<HTMLDivElement | null>} submenuRef - Ref to submenu for outside click handling.
 * @param {React.ReactNode} [children] - Any extra content.
 * @returns {JSX.Element}
 */
function Submenu({
                     items,
                     categories,
                     submenuRef,
                     children,
                 }: {
    items: KeyButton[];
    categories?: CategoryConfig[];
    submenuRef: React.RefObject<HTMLDivElement | null>;
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

/**
 * Main MathKeyboard component.
 * Renders a math symbol keyboard with main keys and dropdown submenus.
 * @param {MathKeyboardProps} props - Keyboard props.
 * @returns {JSX.Element}
 */
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
    /**
     * State for which dropdown menu is open: "math", "symja", "constants", or null.
     */
    const [openMenu, setOpenMenu] = useState<null | 'math' | 'symja' | 'constants'>(null);

    /**
     * Ref to the toolbar container.
     */
    const toolbarRef = useRef<HTMLDivElement | null>(null);

    /**
     * Ref to the currently open submenu.
     */
    const submenuRef = useRef<HTMLDivElement | null>(null);

    /**
     * Helper function to get a list of CSS class names from arguments, filtered and mapped from CSS Modules.
     * @param {...(string | undefined)[]} classes - Class names (may be undefined).
     * @returns {string} - Mapped, space-separated class names.
     */
    function getClassNames(...classes: (string | undefined)[]) {
        // Filter undefined and map using styles (CSS Modules)
        return classes
            .filter(Boolean)
            .map((cls) => styles[cls!])
            .join(' ');
    }

    /**
     * Effect to handle closing the dropdown menu when clicking outside of the toolbar or submenu.
     * Adds/removes an event listener on window "mousedown".
     */
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

    // Render the keyboard container, including dropdown toolbars and main keys grid
    return (
        <div className={styles.keyboardContainer}>
            {/* Toolbar with dropdowns for math functions, symja, and constants */}
            <div
                className={styles.toolbar}
                ref={toolbarRef}
            >
                {/* Dropdown for math functions */}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'math' ? null : 'math')}
                    data-virtualkey="true"
                >
                    📐 Math Functions
                </button>
                {openMenu === 'math' && (
                    <Submenu
                        items={mathFuncKeys}
                        categories={mathCategoryConfig}
                        submenuRef={submenuRef}
                    />
                )}
                {/* Dropdown for symja functions */}
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
                {/* Dropdown for constants */}
                <button
                    type="button"
                    className={styles.dropdownButton}
                    onClick={() => setOpenMenu(openMenu === 'constants' ? null : 'constants')}
                    data-virtualkey="true"
                >
                    📏 Constants
                </button>
                {openMenu === 'constants' && (
                    <Submenu
                        items={constantKeys}
                        categories={constantCategoryConfig}
                        submenuRef={submenuRef}
                    />
                )}
            </div>
            {/* Main grid of keys always visible */}
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
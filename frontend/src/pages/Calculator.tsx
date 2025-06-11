import Header from '../components/App/Header';
import ExpressionList from '../components/App/ExpressionList';
import GraphCanvas from '../components/App/Graph/GraphCanvas';
import MathKeyboard from '../components/App/MathKeyboard';
import { useCalculatorLogic } from '../hooks/useCalcLogic.tsx';
import styles from '../styles/modules/graphCanvas.module.css';

/**
 * Now you can define all keys, including submenus, with all the same options as the main keyboard:
 *  - label
 *  - onClick (with insertIntoExpression and cursor/selection logic)
 *  - className (for optional wide keys, styles)
 *  - dataVirtualKey (for focus handling)
 */
export default function Calculator() {
    const {
        expressions,
        setExpressions,
        results,
        colors,
        disabledFlags,
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
    } = useCalculatorLogic();

    // --- Math Functions (submenu) ---
    const mathFuncKeys = [
        // Funciones definidas por el usuario
        { label: 'f',          onClick: () => insertIntoExpression('f()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas', tooltip: "nose" },
        { label: 'g',          onClick: () => insertIntoExpression('g()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas' },
        { label: 'h',          onClick: () => insertIntoExpression('h()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas' },

        // Variables paramétricas
        { label: 'x',          onClick: () => insertIntoExpression('x()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas' },
        { label: 'y',          onClick: () => insertIntoExpression('y()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas' },
        { label: 'z',          onClick: () => insertIntoExpression('z()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Genéricas' },

        // Funciones especiales
        { label: 'gamma',      onClick: () => insertIntoExpression('gamma()', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Especiales' },
        { label: 'zeta',       onClick: () => insertIntoExpression('zeta()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Especiales'},
        { label: 'erf',        onClick: () => insertIntoExpression('erf()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Especiales' },
        { label: 'fresnelc',   onClick: () => insertIntoExpression('fresnelc()', { deltaCaret: 9, selectLength: 0 }), dataVirtualKey: true, category: 'Especiales' },

        // Operaciones matemáticas básicas
        { label: 'exp',        onClick: () => insertIntoExpression('exp()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Básicas' },
        { label: 'log10',      onClick: () => insertIntoExpression('log10()', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Básicas' },
        { label: 'log2',       onClick: () => insertIntoExpression('log2()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Básicas' },
        { label: 'abs',        onClick: () => insertIntoExpression('abs()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Básicas' },

        // Funciones trigonométricas
        { label: 'sin',        onClick: () => insertIntoExpression('sin()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },
        { label: 'cos',        onClick: () => insertIntoExpression('cos()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },
        { label: 'tan',        onClick: () => insertIntoExpression('tan()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },
        { label: 'csc',        onClick: () => insertIntoExpression('csc()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },
        { label: 'cot',        onClick: () => insertIntoExpression('cot()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },
        { label: 'sec',        onClick: () => insertIntoExpression('sec()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas' },

        // Funciones trigonométricas inversas
        { label: 'arcsin',     onClick: () => insertIntoExpression('arcsin()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },
        { label: 'arccos',     onClick: () => insertIntoExpression('arccos()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },
        { label: 'arctan',     onClick: () => insertIntoExpression('arctan()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },
        { label: 'arccsc',     onClick: () => insertIntoExpression('arccsc()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },
        { label: 'arccot',     onClick: () => insertIntoExpression('arccot()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },
        { label: 'arcsec',     onClick: () => insertIntoExpression('arcsec()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonométricas de Arco' },

        // Funciones hiperbólicas
        { label: 'sinh',       onClick: () => insertIntoExpression('sinh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },
        { label: 'cosh',       onClick: () => insertIntoExpression('cosh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },
        { label: 'tanh',       onClick: () => insertIntoExpression('tanh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },
        { label: 'coth',       onClick: () => insertIntoExpression('coth()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },
        { label: 'sech',       onClick: () => insertIntoExpression('sech()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },
        { label: 'csch',       onClick: () => insertIntoExpression('csch()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas' },

        // Funciones hiperbólicas inversas
        { label: 'arcsinh',    onClick: () => insertIntoExpression('arcsinh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' },
        { label: 'arccosh',    onClick: () => insertIntoExpression('arccosh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' },
        { label: 'arctanh',    onClick: () => insertIntoExpression('arctanh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' },
        { label: 'arccoth',    onClick: () => insertIntoExpression('arccoth()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' },
        { label: 'arcsech',    onClick: () => insertIntoExpression('arcsech()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' },
        { label: 'arccsch',    onClick: () => insertIntoExpression('arccsch()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Hiperbólicas de Arco' }
    ];

    // --- Symja Functions (submenu) ---
    const symjaKeys = [
        // Derivadas y cálculo
        { label: 'D',          onClick: () => insertIntoExpression('D[, ]', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: "Cálculo" },
        { label: 'Diff',       onClick: () => insertIntoExpression('Diff[, ]', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: "Cálculo" },
        { label: 'Integrate',  onClick: () => insertIntoExpression('Integrate[, ]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Cálculo" },
        { label: 'Taylor',     onClick: () => insertIntoExpression('Taylor[, ]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Cálculo" },
        { label: 'Limit',      onClick: () => insertIntoExpression('Limit[, ]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Cálculo" },


        { label: 'Solve',      onClick: () => insertIntoExpression('Solve[, ]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Análisis" },
        { label: 'DSolve',     onClick: () => insertIntoExpression('DSolve[, ]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Análisis" },

        // Álgebra y simplificación
        { label: 'Simplify',   onClick: () => insertIntoExpression('Simplify[]', { deltaCaret: 9, selectLength: 0 }), dataVirtualKey: true, category: "Álgebra" },
        { label: 'Expand',     onClick: () => insertIntoExpression('Expand[]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Álgebra"},

        // Funciones aritméticas
        { label: 'GCD',        onClick: () => insertIntoExpression('GCD[]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Aritmética" },
        { label: 'LCM',        onClick: () => insertIntoExpression('LCM[]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Aritmética" },

        // Operaciones vectoriales y matrices
        { label: 'Dot',        onClick: () => insertIntoExpression('Dot[,]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Operaciones Vectoriales" },
        { label: 'Cross',      onClick: () => insertIntoExpression('Cross[,]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Operaciones Vectoriales" },
        { label: 'Norm',       onClick: () => insertIntoExpression('Norm[]', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: "Operaciones Vectoriales" },
        { label: 'Normalize',  onClick: () => insertIntoExpression('Normalize[]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Operaciones Vectoriales" },
        { label: 'Vectorangle',onClick: () => insertIntoExpression('Vectorangle[,]', { deltaCaret: 11, selectLength: 0 }), dataVirtualKey: true, category: "Operaciones Vectoriales" },


        { label: 'Projection', onClick: () => insertIntoExpression('Projection[]', { deltaCaret: 11, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Eigenvalues',onClick: () => insertIntoExpression('Eigenvalues[]', { deltaCaret: 12, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Inverse',    onClick: () => insertIntoExpression('Inverse[]', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Transpose',  onClick: () => insertIntoExpression('Transpose[]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
    ];

    // --- Constants (submenu) ---
    const constantKeys = [
        { label: 'Pi',               onClick: () => insertIntoExpression('Pi', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'E',                onClick: () => insertIntoExpression('E', { deltaCaret: 1, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'I',                onClick: () => insertIntoExpression('I', { deltaCaret: 1, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Phi',              onClick: () => insertIntoExpression('Phi', { deltaCaret: 3, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Infinity',         onClick: () => insertIntoExpression('Infinity', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'ComplexInfinity',  onClick: () => insertIntoExpression('ComplexInfinity', { deltaCaret: 15, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'EulerGamma',       onClick: () => insertIntoExpression('EulerGamma', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Degree',           onClick: () => insertIntoExpression('Degree', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Catalan',          onClick: () => insertIntoExpression('Catalan', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'MeisselMertens',   onClick: () => insertIntoExpression('MeisselMertens', { deltaCaret: 14, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Glaisher',         onClick: () => insertIntoExpression('Glaisher', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
        { label: 'Khinchin',         onClick: () => insertIntoExpression('Khinchin', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constantes' },
    ];

    // --- Main Keyboard Keys (all properties are configurable) ---
    const keys = [
        { label: 'x²', onClick: () => insertIntoExpression('()^2', { deltaCaret: 1, selectLength: 0 }), dataVirtualKey: true },
        { label: '1/x', onClick: () => insertIntoExpression('1/()', { deltaCaret: 3, selectLength: 0 }), dataVirtualKey: true },
        { label: 'CE', onClick: clearAll, dataVirtualKey: true, className: 'deleteKey' },
        { label: 'C', onClick: () => { if (focusedIndex !== null) handleDeleteRow(focusedIndex); }, dataVirtualKey: true, className: 'deleteKey' },
        { label: '⌫', onClick: backspace, dataVirtualKey: true, className: 'deleteKey' },
        { label: '<', onClick: () => insertIntoExpression('<'), dataVirtualKey: true },
        { label: '>', onClick: () => insertIntoExpression('>'), dataVirtualKey: true },
        { label: 'x', onClick: () => insertIntoExpression('x'), dataVirtualKey: true },
        { label: 'y', onClick: () => insertIntoExpression('y'), dataVirtualKey: true },
        { label: 'z', onClick: () => insertIntoExpression('z'), dataVirtualKey: true },
        { label: '√', onClick: () => insertIntoExpression('Sqrt()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true },
        { label: '(', onClick: () => insertIntoExpression('('), dataVirtualKey: true },
        { label: ')', onClick: () => insertIntoExpression(')'), dataVirtualKey: true },
        { label: '=', onClick: () => insertIntoExpression('='), dataVirtualKey: true },
        { label: '÷', onClick: () => insertIntoExpression('/'), dataVirtualKey: true },
        { label: 'xʸ', onClick: () => insertIntoExpression('^()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true },
        { label: '7', onClick: () => insertIntoExpression('7'), dataVirtualKey: true, className: 'numberKey' },
        { label: '8', onClick: () => insertIntoExpression('8'), dataVirtualKey: true, className: 'numberKey' },
        { label: '9', onClick: () => insertIntoExpression('9'), dataVirtualKey: true, className: 'numberKey' },
        { label: '×', onClick: () => insertIntoExpression('*'), dataVirtualKey: true },
        { label: '10ˣ', onClick: () => insertIntoExpression('10^()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true },
        { label: '4', onClick: () => insertIntoExpression('4'), dataVirtualKey: true, className: 'numberKey' },
        { label: '5', onClick: () => insertIntoExpression('5'), dataVirtualKey: true, className: 'numberKey' },
        { label: '6', onClick: () => insertIntoExpression('6'), dataVirtualKey: true, className: 'numberKey' },
        { label: '−', onClick: () => insertIntoExpression('-'), dataVirtualKey: true },
        { label: 'log', onClick: () => insertIntoExpression('log10()', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true },
        { label: '1', onClick: () => insertIntoExpression('1'), dataVirtualKey: true, className: 'numberKey' },
        { label: '2', onClick: () => insertIntoExpression('2'), dataVirtualKey: true, className: 'numberKey' },
        { label: '3', onClick: () => insertIntoExpression('3'), dataVirtualKey: true, className: 'numberKey' },
        { label: '+', onClick: () => insertIntoExpression('+'), dataVirtualKey: true },
        { label: '(-)', onClick: () => insertIntoExpression('(-)', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true },
        { label: '.', onClick: () => insertIntoExpression("."), dataVirtualKey: true, className: 'numberKey' },
        { label: '0', onClick: () => insertIntoExpression('0'), dataVirtualKey: true, className: 'numberKey' },
        { label: ',', onClick: () => insertIntoExpression(','), dataVirtualKey: true, className: 'numberKey' },
        { label: '↵', onClick: evaluateExpression, className: "entryKey", dataVirtualKey: true },
    ];

    const mathCategoryConfig = [
        { name: "Genéricas", columns: 2 },
        { name: "Especiales", columns: 4 },
        { name: "Básicas", columns: 2 },
        { name: "Trigonométricas", columns: 3 },
        { name: "Trigonométricas de Arco", columns: 3 },
        { name: "Hiperbólicas", columns: 3 },
        { name: "Hiperbólicas de Arco", columns: 3 }
    ];
    const symjaCategoryConfig = [
        { name: "Cálculo", columns: 5 },
        { name: "Álgebra", columns: 2 },
        { name: "Análisis", columns: 2},
        { name: "Aritmética", columns: 2},
        { name: "Operaciones Vectoriales", columns: 5},
        { name: "Matrices", columns: 2}
    ];
    const constantCategoryConfig = [
        { name: "Constantes", columns: 2 },
    ];

    return (
        <div className={styles.pageContainer}>
            <Header title="Placeholder" />
            <div className={styles.mainArea}>
                <div className={styles.canvasWrapper}>
                    <GraphCanvas
                        drawingSets={allDrawingSets}
                        onViewChange={handleViewChange}
                    />
                </div>
                <div className={styles.expressionsWrapper}>
                    <ExpressionList
                        expressions={expressions}
                        onExpressionsChange={setExpressions}
                        onExpressionBlur={handleExpressionBlur}
                        colors={colors}
                        onColorChange={handleColorChange}
                        disabledFlags={disabledFlags}
                        onToggleDisabled={handleToggleDisabled}
                        expressionTypes={results.map((r) => r.exprType)}
                        onDeleteRow={handleDeleteRow}
                        results={results}
                        focusedIndex={focusedIndex}
                        setFocusedIndex={setFocusedIndex}
                        caretPosition={caretPosition}
                        setCaretPosition={setCaretPosition}
                        selectionLength={selectionLength}
                        setSelectionLength={setSelectionLength}
                    />
                    <MathKeyboard
                        keys={keys}
                        symjaKeys={symjaKeys}
                        mathFuncKeys={mathFuncKeys}
                        constantKeys={constantKeys}
                        mathCategoryConfig={mathCategoryConfig}
                        symjaCategoryConfig={symjaCategoryConfig}
                        constantCategoryConfig={constantCategoryConfig}
                    />
                </div>
            </div>
        </div>
    );
}
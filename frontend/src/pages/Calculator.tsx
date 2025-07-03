import { useRef } from 'react'; // Import useRef from React for referencing DOM elements
import Header from '../components/App/Header/Header.tsx'; // Import the Header component
import ExpressionList from '../components/App/ExpressionList'; // Import the ExpressionList component
import GraphCanvas from '../components/App/Graph/GraphCanvas'; // Import the GraphCanvas component
import MathKeyboard from '../components/App/MathKeyboard'; // Import the MathKeyboard component
import { useCalculatorLogic } from '../hooks/Math/useCalcLogic.tsx'; // Import custom hook for calculator logic
import HistoryPanel from '../components/App/HistoryPanel'; // Import the HistoryPanel component
import styles from '../styles/modules/graphCanvas.module.css'; // Import CSS module for styling

/**
 * Calculator
 * Main functional component for the calculator page.
 * Organizes all the main UI elements: header, expression list, graph, math keyboard, and history.
 * Uses the custom hook useCalculatorLogic for all business logic/state.
 * @returns {JSX.Element} Main calculator page.
 */
export default function Calculator() {
    // Destructuring all states and handlers from custom hook for calculator logic.
    // This custom hook centralizes all logic/state for the calculator UI.
    const {
        expressions,            // Array of current expression strings
        setExpressions,         // Setter for expressions
        results,                // Array of results for each expression
        colors,                 // Array of colors for each expression
        setColors,              // Setter for colors
        disabledFlags,          // Array of booleans indicating if an expression is disabled
        focusedIndex,           // Index of the currently focused expression
        setFocusedIndex,        // Setter for focusedIndex
        caretPosition,          // Current caret position in focused expression
        setCaretPosition,       // Setter for caretPosition
        selectionLength,        // Currently selected length in focused expression
        setSelectionLength,     // Setter for selectionLength
        handleExpressionBlur,   // Handler for when an expression loses focus
        handleViewChange,       // Handler for when graph view changes
        handleColorChange,      // Handler for color change of an expression
        handleToggleDisabled,   // Handler for toggling enabled/disabled state of an expression
        handleDeleteRow,        // Handler for deleting an expression row
        insertIntoExpression,   // Handler for inserting text into an expression at the caret
        backspace,              // Handler for backspace action in the input
        clearAll,               // Handler for clearing all expressions/results/colors
        allDrawingSets          // Array of {points, color} to be rendered in the graph
    } = useCalculatorLogic();

    /**
     * Inserts a new blank row below the currently focused one.
     * This is called when the user presses the "enter/return" key on the keyboard.
     * Maintains caret and focus state after inserting.
     */
    function insertRowBelow() {
        // Do nothing if no row is focused
        if (focusedIndex === null) return;
        // Insert a blank string after the currently focused index in the expressions array
        setExpressions(prev => {
            const u = [...prev];
            u.splice(focusedIndex + 1, 0, '');
            return u;
        });
        // After the DOM updates, move the focus/caret to the new row
        setTimeout(() => {
            setFocusedIndex(focusedIndex + 1);
            setCaretPosition(0);
            setSelectionLength(0);
        }, 0);
    }

    /**
     * Reference for the actual canvas element, used for snapshotting or exporting the graph.
     * This is passed to child components that need direct access to the canvas.
     */
    const graphCanvasRef = useRef<HTMLCanvasElement>(null);

    /**
     * Refreshes expressions/colors from history panel.
     * This allows restoring a previous state from the history feature.
     * @param newExprs Array of new expressions (from history)
     * @param newColors Array of new colors (from history)
     * @param _newTypes Array of expression types (ignored here)
     */
    function refreshExpressions(newExprs: string[], newColors: string[], _newTypes: string[]) {
        setExpressions(() => [...newExprs, '']); // Always add a blank row at the end
        setColors(() => newColors);
        // If you have setTypes, you could use _newTypes here
    }

    // --- Math Functions (submenu) ---
    // Define keys for the math function keyboard. Each key has a label, an onClick handler, and a category.
    // The deltaCaret and selectLength allow intelligent caret placement after insertion.
    const mathFuncKeys = [
        { label: 'f',          onClick: () => insertIntoExpression('f()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic', tooltip: "unknown" },
        { label: 'g',          onClick: () => insertIntoExpression('g()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic' },
        { label: 'h',          onClick: () => insertIntoExpression('h()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic' },
        { label: 'x',          onClick: () => insertIntoExpression('x()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic' },
        { label: 'y',          onClick: () => insertIntoExpression('y()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic' },
        { label: 'z',          onClick: () => insertIntoExpression('z()', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Generic' },
        { label: 'gamma',      onClick: () => insertIntoExpression('gamma()', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Special' },
        { label: 'zeta',       onClick: () => insertIntoExpression('zeta()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Special'},
        { label: 'erf',        onClick: () => insertIntoExpression('erf()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Special' },
        { label: 'fresnelc',   onClick: () => insertIntoExpression('fresnelc()', { deltaCaret: 9, selectLength: 0 }), dataVirtualKey: true, category: 'Special' },
        { label: 'exp',        onClick: () => insertIntoExpression('exp()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Basic' },
        { label: 'log10',      onClick: () => insertIntoExpression('log10()', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Basic' },
        { label: 'log2',       onClick: () => insertIntoExpression('log2()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Basic' },
        { label: 'abs',        onClick: () => insertIntoExpression('abs()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Basic' },
        { label: 'sin',        onClick: () => insertIntoExpression('sin()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'cos',        onClick: () => insertIntoExpression('cos()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'tan',        onClick: () => insertIntoExpression('tan()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'csc',        onClick: () => insertIntoExpression('csc()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'cot',        onClick: () => insertIntoExpression('cot()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'sec',        onClick: () => insertIntoExpression('sec()', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: 'Trigonometric' },
        { label: 'arcsin',     onClick: () => insertIntoExpression('arcsin()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'arccos',     onClick: () => insertIntoExpression('arccos()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'arctan',     onClick: () => insertIntoExpression('arctan()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'arccsc',     onClick: () => insertIntoExpression('arccsc()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'arccot',     onClick: () => insertIntoExpression('arccot()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'arcsec',     onClick: () => insertIntoExpression('arcsec()', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Trigonometric' },
        { label: 'sinh',       onClick: () => insertIntoExpression('sinh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'cosh',       onClick: () => insertIntoExpression('cosh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'tanh',       onClick: () => insertIntoExpression('tanh()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'coth',       onClick: () => insertIntoExpression('coth()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'sech',       onClick: () => insertIntoExpression('sech()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'csch',       onClick: () => insertIntoExpression('csch()', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: 'Hyperbolic' },
        { label: 'arcsinh',    onClick: () => insertIntoExpression('arcsinh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' },
        { label: 'arccosh',    onClick: () => insertIntoExpression('arccosh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' },
        { label: 'arctanh',    onClick: () => insertIntoExpression('arctanh()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' },
        { label: 'arccoth',    onClick: () => insertIntoExpression('arccoth()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' },
        { label: 'arcsech',    onClick: () => insertIntoExpression('arcsech()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' },
        { label: 'arccsch',    onClick: () => insertIntoExpression('arccsch()', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Arc Hyperbolic' }
    ];

    // Define keys for the symbolic math keyboard (Symja) with their respective categories.
    const symjaKeys = [
        { label: 'D',          onClick: () => insertIntoExpression('D[, ]', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: "Calculus" },
        { label: 'Diff',       onClick: () => insertIntoExpression('Diff[, ]', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: "Calculus" },
        { label: 'Integrate',  onClick: () => insertIntoExpression('Integrate[, ]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Calculus" },
        { label: 'Taylor',     onClick: () => insertIntoExpression('Taylor[, ]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Calculus" },
        { label: 'Limit',      onClick: () => insertIntoExpression('Limit[, ]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Calculus" },
        { label: 'Solve',      onClick: () => insertIntoExpression('Solve[, ]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Analysis" },
        { label: 'DSolve',     onClick: () => insertIntoExpression('DSolve[, ]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Analysis" },
        { label: 'Simplify',   onClick: () => insertIntoExpression('Simplify[]', { deltaCaret: 9, selectLength: 0 }), dataVirtualKey: true, category: "Algebra" },
        { label: 'Expand',     onClick: () => insertIntoExpression('Expand[]', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: "Algebra"},
        { label: 'GCD',        onClick: () => insertIntoExpression('GCD[]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Arithmetic" },
        { label: 'LCM',        onClick: () => insertIntoExpression('LCM[]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Arithmetic" },
        { label: 'Dot',        onClick: () => insertIntoExpression('Dot[,]', { deltaCaret: 4, selectLength: 0 }), dataVirtualKey: true, category: "Vector Operations" },
        { label: 'Cross',      onClick: () => insertIntoExpression('Cross[,]', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: "Vector Operations" },
        { label: 'Norm',       onClick: () => insertIntoExpression('Norm[]', { deltaCaret: 5, selectLength: 0 }), dataVirtualKey: true, category: "Vector Operations" },
        { label: 'Normalize',  onClick: () => insertIntoExpression('Normalize[]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Vector Operations" },
        { label: 'Vectorangle',onClick: () => insertIntoExpression('Vectorangle[,]', { deltaCaret: 11, selectLength: 0 }), dataVirtualKey: true, category: "Vector Operations" },
        { label: 'Projection', onClick: () => insertIntoExpression('Projection[]', { deltaCaret: 11, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Eigenvalues',onClick: () => insertIntoExpression('Eigenvalues[]', { deltaCaret: 12, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Inverse',    onClick: () => insertIntoExpression('Inverse[]', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
        { label: 'Transpose',  onClick: () => insertIntoExpression('Transpose[]', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: "Matrices" },
    ];

    // Define keys for the constants keyboard. Each key inserts a constant.
    const constantKeys = [
        { label: 'Pi',               onClick: () => insertIntoExpression('Pi', { deltaCaret: 2, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'E',                onClick: () => insertIntoExpression('E', { deltaCaret: 1, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'I',                onClick: () => insertIntoExpression('I', { deltaCaret: 1, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Phi',              onClick: () => insertIntoExpression('Phi', { deltaCaret: 3, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Infinity',         onClick: () => insertIntoExpression('Infinity', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'ComplexInfinity',  onClick: () => insertIntoExpression('ComplexInfinity', { deltaCaret: 15, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'EulerGamma',       onClick: () => insertIntoExpression('EulerGamma', { deltaCaret: 10, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Degree',           onClick: () => insertIntoExpression('Degree', { deltaCaret: 6, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Catalan',          onClick: () => insertIntoExpression('Catalan', { deltaCaret: 7, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'MeisselMertens',   onClick: () => insertIntoExpression('MeisselMertens', { deltaCaret: 14, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Glaisher',         onClick: () => insertIntoExpression('Glaisher', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
        { label: 'Khinchin',         onClick: () => insertIntoExpression('Khinchin', { deltaCaret: 8, selectLength: 0 }), dataVirtualKey: true, category: 'Constants' },
    ];

    // Define the main calculator keyboard with numbers, operators, and common actions.
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
        { label: '↵', onClick: insertRowBelow, className: "entryKey", dataVirtualKey: true },
    ];

    // Configuration for categorizing math function keys (used in MathKeyboard component UI)
    const mathCategoryConfig = [
        { name: "Generic", columns: 2 },
        { name: "Special", columns: 4 },
        { name: "Basic", columns: 2 },
        { name: "Trigonometric", columns: 3 },
        { name: "Arc Trigonometric", columns: 3 },
        { name: "Hyperbolic", columns: 3 },
        { name: "Arc Hyperbolic", columns: 3 }
    ];
    // Configuration for categorizing symbolic math keys
    const symjaCategoryConfig = [
        { name: "Calculus", columns: 5 },
        { name: "Algebra", columns: 2 },
        { name: "Analysis", columns: 2},
        { name: "Arithmetic", columns: 2},
        { name: "Vector Operations", columns: 5},
        { name: "Matrices", columns: 2}
    ];
    // Configuration for constant keys
    const constantCategoryConfig = [
        { name: "Constants", columns: 2 },
    ];

    // Render the calculator UI
    return (
        <div className={styles.pageContainer}>
            {/* Header component with the title of the application */}
            <Header title="Aleph-Zero" />
            <div className={styles.mainArea}>
                {/* History panel for saving and restoring previous calculation states */}
                <HistoryPanel
                    expressions={expressions}
                    results={results}
                    colors={colors}
                    types={results.map((r) => r.exprType || 'UNKNOWN')}
                    graphCanvasRef={graphCanvasRef}
                    refreshExpressions={refreshExpressions}
                />
                <div className={styles.canvasWrapper}>
                    {/* Graph canvas for plotting expressions */}
                    <GraphCanvas
                        drawingSets={allDrawingSets}
                        onViewChange={handleViewChange}
                        canvasRef={graphCanvasRef}
                    />
                </div>
                <div className={styles.expressionsWrapper}>
                    {/* Expression list: main input for user expressions */}
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
                    {/* On-screen math keyboard for fast expression input */}
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
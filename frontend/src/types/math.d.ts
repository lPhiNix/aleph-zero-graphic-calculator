export type MathEvaluationType = 'EVALUATION' | 'CALCULATION' | 'DRAWING';

/**
 * Each object that comes in the "evaluations" array from the backend.
 * Represents a single type of evaluation (value, calculation, or drawing) for a math expression.
 */
export interface MathEvaluationDto {
    evaluationType: MathEvaluationType; // The type of the evaluation (EVALUATION, CALCULATION, DRAWING)
    evaluation: string; // The result string for the evaluation
    evaluationProblems?: string[]; // Optional array of warning or problem messages
}

/**
 * The structure returned by the API for each expression.
 * Contains the original expression, its type, and an array of evaluations.
 */
export interface MathExpressionEvaluationDto {
    expression: string; // The original expression sent to the backend
    type:
        | 'FUNCTION'
        | 'ASSIGNMENT'
        | 'NUMERIC'
        | 'EQUATION'
        | 'MATRIX'
        | 'VECTOR'
        | 'BOOLEAN'
        | 'UNKNOWN'
        | 'NONE'; // The type of the expression as determined by the backend
    evaluations: MathEvaluationDto[]; // Array of evaluations for this expression
}

/**
 * Wrapper for the full API response.
 * Contains the status code, message, path, and a content object with the evaluations array.
 */
export interface MathApiResponse {
    code: string; // Status code from the backend
    message: string; // Human-readable message from the backend
    path: string; // The API path that was called
    content: {
        expressionEvaluations: MathExpressionEvaluationDto[]; // Array of evaluated expressions
    };
}

/**
 * Internal type for frontend state: we store results by index.
 * This type holds the result of evaluating a math expression, including values, calculations, drawing points, errors, and warnings.
 */
export interface ExpressionResult {
    evaluation?: string; // The value/result of the evaluation
    calculation?: string; // The calculation result, if present
    drawingPoints?: Array<{ x: number; y: number }>; // Points for plotting, if present
    errors?: string[]; // Array of error messages, if any
    exprType?: MathExpressionEvaluationDto['type']; // The type of the expression
    warnings?: string[];  // New field for warnings
}
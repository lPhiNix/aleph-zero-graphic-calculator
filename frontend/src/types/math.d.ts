export type MathEvaluationType = 'EVALUATION' | 'CALCULATION' | 'DRAWING';

// Cada objeto que viene en el array "evaluations" del backend
export interface MathEvaluationDto {
    evaluationType: MathEvaluationType;
    evaluation: string;
    evaluationProblems?: string[];
}

// Lo que te envía el API por cada expresión
export interface MathExpressionEvaluationDto {
    expression: string;
    type:
        | 'FUNCTION'
        | 'ASSIGNMENT'
        | 'NUMERIC'
        | 'EQUATION'
        | 'MATRIX'
        | 'VECTOR'
        | 'BOOLEAN'
        | 'UNKNOWN'
        | 'NONE';
    evaluations: MathEvaluationDto[];
}

// Wrapper de la respuesta completa
export interface MathApiResponse {
    code: string;
    message: string;
    path: string;
    content: {
        expressionEvaluations: MathExpressionEvaluationDto[];
    };
}

// Tipo interno para estado en el frontend: guardaremos resultados por índice
export interface ExpressionResult {
    evaluation?: string;
    calculation?: string;
    // Si viene DRAWING, parsearemos a puntos
    drawingPoints?: Array<{ x: number; y: number }>;
    errors?: string[];
    /** Nuevo campo: el tipo de expresión que devolvió el backend */
    exprType?: MathExpressionEvaluationDto['type'];
}

import AxiosConfig from './axiosService.ts'; // Adjust the path according to your structure. Import the Axios singleton config.
import axios, { AxiosError } from 'axios'; // Import axios and AxiosError for HTTP requests and error handling.

const axiosInstance = AxiosConfig.getInstance(); // Get the singleton Axios instance for reuse.

/**
 * Interface for the shape of the evaluation request payload.
 */
interface EvaluationRequest {
    expressions: Array<{ expression: string }>; // Array of expressions to evaluate
    data: {
        decimals: string; // Number of decimal places as a string
        origin: string;   // Origin of the graph/view window as a string
        bound: string;    // Bound of the graph/view window as a string
    };
}

/**
 * Extracts only the { x, y } pairs from Line({{…}}) blocks in a raw string.
 * Used to parse drawing points from the math backend response.
 * @param {string} raw - Raw string containing Line blocks.
 * @returns {Array<{x: number, y: number}>} Array of parsed points.
 */
function parseDrawingPoints(raw: string): Array<{ x: number; y: number }> {
    const points: Array<{ x: number; y: number }> = [];
    const lineRe = /Line\s*\(\s*\{\{([\s\S]*?)}}\s*\)/g; // Regex to match Line blocks
    let lineMatch: RegExpExecArray | null;

    while ((lineMatch = lineRe.exec(raw)) !== null) {
        const innerContent = lineMatch[1]; // Content between double curly braces
        const toParse = '{' + innerContent + '}';
        const pointRe = /\{\s*([\-0-9.eE]+)\s*,\s*([\-0-9.eE]+)\s*\}/g; // Regex to match {x, y} pairs
        let match: RegExpExecArray | null;
        while ((match = pointRe.exec(toParse)) !== null) {
            const x = parseFloat(match[1]);
            const y = parseFloat(match[2]);
            if (!isNaN(x) && !isNaN(y)) {
                points.push({ x, y }); // Only push valid numeric points
            }
        }
    }

    return points;
}

/**
 * Evaluates a single mathematical expression by sending it to the backend.
 * Handles parsing and mapping the response, including drawing points.
 * @param {string} expr - The expression to evaluate.
 * @param {string} decimals - Number of decimals to use in calculation.
 * @param {string} origin - Graph or calculation origin.
 * @param {string} bound - Graph or calculation bound.
 * @returns {Promise<ExpressionResult>} The evaluation result object.
 */
async function evaluateSingleExpression(
    expr: string,
    decimals: string,
    origin: string,
    bound: string
): Promise<ExpressionResult> {
    // Prepare the payload for the API request
    const payload: EvaluationRequest = {
        expressions: [{ expression: expr }],
        data: { decimals, origin, bound },
    };

    try {
        // Send request to the backend API (route is relative, baseURL set in AxiosConfig)
        const response = await axiosInstance.post<MathApiResponse>(
            '/api/v1/math/evaluation',
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );
        // Extract the first evaluation DTO from the API response
        const evalDto: MathExpressionEvaluationDto =
            response.data.content.expressionEvaluations[0];

        // Initialize the result object
        const result: ExpressionResult = {
            exprType: evalDto.type,
            warnings: []
        };

        // Map the different evaluation types into the result object
        for (const item of evalDto.evaluations) {
            switch (item.evaluationType) {
                case 'EVALUATION':
                    result.evaluation = item.evaluation;
                    break;
                case 'CALCULATION':
                    result.calculation = item.evaluation;
                    break;
                case 'DRAWING':
                    result.drawingPoints = parseDrawingPoints(item.evaluation);
                    break;
            }

            // Collect warning messages if present
            if (item.evaluationProblems?.length) {
                result.warnings = [...(result.warnings ?? []), ...item.evaluationProblems];
            }
        }
        return result;
    } catch (error) {
        // If the error is from Axios, try to extract error messages from the response
        if (axios.isAxiosError(error)) {
            const data = (error as AxiosError).response?.data as any;
            if (Array.isArray(data?.errors)) {
                return {
                    errors: data.errors.map((e: any) => e.message || String(e))
                };
            }
        }
        // Return a generic error message in English if the above fails
        return { errors: ['Unexpected error communicating with the server'] };
    }
}

/**
 * Sends multiple expressions in a single request.
 * Returns an array of ExpressionResult in the same order as the expressions.
 * @param {string[]} exprs - Array of expressions to evaluate.
 * @param {string} decimals - Number of decimals to use in calculation.
 * @param {string} origin - Graph or calculation origin.
 * @param {string} bound - Graph or calculation bound.
 * @returns {Promise<ExpressionResult[]>} Array of results.
 */
export async function evaluateBatchExpressions(
    exprs: string[],
    decimals: string,
    origin: string,
    bound: string
): Promise<ExpressionResult[]> {
    // Prepare the payload for the API request
    const payload: EvaluationRequest = {
        expressions: exprs.map((e) => ({ expression: e })),
        data: { decimals, origin, bound },
    };

    try {
        // Send the batch evaluation request to the backend
        const response = await axiosInstance.post<MathApiResponse>(
            '/api/v1/math/evaluation',
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );
        // Extract the DTOs from the API response
        const dtos = response.data.content.expressionEvaluations;

        // Map each DTO to an ExpressionResult object
        return dtos.map((evalDto) => {
            const result: ExpressionResult = {
                exprType: evalDto.type,
                warnings: []
            };

            for (const item of evalDto.evaluations) {
                switch (item.evaluationType) {
                    case 'EVALUATION':
                        result.evaluation = item.evaluation;
                        break;
                    case 'CALCULATION':
                        result.calculation = item.evaluation;
                        break;
                    case 'DRAWING':
                        result.drawingPoints = parseDrawingPoints(item.evaluation);
                        break;
                }

                if (item.evaluationProblems?.length) {
                    result.warnings = [...(result.warnings ?? []), ...item.evaluationProblems];
                }
            }
            return result;
        });
    } catch (error) {
        // Default generic error message in English
        let commonError: string[] = ['Unexpected error communicating with the server'];
        // If Axios error, extract error messages if present
        if (axios.isAxiosError(error)) {
            const data = (error as AxiosError).response?.data as any;
            if (Array.isArray(data?.errors)) {
                commonError = data.errors.map((e: any) => e.message || String(e));
            }
        }
        // Return the error for each requested expression
        return exprs.map(() => ({
            errors: commonError,
            warnings: []
        }));
    }
}

// Export the single-expression evaluation function
export { evaluateSingleExpression };

/**
 * Types of evaluation supported by the backend and DTOs.
 */
export type MathEvaluationType = 'EVALUATION' | 'CALCULATION' | 'DRAWING';

/**
 * DTO for a single evaluation (result, calculation, or drawing).
 */
export interface MathEvaluationDto {
    evaluationType: MathEvaluationType; // Type of evaluation
    evaluation: string; // The result string or drawing string
    evaluationProblems?: string[]; // Optional warnings/problems
}

/**
 * DTO for a complete evaluated expression.
 */
export interface MathExpressionEvaluationDto {
    expression: string; // The input expression string
    type:
        | 'FUNCTION'
        | 'ASSIGNMENT'
        | 'NUMERIC'
        | 'EQUATION'
        | 'MATRIX'
        | 'VECTOR'
        | 'BOOLEAN'
        | 'UNKNOWN'
        | 'NONE'; // The type of the expression evaluated
    evaluations: MathEvaluationDto[]; // Array of evaluation results
}

/**
 * DTO for the overall batch API response.
 */
export interface MathApiResponse {
    code: string; // Status or error code
    message: string; // Human-readable message
    path: string; // API path
    content: {
        expressionEvaluations: MathExpressionEvaluationDto[]; // Array of evaluated expressions
    };
}

/**
 * Type describing the result of an evaluated expression.
 */
export interface ExpressionResult {
    evaluation?: string; // The normal evaluation result
    calculation?: string; // The calculation result (if any)
    drawingPoints?: Array<{ x: number; y: number }>; // Array of drawing points extracted
    errors?: string[]; // Any error messages from evaluation
    exprType?: MathExpressionEvaluationDto['type']; // The type of the expression
    warnings?: string[]; // Any warning messages from evaluation
}
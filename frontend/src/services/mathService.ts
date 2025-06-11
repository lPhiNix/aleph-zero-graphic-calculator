// src/services/mathService.ts
import AxiosConfig from './axiosService.ts'; // Ajusta la ruta según tu estructura
import axios, { AxiosError } from 'axios';

const axiosInstance = AxiosConfig.getInstance();

interface EvaluationRequest {
    expressions: Array<{ expression: string }>;
    data: {
        decimals: string;
        origin: string;
        bound: string;
    };
}

/**
 * Extrae únicamente los pares { x, y } de los bloques Line({{…}})
 */
function parseDrawingPoints(raw: string): Array<{ x: number; y: number }> {
    const points: Array<{ x: number; y: number }> = [];
    const lineRe = /Line\s*\(\s*\{\{([\s\S]*?)}}\s*\)/g;
    let lineMatch: RegExpExecArray | null;

    while ((lineMatch = lineRe.exec(raw)) !== null) {
        const innerContent = lineMatch[1];
        const toParse = '{' + innerContent + '}';
        const pointRe = /\{\s*([\-0-9.eE]+)\s*,\s*([\-0-9.eE]+)\s*\}/g;
        let match: RegExpExecArray | null;
        while ((match = pointRe.exec(toParse)) !== null) {
            const x = parseFloat(match[1]);
            const y = parseFloat(match[2]);
            if (!isNaN(x) && !isNaN(y)) {
                points.push({ x, y });
            }
        }
    }

    return points;
}

async function evaluateSingleExpression(
    expr: string,
    decimals: string,
    origin: string,
    bound: string
): Promise<ExpressionResult> {
    const payload: EvaluationRequest = {
        expressions: [{ expression: expr }],
        data: { decimals, origin, bound },
    };

    try {
        const response = await axiosInstance.post<MathApiResponse>(
            '/api/v1/math/evaluation', // mejor usar rutas relativas con baseURL en AxiosConfig
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );
        const evalDto: MathExpressionEvaluationDto =
            response.data.content.expressionEvaluations[0];

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
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const data = (error as AxiosError).response?.data as any;
            if (Array.isArray(data?.errors)) {
                return {
                    errors: data.errors.map((e: any) => e.message || String(e))
                };
            }
        }
        return { errors: ['Error inesperado al comunicarse con el servidor'] };
    }
}

/**
 * Envía varias expresiones en un solo request.
 * Devuelve un array de ExpressionResult en el mismo orden.
 */
export async function evaluateBatchExpressions(
    exprs: string[],
    decimals: string,
    origin: string,
    bound: string
): Promise<ExpressionResult[]> {
    const payload: EvaluationRequest = {
        expressions: exprs.map((e) => ({ expression: e })),
        data: { decimals, origin, bound },
    };

    try {
        const response = await axiosInstance.post<MathApiResponse>(
            '/api/v1/math/evaluation',
            payload,
            { headers: { 'Content-Type': 'application/json' } }
        );
        const dtos = response.data.content.expressionEvaluations;

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
        let commonError: string[] = ['Error inesperado al comunicarse con el servidor'];
        if (axios.isAxiosError(error)) {
            const data = (error as AxiosError).response?.data as any;
            if (Array.isArray(data?.errors)) {
                commonError = data.errors.map((e: any) => e.message || String(e));
            }
        }
        return exprs.map(() => ({
            errors: commonError,
            warnings: []
        }));
    }
}

export { evaluateSingleExpression };

export type MathEvaluationType = 'EVALUATION' | 'CALCULATION' | 'DRAWING';

export interface MathEvaluationDto {
    evaluationType: MathEvaluationType;
    evaluation: string;
    evaluationProblems?: string[];
}

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

export interface MathApiResponse {
    code: string;
    message: string;
    path: string;
    content: {
        expressionEvaluations: MathExpressionEvaluationDto[];
    };
}

export interface ExpressionResult {
    evaluation?: string;
    calculation?: string;
    drawingPoints?: Array<{ x: number; y: number }>;
    errors?: string[];
    exprType?: MathExpressionEvaluationDto['type'];
    warnings?: string[];
}

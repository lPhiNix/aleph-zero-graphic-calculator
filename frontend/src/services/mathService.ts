import axios, { AxiosError } from 'axios';
import type {
    MathApiResponse,
    MathExpressionEvaluationDto,
    ExpressionResult,
} from '../types/math';

interface EvaluationRequest {
    expressions: Array<{ expression: string }>;
    data: {
        decimals: string;
        origin: string;
        bound: string;
    };
}

/**
 * Convierte la cadena de puntos devuelta por el backend,
 * pero solo extrae las coordenadas que aparecen dentro de los bloques Line({{…}}).
 * Por ejemplo, dado:
 *
 *   "…RGBColor(…),Line({{0,1},{2,3},…}),RGBColor(…),Line({{5,6},{7,8},…})…"
 *
 * Esta función devolverá únicamente los pares:
 *   [{ x: 0, y: 1 }, { x: 2, y: 3 }, …, { x: 5, y: 6 }, { x: 7, y: 8 }, …]
 */
function parseDrawingPoints(raw: string): Array<{ x: number; y: number }> {
    const points: Array<{ x: number; y: number }> = [];

    // 1) Regex para capturar el contenido interno de cada Line({{…}})
    //    - El grupo 1 (match[1]) contendrá algo como "x1,y1},{x2,y2},…"
    const lineRe = /Line\s*\(\s*\{\{([\s\S]*?)}}\s*\)/g;

    let lineMatch: RegExpExecArray | null;
    while ((lineMatch = lineRe.exec(raw)) !== null) {
        const innerContent = lineMatch[1];

        // 2) Reconstruimos un string que contenga únicamente "{x,y},{x,y},…"
        const toParse = '{' + innerContent + '}';

        // 3) Regex para capturar cada par { x, y }
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

export async function evaluateSingleExpression(
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
        const response = await axios.post<MathApiResponse>(
            'http://localhost:8080/api/v1/math/evaluation',
            payload,
            {
                headers: { 'Content-Type': 'application/json' },
            }
        );

        const apiData = response.data;

        const evalDto: MathExpressionEvaluationDto =
            apiData.content.expressionEvaluations[0];

        // Creamos el resultado incluyendo el tipo de expresión
        const result: ExpressionResult = {
            exprType: evalDto.type,
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
                    // Ahora solo se extraen puntos de Line({{…}})
                    result.drawingPoints = parseDrawingPoints(item.evaluation);
                    break;
                default:
                    break;
            }
            if (item.evaluationProblems && item.evaluationProblems.length > 0) {
                if (!result.errors) result.errors = [];
                result.errors.push(...item.evaluationProblems);
            }
        }

        return result;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            const axiosErr = error as AxiosError;
            if (axiosErr.response && axiosErr.response.data) {
                const data = axiosErr.response.data as any;
                if (Array.isArray(data.errors)) {
                    const messages: string[] = data.errors.map(
                        (e: any) => e.message || String(e)
                    );
                    return { errors: messages };
                }
            }
        }
        return { errors: ['Error inesperado al comunicarse con el servidor'] };
    }
}

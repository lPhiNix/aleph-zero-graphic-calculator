// src/services/mathService.ts
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
 * p. ej. "[{-10.0, 0.544021}, { -9.7, 0.3165}, …]" en
 * Array<{x: number; y: number}>.
 */
function parseDrawingPoints(raw: string): Array<{ x: number; y: number }> {
    console.log('[parseDrawingPoints] Raw input:', raw);
    const points: Array<{ x: number; y: number }> = [];
    const re = /\{\s*([\-0-9.eE]+)\s*,\s*([\-0-9.eE]+)\s*\}/g;
    let match: RegExpExecArray | null;
    while ((match = re.exec(raw)) !== null) {
        console.log('[parseDrawingPoints] Match encontrado:', match);
        const x = parseFloat(match[1]);
        const y = parseFloat(match[2]);
        console.log(`[parseDrawingPoints] Parsed x: ${x}, y: ${y}`);
        if (!isNaN(x) && !isNaN(y)) {
            points.push({ x, y });
        } else {
            console.warn('[parseDrawingPoints] Ignorado punto inválido:', match[0]);
        }
    }
    console.log('[parseDrawingPoints] Puntos resultantes:', points);
    return points;
}

export async function evaluateSingleExpression(
    expr: string,
    decimals: string,
    origin: string,
    bound: string
): Promise<ExpressionResult> {
    console.log('[evaluateSingleExpression] Expresión:', expr);
    console.log('[evaluateSingleExpression] Parámetros:', { decimals, origin, bound });

    const payload: EvaluationRequest = {
        expressions: [{ expression: expr }],
        data: { decimals, origin, bound },
    };

    console.log('[evaluateSingleExpression] Payload preparado:', payload);

    try {
        const response = await axios.post<MathApiResponse>(
            'http://localhost:8080/api/v1/math/evaluation',
            payload,
            {
                headers: { 'Content-Type': 'application/json' },
            }
        );

        console.log('[evaluateSingleExpression] Respuesta recibida:', response.data);

        const apiData = response.data;

        if (
            apiData.content.expressionEvaluations.length === 0 ||
            apiData.content.expressionEvaluations[0].evaluations.length === 0
        ) {
            console.warn('[evaluateSingleExpression] No se devolvieron evaluaciones');
            return { errors: ['No se devolvieron evaluaciones para la expresión'] };
        }

        const evalDto: MathExpressionEvaluationDto =
            apiData.content.expressionEvaluations[0];
        console.log('[evaluateSingleExpression] Evaluación DTO:', evalDto);

        const result: ExpressionResult = {};

        for (const item of evalDto.evaluations) {
            console.log('[evaluateSingleExpression] Procesando evaluación:', item);
            switch (item.evaluationType) {
                case 'EVALUATION':
                    result.evaluation = item.evaluation;
                    console.log('[evaluateSingleExpression] Resultado de EVALUATION:', item.evaluation);
                    break;
                case 'CALCULATION':
                    result.calculation = item.evaluation;
                    console.log('[evaluateSingleExpression] Resultado de CALCULATION:', item.evaluation);
                    break;
                case 'DRAWING':
                    result.drawingPoints = parseDrawingPoints(item.evaluation);
                    console.log('[evaluateSingleExpression] Resultado de DRAWING:', result.drawingPoints);
                    break;
                default:
                    console.warn('[evaluateSingleExpression] Tipo de evaluación desconocido:', item.evaluationType);
                    break;
            }
            if (item.evaluationProblems && item.evaluationProblems.length > 0) {
                if (!result.errors) result.errors = [];
                result.errors.push(...item.evaluationProblems);
                console.warn('[evaluateSingleExpression] Problemas detectados:', item.evaluationProblems);
            }
        }

        console.log('[evaluateSingleExpression] Resultado final:', result);
        return result;

    } catch (error) {
        console.error('[evaluateSingleExpression] Error capturado:', error);

        if (axios.isAxiosError(error)) {
            const axiosErr = error as AxiosError;
            if (axiosErr.response && axiosErr.response.data) {
                console.warn('[evaluateSingleExpression] Error de respuesta del servidor:', axiosErr.response.data);
                const data = axiosErr.response.data as any;
                if (Array.isArray(data.errors)) {
                    const messages: string[] = data.errors.map((e: any) => e.message || String(e));
                    console.warn('[evaluateSingleExpression] Mensajes de error del backend:', messages);
                    return { errors: messages };
                }
            }
        }

        console.error('[evaluateSingleExpression] Error inesperado');
        return { errors: ['Error inesperado al comunicarse con el servidor'] };
    }
}

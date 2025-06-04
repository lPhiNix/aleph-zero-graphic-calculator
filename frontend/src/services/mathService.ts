// src/services/mathService.ts
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
    const points: Array<{ x: number; y: number }> = [];
    // Regex para capturar cada "{numero, numero}"
    const re = /\{\s*([\-0-9.eE]+)\s*,\s*([\-0-9.eE]+)\s*\}/g;
    let match: RegExpExecArray | null;
    while ((match = re.exec(raw)) !== null) {
        const x = parseFloat(match[1]);
        const y = parseFloat(match[2]);
        if (!isNaN(x) && !isNaN(y)) {
            points.push({ x, y });
        }
    }
    return points;
}

/**
 * Llama a POST /api/v1/math/evaluation con una sola expresión.
 * Devuelve un objeto ExpressionResult para esa única expresión,
 * o un array de errores si los hubiera.
 */
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

    const response = await fetch('/api/v1/math/evaluation', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        // Si recibimos un 4xx/5xx, intentamos leer el JSON de errores
        const errJson = await response.json().catch(() => null);
        if (errJson && errJson.errors) {
            // Por ejemplo: validación fallida o timeout
            const errors: string[] = (errJson.errors as any[]).map((e) => e.message);
            return { errors };
        }
        return { errors: ['Error inesperado al comunicarse con el servidor'] };
    }

    const apiData: MathApiResponse = await response.json();
    if (
        apiData.content.expressionEvaluations.length === 0 ||
        apiData.content.expressionEvaluations[0].evaluations.length === 0
    ) {
        // no devolvió nada
        return { errors: ['No se devolvieron evaluaciones para la expresión'] };
    }

    const evalDto: MathExpressionEvaluationDto =
        apiData.content.expressionEvaluations[0];
    const result: ExpressionResult = {};

    for (const item of evalDto.evaluations) {
        switch (item.evaluationType) {
            case 'EVALUATION':
                result.evaluation = item.evaluation;
                break;
            case 'CALCULATION':
                result.calculation = item.evaluation;
                break;
            case 'DRAWING':
                // "item.evaluation" es un string tipo "[{x1,y1},{x2,y2},…]"
                result.drawingPoints = parseDrawingPoints(item.evaluation);
                break;
        }
        // Si venían warning/errores en item.evaluationProblems, los acumulamos:
        if (item.evaluationProblems && item.evaluationProblems.length > 0) {
            if (!result.errors) result.errors = [];
            result.errors.push(...item.evaluationProblems);
        }
    }

    return result;
}

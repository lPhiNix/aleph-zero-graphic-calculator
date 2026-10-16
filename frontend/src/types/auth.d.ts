/**
 * Types for the token-based authentication API (refresh token + access token).
 */

/** Pair of tokens returned by the authentication endpoints. */
export interface AuthTokens {
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
}

/** A single validation/detail entry returned by the backend error response. */
export interface ApiValidationErrorDetail {
    category?: string;
    field?: string;
    message?: string;
    rejectedValue?: unknown;
}

/** Standard backend error body ({@code ErrorResponse}). */
export interface ApiErrorResponse {
    appCode?: string;
    title?: string;
    errors?: ApiValidationErrorDetail[];
}

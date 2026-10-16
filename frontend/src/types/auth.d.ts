/**
 * Types for the token-based authentication API (refresh token in HttpOnly cookie
 * + short-lived access token).
 */

/** Response body returned by the login/refresh endpoints. */
export interface AuthResponse {
    accessToken: string;
    tokenType: string;
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

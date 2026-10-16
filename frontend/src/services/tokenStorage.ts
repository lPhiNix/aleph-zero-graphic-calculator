/**
 * Token storage helpers.
 *
 * Centralises where the access/refresh tokens live so the rest of the app does
 * not depend on the concrete storage mechanism. Tokens are kept in
 * sessionStorage for the duration of the browser session.
 */

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";

/** Returns the stored access token, or {@code null} if there is none. */
export function getAccessToken(): string | null {
    return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

/** Returns the stored refresh token, or {@code null} if there is none. */
export function getRefreshToken(): string | null {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY);
}

/**
 * Stores the token pair. The refresh token is optional so callers that only
 * receive an access token (e.g. after a refresh rotation) can keep the previous
 * one when the backend does not send a new one.
 */
export function setTokens(accessToken: string, refreshToken?: string): void {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    if (refreshToken) {
        sessionStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
}

/** Removes every stored token. */
export function clearTokens(): void {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
}

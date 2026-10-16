/**
 * Access token storage.
 *
 * Only the short-lived access token is kept on the client. The refresh token is
 * never accessible to JavaScript: it lives in an HttpOnly cookie managed by the
 * backend, so there is nothing to store or clear for it here.
 */

const ACCESS_TOKEN_KEY = "access_token";

/** Returns the stored access token, or {@code null} if there is none. */
export function getAccessToken(): string | null {
    return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

/** Stores the access token. */
export function setAccessToken(accessToken: string): void {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
}

/** Removes the stored access token. */
export function clearTokens(): void {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
}

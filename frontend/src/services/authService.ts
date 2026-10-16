import { isAxiosError } from "axios";
import AxiosConfig from "./axiosService";
import { clearTokens, setAccessToken } from "./tokenStorage";
import type { ApiErrorResponse, AuthResponse } from "../types/auth";
import type { LoginFormData } from "../types/login";
import type { RegisterFormData } from "../types/register";

/** Parsed representation of an authentication failure. */
export interface AuthError {
    /** Human readable message suitable to show above the form. */
    message: string;
    /** Per-field messages keyed by the field name returned by the backend. */
    fieldErrors: Record<string, string>;
}

function isErrorResponse(data: unknown): data is ApiErrorResponse {
    return typeof data === "object" && data !== null &&
        ("appCode" in data || "title" in data || "errors" in data);
}

/**
 * Converts an unknown thrown value (usually an Axios error wrapping the backend
 * {@code ErrorResponse}) into an {@link AuthError}, extracting the per-field
 * validation messages when they are present.
 */
export function parseAuthError(error: unknown): AuthError {
    if (isAxiosError(error)) {
        const data = error.response?.data;
        if (isErrorResponse(data)) {
            const fieldErrors: Record<string, string> = {};
            data.errors?.forEach((detail) => {
                if (detail.field) {
                    fieldErrors[detail.field] = detail.message ?? "Invalid value";
                }
            });
            return {
                message: data.title ?? error.message,
                fieldErrors,
            };
        }
        return { message: error.message, fieldErrors: {} };
    }

    return {
        message: error instanceof Error ? error.message : "Unexpected error",
        fieldErrors: {},
    };
}

/**
 * Authenticates the user. The refresh token is set by the backend in an HttpOnly
 * cookie (not readable here); only the access token is stored locally.
 */
export async function login(data: LoginFormData): Promise<void> {
    const axios = AxiosConfig.getInstance();
    const res = await axios.post<AuthResponse>("/api/v1/auth/login", data);
    setAccessToken(res.data.accessToken);
}

/** Registers a new user. */
export async function register(data: RegisterFormData): Promise<void> {
    const axios = AxiosConfig.getInstance();
    await axios.post("/api/v1/auth/register", data);
}

/**
 * Revokes the refresh token on the backend (clearing the HttpOnly cookie) and
 * clears the locally stored access token.
 */
export async function logout(): Promise<void> {
    try {
        const axios = AxiosConfig.getInstance();
        await axios.post("/api/v1/auth/logout");
    } catch {
        // Ignore network failures: the local session is cleared anyway.
    } finally {
        clearTokens();
    }
}

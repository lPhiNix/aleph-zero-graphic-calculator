import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from "axios";
import type { AuthResponse } from "../types/auth";
import { clearTokens, getAccessToken, setAccessToken } from "./tokenStorage";

/** Request config with a flag to avoid retrying the same request twice. */
interface RetryableRequestConfig extends InternalAxiosRequestConfig {
    _retry?: boolean;
}

/** Auth endpoints must not trigger the automatic refresh/retry logic. */
const AUTH_ENDPOINT_PREFIX = "/api/v1/auth/";

/**
 * Resolves the backend base URL.
 * <p>
 * In production the Java application also serves the React build (single
 * binary), so backend and frontend share the same origin: the API base URL is
 * empty and requests are relative. In development {@code VITE_BACKEND_URL}
 * points at the backend origin.
 *
 * @returns the configured base URL (possibly empty for same-origin)
 */
export function getApiBaseUrl(): string {
	const configured = window.ENV?.VITE_BACKEND_URL;
	// Use the configured value even when it is an empty string (same-origin);
	// only fall back to the dev default when it is undefined.
	return configured !== undefined ? configured : "http://localhost:8080";
}

/**
 * AxiosConfig
 * Singleton class for configuring an Axios HTTP client instance.
 * This ensures all requests share the same configuration and interceptors.
 */
class AxiosConfig {
	// Static property to hold the singleton instance
	private static instance: AxiosInstance;
	// In-flight refresh, shared so concurrent 401s only refresh once.
	private static refreshPromise: Promise<string> | null = null;

	/**
	 * Requests a new access token. The refresh token lives in an HttpOnly cookie,
	 * so only credentials need to be included. A bare axios call is used to avoid
	 * the interceptors (and any recursion).
	 * @param baseURL the backend base URL.
	 * @returns the new access token.
	 */
	private static async requestNewAccessToken(baseURL: string): Promise<string> {
		const { data } = await axios.post<AuthResponse>(
			`${baseURL}/api/v1/auth/refresh`,
			{},
			{ withCredentials: true },
		);

		if (!data?.accessToken) {
			throw new Error("Refresh did not return an access token");
		}

		setAccessToken(data.accessToken);
		return data.accessToken;
	}

	/**
	 * Returns the singleton Axios instance, creating it if it does not exist.
	 * Configures base URL, timeout, headers, and request/response interceptors.
	 * @returns {AxiosInstance} The configured Axios instance.
	 */
	public static getInstance(): AxiosInstance {
		// If the instance does not exist, create a new one with default config
		if (!AxiosConfig.instance) {
			const baseURL = getApiBaseUrl();

			AxiosConfig.instance = axios.create({
				baseURL,
				timeout: 5000, // Timeout in milliseconds for requests
				withCredentials: true, // send/receive the HttpOnly refresh cookie
				headers: {
					"Content-Type": "application/json", // Default content-type for requests
				},
			});

			// Attach the access token (if any) as a Bearer header.
			AxiosConfig.instance.interceptors.request.use((config) => {
				const token = getAccessToken();
				if (token) {
					config.headers["Authorization"] = `Bearer ${token}`;
				}
				return config;
			});

			/**
			 * Response interceptor.
			 * - On 401 for a non-auth endpoint: refresh the access token once (shared
			 *   across concurrent requests) and replay the original request, without
			 *   reloading the page.
			 * - If the refresh fails: clear the session and redirect to /login.
			 * - Auth endpoints (login/register/refresh/logout) are never retried here,
			 *   so their errors propagate to the caller.
			 */
			AxiosConfig.instance.interceptors.response.use(
				(response) => response, // Pass through successful responses
				async (error: AxiosError) => {
					const original = error.config as RetryableRequestConfig | undefined;
					const status = error.response?.status;
					const url = original?.url ?? "";
					const isAuthEndpoint = url.includes(AUTH_ENDPOINT_PREFIX);

					if (status === 401 && original && !original._retry && !isAuthEndpoint) {
						original._retry = true;
						try {
							if (!AxiosConfig.refreshPromise) {
								AxiosConfig.refreshPromise = AxiosConfig
									.requestNewAccessToken(baseURL)
									.finally(() => {
										AxiosConfig.refreshPromise = null;
									});
							}

							const newToken = await AxiosConfig.refreshPromise;
							original.headers.set("Authorization", `Bearer ${newToken}`);
							return AxiosConfig.instance(original);
						} catch (refreshError) {
							clearTokens();
							window.location.href = "/login";
							return Promise.reject(refreshError);
						}
					}

					return Promise.reject(error); // Reject so calling code can handle it too
				},
			);
		}
		return AxiosConfig.instance;
	}
}

// Export the AxiosConfig class as default for use throughout the app
export default AxiosConfig;

import axios, { AxiosError, AxiosInstance } from 'axios'; // Import axios and typings for error and instance

/**
 * AxiosConfig
 * Singleton class for configuring an Axios HTTP client instance.
 * This ensures all requests share the same configuration and interceptors.
 */
class AxiosConfig {
    // Static property to hold the singleton instance
    private static instance: AxiosInstance;

    /**
     * Returns the singleton Axios instance, creating it if it does not exist.
     * Configures base URL, timeout, headers, and request/response interceptors.
     * @returns {AxiosInstance} The configured Axios instance.
     */
    public static getInstance(): AxiosInstance {
        // If the instance does not exist, create a new one with default config
        if (!AxiosConfig.instance) {
            AxiosConfig.instance = axios.create({
                baseURL: window.ENV?.VITE_BACKEND_URL || "http://localhost:8080", // Use ENV backend URL or fallback to localhost
                timeout: 5000, // Timeout in milliseconds for requests
                headers: {
                    'Content-Type': 'application/json' // Default content-type for requests
                },
            });
        }

        // Attach a request interceptor to add Authorization header if access_token is present in sessionStorage
        AxiosConfig.instance.interceptors.request.use((config) => {
            const token = sessionStorage.getItem('access_token');
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`; // Attach Bearer token for authentication
            }
            return config;
        });

        /**
         * Attach a response interceptor for error handling.
         * - If response is 401 (Unauthorized), always redirect to /login.
         * - If response is 403 (Forbidden) and there is no token, redirect to /login.
         * - If response is 403 but there is a token, do not redirect (let frontend handle it).
         * - Always reject the error so calling code can handle it as well.
         */
        AxiosConfig.instance.interceptors.response.use(
            (response) => response, // Pass through successful responses
            (error: AxiosError) => {
                if (error.response) {
                    const { status } = error.response;
                    const token = sessionStorage.getItem('access_token');

                    if (status === 401) {
                        // Not authenticated → always redirect
                        window.location.href = '/login';
                    } else if (status === 403 && (!token || token.trim() === '')) {
                        // Forbidden without token → redirect
                        window.location.href = '/login';
                    }
                    // If 403 but a token exists → do not redirect (frontend may handle this)
                }

                return Promise.reject(error); // Reject the promise so errors propagate to calling code
            }
        );
        return AxiosConfig.instance;
    }
}

// Export the AxiosConfig class as default for use throughout the app
export default AxiosConfig;
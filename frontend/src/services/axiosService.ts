import axios, {AxiosError, AxiosInstance} from 'axios';

class AxiosConfig {
    private static instance: AxiosInstance;

    // Get singleton instance
    public static getInstance(): AxiosInstance {
        if (!AxiosConfig.instance) {
            AxiosConfig.instance = axios.create({
                baseURL: import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080',
                timeout: 5000, // Timeout ms
                headers: {
                    'Content-Type': 'application/json'
                },
            });
        }

        AxiosConfig.instance.interceptors.request.use((config) => {
            const token = sessionStorage.getItem('access_token');
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
            }
            return config;
        });

        AxiosConfig.instance.interceptors.response.use(
            (response) => response,
            (error: AxiosError) => {
                if (error.response) {
                    const { status } = error.response;
                    const token = sessionStorage.getItem('access_token');

                    if (status === 401) {
                        // No autenticado → redirige siempre
                        window.location.href = '/login';
                    } else if (status === 403 && (!token || token.trim() === '')) {
                        // Prohibido sin token → redirige
                        window.location.href = '/login';
                    }
                    // Si es 403 pero hay token → no redirige (deja que el frontend lo maneje)
                }

                return Promise.reject(error);
            }
        );
        return AxiosConfig.instance;
    }
}

export default AxiosConfig;

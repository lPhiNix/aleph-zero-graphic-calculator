interface AppEnv {
    VITE_BACKEND_URL: string;
    VITE_FRONTEND_URL: string;
}

interface Window {
    ENV: AppEnv;
}

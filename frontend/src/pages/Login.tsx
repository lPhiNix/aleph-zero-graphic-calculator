import * as React from "react";
import { useEffect } from "react";
import pkceChallenge from "pkce-challenge";
import { useNavigate } from "react-router-dom";

const Login: React.FC = () => {
    const navigate = useNavigate();

    function isTokenExpired(token: string) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            // exp está en segundos, Date.now() en milisegundos
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            return true; // Si falla la decodificación, lo consideramos caducado
        }
    }

    useEffect(() => {
        const token = sessionStorage.getItem("access_token");
        if (token && !isTokenExpired(token)) {
            // Solo redirige si el token existe y NO está caducado
            navigate("/calculator", { replace: true });
            return;
        }

        async function generatePkce() {
            const { code_verifier, code_challenge } = await pkceChallenge();

            sessionStorage.setItem("code_verifier", code_verifier);
            sessionStorage.setItem("code_challenge", code_challenge);

            const clientId = "react-client";

            const backendUrl = window.ENV?.VITE_BACKEND_URL || "http://localhost:8080";
            const frontendUrl = window.ENV?.VITE_FRONTEND_URL || "http://localhost:5173";

            const redirectUri = encodeURIComponent(`${frontendUrl}/oauth2/callback`);
            const scope = encodeURIComponent("openid read");
            const responseType = "code";
            const codeChallengeMethod = "S256";

            window.location.href = `${backendUrl}/oauth2/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&code_challenge=${code_challenge}&code_challenge_method=${codeChallengeMethod}`;
        }

        generatePkce();
    }, [navigate]);

    return null;
};

export default Login;
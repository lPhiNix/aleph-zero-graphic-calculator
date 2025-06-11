import * as React from "react";
import {useEffect} from "react";
import pkceChallenge from "pkce-challenge";


const Login: React.FC = () => {
    useEffect(() => {
        async function generatePkce() {
            const { code_verifier, code_challenge } = await pkceChallenge();

            sessionStorage.setItem("code_verifier", code_verifier);
            sessionStorage.setItem("code_challenge", code_challenge);

            const clientId = "react-client";

            const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";
            const frontendUrl = import.meta.env.VITE_FRONTEND_URL || "http://localhost:5173";

            const redirectUri = encodeURIComponent(`${frontendUrl}/oauth2/callback`);
            const scope = encodeURIComponent("openid read");
            const responseType = "code";
            const codeChallengeMethod = "S256";

            window.location.href = `${backendUrl}/oauth2/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&code_challenge=${code_challenge}&code_challenge_method=${codeChallengeMethod}`;
        }

        generatePkce();
    }, []);

    return null;
};

export default Login;

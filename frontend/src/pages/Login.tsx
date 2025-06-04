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
            const redirectUri = encodeURIComponent("http://localhost:5173/oauth2/callback");
            const scope = encodeURIComponent("openid read");
            const responseType = "code";
            const codeChallengeMethod = "S256";

            window.location.href = `http://localhost:8080/oauth2/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&code_challenge=${code_challenge}&code_challenge_method=${codeChallengeMethod}`;
        }

        generatePkce();
    }, []);

    return null;
};

export default Login;

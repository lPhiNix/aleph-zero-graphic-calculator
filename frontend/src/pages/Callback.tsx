import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import * as React from "react";

const Callback: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const code = params.get("code");
        const error = params.get("error");
        if (error) {
            console.error("OAuth error:", error);
            navigate("/" + error);
            return;
        }

        if (!code) {
            console.error("No authorization code found in URL");
            navigate("/login");
            return;
        }

        const codeVerifier = sessionStorage.getItem("code_verifier");
        if (!codeVerifier) {
            console.error("No code_verifier found in sessionStorage");
            navigate("/login");
            return;
        }

        const body = new URLSearchParams();
        body.append("grant_type", "authorization_code");
        body.append("code", code);
        body.append("redirect_uri", "http://localhost:5173/oauth2/callback");
        body.append("client_id", "react-client");
        body.append("code_verifier", codeVerifier);

        fetch("http://localhost:8080/oauth2/token", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: body.toString(),
            credentials: "include",
        })
            .then(res => {
                if (!res.ok) throw new Error("Token exchange failed");
                return res.json();
            })
            .then((data) => {
                //
                sessionStorage.setItem("access_token", data.access_token);
                navigate("/");
            })
            .catch((err) => {
                console.error(err);
                navigate("/?msg=Error getting token: " + err.message);
            });
    }, [location, navigate]);

    return <div>Loading...</div>;
};

export default Callback;

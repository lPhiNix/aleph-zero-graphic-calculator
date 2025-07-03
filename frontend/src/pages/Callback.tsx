import { useEffect } from "react"; // Import useEffect from React for side effects
import { useNavigate, useLocation } from "react-router-dom"; // Import navigation and location hooks from React Router
import * as React from "react"; // Import React namespace for type usage

/**
 * Callback component to handle OAuth2 redirect callback.
 * This component is mounted when the user is redirected back from the OAuth2 provider.
 * It handles errors, exchanges authorization code for access token, stores the token,
 * and redirects to the application's main page or login in case of errors.
 */
const Callback: React.FC = () => {
    // useNavigate provides an imperative API for changing routes
    const navigate = useNavigate();
    // useLocation provides access to the current location object (including query string)
    const location = useLocation();

    // Backend URL, configurable via ENV (for deployment flexibility)
    const backendUrl = window.ENV?.VITE_BACKEND_URL || "http://localhost:8080";
    // Frontend URL, configurable via ENV (for deployment flexibility)
    const frontendUrl = window.ENV?.VITE_FRONTEND_URL || "http://localhost:5173";

    /**
     * Handles the OAuth2 callback logic when the component is mounted or when the location changes.
     * 1. Checks for errors in the URL.
     * 2. Extracts the authorization code.
     * 3. Exchanges the code for an access token using the PKCE flow.
     * 4. Stores the access token in sessionStorage.
     * 5. Redirects the user to the main page or login page if errors occur.
     */
    useEffect(() => {
        // Parse URL parameters from the query string in the callback URL
        const params = new URLSearchParams(location.search);
        // Get the authorization code returned by the OAuth provider
        const code = params.get("code");
        // Get the error parameter (if present)
        const error = params.get("error");
        // If there is an error, log and redirect to a route representing that error
        if (error) {
            console.error("OAuth error:", error);
            navigate("/" + error);
            return;
        }

        // If there is no authorization code, log and redirect to login page
        if (!code) {
            console.error("No authorization code found in URL");
            navigate("/login");
            return;
        }

        // Retrieve the code_verifier from sessionStorage (required for PKCE)
        const codeVerifier = sessionStorage.getItem("code_verifier");
        // If code_verifier is missing, redirect to login and log error
        if (!codeVerifier) {
            console.error("No code_verifier found in sessionStorage");
            navigate("/login");
            return;
        }

        // Build the POST body for exchanging code for access token
        const body = new URLSearchParams();
        body.append("grant_type", "authorization_code"); // Standard OAuth2 grant type
        body.append("code", code); // The authorization code
        body.append("redirect_uri", `${frontendUrl}/oauth2/callback`); // Must match the registered redirect URI
        body.append("client_id", "react-client"); // The client ID configured in the backend
        body.append("code_verifier", codeVerifier); // PKCE code verifier

        // Exchange the code for an access token by POSTing to the backend
        fetch(`${backendUrl}/oauth2/token`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded", // URL-encoded form body
            },
            body: body.toString(),
            credentials: "include", // Include cookies for authentication if needed
        })
            // Handle response: check for HTTP errors
            .then(res => {
                if (!res.ok) throw new Error("Token exchange failed");
                return res.json();
            })
            // On success: store the access token and redirect to the home page
            .then((data) => {
                sessionStorage.setItem("access_token", data.access_token);
                navigate("/");
            })
            // On error: log the error and redirect to home page with error message
            .catch((err) => {
                console.error(err);
                navigate("/?msg=Error getting token: " + err.message);
            });
    }, [location, navigate]); // Re-run effect if location or navigate changes

    // Show a loading message while the token exchange is in progress
    return <div>Loading...</div>;
};

// Export the Callback component as default
export default Callback;
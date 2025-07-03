import * as React from "react"; // Import React namespace for JSX and type usage
import { useEffect } from "react"; // Import useEffect for side effects in functional components
import pkceChallenge from "pkce-challenge"; // Import PKCE challenge utility for OAuth2 PKCE flow
import { useNavigate } from "react-router-dom"; // Import useNavigate for imperative navigation

/**
 * Login component
 * Handles the login process using OAuth2 PKCE flow.
 * If an access token is present and valid in sessionStorage, redirects to /calculator.
 * Otherwise, generates PKCE parameters, stores them, and redirects to the OAuth2 authorization endpoint.
 * @returns {null} This component does not render any UI.
 */
const Login: React.FC = (): null => {
    // useNavigate provides a function to imperatively change routes
    const navigate = useNavigate();

    /**
     * Checks if a JWT access token is expired.
     * Decodes the token, extracts the exp (expiry) field, and compares to the current time.
     * If decoding fails, treats token as expired for safety.
     * @param {string} token - The JWT access token string.
     * @returns {boolean} True if expired or invalid, false if still valid.
     */
    function isTokenExpired(token: string) {
        try {
            // Decode the payload part of the JWT (second part, base64-encoded)
            const payload = JSON.parse(atob(token.split('.')[1]));
            // exp is in seconds, Date.now() is in milliseconds
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            // If decoding fails, treat the token as expired for extra security
            return true;
        }
    }

    /**
     * useEffect hook
     * On mount, checks for an existing and valid token.
     * If valid, redirects to /calculator.
     * If not, generates PKCE parameters, stores them, and redirects to the OAuth2 provider for login.
     */
    useEffect(() => {
        // Get the access token from sessionStorage (if any)
        const token = sessionStorage.getItem("access_token");
        // If token exists and is NOT expired, redirect to calculator
        if (token && !isTokenExpired(token)) {
            navigate("/calculator", { replace: true });
            return;
        }

        /**
         * Generates PKCE parameters and redirects to the authorization endpoint.
         * Uses the pkce-challenge library to generate code_verifier and code_challenge.
         * Stores them in sessionStorage for later use in the callback.
         * Then builds the OAuth2 authorization URL with all required parameters.
         * Finally, sets window.location.href to redirect the browser.
         */
        async function generatePkce() {
            // Generate code_verifier and code_challenge for PKCE
            const { code_verifier, code_challenge } = await pkceChallenge();

            // Store both in sessionStorage for later retrieval in the callback
            sessionStorage.setItem("code_verifier", code_verifier);
            sessionStorage.setItem("code_challenge", code_challenge);

            // OAuth2 client ID configured in the backend
            const clientId = "react-client";

            // Backend and frontend URLs, supporting environment variable overrides for flexible deployments
            const backendUrl = window.ENV?.VITE_BACKEND_URL || "http://localhost:8080";
            const frontendUrl = window.ENV?.VITE_FRONTEND_URL || "http://localhost:5173";

            // Build the redirect_uri parameter (must match backend config)
            const redirectUri = encodeURIComponent(`${frontendUrl}/oauth2/callback`);
            // OAuth2 scopes required by the application
            const scope = encodeURIComponent("openid read");
            // Response type for authorization code flow
            const responseType = "code";
            // PKCE code_challenge method
            const codeChallengeMethod = "S256";

            // Construct the full OAuth2 authorization URL with all required parameters
            window.location.href = `${backendUrl}/oauth2/authorize?response_type=${responseType}&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&code_challenge=${code_challenge}&code_challenge_method=${codeChallengeMethod}`;
        }

        // Call the PKCE generation and redirect function if token is missing or expired
        generatePkce();
    }, [navigate]); // Only rerun effect if the navigate function changes

    // This component does not render any UI
    return null;
};

// Export the Login component as default
export default Login;
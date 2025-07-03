import { Navigate } from "react-router-dom"; // Import Navigate from react-router-dom for navigation/redirection
import * as React from "react"; // Import React namespace for type usage and JSX

/**
 * Props for ProtectedRoute component.
 * @property {React.ReactElement} children - React node to render if access is allowed.
 * @property {string} [redirectTo] - Optional path to redirect if not allowed. Defaults to "/login".
 */
interface ProtectedRouteProps {
    children: React.ReactElement; // The component(s) to render if access is granted
    redirectTo?: string; // Optional path to redirect to, defaults to "/login"
}

/**
 * ProtectedRoute component
 * This component checks for a valid access token in sessionStorage.
 * If a token is present, it renders its children (the protected page).
 * If no token is present, it redirects to the specified path (default is "/login").
 * Usage: Wrap around any route/page that requires authentication.
 *
 * @param {ProtectedRouteProps} props - The props for ProtectedRoute.
 * @returns {React.ReactElement|null} The protected children or a redirect.
 */
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, redirectTo = "/login" }) => {
    // Retrieve the access token from sessionStorage.
    const token = sessionStorage.getItem("access_token");
    // If token does not exist, redirect to the login (or custom) page.
    if (!token) {
        return <Navigate to={redirectTo} replace />;
    }
    // If token exists, render the protected children.
    return children;
};

// Export the ProtectedRoute component as default for usage in route definitions.
export default ProtectedRoute;
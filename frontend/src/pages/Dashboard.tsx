import { useLocation } from "react-router-dom"; // Import useLocation hook from react-router-dom to access the current URL location and its query parameters
import * as React from "react"; // Import React namespace for type usage and JSX
import { useEffect } from "react"; // Import useEffect for side effects in functional components
import styles from "../styles/modules/dashboard.module.css"; // Import CSS module for dashboard styling
import dashboardImage from "../assets/logo/alephzeroLogo2.png"; // Import the dashboard logo image

/**
 * Dashboard component
 * This component serves as a landing/splash page for the dashboard.
 * If a query parameter "msg" is present, it shows a message to the user.
 * It will redirect to /calculator on mount or when the "attempt" query parameter changes.
 * @returns {JSX.Element} The dashboard page content
 */
const Dashboard: React.FC = () => {
    // Get the location object to access query parameters from the URL
    const location = useLocation();
    // Parse query parameters from the current URL
    const params = new URLSearchParams(location.search);
    // Get the "msg" parameter, if present
    const message = params.get("msg");
    // Get the "attempt" parameter, or default to 0, and parse as integer
    const attempt = parseInt(params.get("attempt") || "0", 10);

    /**
     * useEffect hook
     * Redirects to "/calculator" when this component mounts, or when "attempt" changes.
     * This is useful for retrying or forced navigation after a login or failed attempt.
     */
    useEffect(() => {
        window.location.href = "/calculator"; // Replace current document location with /calculator
    }, [attempt]);

    // Render the dashboard: splash image and an optional message if present
    return (
        <div className={styles.container}>
            <img
                src={dashboardImage} // Show the dashboard logo
                alt="Aleph-Zero" // Alternative text for accessibility
                className={styles.dashboardImage} // CSS class for styling the image
            />
            {/* Show message paragraph if there is a message in the query parameter */}
            {message && <p>{message}</p>}
        </div>
    );
};

// Export the Dashboard component as default for use in routing and main app
export default Dashboard;
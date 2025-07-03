import { BrowserRouter as Router, Routes, Route } from "react-router-dom"; // Import routing utilities from react-router-dom
import Login from "./pages/Login"; // Import Login page component
import Callback from "./pages/Callback"; // Import Callback page component (OAuth2 callback)
import Dashboard from "./pages/Dashboard"; // Import Dashboard page component (landing or splash page)
import Calculator from "./pages/Calculator"; // Import Calculator page component (main calculator UI)
import ProtectedRoute from "./routes/ProtectedRoute"; // Import ProtectedRoute for authenticated access

/**
 * App component
 * Sets up the main routes for the single-page application.
 * Applies protection to the Calculator route, so it is only accessible if authenticated.
 * @returns {JSX.Element} The root router and route definitions
 */
function App() {
    return (
        <Router>
            <Routes>
                {/* Route for login page */}
                <Route path="/login" element={<Login />} />
                {/* Route for OAuth2 provider callback */}
                <Route path="/oauth2/callback" element={<Callback />} />
                {/* Route for dashboard (landing) page */}
                <Route path="/" element={<Dashboard />} />
                {/* Protected route for calculator, requires authentication */}
                <Route
                    path="/calculator"
                    element={
                        <ProtectedRoute>
                            <Calculator />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </Router>
    );
}

// Export the App component as default for use as the root of the React application
export default App;
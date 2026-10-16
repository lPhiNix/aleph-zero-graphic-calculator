import { BrowserRouter as Router, Routes, Route } from "react-router-dom"; // Import routing utilities from react-router-dom
import LoginPage from "./pages/LoginPage"; // Import Login page component
import RegisterPage from "./pages/RegisterPage"; // Import Register page component
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
                <Route path="/login" element={<LoginPage />} />
                {/* Route for register page */}
                <Route path="/register" element={<RegisterPage />} />
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

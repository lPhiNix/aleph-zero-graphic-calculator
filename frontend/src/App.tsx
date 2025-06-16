import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Callback from "./pages/Callback";
import Dashboard from "./pages/Dashboard";
import Calculator from "./pages/Calculator";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/oauth2/callback" element={<Callback />} />
                <Route path="/" element={<Dashboard />} />
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

export default App;
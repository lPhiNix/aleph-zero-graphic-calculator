// src/App.tsx
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import GraphPage from './pages/GraphPage'; // <-- IMPORTA tu nueva página

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login"    element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/calculator"    element={<GraphPage />} />
                <Route path="*"         element={<Navigate to="/calculator" replace />} />
            </Routes>
        </Router>
    );
}

export default App;

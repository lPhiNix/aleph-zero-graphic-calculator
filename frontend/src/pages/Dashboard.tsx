import { useLocation, useNavigate } from "react-router-dom";
import * as React from "react";
import { useEffect } from "react";

const Dashboard: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const params = new URLSearchParams(location.search);
    const message = params.get("msg");
    const attempt = parseInt(params.get("attempt") || "0", 10);

    useEffect(() => {
        // Si no hay token, redirige directamente a login

        // Redirige a calculator y refresca la página
        const targetUrl = `/calculator`;
        // Usamos location.href para forzar reload completo
        window.location.href = targetUrl;
    }, [attempt, navigate]);

    return (
        <div>
            <h1>Dashboard</h1>
            {message && <p>{message}</p>}
        </div>
    );
};

export default Dashboard;

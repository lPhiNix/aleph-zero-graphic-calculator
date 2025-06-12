import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import * as React from "react";

const Dashboard: React.FC = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const token = sessionStorage.getItem("access_token");
        if (token) {
            navigate("/calculator", { replace: true });
        } else {
            navigate("/login", { replace: true });
        }
    }, [navigate]);

    return <div>Redirigiendo...</div>;
};

export default Dashboard;
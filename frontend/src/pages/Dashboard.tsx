import { useLocation } from "react-router-dom";
import * as React from "react";

const Dashboard: React.FC = () => {
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const message = params.get("msg");

    return (
        <div>
            <h1>Dashboard</h1>
            {message && <p>{message}</p>}
        </div>
    );
};

export default Dashboard;

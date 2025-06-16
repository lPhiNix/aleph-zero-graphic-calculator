import { useLocation } from "react-router-dom";
import * as React from "react";
import { useEffect } from "react";
import styles from "../styles/modules/dashboard.module.css";
import dashboardImage from "../assets/logo/alephzeroLogo2.png";

const Dashboard: React.FC = () => {
    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const message = params.get("msg");
    const attempt = parseInt(params.get("attempt") || "0", 10);

    useEffect(() => {
        window.location.href = "/calculator";
    }, [attempt]);

    return (
        <div className={styles.container}>
            <img
                src={dashboardImage}
                alt="Aleph-Zero"
                className={styles.dashboardImage}
            />
            {message && <p>{message}</p>}
        </div>
    );
};

export default Dashboard;

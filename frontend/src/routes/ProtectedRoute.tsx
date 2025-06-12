import { Navigate } from "react-router-dom";
import * as React from "react";

interface ProtectedRouteProps {
    children: React.ReactElement;
    redirectTo?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, redirectTo = "/login" }) => {
    const token = sessionStorage.getItem("access_token");
    if (!token) {
        return <Navigate to={redirectTo} replace />;
    }
    return children;
};

export default ProtectedRoute;
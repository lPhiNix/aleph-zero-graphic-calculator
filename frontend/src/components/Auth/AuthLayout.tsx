// components/Auth/AuthLayout.tsx
import styles from '../../styles/dialog.module.css';
import * as React from "react";

interface AuthLayoutProps {
    title: string;
    children: React.ReactNode;
    subtitle?: React.ReactNode;
}

export function AuthLayout({ title, children, subtitle }: AuthLayoutProps) {
    return (
        <div className={styles.container}>
            <div className={styles.loginBox}>
                <div className={styles.left}>
                    <h1 className={styles.title}>{title}</h1>
                    {subtitle}
                    {children}
                </div>
                <div className={styles.right}></div>
            </div>
        </div>
    );
}

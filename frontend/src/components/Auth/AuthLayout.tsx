import styles from '../../styles/modules/dialog.module.css'; // Import dialog styles
import * as React from "react"; // Import React for types and JSX

/**
 * Props for the AuthLayout component.
 * @property {string} title - The main title for the authentication box.
 * @property {React.ReactNode} children - The main content (usually a form).
 * @property {React.ReactNode} [subtitle] - Optional subtitle or description.
 */
interface AuthLayoutProps {
    title: string;
    children: React.ReactNode;
    subtitle?: React.ReactNode;
}

/**
 * AuthLayout
 * A layout for authentication pages, including a title, optional subtitle, children content, and a styled two-column box.
 * @param {AuthLayoutProps} props - Layout properties including title, children, and optional subtitle.
 * @returns {JSX.Element} The rendered authentication layout.
 */
export function AuthLayout({ title, children, subtitle }: AuthLayoutProps) {
    return (
        <div className={styles.container}>
            <div className={styles.loginBox}>
                {/* Left side: title, subtitle, and content */}
                <div className={styles.left}>
                    <h1 className={styles.title}>{title}</h1>
                    {subtitle}
                    {children}
                </div>
                {/* Right side: for illustration, decoration, or left blank */}
                <div className={styles.right}></div>
            </div>
        </div>
    );
}
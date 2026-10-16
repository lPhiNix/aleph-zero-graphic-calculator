import styles from '../../styles/modules/dialog.module.css'; // Import dialog styles
import * as React from "react"; // Import React for types and JSX

/**
 * Props for the AuthLayout component.
 * @property {string} title - The main title for the authentication box.
 * @property {React.ReactNode} children - The main content (usually a form).
 * @property {React.ReactNode} [subtitle] - Optional subtitle or description.
 * @property {"login" | "register"} [illustration] - Which illustration to show on the right panel.
 */
interface AuthLayoutProps {
    title: string;
    children: React.ReactNode;
    subtitle?: React.ReactNode;
    illustration?: "login" | "register";
}

/**
 * AuthLayout
 * A layout for authentication pages, including a title, optional subtitle, children content, and a styled two-column box.
 * @param {AuthLayoutProps} props - Layout properties including title, children, and optional subtitle.
 * @returns {JSX.Element} The rendered authentication layout.
 */
export function AuthLayout({ title, children, subtitle, illustration }: AuthLayoutProps) {
    // Pick the illustration class based on the requested variant.
    const illustrationClass =
        illustration === "register" ? styles.rightRegister :
        illustration === "login" ? styles.rightLogin : "";

    return (
        <div className={styles.container}>
            <div className={styles.loginBox}>
                {/* Left side: title, subtitle, and content */}
                <div className={styles.left}>
                    <h1 className={styles.title}>{title}</h1>
                    {subtitle}
                    {children}
                </div>
                {/* Right side: illustration for the current auth variant */}
                <div className={`${styles.right} ${illustrationClass}`}></div>
            </div>
        </div>
    );
}
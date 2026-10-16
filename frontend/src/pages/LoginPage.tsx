import { Link } from "react-router-dom"; // Import Link to navigate to the register page
import { AuthLayout } from "../components/Auth/AuthLayout"; // Import the shared auth layout
import { SignInForm } from "../components/Auth/Form/Forms/SignInForm.tsx"; // Import the sign-in form

/**
 * LoginPage
 * Full-page login view. Renders the sign-in form inside the shared auth layout.
 * @returns {JSX.Element} The rendered login page.
 */
export default function LoginPage() {
    return (
        <AuthLayout
            title="Sign In"
            illustration="login"
            subtitle={
                <p className="registerText">
                    Please enter your login credentials<br /> or{' '}
                    <Link to="/register" className="link">click here</Link> to register.
                </p>
            }
        >
            <SignInForm />
        </AuthLayout>
    );
}

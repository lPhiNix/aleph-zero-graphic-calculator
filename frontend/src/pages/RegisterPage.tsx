import { Link } from "react-router-dom"; // Import Link to navigate to the login page
import { AuthLayout } from "../components/Auth/AuthLayout"; // Import the shared auth layout
import { SignUpForm } from "../components/Auth/Form/Forms/SignUpForm.tsx"; // Import the sign-up form

/**
 * RegisterPage
 * Full-page registration view. Renders the sign-up form inside the shared auth layout.
 * @returns {JSX.Element} The rendered register page.
 */
export default function RegisterPage() {
    return (
        <AuthLayout
            title="Sign Up"
            illustration="register"
            subtitle={
                <p className="registerText">
                    Already have an account?{' '}
                    <Link to="/login" className="link">Sign in here</Link>.
                </p>
            }
        >
            <SignUpForm />
        </AuthLayout>
    );
}

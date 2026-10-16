import { useState } from "react"; // Import useState for local form state (rememberMe, loading, errors)
import { useNavigate } from "react-router-dom"; // Import useNavigate to redirect after a successful login
import { AuthForm } from '../AuthForm.tsx'; // Import the main authentication form component
import { AuthFormField } from '../AuthForm.tsx'; // Import the type/interface for form fields
import { LoginFormData } from "../../../../types/login"; // Import the type for login form data
import * as AuthService from "../../../../services/authService.ts"; // Import auth service (login + error parsing)

/**
 * SignInForm component renders a login form using AuthForm.
 * It provides username and password fields and handles submission.
 * @returns {JSX.Element} The rendered sign-in form.
 */
export function SignInForm() {
    const navigate = useNavigate(); // Imperative navigation for redirecting after login

    // Local UI state not managed by AuthForm's field state.
    const [rememberMe, setRememberMe] = useState(false); // "Remember me" checkbox state
    const [loading, setLoading] = useState(false); // True while the login request is in flight
    const [error, setError] = useState<string | undefined>(undefined); // General error message
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({}); // Per-field errors

    /**
     * Initial state for the form fields.
     * Contains empty username and password, and rememberMe is unchecked.
     */
    const initialState: LoginFormData = {
        username: '', // Username or email input, initially empty
        password: '', // Password input, initially empty
        rememberMe: false // Remember me checkbox, initially not checked
    };

    /**
     * Array of field definitions for AuthForm.
     * Each field includes its name, label (empty here), placeholder, type, and required flag.
     */
    const fields: AuthFormField[] = [
        { name: 'username', label: '', placeholder: 'Username or email', required: true }, // Username/email field
        { name: 'password', label: '', type: 'password', placeholder: 'Password', required: true } // Password field
    ];

    /**
     * Handles form submission.
     * Authenticates the user, stores the tokens and redirects to the calculator.
     * @param {LoginFormData} data - The submitted form data.
     */
    const handleSubmit = async (data: LoginFormData) => {
        setLoading(true);
        setError(undefined);
        setFieldErrors({});

        try {
            await AuthService.login({ ...data, rememberMe });
            navigate("/calculator", { replace: true });
        } catch (e) {
            const parsed = AuthService.parseAuthError(e);
            setError(parsed.message);
            setFieldErrors(parsed.fieldErrors);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Extra options rendered below the main form fields.
     * Includes a "Remember me" checkbox and a "Forgot Password?" link.
     */
    const extraOptions = (
        <>
            <label className="checkbox">
                <input
                    type="checkbox"
                    name="rememberMe"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                />
                Remember me {/* Text for the remember me checkbox */}
            </label>
            <a href="#" className="link">Forgot Password?</a> {/* Link to forgot password (dummy link) */}
        </>
    );

    // Render the authentication form with all props
    return (
        <AuthForm
            initialState={initialState} // The initial form values
            fields={fields} // The field definitions for input fields
            onSubmit={handleSubmit} // Handler for form submission
            extraOptions={extraOptions} // Extra options to render below fields
            buttonText="Sign In" // Text for the form submit button
            loading={loading} // Disable the button while submitting
            error={error} // General error message
            fieldErrors={fieldErrors} // Per-field error messages
        />
    );
}

import { useState } from "react"; // Import useState for loading/error state
import { useNavigate } from "react-router-dom"; // Import useNavigate to redirect to login after a successful register
import { AuthForm } from '../AuthForm.tsx'; // Import the main authentication form component
import { AuthFormField } from '../AuthForm.tsx'; // Import the type/interface for form fields
import { RegisterFormData } from "../../../../types/register"; // Import the type for registration form data
import * as AuthService from "../../../../services/authService.ts"; // Import auth service (register + error parsing)

/**
 * SignUpForm component renders a registration form using AuthForm.
 * It provides username, email, password, and confirm password fields and handles submission.
 * @returns {JSX.Element} The rendered sign-up form.
 */
export function SignUpForm() {
    const navigate = useNavigate(); // Imperative navigation for redirecting after register

    // Local UI state for the submission lifecycle.
    const [loading, setLoading] = useState(false); // True while the register request is in flight
    const [error, setError] = useState<string | undefined>(undefined); // General error message
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({}); // Per-field errors

    /**
     * Initial state for the form fields.
     * Contains empty username, email, password, and confirmPassword.
     */
    const initialState: RegisterFormData = {
        username: '', // Username input, initially empty
        email: '', // Email input, initially empty
        password: '', // Password input, initially empty
        confirmPassword: '' // Confirm password input, initially empty
    };

    /**
     * Array of field definitions for AuthForm.
     * Each field includes its name, label (empty here), placeholder, type, and required flag.
     */
    const fields: AuthFormField[] = [
        { name: 'username', label: '', placeholder: 'Username', required: true }, // Username field
        { name: 'email', label: '', type: 'email', placeholder: 'Email', required: true }, // Email field
        { name: 'password', label: '', type: 'password', placeholder: 'Password', required: true }, // Password field
        { name: 'confirmPassword', label: '', type: 'password', placeholder: 'Confirm Password', required: true } // Confirm Password field
    ];

    /**
     * Handles form submission.
     * Registers the user and, on success, redirects to the login page.
     * @param {RegisterFormData} data - The submitted form data.
     */
    const handleSubmit = async (data: RegisterFormData) => {
        setLoading(true);
        setError(undefined);
        setFieldErrors({});

        try {
            await AuthService.register(data);
            navigate("/login", { replace: true });
        } catch (e) {
            const parsed = AuthService.parseAuthError(e);
            setError(parsed.message);
            setFieldErrors(parsed.fieldErrors);
        } finally {
            setLoading(false);
        }
    };

    // Render the authentication form with all props
    return (
        <AuthForm
            initialState={initialState} // The initial form values
            fields={fields} // The field definitions for input fields
            onSubmit={handleSubmit} // Handler for form submission
            buttonText="Sign Up" // Text for the form submit button
            loading={loading} // Disable the button while submitting
            error={error} // General error message
            fieldErrors={fieldErrors} // Per-field error messages
        />
    );
}

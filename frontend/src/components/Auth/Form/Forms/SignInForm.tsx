import { AuthForm } from '../AuthForm.tsx'; // Import the main authentication form component
import { AuthFormField } from '../AuthForm.tsx'; // Import the type/interface for form fields
import { LoginFormData } from "../../../../types/login"; // Import the type for login form data

/**
 * SignInForm component renders a login form using AuthForm.
 * It provides username and password fields and handles submission.
 * @returns {JSX.Element} The rendered sign-in form.
 */
export function SignInForm() {
    /**
     * Initial state for the form fields.
     * Contains empty username and password, and rememberMe is unchecked.
     */
    const initialState: LoginFormData = {
        username: '', // Username input, initially empty
        password: '', // Password input, initially empty
        rememberMe: false // Remember me checkbox, initially not checked
    };

    /**
     * Array of field definitions for AuthForm.
     * Each field includes its name, label (empty here), placeholder, type, and required flag.
     */
    const fields: AuthFormField[] = [
        { name: 'username', label: '', placeholder: 'Username', required: true }, // Username field
        { name: 'password', label: '', type: 'password', placeholder: 'Password', required: true } // Password field
    ];

    /**
     * Handles form submission.
     * Logs the login form data to the console.
     * @param {LoginFormData} data - The submitted form data.
     */
    const handleSubmit = (data: LoginFormData) => {
        console.log('Login:', data);
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
                    checked={initialState.rememberMe}
                    onChange={() => {}} // No-op handler, as AuthForm manages state
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
        />
    );
}
import { AuthForm } from '../AuthForm.tsx'; // Import the main authentication form component
import { AuthFormField } from '../AuthForm.tsx'; // Import the type/interface for form fields

/**
 * SignUpForm component renders a registration form using AuthForm.
 * It provides username, email, password, and confirm password fields and handles submission.
 * @returns {JSX.Element} The rendered sign-up form.
 */
export function SignUpForm() {
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
     * Logs the registration form data to the console.
     * @param {RegisterFormData} data - The submitted form data.
     */
    const handleSubmit = (data: RegisterFormData) => {
        console.log('Register:', data);
    };

    // Render the authentication form with all props
    return (
        <AuthForm
            initialState={initialState} // The initial form values
            fields={fields} // The field definitions for input fields
            onSubmit={handleSubmit} // Handler for form submission
            buttonText="Sign Up" // Text for the form submit button
        />
    );
}
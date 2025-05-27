// components/Auth/SignUpForm.tsx
import { AuthForm } from '../AuthForm.tsx';
import { AuthFormField } from '../AuthForm.tsx';

interface RegisterFormData {
    username: string;
    email: string;
    password: string;
    confirmPassword: string;
}

export function SignUpForm() {
    const initialState: RegisterFormData = {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    };

    const fields: AuthFormField[] = [
        { name: 'username', label: '', placeholder: 'Username', required: true },
        { name: 'email', label: '', type: 'email', placeholder: 'Email', required: true },
        { name: 'password', label: '', type: 'password', placeholder: 'Password', required: true },
        { name: 'confirmPassword', label: '', type: 'password', placeholder: 'Confirm Password', required: true }
    ];

    const handleSubmit = (data: RegisterFormData) => {
        console.log('Register:', data);
    };

    return (
        <AuthForm
            initialState={initialState}
            fields={fields}
            onSubmit={handleSubmit}
            buttonText="Sign Up"
        />
    );
}

import { AuthForm } from '../AuthForm.tsx';
import { AuthFormField } from '../AuthForm.tsx';

interface LoginFormData {
    username: string;
    password: string;
    rememberMe: boolean;
}

export function SignInForm() {
    const initialState: LoginFormData = {
        username: '',
        password: '',
        rememberMe: false
    };

    const fields: AuthFormField[] = [
        { name: 'username', label: '', placeholder: 'Username', required: true },
        { name: 'password', label: '', type: 'password', placeholder: 'Password', required: true }
    ];

    const handleSubmit = (data: LoginFormData) => {
        console.log('Login:', data);
    };

    const extraOptions = (
        <>
            <label className="checkbox">
                <input
                    type="checkbox"
                    name="rememberMe"
                    checked={initialState.rememberMe}
                    onChange={() => {}} // para cumplir con React
                />
                Remember me
            </label>
            <a href="#" className="link">Forgot Password?</a>
        </>
    );

    return (
        <AuthForm
            initialState={initialState}
            fields={fields}
            onSubmit={handleSubmit}
            extraOptions={extraOptions}
            buttonText="Sign In"
        />
    );
}

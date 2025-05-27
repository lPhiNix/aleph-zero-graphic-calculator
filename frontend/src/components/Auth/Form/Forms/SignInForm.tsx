import { AuthForm } from '../AuthForm.tsx';
import { AuthFormField } from '../AuthForm.tsx';
import {LoginFormData} from "../../../../types/login";

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
                    onChange={() => {}}
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

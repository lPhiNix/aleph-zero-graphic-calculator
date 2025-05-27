// pages/LoginPage.tsx
import { Link } from 'react-router-dom';
import { AuthLayout } from '../components/Auth/AuthLayout';
import { SignInForm } from '../components/Auth/Form/Forms/SignInForm.tsx';

export default function LoginPage() {
    return (
        <AuthLayout
            title="Sign In"
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

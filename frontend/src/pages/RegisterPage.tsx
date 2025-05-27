// pages/RegisterPage.tsx
import { Link } from 'react-router-dom';
import { AuthLayout } from '../components/Auth/AuthLayout';
import { SignUpForm } from '../components/Auth/Form/Forms/SignUpForm.tsx';

export default function RegisterPage() {
    return (
        <AuthLayout
            title="Sign Up"
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

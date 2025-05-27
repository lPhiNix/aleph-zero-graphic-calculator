// pages/RegisterPage.tsx
import { AuthLayout } from '../components/Auth/AuthLayout';
import { SignUpForm } from '../components/Auth/Form/Forms/SignUpForm.tsx';

export default function RegisterPage() {
    return (
        <AuthLayout
            title="Sign Up"
            subtitle={
                <p className="registerText">
                    Already have an account?{' '}
                    <a href="/login" className="link">Sign in here</a>.
                </p>
            }
        >
            <SignUpForm />
        </AuthLayout>
    );
}

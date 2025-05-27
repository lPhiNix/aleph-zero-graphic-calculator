// pages/LoginPage.tsx
import { AuthLayout } from '../components/Auth/AuthLayout';
import { SignInForm } from '../components/Auth/Form/Forms/SignInForm.tsx';

export default function LoginPage() {
    return (
        <AuthLayout
            title="Sign In"
            subtitle={
                <p className="registerText">
                    Please enter your login credentials<br /> or{' '}
                    <a href="/register" className="link">click here</a> to register.
                </p>
            }
        >
            <SignInForm />
        </AuthLayout>
    );
}

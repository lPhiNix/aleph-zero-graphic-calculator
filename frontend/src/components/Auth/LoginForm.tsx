import { useState } from 'react';
import { AuthInput } from './AuthInput';
import { LoginFormData } from '../../types/auth';
import styles from '../../styles/login.module.css';
import * as React from "react";

export function LoginForm() {
    const [form, setForm] = useState<LoginFormData>({
        username: '',
        password: '',
        rememberMe: false,
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Login:', form);
        // Aquí iría el fetch o axios
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <p className={styles.registerText}>
                Please enter your login credentials<br /> or{' '}
                <a href="#" className={styles.link}>click here</a> to register.
            </p>

            <div className={styles.formMain}>
                <AuthInput
                    label={""}
                    name="username"
                    placeholder={"Username"}
                    value={form.username}
                    onChange={handleChange}
                    required
                />
                <AuthInput
                    label={""}
                    name="password"
                    placeholder="Password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className={styles.formOptions}>
                <label className={styles.checkbox}>
                    <input
                        type="checkbox"
                        name="rememberMe"
                        checked={form.rememberMe}
                        onChange={handleChange}
                    />
                    Remember me
                </label>
                <a href="#" className={styles.link}>Forgot Password?</a>
            </div>

            <div className={styles.formMain}>
                <button type="submit" className={styles.button}>Sign In</button>
            </div>
        </form>

    );
}

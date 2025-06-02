import { AuthInput } from '../AuthInput.tsx';
import styles from '../../../styles/modules/dialog.module.css';
import { useState } from 'react';
import * as React from "react";

export interface AuthFormField {
    name: string;
    label: string;
    type?: string;
    placeholder?: string;
    required?: boolean;
}

interface AuthFormProps<T> {
    initialState: T;
    fields: AuthFormField[];
    onSubmit: (data: T) => void;
    extraOptions?: React.ReactNode;
    buttonText: string;
}

export function AuthForm<T extends Record<string, any>>({
                                                            initialState,
                                                            fields,
                                                            onSubmit,
                                                            extraOptions,
                                                            buttonText
                                                        }: AuthFormProps<T>) {
    const [formData, setFormData] = useState<T>(initialState);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.formMain}>
                {fields.map((field) => (
                    <AuthInput
                        key={field.name}
                        label={field.label}
                        name={field.name}
                        type={field.type}
                        placeholder={field.placeholder}
                        required={field.required}
                        value={formData[field.name]}
                        onChange={handleChange}
                    />
                ))}
            </div>

            {extraOptions && <div className={styles.formOptions}>{extraOptions}</div>}

            <div className={styles.formMain}>
                <button type="submit" className={styles.button}>
                    {buttonText}
                </button>
            </div>
        </form>
    );
}

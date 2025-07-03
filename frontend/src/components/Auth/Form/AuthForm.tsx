import { AuthInput } from '../AuthInput.tsx'; // Import the custom input component for the form fields
import styles from '../../../styles/modules/dialog.module.css'; // Import styles for the form
import { useState } from 'react'; // Import useState for form state management
import * as React from "react"; // Import React for JSX and types

/**
 * Interface for a single form field used in authentication forms.
 * @property {string} name - The key for the field in the form data.
 * @property {string} label - The label to display with the field.
 * @property {string} [type] - The input type (e.g., "text", "password", "email").
 * @property {string} [placeholder] - Placeholder text for the field.
 * @property {boolean} [required] - If the field is required for form submission.
 */
export interface AuthFormField {
    name: string;
    label: string;
    type?: string;
    placeholder?: string;
    required?: boolean;
}

/**
 * Props for the AuthForm component.
 * @template T - The type of data managed by the form.
 * @property {T} initialState - The initial values for the form fields.
 * @property {AuthFormField[]} fields - The fields to render in the form.
 * @property {(data: T) => void} onSubmit - Callback fired when the form is submitted.
 * @property {React.ReactNode} [extraOptions] - Optional additional content (e.g., checkboxes, links).
 * @property {string} buttonText - The label for the submit button.
 */
interface AuthFormProps<T> {
    initialState: T;
    fields: AuthFormField[];
    onSubmit: (data: T) => void;
    extraOptions?: React.ReactNode;
    buttonText: string;
}

/**
 * AuthForm
 * Generic authentication form for login, registration, etc. Supports dynamic fields and flexible state.
 * @template T - Type of the form state object.
 * @param {AuthFormProps<T>} props - Props for configuring the form.
 * @returns {JSX.Element} The rendered authentication form.
 */
export function AuthForm<T extends Record<string, any>>({
                                                            initialState,
                                                            fields,
                                                            onSubmit,
                                                            extraOptions,
                                                            buttonText
                                                        }: AuthFormProps<T>) {
    /**
     * State for the form's input values.
     * Starts with the initialState and updates on user input.
     */
    const [formData, setFormData] = useState<T>(initialState);

    /**
     * Handles input changes for all form fields.
     * Updates formData state based on input name and value.
     * Supports checkbox inputs by using the checked property.
     * @param {React.ChangeEvent<HTMLInputElement>} e - The change event from the input.
     */
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    /**
     * Handles form submission.
     * Prevents default form behavior and calls the onSubmit callback with form data.
     * @param {React.FormEvent} e - The form submission event.
     */
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    // Render the form with fields, extra options, and a submit button
    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.formMain}>
                {/* Render each form field as an AuthInput component */}
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

            {/* Render extra options if provided, e.g., checkboxes, links */}
            {extraOptions && <div className={styles.formOptions}>{extraOptions}</div>}

            <div className={styles.formMain}>
                {/* Submit button for the form */}
                <button type="submit" className={styles.button}>
                    {buttonText}
                </button>
            </div>
        </form>
    );
}
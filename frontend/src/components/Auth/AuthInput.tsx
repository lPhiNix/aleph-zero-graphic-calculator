import { InputHTMLAttributes } from 'react'; // Import type for standard input props
import styles from '../../styles/modules/dialog.module.css'; // Import form/dialog styles

/**
 * Props for the AuthInput component.
 * Extends standard input props and adds a required label.
 */
interface AuthInputProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string; // Label to display above the input
}

/**
 * AuthInput
 * A reusable form input component with a label, styled for authentication dialogs.
 * @param {AuthInputProps} props - Props including label and standard input props.
 * @returns {JSX.Element} The rendered input group.
 */
export function AuthInput({ label, ...props }: AuthInputProps) {
    return (
        <div className={styles.inputGroup}>
            {/* Render the label above the input */}
            <label className={styles.label}>{label}</label>
            {/* Render the input, spreading all other props */}
            <input className={styles.input} {...props} />
        </div>
    );
}
import { InputHTMLAttributes } from 'react';
import styles from '../../styles/dialog.module.css';

interface AuthInputProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
}

export function AuthInput({ label, ...props }: AuthInputProps) {
    return (
        <div className={styles.inputGroup}>
            <label className={styles.label}>{label}</label>
            <input className={styles.input} {...props} />
        </div>
    );
}

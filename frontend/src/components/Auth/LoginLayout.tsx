import { LoginForm } from './LoginForm';
import styles from '../../styles/login.module.css';

export function LoginLayout() {
    return (
        <div className={styles.container}>
            <div className={styles.loginBox}>
                <div className={styles.left}>
                    <h1 className={styles.title}>Sign In</h1>
                    <LoginForm />
                </div>
                <div className={styles.right}></div>
            </div>
        </div>
    );
}

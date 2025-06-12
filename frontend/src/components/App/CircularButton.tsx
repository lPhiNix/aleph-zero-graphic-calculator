import { useState, useRef, useEffect } from 'react';
import buttonStyles from '../../styles/modules/circularButton.module.css';
import styles from '../../styles/modules/mathkeyboard.module.css';
import AxiosConfig from '../../services/axiosService.ts';

interface UserMenuCircularButtonProps {
    initial: string;
}

const backendUrl = window.ENV?.VITE_BACKEND_URL || "http://localhost:8080";

export default function UserMenuCircularButton({ initial }: UserMenuCircularButtonProps) {
    const [open, setOpen] = useState(false);
    const btnRef = useRef<HTMLButtonElement | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!open) return;
        function handleClick(e: MouseEvent) {
            if (
                btnRef.current &&
                !btnRef.current.contains(e.target as Node) &&
                menuRef.current &&
                !menuRef.current.contains(e.target as Node)
            ) {
                setOpen(false);
            }
        }
        window.addEventListener('mousedown', handleClick);
        return () => window.removeEventListener('mousedown', handleClick);
    }, [open]);

    const handleLogout = () => {
        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('code_challenge');
        sessionStorage.removeItem('code_verifier');
        window.location.href = `${backendUrl}/logout`;
    };

    const handleDelete = async () => {
        try {
            const axios = AxiosConfig.getInstance();
            await axios.post('/api/user/delete');
        } catch (e) {
            // opcional: notificación de error
        }
        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('code_challenge');
        sessionStorage.removeItem('code_verifier');
        window.location.href = `${backendUrl}/logout`;
    };

    return (
        <div style={{ position: 'relative', display: 'inline-block' }}>
            <button
                ref={btnRef}
                className={buttonStyles.circularButton}
                style={{ fontWeight: 700, fontSize: '1.1em' }}
                aria-label="Menú de usuario"
                onClick={() => setOpen((v) => !v)}
            >
                {initial}
            </button>
            {open && (
                <div
                    ref={menuRef}
                    className={styles.categorySubmenu}
                    style={{ right: 0, left: 'auto', minWidth: '11rem', padding: '0.7rem 1.1rem' }}
                >
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.7rem' }}>
                        <button
                            type="button"
                            className={styles.keyButton}
                            onClick={handleLogout}
                            style={{ fontWeight: 500, textAlign: 'left' }}
                        >
                            Cerrar sesión
                        </button>
                        <button
                            type="button"
                            className={`${styles.keyButton} ${styles.deleteKey}`}
                            onClick={handleDelete}
                            style={{ fontWeight: 500, textAlign: 'left' }}
                        >
                            Borrar cuenta
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
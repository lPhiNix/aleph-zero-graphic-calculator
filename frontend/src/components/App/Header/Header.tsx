// src/components/Header/Header.tsx
import styles from '../../../styles/modules/header.module.css';
import { useCurrentUser } from '../../../hooks/User/useCurrentUser.tsx';
import UserMenuCircularButton from './AccountButton.tsx';

// ✅ Importa tu logo
import logo from '../../../assets/logo/alephzeroLogo3.png';

interface HeaderProps {
    title?: string;
}

export default function Header({ }: HeaderProps) {
    const { user, loading } = useCurrentUser();
    const initial = loading ? '...' : (user?.username?.[0]?.toUpperCase() || '?');

    return (
        <header className={styles.header}>
            {/* Reemplaza el h1 por tu logo */}
            <img src={logo} alt="Logo" className={styles.logo} />

            <UserMenuCircularButton initial={initial} />
        </header>
    );
}

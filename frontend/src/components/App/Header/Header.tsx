import styles from '../../../styles/modules/header.module.css';
import { useCurrentUser } from '../../../hooks/User/useCurrentUser.tsx';
import UserMenuCircularButton from '../CircularButton';

interface HeaderProps {
    title: string;
}

export default function Header({ title }: HeaderProps) {
    const { user, loading } = useCurrentUser();
    const initial = loading ? '...' : (user?.username?.[0]?.toUpperCase() || 'U');

    return (
        <header className={styles.header}>
            <h1 className={styles.title}>{title}</h1>
            <UserMenuCircularButton initial={initial} />
        </header>
    );
}
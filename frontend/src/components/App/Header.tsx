// src/components/Graph/Header.tsx
import styles from '../../styles/modules/header.module.css';

interface HeaderProps {
    title: string;
}

export default function Header({ title }: HeaderProps) {
    return (
        <header className={styles.header}>
            <h1 className={styles.title}>{title}</h1>
            {/* Aquí podrías añadir botones, menús, logo, etc. */}
        </header>
    );
}

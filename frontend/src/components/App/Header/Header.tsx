import styles from '../../../styles/modules/header.module.css'; // Import header styles from CSS module
import { useCurrentUser } from '../../../hooks/User/useCurrentUser.tsx'; // Import custom hook to get current user info
import UserMenuCircularButton from './AccountButton.tsx'; // Import the user circular menu button component

// Import your logo
import logo from '../../../assets/logo/alephzeroLogo3.png'; // Import logo image asset

/**
 * Props for Header component
 * @property {string} [title] - Optional title for the header (not currently used)
 */
interface HeaderProps {
    title?: string;
}

/**
 * Header component
 * Renders the application header with logo and user menu button.
 * @param {HeaderProps} props - The props for the Header component (currently unused).
 * @returns {JSX.Element} The rendered header element.
 */
export default function Header({ }: HeaderProps) {
    // Extract user and loading state from the custom hook
    const { user, loading } = useCurrentUser();
    // Determine the initial to display in the user menu button: '...' if loading, else first letter of username (uppercase) or '?' if not available
    const initial = loading ? '...' : (user?.username?.[0]?.toUpperCase() || '?');

    return (
        // Render the header element with CSS class from module
        <header className={styles.header}>
            {/* Replace h1 with your logo */}
            <img src={logo} alt="Logo" className={styles.logo} />

            {/* Render the circular button for user menu, passing the computed initial */}
            <UserMenuCircularButton initial={initial} />
        </header>
    );
}
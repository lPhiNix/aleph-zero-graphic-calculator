import { useState, useRef, useEffect } from 'react'; // Import React hooks for component state, refs, and side effects
import buttonStyles from '../../../styles/modules/accountButton.module.css'; // Import custom CSS module for button styling
import styles from '../../../styles/modules/mathkeyboard.module.css'; // Import custom CSS module for keyboard styling
import AxiosConfig from '../../../services/axiosService.ts'; // Import Axios configuration singleton for HTTP requests
import { useCurrentUser } from '../../../hooks/User/useCurrentUser.tsx'; // Import custom hook to get the current user

/**
 * Props for the UserMenuCircularButton component.
 * @property {string} initial - The initial (letter) to display in the button.
 */
interface UserMenuCircularButtonProps {
    initial: string;
}

// Get backend URL from environment variable or use default localhost.
// Used for redirections such as logout.
const backendUrl = window.ENV?.VITE_BACKEND_URL || "http://localhost:8080";

/**
 * UserMenuCircularButton
 * Renders a circular button that shows the user's initial and displays a dropdown menu with user actions when clicked.
 * @param {UserMenuCircularButtonProps} props - The props object with the initial.
 * @returns {JSX.Element} The rendered component.
 */
export default function UserMenuCircularButton({ initial }: UserMenuCircularButtonProps) {
    // State to track if the menu is open
    const [open, setOpen] = useState(false);
    // Ref for the button element to detect outside clicks
    const btnRef = useRef<HTMLButtonElement | null>(null);
    // Ref for the menu element to detect outside clicks
    const menuRef = useRef<HTMLDivElement | null>(null);

    // Get the current user from custom hook
    const { user } = useCurrentUser();

    /**
     * useEffect to handle closing the menu when clicking outside.
     * Adds a mousedown event listener when menu is open, and cleans up on close.
     */
    useEffect(() => {
        if (!open) return;
        /**
         * Handles clicks outside the menu/button to close the menu.
         * @param {MouseEvent} e - The mouse event.
         */
        function handleClick(e: MouseEvent) {
            if (
                btnRef.current &&
                !btnRef.current.contains(e.target as Node) &&
                menuRef.current &&
                !menuRef.current.contains(e.target as Node)
            ) {
                setOpen(false); // Close the menu if click is outside both button and menu
            }
        }
        window.addEventListener('mousedown', handleClick); // Listen on window for outside clicks
        return () => window.removeEventListener('mousedown', handleClick); // Cleanup listener on unmount or menu close
    }, [open]);

    /**
     * Logs the user out by clearing relevant session storage items and redirecting to the backend logout endpoint.
     */
    const handleLogout = () => {
        sessionStorage.removeItem('access_token'); // Remove access token from session storage
        sessionStorage.removeItem('code_challenge'); // Remove PKCE code challenge if present
        sessionStorage.removeItem('code_verifier'); // Remove PKCE code verifier if present
        window.location.href = `${backendUrl}/logout`; // Redirect to backend logout endpoint
    };

    /**
     * Deletes the current user account by calling the backend API, then logs the user out.
     * Handles errors silently.
     */
    const handleDelete = async () => {
        try {
            const axios = AxiosConfig.getInstance(); // Get Axios singleton instance
            await axios.post('/api/user/delete'); // Call delete user endpoint
        } catch (e) {
            // optional: error notification (not implemented)
        }
        sessionStorage.removeItem('access_token'); // Clear session storage credentials
        sessionStorage.removeItem('code_challenge');
        sessionStorage.removeItem('code_verifier');
        window.location.href = `${backendUrl}/logout`; // Redirect to backend logout endpoint
    };

    // Render the circular user menu button and dropdown menu when open
    return (
        // Container for positioning the menu relative to the button
        <div style={{ position: 'relative', display: 'inline-block' }}>
            {/* Circular button showing the user's initial */}
            <button
                ref={btnRef}
                className={buttonStyles.circularButton}
                style={{ fontWeight: 700, fontSize: '1.1em' }}
                aria-label="User menu" // Changed from "Menú de usuario"
                onClick={() => setOpen((v) => !v)} // Toggle menu open/close on click
            >
                {initial}
            </button>
            {/* Render the dropdown menu if open */}
            {open && (
                <div
                    ref={menuRef}
                    className={styles.categorySubmenu}
                    style={{ right: 0, left: 'auto', minWidth: '13rem', padding: '0.7rem 1.1rem' }}
                >
                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '0.9rem'
                    }}>
                        {/* User information section */}
                        <div style={{
                            marginBottom: '0.3rem',
                            paddingBottom: '0.5rem',
                            borderBottom: '1px solid #ccc',
                            fontSize: '0.96rem'
                        }}>
                            <div>
                                <strong>User:</strong> {/* Changed from "Usuario:" */}
                                {user?.username || <span style={{color:'#aaa'}}>Loading...</span> /* Changed from "Cargando..." */}
                            </div>
                            <div style={{
                                fontSize: '0.85em',
                                color: '#888',
                                wordBreak: 'break-all'
                            }}>
                                <strong>ID:</strong> {user?.publicId || <span style={{color:'#aaa'}}>---</span>}
                            </div>
                        </div>
                        {/* Action buttons section */}
                        <button
                            type="button"
                            className={styles.keyButton}
                            onClick={handleLogout}
                            style={{ fontWeight: 500, textAlign: 'left' }}
                        >
                            🔑 Sign Out {/* Button to log out */}
                        </button>
                        <button
                            type="button"
                            className={`${styles.keyButton} ${styles.deleteKey}`}
                            onClick={handleDelete}
                            style={{ fontWeight: 500, textAlign: 'left' }}
                        >
                            🗑️ Delete Account {/* Button to delete account */}
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
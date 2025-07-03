import { StrictMode } from 'react'; // Import StrictMode for highlighting potential problems in development
import { createRoot } from 'react-dom/client'; // Import createRoot for rendering the app in React 18+
import './styles/index.css'; // Import base CSS styles for the app
import './styles/root/theme.css'; // Import theme-related CSS variables and styles
import './styles/root/color.pattern.css'; // Import color pattern CSS for consistent coloring

import App from './App.tsx'; // Import the root App component

// Render the App component inside the element with id 'root' using React 18's createRoot API
createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <App />
    </StrictMode>,
);
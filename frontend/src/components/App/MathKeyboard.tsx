import styles from '../../styles/modules/mathKeyboard.module.css';

interface KeyButton {
    /** Texto que se muestra en la tecla (por ejemplo: "sin", "7", "×", etc.) */
    label: string;
    /** Función que se dispara al hacer click en esta tecla */
    onClick: () => void;
    /**
     * (Opcional) Clase adicional para “extender” el ancho de la tecla.
     * Ejemplo: la tecla “Enter” puede ocupar 2 columnas: className="wideKey"
     */
    className?: string;
}

interface MathKeyboardProps {
    /**
     * Array con TODAS las teclas que se van a dibujar, de izquierda a derecha,
     * de arriba hacia abajo. El grid es siempre 5 columnas. Si quieres que una
     * tecla ocupe dos columnas (como “Enter”), entonces se le pasa className="wideKey".
     */
    keys: KeyButton[];
}

/**
 * MathKeyboard: dibuja un teclado tipo “científico” como el de la imagen,
 * pero con un único menú desplegable “Funciones ▾” en la barra superior,
 * y luego un grid de 7 filas × 5 columnas de teclas, que se pasan por props.
 */
export default function MathKeyboard({ keys }: MathKeyboardProps) {
    return (
        <div className={styles.keyboardContainer}>
            {/* ─── BARRA SUPERIOR (toolbar) ─────────────────────────────── */}
            <div className={styles.toolbar}>
                <button type="button" className={styles.dropdownButton}>
                    Funciones ▾
                </button>
            </div>

            {/* ─── GRID DE TECLAS (7 × 5) ────────────────────────────────── */}
            <div className={styles.grid}>
                {keys.map((key, idx) => (
                    <button
                        key={idx}
                        type="button"
                        className={`${styles.keyButton} ${key.className || ''}`}
                        onClick={key.onClick}
                    >
                        {key.label}
                    </button>
                ))}
            </div>
        </div>
    );
}

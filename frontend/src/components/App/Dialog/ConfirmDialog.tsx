import React from "react";
import styles from "../../../styles/modules/mathkeyboard.module.css";

interface ConfirmDialogProps {
    open: boolean;
    title?: string;
    description?: string;
    confirmLabel?: string;
    cancelLabel?: string;
    onConfirm: () => void;
    onCancel: () => void;
    loading?: boolean;
    danger?: boolean;
    children?: React.ReactNode;
}

export default function ConfirmDialog({
                                          open,
                                          title = "¿Estás seguro?",
                                          description = "Esta acción no se puede deshacer.",
                                          confirmLabel = "Sí, continuar",
                                          cancelLabel = "Cancelar",
                                          onConfirm,
                                          onCancel,
                                          loading = false,
                                          danger = false,
                                          children,
                                      }: ConfirmDialogProps) {
    if (!open) return null;

    return (
        <div className={styles.keyboardModalBackdrop} tabIndex={-1} aria-modal="true" role="dialog">
            <div className={styles.keyboardModal}>
                <div className={styles.keyboardModalHeader}>
                    <span className={styles.keyboardModalTitle}>{title}</span>
                </div>
                <div className={styles.keyboardModalBody}>
                    <div style={{ marginBottom: "1.1em", color: "#888" }}>
                        {description}
                        {children}
                    </div>
                    <div className={styles.keyboardModalActions}>
                        <button
                            type="button"
                            className={`${styles.keyButton} ${danger ? styles.deleteKey : ""}`}
                            style={{ minWidth: 130, marginRight: 10, fontWeight: 500 }}
                            onClick={onConfirm}
                            disabled={loading}
                        >
                            {confirmLabel}
                        </button>
                        <button
                            type="button"
                            className={styles.keyButton}
                            style={{ minWidth: 110, fontWeight: 500 }}
                            onClick={onCancel}
                            disabled={loading}
                        >
                            {cancelLabel}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
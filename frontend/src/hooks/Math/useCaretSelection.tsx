import { useState } from 'react';

/**
 * useCaretSelection
 * Custom React hook to manage caret and text selection state for an input field.
 * Provides state and setters for:
 * - focusedIndex: which input row is focused (or null if none)
 * - caretPosition: the caret (cursor) position in the input
 * - selectionLength: length of the text selection (0 means no selection)
 *
 * @returns {object} State and setters for caret and selection management.
 */
export function useCaretSelection() {
    // Index of the currently focused input (null if none focused)
    const [focusedIndex, setFocusedIndex] = useState<number | null>(null);
    // Caret (cursor) position within the input
    const [caretPosition, setCaretPosition] = useState<number>(0);
    // Text selection length (zero if no selection)
    const [selectionLength, setSelectionLength] = useState<number>(0);

    return {
        focusedIndex,
        setFocusedIndex,
        caretPosition,
        setCaretPosition,
        selectionLength,
        setSelectionLength
    };
}
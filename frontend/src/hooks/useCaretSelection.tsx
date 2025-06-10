import { useState } from 'react';

export function useCaretSelection() {
    const [focusedIndex, setFocusedIndex] = useState<number | null>(null);
    const [caretPosition, setCaretPosition] = useState<number>(0);
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
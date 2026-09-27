import { useEffect, useState } from "react";

interface ConfirmButtonProps {
    onConfirm: () => Promise<void>;
    label?: string;
}

/** Two-step destructive action: the first click arms it for 3 seconds, the second runs it. */
const ConfirmButton = ({ onConfirm, label = "Delete" }: ConfirmButtonProps) => {
    const [armed, setArmed] = useState(false);
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        if (!armed) {
            return undefined;
        }
        const timeout = setTimeout(() => setArmed(false), 3000);
        return () => clearTimeout(timeout);
    }, [armed]);

    const handleClick = async () => {
        if (!armed) {
            setArmed(true);
            return;
        }
        setBusy(true);
        try {
            await onConfirm();
        } finally {
            setBusy(false);
            setArmed(false);
        }
    };

    return (
        <button
            type="button"
            onClick={handleClick}
            disabled={busy}
            className={`text-sm px-2 py-1 rounded ${
                armed ? "bg-red-600 text-white" : "text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/30"
            }`}
        >
            {armed ? "Confirm?" : label}
        </button>
    );
};

export default ConfirmButton;

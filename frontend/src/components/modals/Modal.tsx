import React, { useState } from "react";
import { errorMessage } from "../../api/client";
import { primaryButtonClass } from "../ui";

interface ModalProps {
    title: string;
    onClose: () => void;
    /** Resolve to close the modal; reject to show the error inside it. */
    onSubmit: () => Promise<void>;
    submitLabel?: string;
    children: React.ReactNode;
}

const Modal = ({ title, onClose, onSubmit, submitLabel = "Save", children }: ModalProps) => {
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            await onSubmit();
            onClose();
        } catch (err) {
            setError(errorMessage(err));
            setSubmitting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 px-4" role="dialog" aria-modal="true" aria-label={title}>
            <div className="bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-6 rounded-lg shadow-lg w-full max-w-md">
                <h3 className="text-lg font-semibold mb-4">{title}</h3>
                <form className="space-y-4" onSubmit={handleSubmit}>
                    {children}
                    {error && (
                        <p role="alert" className="text-sm text-red-600 dark:text-red-400">
                            {error}
                        </p>
                    )}
                    <div className="flex justify-end gap-2">
                        <button type="button" onClick={onClose} className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded text-sm">
                            Cancel
                        </button>
                        <button type="submit" disabled={submitting} className={primaryButtonClass}>
                            {submitting ? "Saving…" : submitLabel}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Modal;

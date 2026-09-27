import React from "react";
import { AsyncState } from "../hooks/useAsync";

export const Loading = ({ label = "Loading…" }: { label?: string }) => (
    <p className="text-sm text-gray-500 dark:text-gray-400 py-6 text-center">{label}</p>
);

export const ErrorMessage = ({ message, onRetry }: { message: string; onRetry?: () => void }) => (
    <div
        role="alert"
        className="rounded border border-red-300 bg-red-50 text-red-700 dark:bg-red-900/20 dark:border-red-800 dark:text-red-300 p-3 text-sm flex items-center justify-between gap-4"
    >
        <span>{message}</span>
        {onRetry && (
            <button type="button" onClick={onRetry} className="underline font-medium">
                Retry
            </button>
        )}
    </div>
);

export const Empty = ({ message }: { message: string }) => (
    <p className="text-sm text-gray-500 dark:text-gray-400 py-6 text-center">{message}</p>
);

interface AsyncContentProps<T> {
    state: AsyncState<T>;
    isEmpty?: (data: T) => boolean;
    emptyMessage?: string;
    children: (data: T) => React.ReactNode;
}

/** Loading → error (with retry) → empty → content, in that order of precedence. */
export function AsyncContent<T>({ state, isEmpty, emptyMessage = "Nothing here yet.", children }: AsyncContentProps<T>) {
    if (state.error) {
        return <ErrorMessage message={state.error} onRetry={state.reload} />;
    }
    if (state.data === null) {
        return <Loading />;
    }
    if (isEmpty?.(state.data)) {
        return <Empty message={emptyMessage} />;
    }
    return <>{children(state.data)}</>;
}

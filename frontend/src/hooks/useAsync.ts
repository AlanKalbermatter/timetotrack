import { DependencyList, useCallback, useEffect, useState } from "react";
import { errorMessage } from "../api/client";

export interface AsyncState<T> {
    data: T | null;
    error: string | null;
    loading: boolean;
    reload: () => void;
}

/** Runs `load` on mount, when `deps` change and on reload(). Results arriving after unmount are ignored. */
export function useAsync<T>(load: () => Promise<T>, deps: DependencyList = []): AsyncState<T> {
    const [data, setData] = useState<T | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [version, setVersion] = useState(0);

    useEffect(() => {
        let active = true;
        setLoading(true);
        load()
            .then((result) => {
                if (active) {
                    setData(result);
                    setError(null);
                }
            })
            .catch((err: unknown) => {
                if (active) {
                    setError(errorMessage(err));
                }
            })
            .finally(() => {
                if (active) {
                    setLoading(false);
                }
            });
        return () => {
            active = false;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [...deps, version]);

    const reload = useCallback(() => setVersion((v) => v + 1), []);

    return { data, error, loading, reload };
}

import axios, { AxiosError } from "axios";

const TOKEN_KEY = "ttt.token";

export const tokenStore = {
    get: (): string | null => localStorage.getItem(TOKEN_KEY),
    set: (token: string): void => localStorage.setItem(TOKEN_KEY, token),
    clear: (): void => localStorage.removeItem(TOKEN_KEY),
};

/** Same-origin "/api": the CRA dev proxy or nginx forwards it to the gateway. */
export const api = axios.create({ baseURL: "/api", headers: { "Content-Type": "application/json" } });

api.interceptors.request.use((config) => {
    const token = tokenStore.get();
    if (token) {
        config.headers.set("Authorization", `Bearer ${token}`);
    }
    return config;
});

let onUnauthorized: () => void = () => undefined;

export const setUnauthorizedHandler = (handler: () => void): void => {
    onUnauthorized = handler;
};

api.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
        const isAuthCall = error.config?.url?.startsWith("/auth/") ?? false;
        if (error.response?.status === 401 && !isAuthCall) {
            onUnauthorized();
        }
        return Promise.reject(error);
    },
);

/** A human-readable message: the API's {"error": ...} when present. */
export const errorMessage = (error: unknown): string => {
    if (axios.isAxiosError(error)) {
        const data = error.response?.data as { error?: string } | undefined;
        if (data?.error) {
            return data.error;
        }
        return error.response ? `Request failed (${error.response.status})` : "Cannot reach the server";
    }
    if (error instanceof Error && error.message) {
        return error.message;
    }
    return "Something went wrong";
};

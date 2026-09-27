import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import * as authApi from "../api/auth";
import { setUnauthorizedHandler, tokenStore } from "../api/client";
import { AuthResponse, User } from "../api/types";

const USER_KEY = "ttt.user";

interface AuthContextValue {
    user: User | null;
    isAuthenticated: boolean;
    login: (email: string, password: string) => Promise<void>;
    register: (input: authApi.RegisterInput) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const readStoredUser = (): User | null => {
    if (!tokenStore.get()) {
        return null;
    }
    try {
        const raw = localStorage.getItem(USER_KEY);
        return raw ? (JSON.parse(raw) as User) : null;
    } catch {
        return null;
    }
};

/**
 * Holds the session (JWT + user) in localStorage and exposes login/register/logout.
 * Any 401 from the API logs the user out.
 */
export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(readStoredUser);

    const logout = useCallback(() => {
        tokenStore.clear();
        localStorage.removeItem(USER_KEY);
        setUser(null);
    }, []);

    // Any 401 from the API (expired or revoked token) ends the session.
    useEffect(() => {
        setUnauthorizedHandler(logout);
    }, [logout]);

    const accept = useCallback((response: AuthResponse) => {
        tokenStore.set(response.token);
        localStorage.setItem(USER_KEY, JSON.stringify(response.user));
        setUser(response.user);
    }, []);

    const value = useMemo<AuthContextValue>(
        () => ({
            user,
            isAuthenticated: user !== null,
            login: async (email, password) => accept(await authApi.login(email, password)),
            register: async (input) => accept(await authApi.register(input)),
            logout,
        }),
        [user, accept, logout],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextValue => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};

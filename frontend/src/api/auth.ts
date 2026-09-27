/** Public auth endpoints. Both return a JWT plus the user profile; AuthContext stores them. */
import { api } from "./client";
import { AuthResponse } from "./types";

export interface RegisterInput {
    email: string;
    username: string;
    fullName: string;
    password: string;
}

export const login = async (email: string, password: string): Promise<AuthResponse> =>
    (await api.post<AuthResponse>("/auth/login", { email, password })).data;

export const register = async (input: RegisterInput): Promise<AuthResponse> =>
    (await api.post<AuthResponse>("/auth/register", input)).data;

import api from "./axios";

export interface User {
    id?: number;
    username: string;
    email: string;
    fullName: string;
}

export const getUsers = async (): Promise<User[]> => {
    try {
        const response = await api.get("/users");
        return response.data;
    } catch (err) {
        console.error("Failed to fetch users", err);
        throw err;
    }
};

export const createUser = async (user: any) => {
    const response = await api.post("/users", user);
    return response.data;
};
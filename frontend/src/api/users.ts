import { api } from "./client";
import { User } from "./types";

export const listUsers = async (): Promise<User[]> => (await api.get<User[]>("/users")).data;

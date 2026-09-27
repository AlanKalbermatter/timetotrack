/** Team directory (read-only; accounts are created through registration). */
import { api } from "./client";
import { User } from "./types";

export const listUsers = async (): Promise<User[]> => (await api.get<User[]>("/users")).data;

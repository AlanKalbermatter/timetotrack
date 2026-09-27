import { api } from "./client";
import { Customer } from "./types";

export const listCustomers = async (): Promise<Customer[]> => (await api.get<Customer[]>("/customers")).data;

export const createCustomer = async (name: string): Promise<Customer> =>
    (await api.post<Customer>("/customers", { name })).data;

export const deleteCustomer = async (id: number): Promise<void> => {
    await api.delete(`/customers/${id}`);
};

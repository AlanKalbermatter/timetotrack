/** Projects API: each project belongs to one customer. */
import { api } from "./client";
import { Project } from "./types";

export const listProjects = async (): Promise<Project[]> => (await api.get<Project[]>("/projects")).data;

export const createProject = async (name: string, customerId: number): Promise<Project> =>
    (await api.post<Project>("/projects", { name, customerId })).data;

export const deleteProject = async (id: number): Promise<void> => {
    await api.delete(`/projects/${id}`);
};

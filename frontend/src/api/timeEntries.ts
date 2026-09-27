import { browserTimeZone } from "../utils/time";
import { api } from "./client";
import { Summary, TimeEntry } from "./types";

export interface NewTimeEntryInput {
    projectId: number;
    description?: string;
    from: string;
    to: string;
}

export const listTimeEntries = async (range?: { from: Date; to: Date }): Promise<TimeEntry[]> =>
    (await api.get<TimeEntry[]>("/time-entries", {
        params: range ? { from: range.from.toISOString(), to: range.to.toISOString() } : undefined,
    })).data;

export const createTimeEntry = async (input: NewTimeEntryInput): Promise<TimeEntry> =>
    (await api.post<TimeEntry>("/time-entries", input)).data;

export const deleteTimeEntry = async (id: number): Promise<void> => {
    await api.delete(`/time-entries/${id}`);
};

/** The running timer, or null (the API answers 204). */
export const currentTimer = async (): Promise<TimeEntry | null> => {
    const response = await api.get<TimeEntry>("/time-entries/current");
    return response.status === 204 ? null : response.data;
};

export const startTimer = async (projectId: number, description?: string): Promise<TimeEntry> =>
    (await api.post<TimeEntry>("/time-entries/start", { projectId, description })).data;

export const stopTimer = async (): Promise<TimeEntry> => (await api.post<TimeEntry>("/time-entries/stop")).data;

export const getSummary = async (from: Date, to: Date): Promise<Summary> =>
    (await api.get<Summary>("/time-entries/summary", {
        params: { from: from.toISOString(), to: to.toISOString(), tz: browserTimeZone() },
    })).data;

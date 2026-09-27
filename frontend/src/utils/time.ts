const pad = (n: number): string => String(n).padStart(2, "0");

/** "45m", "1h 05m". */
export const formatDuration = (totalSeconds: number): string => {
    const seconds = Math.max(0, Math.floor(totalSeconds));
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return hours > 0 ? `${hours}h ${pad(minutes)}m` : `${minutes}m`;
};

/** "1:02:05", for the live timer. */
export const formatClock = (totalSeconds: number): string => {
    const seconds = Math.max(0, Math.floor(totalSeconds));
    return `${Math.floor(seconds / 3600)}:${pad(Math.floor((seconds % 3600) / 60))}:${pad(seconds % 60)}`;
};

/** Whole seconds from `fromIso` to `toIso`, or to `now` for a running entry. Never negative. */
export const secondsBetween = (fromIso: string, toIso: string | null, now: Date = new Date()): number => {
    const end = toIso ? new Date(toIso) : now;
    return Math.max(0, Math.floor((end.getTime() - new Date(fromIso).getTime()) / 1000));
};

export const startOfDay = (date: Date): Date => {
    const result = new Date(date);
    result.setHours(0, 0, 0, 0);
    return result;
};

export const addDays = (date: Date, days: number): Date => {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
};

/** Monday 00:00 local time of the week containing `date`. */
export const startOfWeek = (date: Date): Date => {
    const day = startOfDay(date);
    const daysSinceMonday = (day.getDay() + 6) % 7;
    return addDays(day, -daysSinceMonday);
};

/** YYYY-MM-DD in local time; matches the server's byDay keys when the browser time zone is sent. */
export const localDateKey = (date: Date): string =>
    `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

/** Value for <input type="datetime-local">, in local time. */
export const toInputValue = (date: Date): string =>
    `${localDateKey(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}`;

/** A datetime-local value (local time, no offset) as an ISO-8601 UTC string. */
export const fromInputValue = (value: string): string => new Date(value).toISOString();

export const formatDateTime = (iso: string): string =>
    new Date(iso).toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });

export const browserTimeZone = (): string => Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC";

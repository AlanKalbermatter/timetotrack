export interface User {
    id: number;
    username: string;
    email: string;
    fullName: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

export interface Customer {
    id: number;
    name: string;
}

export interface Project {
    id: number;
    name: string;
    customerId: number;
    customerName: string;
}

export interface TimeEntry {
    id: number;
    projectId: number;
    projectName: string;
    description: string | null;
    from: string;
    /** null while the timer is running */
    to: string | null;
}

export interface Summary {
    totalSeconds: number;
    byProject: { projectId: number; projectName: string; seconds: number }[];
    byDay: { date: string; seconds: number }[];
}

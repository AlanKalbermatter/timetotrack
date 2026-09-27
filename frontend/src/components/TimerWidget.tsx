import { useEffect, useState } from "react";
import { errorMessage } from "../api/client";
import { currentTimer, startTimer, stopTimer } from "../api/timeEntries";
import { Project, TimeEntry } from "../api/types";
import { formatClock, secondsBetween } from "../utils/time";
import { Empty, ErrorMessage, Loading } from "./states";
import { cardClass, inputClass, primaryButtonClass } from "./ui";

interface TimerWidgetProps {
    projects: Project[];
    /** Called after the timer starts or stops, so totals can refresh. */
    onChange: () => void;
}

const TimerWidget = ({ projects, onChange }: TimerWidgetProps) => {
    const [running, setRunning] = useState<TimeEntry | null>(null);
    const [loaded, setLoaded] = useState(false);
    const [projectId, setProjectId] = useState<number | null>(null);
    const [description, setDescription] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [busy, setBusy] = useState(false);
    const [now, setNow] = useState(() => new Date());

    useEffect(() => {
        currentTimer()
            .then(setRunning)
            .catch((err: unknown) => setError(errorMessage(err)))
            .finally(() => setLoaded(true));
    }, []);

    useEffect(() => {
        if (!running) {
            return undefined;
        }
        const interval = setInterval(() => setNow(new Date()), 1000);
        return () => clearInterval(interval);
    }, [running]);

    useEffect(() => {
        if (projectId === null && projects.length > 0) {
            setProjectId(projects[0].id);
        }
    }, [projects, projectId]);

    const act = async (action: () => Promise<TimeEntry | null>) => {
        setBusy(true);
        setError(null);
        try {
            setRunning(await action());
            setNow(new Date());
            onChange();
        } catch (err) {
            setError(errorMessage(err));
        } finally {
            setBusy(false);
        }
    };

    const start = () =>
        act(async () => {
            const entry = await startTimer(projectId as number, description.trim() || undefined);
            setDescription("");
            return entry;
        });

    const stop = () =>
        act(async () => {
            await stopTimer();
            return null;
        });

    const renderBody = () => {
        if (!loaded) {
            return <Loading label="Loading timer…" />;
        }
        if (running) {
            return (
                <div className="flex items-center justify-between gap-4 flex-wrap">
                    <div className="min-w-0">
                        <p className="text-sm text-gray-500 dark:text-gray-400">Tracking</p>
                        <p className="font-semibold truncate">{running.projectName}</p>
                        {running.description && <p className="text-sm text-gray-500 dark:text-gray-400 truncate">{running.description}</p>}
                    </div>
                    <div className="flex items-center gap-4">
                        <span className="font-mono text-2xl tabular-nums">{formatClock(secondsBetween(running.from, null, now))}</span>
                        <button type="button" onClick={stop} disabled={busy}
                                className="bg-red-600 hover:bg-red-700 disabled:opacity-60 text-white px-4 py-2 rounded shadow text-sm">
                            Stop
                        </button>
                    </div>
                </div>
            );
        }
        if (projects.length === 0) {
            return <Empty message="Create a customer and a project to start tracking time." />;
        }
        return (
            <div className="flex flex-col sm:flex-row gap-3">
                <select aria-label="Project" value={projectId ?? ""} onChange={(e) => setProjectId(Number(e.target.value))}
                        className={`${inputClass} sm:w-64`}>
                    {projects.map((project) => (
                        <option key={project.id} value={project.id}>
                            {project.customerName} · {project.name}
                        </option>
                    ))}
                </select>
                <input aria-label="Description" placeholder="What are you working on?" maxLength={500}
                       value={description} onChange={(e) => setDescription(e.target.value)} className={inputClass} />
                <button type="button" onClick={start} disabled={busy || projectId === null} className={primaryButtonClass}>
                    Start
                </button>
            </div>
        );
    };

    return (
        <section className={cardClass}>
            <h2 className="text-lg font-semibold mb-4">Timer</h2>
            {renderBody()}
            {error && (
                <div className="mt-4">
                    <ErrorMessage message={error} />
                </div>
            )}
        </section>
    );
};

export default TimerWidget;

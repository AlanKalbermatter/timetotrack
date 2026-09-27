import { useState } from "react";
import { errorMessage } from "../api/client";
import { listProjects } from "../api/projects";
import { createTimeEntry, deleteTimeEntry, listTimeEntries } from "../api/timeEntries";
import ConfirmButton from "../components/ConfirmButton";
import NewTimeEntryModal from "../components/modals/NewTimeEntryModal";
import PageCard from "../components/PageCard";
import { AsyncContent, ErrorMessage } from "../components/states";
import { cellClass, primaryButtonClass, rowClass, tableClass, theadClass } from "../components/ui";
import { useAsync } from "../hooks/useAsync";
import { formatDateTime, formatDuration, secondsBetween } from "../utils/time";

/** The signed-in user's entries from the last 30 days, with manual entry and delete. */
const TimeEntries = () => {
    const entries = useAsync(() => listTimeEntries());
    const projects = useAsync(listProjects);
    const [showModal, setShowModal] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);
    const hasProjects = (projects.data?.length ?? 0) > 0;

    const remove = async (id: number) => {
        setActionError(null);
        try {
            await deleteTimeEntry(id);
            entries.reload();
        } catch (err) {
            setActionError(errorMessage(err));
        }
    };

    return (
        <PageCard
            title="Time entries"
            action={
                <button type="button" onClick={() => setShowModal(true)} disabled={!hasProjects}
                        title={hasProjects ? undefined : "Create a project first"} className={primaryButtonClass}>
                    + New Time Entry
                </button>
            }
        >
            {actionError && (
                <div className="mb-4">
                    <ErrorMessage message={actionError} />
                </div>
            )}
            <AsyncContent state={entries} isEmpty={(list) => list.length === 0}
                          emptyMessage="No time entries in the last 30 days. Start a timer on the dashboard or add one manually.">
                {(list) => (
                    <div className="overflow-x-auto">
                        <table className={tableClass}>
                            <thead className={theadClass}>
                                <tr>
                                    <th className={cellClass}>Project</th>
                                    <th className={cellClass}>Description</th>
                                    <th className={cellClass}>Start</th>
                                    <th className={cellClass}>End</th>
                                    <th className={cellClass}>Duration</th>
                                    <th className={`${cellClass} text-right`}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {list.map((entry) => (
                                    <tr key={entry.id} className={rowClass}>
                                        <td className={cellClass}>{entry.projectName}</td>
                                        <td className={cellClass}>{entry.description ?? "—"}</td>
                                        <td className={`${cellClass} whitespace-nowrap`}>{formatDateTime(entry.from)}</td>
                                        <td className={`${cellClass} whitespace-nowrap`}>
                                            {entry.to ? (
                                                formatDateTime(entry.to)
                                            ) : (
                                                <span className="inline-block px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900/40 dark:text-green-300">
                                                    Running
                                                </span>
                                            )}
                                        </td>
                                        <td className={cellClass}>{entry.to ? formatDuration(secondsBetween(entry.from, entry.to)) : "—"}</td>
                                        <td className={`${cellClass} text-right`}>
                                            <ConfirmButton onConfirm={() => remove(entry.id)} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </AsyncContent>
            <p className="mt-4 text-xs text-gray-500 dark:text-gray-400">Showing your entries from the last 30 days.</p>
            {showModal && projects.data && (
                <NewTimeEntryModal
                    projects={projects.data}
                    onClose={() => setShowModal(false)}
                    onSave={async (input) => {
                        await createTimeEntry(input);
                        entries.reload();
                    }}
                />
            )}
        </PageCard>
    );
};

export default TimeEntries;

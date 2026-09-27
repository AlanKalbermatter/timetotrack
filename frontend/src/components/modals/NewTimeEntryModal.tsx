import { useState } from "react";
import { NewTimeEntryInput } from "../../api/timeEntries";
import { Project } from "../../api/types";
import { fromInputValue, toInputValue } from "../../utils/time";
import { inputClass } from "../ui";
import Modal from "./Modal";

interface NewTimeEntryModalProps {
    projects: Project[];
    onClose: () => void;
    onSave: (input: NewTimeEntryInput) => Promise<void>;
}

const ONE_HOUR_MS = 60 * 60 * 1000;

const NewTimeEntryModal = ({ projects, onClose, onSave }: NewTimeEntryModalProps) => {
    const [projectId, setProjectId] = useState<number>(projects[0]?.id ?? 0);
    const [description, setDescription] = useState("");
    const [from, setFrom] = useState(() => toInputValue(new Date(Date.now() - ONE_HOUR_MS)));
    const [to, setTo] = useState(() => toInputValue(new Date()));

    const submit = async () => {
        if (new Date(to).getTime() <= new Date(from).getTime()) {
            throw new Error("End must be after start");
        }
        await onSave({
            projectId,
            description: description.trim() || undefined,
            from: fromInputValue(from),
            to: fromInputValue(to),
        });
    };

    return (
        <Modal title="New Time Entry" onClose={onClose} onSubmit={submit}>
            <select aria-label="Project" value={projectId} onChange={(e) => setProjectId(Number(e.target.value))} className={inputClass}>
                {projects.map((project) => (
                    <option key={project.id} value={project.id}>
                        {project.customerName} · {project.name}
                    </option>
                ))}
            </select>
            <input aria-label="Description" placeholder="Description (optional)" maxLength={500}
                   value={description} onChange={(e) => setDescription(e.target.value)} className={inputClass} />
            <label className="block text-sm">
                Start
                <input type="datetime-local" required value={from} onChange={(e) => setFrom(e.target.value)} className={`${inputClass} mt-1`} />
            </label>
            <label className="block text-sm">
                End
                <input type="datetime-local" required value={to} onChange={(e) => setTo(e.target.value)} className={`${inputClass} mt-1`} />
            </label>
        </Modal>
    );
};

export default NewTimeEntryModal;

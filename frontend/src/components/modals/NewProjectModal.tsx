import { useState } from "react";
import { Customer } from "../../api/types";
import { inputClass } from "../ui";
import Modal from "./Modal";

interface NewProjectModalProps {
    customers: Customer[];
    onClose: () => void;
    onSave: (name: string, customerId: number) => Promise<void>;
}

const NewProjectModal = ({ customers, onClose, onSave }: NewProjectModalProps) => {
    const [name, setName] = useState("");
    const [customerId, setCustomerId] = useState<number>(customers[0]?.id ?? 0);

    return (
        <Modal title="New Project" onClose={onClose} onSubmit={() => onSave(name, customerId)}>
            <input autoFocus required maxLength={120} placeholder="Project name" aria-label="Project name"
                   value={name} onChange={(e) => setName(e.target.value)} className={inputClass} />
            <select aria-label="Customer" value={customerId} onChange={(e) => setCustomerId(Number(e.target.value))} className={inputClass}>
                {customers.map((customer) => (
                    <option key={customer.id} value={customer.id}>
                        {customer.name}
                    </option>
                ))}
            </select>
        </Modal>
    );
};

export default NewProjectModal;

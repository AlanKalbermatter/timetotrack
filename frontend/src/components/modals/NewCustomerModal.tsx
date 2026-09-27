import { useState } from "react";
import { inputClass } from "../ui";
import Modal from "./Modal";

interface NewCustomerModalProps {
    onClose: () => void;
    onSave: (name: string) => Promise<void>;
}

/** Creates a customer. API errors (e.g. duplicate name) are shown inside the modal. */
const NewCustomerModal = ({ onClose, onSave }: NewCustomerModalProps) => {
    const [name, setName] = useState("");

    return (
        <Modal title="New Customer" onClose={onClose} onSubmit={() => onSave(name)}>
            <input autoFocus required maxLength={120} placeholder="Customer name" aria-label="Customer name"
                   value={name} onChange={(e) => setName(e.target.value)} className={inputClass} />
        </Modal>
    );
};

export default NewCustomerModal;

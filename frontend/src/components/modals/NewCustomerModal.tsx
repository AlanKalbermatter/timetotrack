import React from "react";

interface NewCustomerModalProps {
    onClose: () => void;
    onSave: (data: any) => void;
}

const NewCustomerModal: React.FC<NewCustomerModalProps> = ({ onClose, onSave }) => {
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const dummyData = {
            name: "New Client",
            email: "newclient@example.com",
        };
        onSave(dummyData);
        onClose();
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white dark:bg-gray-800 dark:text-white p-6 rounded-lg shadow-lg w-full max-w-md">
                <h3 className="text-lg font-semibold mb-4">New Customer</h3>
                <form className="space-y-4" onSubmit={handleSubmit}>
                    <input type="text" placeholder="Customer Name" className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600 dark:text-white" />
                    <input type="email" placeholder="Email" className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600 dark:text-white" />

                    <div className="flex justify-end space-x-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded"
                        >
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default NewCustomerModal;
import React, { useState } from "react";
import NewCustomerModal from "../components/modals/NewCustomerModal";

const Customers = () => {
    const [showModal, setShowModal] = useState(false);

    const customers = [
        { id: 1, name: "Acme Inc.", email: "contact@acme.com" },
        { id: 2, name: "Beta LLC", email: "info@beta.com" },
    ];

    const handleSave = (data: any) => {
        console.log("New customer added:", data);
    };

    return (
        <>
            <div className="bg-white dark:bg-[#1E1E2F] text-gray-800 dark:text-gray-100 shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold mb-4">Customers</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-full table-auto text-sm text-left">
                        <thead className="bg-gray-100 dark:bg-[#2A2A3F] text-gray-600 dark:text-gray-300">
                        <tr>
                            <th className="px-4 py-2">Name</th>
                            <th className="px-4 py-2">Email</th>
                        </tr>
                        </thead>
                        <tbody>
                        {customers.map((customer) => (
                            <tr
                                key={customer.id}
                                className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-[#2A2A3F]"
                            >
                                <td className="px-4 py-2">{customer.name}</td>
                                <td className="px-4 py-2">{customer.email}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="flex justify-end mt-4">
                <button
                    onClick={() => setShowModal(true)}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded shadow"
                >
                    + New Customer
                </button>
            </div>

            {showModal && (
                <NewCustomerModal
                    onClose={() => setShowModal(false)}
                    onSave={handleSave}
                />
            )}
        </>
    );
};

export default Customers;
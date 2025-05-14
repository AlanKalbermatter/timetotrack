import React, { useState } from "react";
import NewTimeEntryModal from "../components/modals/NewTimeEntryModal";

const TimeEntries = () => {
    const [showModal, setShowModal] = useState(false);

    const entries = [
        {
            id: 1,
            user: "Alan Kalbermatter",
            project: "Frontend Redesign",
            from: "2025-05-12 09:00",
            to: "2025-05-12 12:00",
            duration: "3h",
        },
        {
            id: 2,
            user: "Laura Fernández",
            project: "API Integration",
            from: "2025-05-12 13:30",
            to: "2025-05-12 16:00",
            duration: "2h 30m",
        },
    ];

    const handleSave = (data: any) => {
        console.log("New Time Entry:", data);
    };

    return (
        <>
            <div className="bg-white dark:bg-[#1E1E2F] shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-4">Time Entries</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-full table-auto text-sm text-left">
                        <thead className="bg-gray-100 dark:bg-[#2F2F40] text-gray-600 dark:text-gray-300">
                        <tr>
                            <th className="px-4 py-2">User</th>
                            <th className="px-4 py-2">Project</th>
                            <th className="px-4 py-2">From</th>
                            <th className="px-4 py-2">To</th>
                            <th className="px-4 py-2">Duration</th>
                        </tr>
                        </thead>
                        <tbody>
                        {entries.map((entry) => (
                            <tr key={entry.id} className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-[#2F2F40]">
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{entry.user}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{entry.project}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{entry.from}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{entry.to}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{entry.duration}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="flex justify-end mt-6">
                <button
                    onClick={() => setShowModal(true)}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded shadow"
                >
                    + New Time Entry
                </button>
            </div>

            {showModal && (
                <NewTimeEntryModal
                    onClose={() => setShowModal(false)}
                    onSave={handleSave}
                />
            )}
        </>
    );
};

export default TimeEntries;
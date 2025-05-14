import WeeklyTimeChart from "../components/charts/WeeklyTimeChart";
import NewTimeEntryModal from "../components/modals/NewTimeEntryModal";
import React, { useState } from "react";

const Dashboard = () => {
    const [showModal, setShowModal] = useState(false);

    const handleSave = (data: any) => {
        console.log("New Time Entry:", data);
    };

    return (
        <>
            <div className="bg-gray-100 dark:bg-[#2A2A3F] min-h-screen p-6 space-y-6">
                {/* Overview Cards */}
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    {[
                        { label: "Tracked Today", value: "3h 45m" },
                        { label: "This Week", value: "18h 20m" },
                        { label: "Projects", value: "6" },
                        { label: "Clients", value: "3" },
                    ].map((item, index) => (
                        <div key={index} className="bg-white dark:bg-[#1E1E2F] shadow rounded-lg p-4">
                            <h2 className="text-sm font-medium text-gray-500 dark:text-gray-300">
                                {item.label}
                            </h2>
                            <p className="mt-2 text-2xl font-semibold text-gray-800 dark:text-white">
                                {item.value}
                            </p>
                        </div>
                    ))}
                </div>

                {/* Recent Time Entries */}
                <div className="bg-white dark:bg-[#1E1E2F] text-gray-800 dark:text-white shadow rounded-lg p-6">
                    <h2 className="text-lg font-semibold mb-4">Recent Time Entries</h2>
                    <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                        {[
                            { id: 1, project: "Website Redesign", duration: "2h", date: "2025-05-09" },
                            { id: 2, project: "Email Campaign", duration: "1h 45m", date: "2025-05-09" },
                        ].map((entry) => (
                            <li key={entry.id} className="py-3 flex justify-between items-center">
                                <div>
                                    <p className="text-sm font-medium text-gray-700 dark:text-gray-200">
                                        {entry.project}
                                    </p>
                                    <p className="text-xs text-gray-400">{entry.date}</p>
                                </div>
                                <p className="text-sm font-semibold text-gray-700 dark:text-gray-100">
                                    {entry.duration}
                                </p>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Quick Actions */}
                <div className="flex justify-end">
                    <button
                        onClick={() => setShowModal(true)}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded shadow"
                    >
                        + New Time Entry
                    </button>
                </div>

                {/* Chart */}
                <div className="bg-white dark:bg-[#1E1E2F] rounded-lg shadow p-6">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-4">
                        Weekly Time Chart
                    </h2>
                    <WeeklyTimeChart
                        labels={["Mon", "Tue", "Wed", "Thu", "Fri"]}
                        data={[2, 3.5, 4, 2.5, 3]}
                    />
                </div>
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

export default Dashboard;
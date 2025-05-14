import React, { useState } from "react";
import NewProjectModal from "../components/modals/NewProjectModal";

const Projects = () => {
    const [showModal, setShowModal] = useState(false);

    const projects = [
        { id: 1, name: "Website Redesign", customer: "Acme Inc." },
        { id: 2, name: "Mobile App", customer: "Beta LLC" },
    ];

    const handleSave = (data: any) => {
        console.log("New project created:", data);
    };

    return (
        <>
            {/* Card con la tabla */}
            <div className="bg-white dark:bg-[#1E1E2F] shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-4">Projects</h2>

                <div className="overflow-x-auto">
                    <table className="min-w-full table-auto text-sm text-left">
                        <thead className="bg-gray-100 dark:bg-[#2F2F40] text-gray-600 dark:text-gray-300">
                        <tr>
                            <th className="px-4 py-2">Project Name</th>
                            <th className="px-4 py-2">Customer</th>
                        </tr>
                        </thead>
                        <tbody>
                        {projects.map((project) => (
                            <tr key={project.id} className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-[#2F2F40]">
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{project.name}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{project.customer}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Botón fuera del card */}
            <div className="flex justify-end mt-4">
                <button
                    onClick={() => setShowModal(true)}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded shadow"
                >
                    + New Project
                </button>
            </div>

            {showModal && (
                <NewProjectModal
                    onClose={() => setShowModal(false)}
                    onSave={handleSave}
                />
            )}
        </>
    );
};

export default Projects;
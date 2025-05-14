import React, { useState, useEffect } from "react";
import NewUserModal from "../components/modals/NewUserModal";
import { getUsers } from "../api/users";

const Users = () => {
    const [showModal, setShowModal] = useState(false);
    const [users, setUsers] = useState<any[]>([]);

    useEffect(() => {
        getUsers()
            .then(setUsers)
            .catch((error) => console.error("Failed to fetch users:", error));
    }, []);

    const handleSave = (newUser: any) => {
        setUsers((prev) => [...prev, { id: prev.length + 1, ...newUser }]);
    };

    return (
        <>
            <div className="bg-white dark:bg-[#1E1E2F] shadow rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-4">Users</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-full table-auto text-sm text-left">
                        <thead className="bg-gray-100 dark:bg-[#2F2F40] text-gray-600 dark:text-gray-300">
                        <tr>
                            <th className="px-4 py-2">Full Name</th>
                            <th className="px-4 py-2">Email</th>
                            <th className="px-4 py-2">Username</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((user) => (
                            <tr key={user.id} className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-[#2F2F40]">
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{user.fullName}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{user.email}</td>
                                <td className="px-4 py-2 text-gray-800 dark:text-gray-100">{user.username}</td>
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
                    + New User
                </button>
            </div>

            {showModal && (
                <NewUserModal onClose={() => setShowModal(false)} onSave={handleSave} />
            )}
        </>
    );
};

export default Users;
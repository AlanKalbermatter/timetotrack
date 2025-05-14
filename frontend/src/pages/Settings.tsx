import React, { useEffect, useState } from "react";

const Settings = () => {
    const [darkMode, setDarkMode] = useState<boolean>(() => {
        const saved = localStorage.getItem("darkMode");
        return saved === "true";
    });

    useEffect(() => {
        if (darkMode) {
            document.documentElement.classList.add("dark");
        } else {
            document.documentElement.classList.remove("dark");
        }
        localStorage.setItem("darkMode", darkMode.toString());
    }, [darkMode]);

    return (
        <div className="bg-white dark:bg-gray-900 text-gray-800 dark:text-white shadow rounded-lg p-6">
            <h2 className="text-lg font-semibold mb-4">Settings</h2>
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <span>Dark Mode</span>
                    <label className="inline-flex items-center cursor-pointer">
                        <input
                            type="checkbox"
                            checked={darkMode}
                            onChange={() => setDarkMode(!darkMode)}
                            className="sr-only"
                        />
                        <div className="w-11 h-6 bg-gray-300 dark:bg-gray-600 rounded-full relative">
                            <div
                                className={`absolute left-1 top-1 w-4 h-4 rounded-full bg-white transition-transform ${
                                    darkMode ? "translate-x-5" : ""
                                }`}
                            ></div>
                        </div>
                        <span className="ml-2 text-sm">
                            {darkMode ? "On" : "Off"}
                        </span>
                    </label>
                </div>
            </div>
        </div>
    );
};

export default Settings;
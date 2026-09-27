import PageCard from "../components/PageCard";
import { useTheme } from "../context/ThemeContext";

const Settings = () => {
    const { darkMode, toggleDarkMode } = useTheme();

    return (
        <PageCard title="Settings">
            <div className="flex items-center justify-between">
                <span id="dark-mode-label">Dark mode</span>
                <button
                    type="button"
                    role="switch"
                    aria-checked={darkMode}
                    aria-labelledby="dark-mode-label"
                    onClick={toggleDarkMode}
                    className={`w-11 h-6 rounded-full relative transition-colors ${darkMode ? "bg-indigo-600" : "bg-gray-300"}`}
                >
                    <span className={`absolute left-1 top-1 w-4 h-4 rounded-full bg-white transition-transform ${darkMode ? "translate-x-5" : ""}`} />
                </button>
            </div>
        </PageCard>
    );
};

export default Settings;

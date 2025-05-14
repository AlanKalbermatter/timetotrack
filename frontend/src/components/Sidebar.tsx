import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
    FaCog as FaCogIcon,
    FaTachometerAlt as FaTachometerAltIcon,
    FaClock as FaClockIcon,
    FaProjectDiagram as FaProjectDiagramIcon,
    FaUsers as FaUsersIcon,
    FaUserTie as FaUserTieIcon
} from "react-icons/fa";

// Workarounds for TypeScript JSX component typing
const FaCog = FaCogIcon as unknown as React.FC<{ size?: number }>;
const FaTachometerAlt = FaTachometerAltIcon as unknown as React.FC<{ size?: number }>;
const FaClock = FaClockIcon as unknown as React.FC<{ size?: number }>;
const FaProjectDiagram = FaProjectDiagramIcon as unknown as React.FC<{ size?: number }>;
const FaUsers = FaUsersIcon as unknown as React.FC<{ size?: number }>;
const FaUserTie = FaUserTieIcon as unknown as React.FC<{ size?: number }>;

const Sidebar = () => {
    const [collapsed, setCollapsed] = useState(false);

    const navItems = [
        { label: "Dashboard", icon: <FaTachometerAlt size={18} />, to: "/" },
        { label: "Time Entries", icon: <FaClock size={18} />, to: "/time-entries" },
        { label: "Projects", icon: <FaProjectDiagram size={18} />, to: "/projects" },
        { label: "Customers", icon: <FaUsers size={18} />, to: "/customers" },
        { label: "Users", icon: <FaUserTie size={18} />, to: "/users" }
    ];

    return (
        <div
            className={`bg-[#1E1E2F] text-[#A0A0B2] h-screen p-4 flex flex-col justify-between
            transition-all duration-300 ease-in-out
            ${collapsed ? "w-16" : "w-48"}`}
        >
            {/* Collapse Button */}
            <div className={`mb-4 flex ${collapsed ? "justify-center" : "justify-end"}`}>
                <button
                    onClick={() => setCollapsed(!collapsed)}
                    className="text-sm p-2 rounded hover:bg-[#2A2A3F]"
                    title={collapsed ? "Expand Sidebar" : "Collapse Sidebar"}
                >
                    {collapsed ? "▶" : "◀"}
                </button>
            </div>

            {/* Navigation Links */}
            <ul className="space-y-4 mt-4 flex-1">
                {navItems.map(({ label, icon, to }) => (
                    <li key={label} title={collapsed ? label : ""}>
                        <NavLink
                            to={to}
                            className={({ isActive }) =>
                                `flex items-center gap-3 p-2 rounded transition-colors hover:text-[#A970FF] hover:bg-[#2A2A3F] ${
                                    isActive ? "text-[#A970FF] font-medium" : ""
                                }`
                            }
                        >
                            {icon}
                            {!collapsed && <span>{label}</span>}
                        </NavLink>
                    </li>
                ))}
            </ul>

            {/* Settings */}
            <div>
                <NavLink
                    to="/settings"
                    className={({ isActive }) =>
                        `flex items-center justify-center gap-2 text-[#A0A0B2] hover:text-[#A970FF] p-2 rounded hover:bg-[#2A2A3F] w-full ${
                            isActive ? "text-[#A970FF] font-medium" : ""
                        }`
                    }
                    title="Settings"
                >
                    <FaCog size={18} />
                    {!collapsed && <span>Settings</span>}
                </NavLink>
            </div>
        </div>
    );
};

export default Sidebar;
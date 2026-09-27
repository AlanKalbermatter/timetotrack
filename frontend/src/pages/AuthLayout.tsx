import React from "react";
import { cardClass } from "../components/ui";

interface AuthLayoutProps {
    title: string;
    footer: React.ReactNode;
    children: React.ReactNode;
}

const AuthLayout = ({ title, footer, children }: AuthLayoutProps) => (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-[#12121A] px-4">
        <div className="w-full max-w-sm">
            <h1 className="text-center text-2xl font-semibold mb-6 text-gray-800 dark:text-white">TimeToTrack</h1>
            <div className={cardClass}>
                <h2 className="text-lg font-semibold mb-4">{title}</h2>
                {children}
            </div>
            <p className="text-center text-sm mt-4 text-gray-600 dark:text-gray-400">{footer}</p>
        </div>
    </div>
);

export default AuthLayout;

import React from "react";
import { cardClass } from "./ui";

interface PageCardProps {
    title: string;
    action?: React.ReactNode;
    children: React.ReactNode;
}

/** Card with a title row and an optional action (usually a "+ New" button). */
const PageCard = ({ title, action, children }: PageCardProps) => (
    <section className={cardClass}>
        <div className="flex items-center justify-between gap-4 mb-4">
            <h2 className="text-lg font-semibold">{title}</h2>
            {action}
        </div>
        {children}
    </section>
);

export default PageCard;

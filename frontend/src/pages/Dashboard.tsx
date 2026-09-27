import { listCustomers } from "../api/customers";
import { listProjects } from "../api/projects";
import { getSummary, listTimeEntries } from "../api/timeEntries";
import WeeklyTimeChart from "../components/charts/WeeklyTimeChart";
import PageCard from "../components/PageCard";
import { AsyncContent, Empty } from "../components/states";
import TimerWidget from "../components/TimerWidget";
import { cardClass } from "../components/ui";
import { useAsync } from "../hooks/useAsync";
import { addDays, formatDateTime, formatDuration, localDateKey, secondsBetween, startOfDay, startOfWeek } from "../utils/time";

const CHART_DAYS = 7;
const RECENT_ENTRIES = 5;

/** Everything the dashboard shows, fetched in parallel. "Today" and "this week" use the browser's time zone. */
const loadDashboard = async () => {
    const now = new Date();
    const today = startOfDay(now);
    const tomorrow = addDays(today, 1);
    const chartStart = addDays(today, -(CHART_DAYS - 1));

    const [todaySummary, weekSummary, chartSummary, projects, customers, recent] = await Promise.all([
        getSummary(today, tomorrow),
        getSummary(startOfWeek(now), tomorrow),
        getSummary(chartStart, tomorrow),
        listProjects(),
        listCustomers(),
        listTimeEntries({ from: addDays(today, -30), to: tomorrow }),
    ]);

    const days = Array.from({ length: CHART_DAYS }, (_, i) => addDays(chartStart, i));
    const secondsByDay = new Map(chartSummary.byDay.map((day) => [day.date, day.seconds] as [string, number]));

    return {
        todaySeconds: todaySummary.totalSeconds,
        weekSeconds: weekSummary.totalSeconds,
        projects,
        customerCount: customers.length,
        recent: recent.slice(0, RECENT_ENTRIES),
        chart: {
            labels: days.map((day) => day.toLocaleDateString(undefined, { weekday: "short" })),
            hours: days.map((day) => Math.round(((secondsByDay.get(localDateKey(day)) ?? 0) / 3600) * 100) / 100),
        },
    };
};

const Dashboard = () => {
    const dashboard = useAsync(loadDashboard);

    return (
        <div className="space-y-6">
            <AsyncContent state={dashboard}>
                {(data) => (
                    <>
                        <TimerWidget projects={data.projects} onChange={dashboard.reload} />

                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                            {[
                                { label: "Tracked today", value: formatDuration(data.todaySeconds) },
                                { label: "This week", value: formatDuration(data.weekSeconds) },
                                { label: "Projects", value: String(data.projects.length) },
                                { label: "Customers", value: String(data.customerCount) },
                            ].map((card) => (
                                <div key={card.label} className={cardClass}>
                                    <h2 className="text-sm font-medium text-gray-500 dark:text-gray-300">{card.label}</h2>
                                    <p className="mt-2 text-2xl font-semibold">{card.value}</p>
                                </div>
                            ))}
                        </div>

                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <PageCard title="Recent time entries">
                                {data.recent.length === 0 ? (
                                    <Empty message="No entries yet. Start the timer above." />
                                ) : (
                                    <ul className="divide-y divide-gray-200 dark:divide-gray-700">
                                        {data.recent.map((entry) => (
                                            <li key={entry.id} className="py-3 flex justify-between items-center gap-4">
                                                <div className="min-w-0">
                                                    <p className="text-sm font-medium truncate">
                                                        {entry.projectName}
                                                        {entry.description ? ` · ${entry.description}` : ""}
                                                    </p>
                                                    <p className="text-xs text-gray-400">{formatDateTime(entry.from)}</p>
                                                </div>
                                                <p className="text-sm font-semibold whitespace-nowrap">
                                                    {entry.to ? formatDuration(secondsBetween(entry.from, entry.to)) : "Running"}
                                                </p>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </PageCard>
                            <PageCard title="Last 7 days">
                                <WeeklyTimeChart labels={data.chart.labels} data={data.chart.hours} />
                            </PageCard>
                        </div>
                    </>
                )}
            </AsyncContent>
        </div>
    );
};

export default Dashboard;

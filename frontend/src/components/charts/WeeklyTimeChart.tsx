import { Line } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    LineElement,
    CategoryScale,
    LinearScale,
    PointElement,
    Tooltip,
    Legend,
} from 'chart.js';

ChartJS.register(LineElement, CategoryScale, LinearScale, PointElement, Tooltip, Legend);

type WeeklyTimeChartProps = {
    data: number[];
    labels: string[];
};

const WeeklyTimeChart = ({ data, labels }: WeeklyTimeChartProps) => {
    const chartData = {
        labels,
        datasets: [
            {
                label: 'Hours Tracked',
                data,
                fill: false,
                borderColor: '#6366F1',
                backgroundColor: '#6366F1',
                tension: 0.3,
            },
        ],
    };

    const chartOptions = {
        responsive: true,
        plugins: {
            legend: {
                display: true,
                position: 'top' as const,
            },
        },
        scales: {
            y: {
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Hours',
                },
            },
            x: {
                title: {
                    display: true,
                    text: 'Day of Week',
                },
            },
        },
    };

    return <Line data={chartData} options={chartOptions} />;
};

export default WeeklyTimeChart;
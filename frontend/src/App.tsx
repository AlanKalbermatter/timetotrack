import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import MainLayout from "./layout/MainLayout";
import Dashboard from "./pages/Dashboard";
import TimeEntries from "./pages/TimeEntries";
import Projects from "./pages/Projects";
import Customers from "./pages/Customers";
import Users from "./pages/Users";
import Settings from "./pages/Settings";

const App = () => {
    return (
        <Router>
            <div className="dark">
            <MainLayout>
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/time-entries" element={<TimeEntries />} />
                    <Route path="/projects" element={<Projects />} />
                    <Route path="/customers" element={<Customers />} />
                    <Route path="/users" element={<Users />} />
                    <Route path="/Settings" element={<Settings />} />
                </Routes>
            </MainLayout>
            </div>
        </Router>
    );
};

export default App;
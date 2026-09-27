import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import RequireAuth from "./auth/RequireAuth";
import MainLayout from "./layout/MainLayout";
import Customers from "./pages/Customers";
import Dashboard from "./pages/Dashboard";
import Login from "./pages/Login";
import Projects from "./pages/Projects";
import Register from "./pages/Register";
import Settings from "./pages/Settings";
import TimeEntries from "./pages/TimeEntries";
import Users from "./pages/Users";

/** Routing: /login and /register are public; everything else requires a session and renders inside MainLayout. */
const App = () => (
    <Router>
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route
                    path="/*"
                    element={
                        <RequireAuth>
                            <MainLayout>
                                <Routes>
                                    <Route index element={<Dashboard />} />
                                    <Route path="time-entries" element={<TimeEntries />} />
                                    <Route path="projects" element={<Projects />} />
                                    <Route path="customers" element={<Customers />} />
                                    <Route path="users" element={<Users />} />
                                    <Route path="settings" element={<Settings />} />
                                    <Route path="*" element={<Navigate to="/" replace />} />
                                </Routes>
                            </MainLayout>
                        </RequireAuth>
                    }
                />
            </Routes>
        </AuthProvider>
    </Router>
);

export default App;

import React, { useState } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import { errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import { inputClass, primaryButtonClass } from "../components/ui";
import AuthLayout from "./AuthLayout";

const Login = () => {
    const { login, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = (location.state as { from?: string } | null)?.from ?? "/";
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);

    if (isAuthenticated) {
        return <Navigate to={from} replace />;
    }

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            await login(email, password);
            navigate(from, { replace: true });
        } catch (err) {
            setError(errorMessage(err));
            setSubmitting(false);
        }
    };

    return (
        <AuthLayout
            title="Sign in"
            footer={
                <>
                    No account?{" "}
                    <Link to="/register" className="text-indigo-500 hover:underline">
                        Create one
                    </Link>
                </>
            }
        >
            <form onSubmit={handleSubmit} className="space-y-4">
                <input type="email" required autoComplete="email" placeholder="Email" aria-label="Email"
                       value={email} onChange={(e) => setEmail(e.target.value)} className={inputClass} />
                <input type="password" required autoComplete="current-password" placeholder="Password" aria-label="Password"
                       value={password} onChange={(e) => setPassword(e.target.value)} className={inputClass} />
                {error && (
                    <p role="alert" className="text-sm text-red-600 dark:text-red-400">
                        {error}
                    </p>
                )}
                <button type="submit" disabled={submitting} className={`${primaryButtonClass} w-full`}>
                    {submitting ? "Signing in…" : "Sign in"}
                </button>
            </form>
            <p className="mt-4 text-xs text-gray-500 dark:text-gray-400">Demo account: demo@timetotrack.dev / demo1234</p>
        </AuthLayout>
    );
};

export default Login;

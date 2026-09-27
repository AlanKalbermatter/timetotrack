import React, { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import { inputClass, primaryButtonClass } from "../components/ui";
import AuthLayout from "./AuthLayout";

/** Sign-up page. A new account is signed in immediately. */
const Register = () => {
    const { register, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState({ fullName: "", username: "", email: "", password: "" });
    const [error, setError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);

    if (isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    const update = (field: keyof typeof form) => (event: React.ChangeEvent<HTMLInputElement>) =>
        setForm((current) => ({ ...current, [field]: event.target.value }));

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            await register(form);
            navigate("/", { replace: true });
        } catch (err) {
            setError(errorMessage(err));
            setSubmitting(false);
        }
    };

    return (
        <AuthLayout
            title="Create your account"
            footer={
                <>
                    Already registered?{" "}
                    <Link to="/login" className="text-indigo-500 hover:underline">
                        Sign in
                    </Link>
                </>
            }
        >
            <form onSubmit={handleSubmit} className="space-y-4">
                <input required autoComplete="name" placeholder="Full name" aria-label="Full name"
                       value={form.fullName} onChange={update("fullName")} className={inputClass} />
                <input required autoComplete="username" placeholder="Username" aria-label="Username"
                       value={form.username} onChange={update("username")} className={inputClass} />
                <input type="email" required autoComplete="email" placeholder="Email" aria-label="Email"
                       value={form.email} onChange={update("email")} className={inputClass} />
                <input type="password" required minLength={8} autoComplete="new-password" placeholder="Password (8+ characters)"
                       aria-label="Password" value={form.password} onChange={update("password")} className={inputClass} />
                {error && (
                    <p role="alert" className="text-sm text-red-600 dark:text-red-400">
                        {error}
                    </p>
                )}
                <button type="submit" disabled={submitting} className={`${primaryButtonClass} w-full`}>
                    {submitting ? "Creating account…" : "Create account"}
                </button>
            </form>
        </AuthLayout>
    );
};

export default Register;

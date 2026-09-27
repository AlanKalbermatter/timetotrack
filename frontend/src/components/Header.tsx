import { useAuth } from "../auth/AuthContext";

const initialsOf = (fullName: string): string =>
    fullName
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0].toUpperCase())
        .join("");

const Header = () => {
    const { user, logout } = useAuth();

    return (
        <header className="bg-primary text-white px-6 py-4 shadow-md font-rounded w-full">
            <div className="flex items-center justify-between">
                <div className="w-1/3 flex items-center gap-2">
                    <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center text-sm font-semibold" aria-hidden="true">
                        {user ? initialsOf(user.fullName) : ""}
                    </div>
                    <span className="text-sm hidden sm:inline">{user?.fullName}</span>
                </div>
                <div className="w-1/3 text-center">
                    <h1 className="text-lg font-semibold">TimeToTrack</h1>
                </div>
                <div className="w-1/3 flex justify-end">
                    <button type="button" onClick={logout} className="text-sm bg-white/10 hover:bg-white/20 px-3 py-1.5 rounded">
                        Log out
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;

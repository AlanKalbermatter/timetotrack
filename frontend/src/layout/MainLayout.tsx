import Sidebar from "../components/Sidebar";
import Header from "../components/Header";

const MainLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
        <div className="flex h-screen overflow-hidden bg-gray-50 dark:bg-[#12121A] transition-colors duration-300">
            {/* Sidebar with fixed width and no shrink */}
            <div className="flex-shrink-0">
                <Sidebar />
            </div>

            {/* Main context area */}
            <div className="flex-1 flex flex-col min-w-0">
                <Header />
                <main className="flex-1 p-6 overflow-y-auto scrollbar-hide text-gray-900 dark:text-gray-100">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default MainLayout;
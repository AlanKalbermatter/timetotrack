import { FaPlay } from 'react-icons/fa';
const PlayIcon = FaPlay as unknown as React.FC;

const Header = () => {
    return (
        <header className="bg-primary text-white px-6 py-4 shadow-md font-rounded w-full">
            <div className="flex items-center justify-between">
                {/* Left: User profile */}
                <div className="w-1/3 flex justify-start items-center space-x-2">
                    <div className="w-8 h-8 rounded-full bg-gray-200"></div>
                </div>

                {/* Center: App title */}
                <div className="w-1/3 text-center">
                    <h1 className="text-lg font-semibold">TimeToTrack</h1>
                </div>

                {/* Right: Play icon with tooltip */}
                <div className="w-1/3 flex justify-end items-center relative group">
                    <button className="bg-yellow-500 hover:bg-yellow-600 text-white p-2 rounded-full">
                        <PlayIcon />
                    </button>
                    <span className="absolute right-12 top-1/2 transform -translate-y-1/2 bg-black text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity">
            Start Timer
          </span>
                </div>
            </div>
        </header>
    );
};

export default Header;
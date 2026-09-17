import { Briefcase } from "lucide-react";

export function ProfileWork() {
  return (
    <div className="bg-white p-4 rounded-xl shadow-sm">

      <h2 className="text-lg font-bold mb-3 text-gray-900">
        Công việc
      </h2>

      <div className="flex items-center space-x-3 text-sm text-gray-700">
        <Briefcase className="w-5 h-5 text-gray-500 shrink-0" />
        <span>Cao Lãnh</span>
      </div>

      <button className="w-full bg-gray-200 text-gray-800 font-semibold py-1.5 rounded-lg mt-4 hover:bg-gray-300 text-sm transition">
        Thêm công việc
      </button>

    </div>
  );
}
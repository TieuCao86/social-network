import { GraduationCap } from "lucide-react";

export function ProfileEducation() {
  return (
    <div className="bg-white p-4 rounded-xl shadow-sm">

      <h2 className="text-lg font-bold mb-3 text-gray-900">
        Giáo dục
      </h2>

      <div className="flex items-center space-x-3 text-sm text-gray-700">
        <GraduationCap className="w-5 h-5 text-gray-500 shrink-0" />
        <span>THPT TP Cao Lãnh Confessions</span>
      </div>

    </div>
  );
}
import { ChevronDown } from "lucide-react";

export function ProfileTabs() {
  return (
    <div className="flex space-x-1 border-t border-gray-200 pt-1 overflow-x-auto text-sm">

      <button className="px-4 py-3 font-semibold text-teal-700 border-b-4 border-teal-600">
        Tất cả
      </button>

      <button className="px-4 py-3 font-semibold text-gray-600 hover:bg-gray-100 rounded-md transition">
        Giới thiệu
      </button>

      <button className="px-4 py-3 font-semibold text-gray-600 hover:bg-gray-100 rounded-md transition">
        Bạn bè
      </button>

      <button className="px-4 py-3 font-semibold text-gray-600 hover:bg-gray-100 rounded-md transition">
        Ảnh
      </button>

      <button className="px-4 py-3 font-semibold text-gray-600 hover:bg-gray-100 rounded-md transition">
        Reels
      </button>

      <button className="px-4 py-3 font-semibold text-gray-600 hover:bg-gray-100 rounded-md flex items-center space-x-1 transition">
        <span>Xem thêm</span>
        <ChevronDown className="w-3 h-3" />
      </button>

    </div>
  );
}
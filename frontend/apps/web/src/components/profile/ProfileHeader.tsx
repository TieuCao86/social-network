import { Plus, Pencil, MoreHorizontal } from "lucide-react";

export function ProfileHeader() {
  return (
    <div className="px-4 md:px-8 pb-4 border-b border-gray-200">
      <div className="flex flex-col md:flex-row items-center md:items-end justify-between -mt-16 mb-4">

        <div className="flex flex-col md:flex-row items-center md:items-end space-y-4 md:space-y-0 md:space-x-6">

          <div className="relative w-40 h-40 rounded-full border-4 border-white overflow-hidden shadow-lg bg-white">
            <img
              src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80"
              alt="Profile Avatar"
              className="w-full h-full object-cover"
            />
          </div>

          <div className="text-center md:text-left mb-2">
            <h1 className="text-2xl font-bold text-gray-900">
              Cao Quốc Trung
            </h1>

            <p className="text-gray-600 text-sm font-semibold">
              659 người bạn
            </p>

            <p className="text-gray-600 text-sm mt-1">
              Chỉ yêu mình em AT
            </p>
          </div>

        </div>

        <div className="flex space-x-2 mt-4 md:mt-0">

          <button className="bg-teal-600 text-white px-4 py-2 rounded-md font-semibold text-sm hover:bg-teal-700 flex items-center space-x-2 transition">
            <Plus className="w-4 h-4" />
            <span>Thêm vào tin</span>
          </button>

          <button className="bg-gray-200 text-gray-800 px-4 py-2 rounded-md font-semibold text-sm hover:bg-gray-300 flex items-center space-x-2 transition">
            <Pencil className="w-4 h-4" />
            <span>Chỉnh sửa trang cá nhân</span>
          </button>

          <button className="bg-gray-200 text-gray-800 px-3 py-2 rounded-md font-semibold text-sm hover:bg-gray-300 transition">
            <MoreHorizontal className="w-4 h-4" />
          </button>

        </div>
      </div>
    </div>
  );
}
import {
  Home,
  MapPin,
  Heart,
  User,
} from "lucide-react";

export function ProfileIntro() {
  return (
    <div className="bg-white p-4 rounded-xl shadow-sm">

      <h2 className="text-lg font-bold mb-3 text-gray-900">
        Thông tin cá nhân
      </h2>

      <ul className="space-y-3 text-sm text-gray-700">

        <li className="flex items-center space-x-3">
          <Home className="w-5 h-5 text-gray-500 shrink-0" />
          <span>
            Sống tại <strong className="text-black">Cao Lãnh</strong>
          </span>
        </li>

        <li className="flex items-center space-x-3">
          <MapPin className="w-5 h-5 text-gray-500 shrink-0" />
          <span>
            Từ <strong className="text-black">Cao Lãnh</strong>
          </span>
        </li>

        <li className="flex items-center space-x-3">
          <Heart className="w-5 h-5 text-gray-500 shrink-0" />
          <span>Đang hẹn hò</span>
        </li>

        <li className="flex items-center space-x-3">
          <User className="w-5 h-5 text-gray-500 shrink-0" />
          <span>Nam</span>
        </li>

      </ul>

      <button className="w-full bg-gray-200 text-gray-800 font-semibold py-1.5 rounded-lg mt-4 hover:bg-gray-300 text-sm transition">
        Chỉnh sửa chi tiết
      </button>

    </div>
  );
}
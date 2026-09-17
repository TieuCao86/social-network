import { Camera } from "lucide-react";

export function ProfileCover() {
  return (
    <div className="relative h-64 md:h-80 bg-gray-300">
      <img
        src="https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1200&q=80"
        alt="Cover Photo"
        className="w-full h-full object-cover"
      />

      <button className="absolute bottom-4 right-4 bg-white px-3 py-1.5 rounded-md font-semibold text-sm shadow hover:bg-gray-100 flex items-center space-x-2 text-gray-800 transition">
        <Camera className="w-4 h-4" />
        <span>Chỉnh sửa ảnh bìa</span>
      </button>
    </div>
  );
}
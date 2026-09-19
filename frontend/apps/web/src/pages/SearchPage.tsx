import { useState } from "react";
import {
  SlidersHorizontal,
  User,
  Users,
  FileText,
  Compass,
  Calendar,
  MapPin,
  ChevronDown,
  MessageSquare,
  UserPlus,
  Check,
  Globe,
  MoreHorizontal,
  Share2,
  ThumbsUp,
} from "lucide-react";
import { Navbar } from "../components/ui/Navbar";
import { PostCard } from "../components/post/PostCard";

export function SearchPage() {
  const [activeTab, setActiveTab] = useState("home");
  const [selectedCategory, setSelectedCategory] = useState("all");

  // State cho các bộ lọc toggle bên trái
  const [newPostsOnly, setNewPostsOnly] = useState(false);
  const [viewedPostsOnly, setViewedPostsOnly] = useState(false);

  // Hardcode dữ liệu mẫu phần "Mọi người" giống ảnh
  const peopleResults = [
    {
      id: 1,
      name: "Nguyễn Thư",
      subtitle: "Bạn bè • Sống tại Cao Lãnh • 266 người theo dõi",
      mutualFriends: "328 bạn chung",
      avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
      actionType: "message", // message | follow | add
      actionText: "Nhắn tin",
    },
    {
      id: 2,
      name: "Thư Thư (Mint)",
      subtitle: "Bạn bè • 237 người theo dõi",
      mutualFriends: "76 bạn chung",
      avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
      actionType: "message",
      actionText: "Nhắn tin",
    },
    {
      id: 3,
      name: "Minh Thư",
      subtitle: "Bạn bè • 1K người theo dõi • Trường Đại học Y Dược Cần Thơ",
      mutualFriends: "165 bạn chung",
      avatar: "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=150",
      actionType: "message",
      actionText: "Nhắn tin",
    },
    {
      id: 4,
      name: "Thư Đoàn",
      subtitle: "Người sáng tạo nội dung số • 1,5K người theo dõi • @thu.doan.753515 • Cao Lãnh",
      mutualFriends: "",
      avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
      actionType: "following",
      actionText: "Đang theo dõi",
    },
    {
      id: 5,
      name: "Nguyễn Thư",
      subtitle: "Bạn bè • 564 người theo dõi",
      mutualFriends: "",
      avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
      actionType: "message",
      actionText: "Nhắn tin",
    },
  ];

  // Mock post kết quả tìm kiếm
  const mockPost = {
    id: "p1",
    author: {
      username: "nguyenthu",
      fullName: "Nguyễn Thư",
      avatarUrl: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
    },
    createdAt: new Date(Date.now() - 15 * 3600 * 1000).toISOString(),
    content: "Nguyễn Thư đã thêm một ảnh mới.",
    mediaList: [
      {
        type: "IMAGE" as const,
        url: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=80",
      },
    ],
    totalReactions: 124,
    commentCount: 18,
    shareCount: 4,
    currentUserReaction: undefined,
    topReactions: [],
  };

  return (
    <div className="min-h-screen bg-slate-100 text-gray-800 flex flex-col">
      {/* Navbar phía trên */}
      <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />

      {/* Main Layout Container */}
      <div className="flex-1 max-w-7xl w-full mx-auto flex flex-col md:flex-row gap-4 p-3 md:p-4">
        
        {/* ================= CỘT TRÁI: BỘ LỌC TÌM KIẾM ================= */}
        <aside className="w-full md:w-80 bg-white p-4 rounded-xl shadow-sm border border-gray-200 h-fit md:sticky md:top-20 space-y-4">
          <h2 className="text-xl font-bold text-gray-900 border-b border-gray-200 pb-3">
            Kết quả tìm kiếm
          </h2>

          <div className="space-y-1">
            <p className="text-xs font-bold text-gray-500 uppercase px-3 py-1 mb-1">
              Bộ lọc
            </p>

            {/* Tất cả */}
            <button
              onClick={() => setSelectedCategory("all")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                selectedCategory === "all"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${selectedCategory === "all" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <SlidersHorizontal className="w-4 h-4" />
              </div>
              <span>Tất cả</span>
            </button>

            {/* Toggle: Bài viết mới đây */}
            <div className="flex items-center justify-between px-3 py-2 hover:bg-gray-50 rounded-lg text-sm text-gray-700">
              <span className="font-medium">Bài viết mới đây</span>
              <label className="relative inline-flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={newPostsOnly}
                  onChange={(e) => setNewPostsOnly(e.target.checked)}
                  className="sr-only peer"
                />
                <div className="w-9 h-5 bg-gray-300 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-teal-600"></div>
              </label>
            </div>

            {/* Toggle: Bài viết bạn đã xem */}
            <div className="flex items-center justify-between px-3 py-2 hover:bg-gray-50 rounded-lg text-sm text-gray-700">
              <span className="font-medium">Bài viết bạn đã xem</span>
              <label className="relative inline-flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={viewedPostsOnly}
                  onChange={(e) => setViewedPostsOnly(e.target.checked)}
                  className="sr-only peer"
                />
                <div className="w-9 h-5 bg-gray-300 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-teal-600"></div>
              </label>
            </div>

            {/* Các dropdown bộ lọc bổ sung */}
            <div className="space-y-1 pt-2 border-t border-gray-100">
              <div className="flex items-center justify-between px-3 py-2 hover:bg-gray-100 rounded-lg text-sm text-gray-700 cursor-pointer">
                <span className="font-medium">Ngày đăng</span>
                <ChevronDown className="w-4 h-4 text-gray-500" />
              </div>
              <div className="flex items-center justify-between px-3 py-2 hover:bg-gray-100 rounded-lg text-sm text-gray-700 cursor-pointer">
                <span className="font-medium">Bài viết của</span>
                <ChevronDown className="w-4 h-4 text-gray-500" />
              </div>
              <div className="flex items-center justify-between px-3 py-2 hover:bg-gray-100 rounded-lg text-sm text-gray-700 cursor-pointer">
                <span className="font-medium">Vị trí được gắn thẻ</span>
                <ChevronDown className="w-4 h-4 text-gray-500" />
              </div>
            </div>

            {/* Danh mục chuyển đổi tìm kiếm */}
            <div className="pt-3 border-t border-gray-100 space-y-1">
              <button
                onClick={() => setSelectedCategory("people")}
                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                  selectedCategory === "people" ? "bg-teal-50 text-teal-700" : "hover:bg-gray-100 text-gray-700"
                }`}
              >
                <User className="w-5 h-5 text-gray-500" />
                <span>Mọi người</span>
              </button>

              <button
                onClick={() => setSelectedCategory("reels")}
                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                  selectedCategory === "reels" ? "bg-teal-50 text-teal-700" : "hover:bg-gray-100 text-gray-700"
                }`}
              >
                <Compass className="w-5 h-5 text-gray-500" />
                <span>Thước phim</span>
              </button>

              <button
                onClick={() => setSelectedCategory("groups")}
                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                  selectedCategory === "groups" ? "bg-teal-50 text-teal-700" : "hover:bg-gray-100 text-gray-700"
                }`}
              >
                <Users className="w-5 h-5 text-gray-500" />
                <span>Nhóm</span>
              </button>
            </div>
          </div>
        </aside>

        {/* ================= CỘT PHẢI: NỘI DUNG KẾT QUẢ TÌM KIẾM ================= */}
        <main className="flex-1 space-y-6">

          {/* 1. KHU VỰC KẾT QUẢ "MỌI NGƯỜI" */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 space-y-4">
            <h3 className="font-bold text-lg text-gray-900 border-b border-gray-100 pb-3">
              Mọi người
            </h3>

            <div className="space-y-3">
              {peopleResults.map((person) => (
                <div
                  key={person.id}
                  className="flex items-center justify-between p-2 hover:bg-slate-50 rounded-xl transition border border-transparent hover:border-gray-200"
                >
                  <div className="flex items-center space-x-3">
                    <img
                      src={person.avatar}
                      alt={person.name}
                      className="w-14 h-14 rounded-full object-cover border border-gray-200 shrink-0"
                    />
                    <div>
                      <h4 className="font-bold text-gray-900 text-sm hover:underline cursor-pointer">
                        {person.name}
                      </h4>
                      <p className="text-xs text-gray-500">{person.subtitle}</p>
                      {person.mutualFriends && (
                        <p className="text-[11px] text-gray-400 mt-0.5 flex items-center gap-1">
                          <span>👥</span> {person.mutualFriends}
                        </p>
                      )}
                    </div>
                  </div>

                  <button
                    className={`px-4 py-1.5 rounded-lg font-semibold text-xs transition shrink-0 ${
                      person.actionType === "following"
                        ? "bg-gray-200 text-gray-800 hover:bg-gray-300"
                        : "bg-teal-50 text-teal-700 hover:bg-teal-100 border border-teal-200"
                    }`}
                  >
                    {person.actionText}
                  </button>
                </div>
              ))}
            </div>

            {/* Nút Xem tất cả */}
            <div className="pt-2 border-t border-gray-100 text-center">
              <button className="w-full py-2 bg-gray-100 hover:bg-gray-200 text-gray-800 font-semibold text-xs rounded-lg transition">
                Xem tất cả
              </button>
            </div>
          </div>

          {/* 2. KHU VỰC BÀI VIẾT LIÊN QUAN */}
          <div className="space-y-4">
            <h3 className="font-bold text-lg text-gray-900 px-1">Bài viết</h3>
            <PostCard post={mockPost} />
          </div>

        </main>
      </div>
    </div>
  );
}
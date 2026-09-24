import { useEffect, useState } from "react";

import {
  SlidersHorizontal,
  User,
  Users,
  Compass,
  ChevronDown,
} from "lucide-react";
import { Navbar } from "../components/ui/Navbar";
import { PostCard } from "../components/post/PostCard";

import { useNavigate, useSearchParams } from "react-router-dom";
import {
  createUserService,
  getApiClient,
  getRelationshipText,
} from "@social/shared";
import type { UserSearchResponse } from "@social/shared";

export function SearchPage() {
  const [activeTab, setActiveTab] = useState("");

  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [selectedCategory, setSelectedCategory] = useState("all");

  // State cho các bộ lọc toggle bên trái
  const [newPostsOnly, setNewPostsOnly] = useState(false);
  const [viewedPostsOnly, setViewedPostsOnly] = useState(false);

  const keyword = searchParams.get("q") || "";

  const [peopleResults, setPeopleResults] = useState<UserSearchResponse[]>([]);
  const [loadingPeople, setLoadingPeople] = useState(false);
  const [peopleError, setPeopleError] = useState<string | null>(null);

  // Mock post kết quả tìm kiếm
  const mockPost = {
    id: "p1",
    author: {
      username: "nguyenthu",
      fullName: "Nguyễn Thư",
      avatarUrl:
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
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

  useEffect(() => {
    const searchUsers = async () => {
      if (!keyword.trim()) {
        setPeopleResults([]);
        return;
      }

      try {
        setLoadingPeople(true);
        setPeopleError(null);

        const client = getApiClient();
        const userService = createUserService(client);

        const response = await userService.searchUsers(keyword, 0, 10);

        setPeopleResults(response.content);
      } catch (error) {
        console.error("Search users error:", error);
        setPeopleError("Không thể tìm kiếm người dùng.");
        setPeopleResults([]);
      } finally {
        setLoadingPeople(false);
      }
    };

    searchUsers();
  }, [keyword]);

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
              <div
                className={`p-2 rounded-full ${selectedCategory === "all" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}
              >
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
                  selectedCategory === "people"
                    ? "bg-teal-50 text-teal-700"
                    : "hover:bg-gray-100 text-gray-700"
                }`}
              >
                <User className="w-5 h-5 text-gray-500" />
                <span>Mọi người</span>
              </button>

              <button
                onClick={() => setSelectedCategory("reels")}
                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                  selectedCategory === "reels"
                    ? "bg-teal-50 text-teal-700"
                    : "hover:bg-gray-100 text-gray-700"
                }`}
              >
                <Compass className="w-5 h-5 text-gray-500" />
                <span>Thước phim</span>
              </button>

              <button
                onClick={() => setSelectedCategory("groups")}
                className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                  selectedCategory === "groups"
                    ? "bg-teal-50 text-teal-700"
                    : "hover:bg-gray-100 text-gray-700"
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
                  key={person.userId}
                  className="flex items-center justify-between p-2 hover:bg-slate-50 rounded-xl transition border border-transparent hover:border-gray-200"
                >
                  <div className="flex items-center space-x-3">
                    <div
                      onClick={() => navigate(`/profile/${person.userId}`)}
                      className="w-14 h-14 rounded-full bg-gray-200 flex items-center justify-center shrink-0 cursor-pointer hover:opacity-80 transition"
                    >
                      <User className="w-7 h-7 text-gray-500" />
                    </div>

                    <div>
                      <h4
                        onClick={() => navigate(`/profile/${person.userId}`)}
                        className="font-bold text-gray-900 text-sm hover:underline cursor-pointer"
                      >
                        {person.fullName || person.username}
                      </h4>

                      <p className="text-xs text-gray-500">
                        @{person.username}
                      </p>

                      <p className="text-[11px] text-gray-400 mt-0.5">
                        {getRelationshipText(person.relationshipStatus)}
                      </p>
                    </div>
                  </div>

                  <button className="px-4 py-1.5 rounded-lg font-semibold text-xs transition shrink-0 bg-teal-50 text-teal-700 hover:bg-teal-100 border border-teal-200">
                    {getRelationshipText(person.relationshipStatus)}
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

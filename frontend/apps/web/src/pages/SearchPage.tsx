import { useState } from "react";
import {
  Search,
  SlidersHorizontal,
  Users,
  User,
  FileText,
  Compass,
  Calendar,
  Globe,
  UserPlus,
  Check,
  ChevronRight,
  MoreHorizontal,
} from "lucide-react";
import { Navbar } from "../components/ui/Navbar";
import { PostCard } from "../components/post/PostCard";

export function SearchPage() {
  const [activeTab, setActiveTab] = useState("all");
  const [filterType, setFilterType] = useState("all"); // all, posts, people, groups, pages

  // Dữ liệu mẫu nhóm tìm kiếm được
  const groupResults = [
    {
      id: 1,
      name: "Hội thanh lý trang sức Vàng 10k 14k 18k Bạc 925",
      privacy: "Công khai",
      members: "428K thành viên",
      postsPerDay: "90+ bài viết/ngày",
      img: "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=150",
      joined: false,
    },
    {
      id: 2,
      name: "Thanh lý trang sức vàng 10K 14K 18K",
      privacy: "Công khai",
      members: "73K thành viên",
      postsPerDay: "90+ bài viết/ngày",
      img: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=150",
      joined: true,
    },
    {
      id: 3,
      name: "Hội Cá Cảnh Dưới 10k - Giao lưu chia sẻ",
      privacy: "Công khai",
      members: "266K thành viên",
      postsPerDay: "90+ bài viết/ngày",
      img: "https://images.unsplash.com/photo-1520302630591-fd1c66edc19d?w=150",
      joined: false,
    },
  ];

  // Dữ liệu mẫu người dùng/bạn bè tìm kiếm được
  const peopleResults = [
    {
      id: 1,
      name: "Nguyễn Văn Tín",
      subtitle: "Học tại Trường Đại học Công nghiệp TP. Hồ Chí Minh",
      mutualFriends: "15 bạn chung",
      img: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
      isFriend: false,
    },
    {
      id: 2,
      name: "Trần Thị Ánh",
      subtitle: "Sống tại Hồ Chí Minh",
      mutualFriends: "3 bạn chung",
      img: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
      isFriend: true,
    },
  ];

  return (
    <div className="min-h-screen bg-slate-100 text-gray-800 flex flex-col">
      {/* Navbar phía trên */}
      <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />

      {/* Main Container */}
      <div className="flex-1 max-w-7xl w-full mx-auto flex flex-col md:flex-row gap-4 p-3 md:p-4">
        
        {/* CỘT TRÁI: BỘ LỌC TÌM KIẾM (Sidebar) */}
        <aside className="w-full md:w-80 bg-white p-4 rounded-xl shadow-sm border border-gray-200 h-fit md:sticky md:top-20 space-y-4">
          <h2 className="text-xl font-bold text-gray-900 border-b border-gray-200 pb-3">
            Kết quả tìm kiếm
          </h2>

          <div className="space-y-1">
            <p className="text-xs font-bold text-gray-500 uppercase px-3 py-1">
              Bộ lọc tìm kiếm
            </p>

            <button
              onClick={() => setFilterType("all")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                filterType === "all"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${filterType === "all" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <SlidersHorizontal className="w-4 h-4" />
              </div>
              <span>Tất cả</span>
            </button>

            <button
              onClick={() => setFilterType("posts")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                filterType === "posts"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${filterType === "posts" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <FileText className="w-4 h-4" />
              </div>
              <span>Bài viết</span>
            </button>

            <button
              onClick={() => setFilterType("people")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                filterType === "people"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${filterType === "people" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <User className="w-4 h-4" />
              </div>
              <span>Mọi người</span>
            </button>

            <button
              onClick={() => setFilterType("groups")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                filterType === "groups"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${filterType === "groups" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <Users className="w-4 h-4" />
              </div>
              <span>Nhóm</span>
            </button>

            <button
              onClick={() => setFilterType("pages")}
              className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg font-semibold text-sm transition ${
                filterType === "pages"
                  ? "bg-teal-50 text-teal-700"
                  : "hover:bg-gray-100 text-gray-700"
              }`}
            >
              <div className={`p-2 rounded-full ${filterType === "pages" ? "bg-teal-600 text-white" : "bg-gray-200 text-gray-700"}`}>
                <Compass className="w-4 h-4" />
              </div>
              <span>Trang</span>
            </button>
          </div>
        </aside>

        {/* CỘT PHẢI: KẾT QUẢ HIỂN THỊ CHÍNH */}
        <main className="flex-1 space-y-6">

          {/* 1. KHU VỰC KẾT QUẢ NHÓM (GROUPS) */}
          {(filterType === "all" || filterType === "groups") && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 space-y-4">
              <div className="flex items-center justify-between border-b border-gray-100 pb-3">
                <h3 className="font-bold text-lg text-gray-900">Nhóm</h3>
                <span className="text-teal-600 text-sm font-semibold cursor-pointer hover:underline">
                  Xem tất cả
                </span>
              </div>

              <div className="space-y-3">
                {groupResults.map((group) => (
                  <div
                    key={group.id}
                    className="flex items-center justify-between p-2 hover:bg-slate-50 rounded-xl transition border border-transparent hover:border-gray-200"
                  >
                    <div className="flex items-center space-x-3">
                      <img
                        src={group.img}
                        alt={group.name}
                        className="w-14 h-14 rounded-xl object-cover border border-gray-200 shrink-0"
                      />
                      <div>
                        <h4 className="font-bold text-gray-900 text-sm hover:underline cursor-pointer">
                          {group.name}
                        </h4>
                        <p className="text-xs text-gray-500">
                          {group.privacy} · {group.members} · {group.postsPerDay}
                        </p>
                      </div>
                    </div>

                    <button
                      className={`px-4 py-1.5 rounded-lg font-semibold text-xs transition flex items-center space-x-1 shrink-0 ${
                        group.joined
                          ? "bg-gray-200 text-gray-800 hover:bg-gray-300"
                          : "bg-teal-50 text-teal-700 hover:bg-teal-100 border border-teal-200"
                      }`}
                    >
                      {group.joined ? (
                        <>
                          <Check className="w-3.5 h-3.5" />
                          <span>Đã tham gia</span>
                        </>
                      ) : (
                        <span>Tham gia</span>
                      )}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 2. KHU VỰC KẾT QUẢ MỌI NGƯỜI / BẠN BÈ (PEOPLE) */}
          {(filterType === "all" || filterType === "people") && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 space-y-4">
              <div className="flex items-center justify-between border-b border-gray-100 pb-3">
                <h3 className="font-bold text-lg text-gray-900">Mọi người</h3>
                <span className="text-teal-600 text-sm font-semibold cursor-pointer hover:underline">
                  Xem tất cả
                </span>
              </div>

              <div className="space-y-3">
                {peopleResults.map((person) => (
                  <div
                    key={person.id}
                    className="flex items-center justify-between p-2 hover:bg-slate-50 rounded-xl transition border border-transparent hover:border-gray-200"
                  >
                    <div className="flex items-center space-x-3">
                      <img
                        src={person.img}
                        alt={person.name}
                        className="w-14 h-14 rounded-full object-cover border border-gray-200 shrink-0"
                      />
                      <div>
                        <h4 className="font-bold text-gray-900 text-sm hover:underline cursor-pointer">
                          {person.name}
                        </h4>
                        <p className="text-xs text-gray-500">{person.subtitle}</p>
                        <p className="text-[11px] text-gray-400 mt-0.5">
                          {person.mutualFriends}
                        </p>
                      </div>
                    </div>

                    <button
                      className={`px-4 py-1.5 rounded-lg font-semibold text-xs transition flex items-center space-x-1.5 shrink-0 ${
                        person.isFriend
                          ? "bg-gray-200 text-gray-800 hover:bg-gray-300"
                          : "bg-teal-600 text-white hover:bg-teal-700"
                      }`}
                    >
                      <UserPlus className="w-3.5 h-3.5" />
                      <span>{person.isFriend ? "Bạn bè" : "Thêm bạn bè"}</span>
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 3. KHU VỰC KẾT QUẢ BÀI VIẾT (POSTS) */}
          {(filterType === "all" || filterType === "posts") && (
            <div className="space-y-4">
              <h3 className="font-bold text-lg text-gray-900 px-1">Bài viết liên quan</h3>
              {/* Bạn có thể dùng chung PostCard sẵn có */}
              <div className="bg-white rounded-xl shadow-sm p-6 text-center text-gray-500">
                Hiển thị các bài viết khớp với từ khóa tìm kiếm của bạn...
              </div>
            </div>
          )}

        </main>
      </div>
    </div>
  );
}
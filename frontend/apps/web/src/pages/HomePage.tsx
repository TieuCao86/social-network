import React, { useState } from "react";
import logoImg_N from "../assets/logo_N.png";
import {
  Home,
  Compass,
  MessageSquare,
  Users,
  Bell,
  User,
  Search,
  Image as ImageIcon,
  Share2,
  ThumbsUp,
  MoreHorizontal,
  X,
  Minus,
  Video,
  Paperclip,
  ThumbsUp as LikeIcon,
  ChevronDown,
  CircleCheck,
  Plus,
  MessageCircle,
} from "lucide-react";

import { useFeed } from "@social/shared";

export function HomePage() {
  const [activeTab, setActiveTab] = useState("home");
  const [chatOpen, setChatOpen] = useState(true);
  const [message, setMessage] = useState("");

  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useFeed();

  const posts = data?.pages.flatMap((page) => page.content) ?? [];

  const groups = [
    {
      id: 1,
      name: "Hội Cá Vàng - Goldfish Việt Nam",
      img: "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=100",
    },
    {
      id: 2,
      name: "Hội chơi cá Longfin - L144 Bristlenose Pleco",
      img: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=100",
    },
    {
      id: 3,
      name: "Hội Cá Thủy Phi Tiên - Giao lưu chia sẻ kinh nghiệm",
      img: "https://images.unsplash.com/photo-1520302630591-fd1c66edc19d?w=100",
    },
    {
      id: 4,
      name: "Hội Cá Bảy Màu TPHCM (Guppy)",
      img: "https://images.unsplash.com/photo-1535591273668-578e31182c4f?w=100",
    },
  ];

  const tabs = [
    { key: "home", label: "Trang chủ", icon: Home },
    { key: "msg", label: "Tin nhắn", icon: MessageSquare },
    { key: "notif", label: "Thông báo", icon: Bell },
    { key: "explore", label: "Khám phá", icon: Compass },
    { key: "profile", label: "Cá nhân", icon: User },
  ];

  const stories = [
    {
      img: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200",
      label: "Storys",
    },
    {
      img: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
      label: "Explore",
    },
    {
      img: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=200",
      label: "Stories",
    },
    {
      img: "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200",
      label: "Star",
    },
  ];

  return (
    <div className="min-h-screen w-full bg-slate-100 flex flex-col text-gray-800 relative">
      {/* ================= HEADER ================= */}
      <header className="bg-white border-b border-gray-200 px-4 md:px-8 py-2.5 flex items-center justify-between sticky top-0 z-30 shadow-sm">
        {/* LOGO & SEARCH */}
        <div className="flex items-center space-x-4 md:space-x-6">
          <div className="flex items-center cursor-pointer">
            <img
              src={logoImg_N}
              alt="Logo"
              className="h-12 md:h-16 w-auto object-contain"
            />
          </div>

          <div className="relative w-44 sm:w-64 md:w-72">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm kiếm..."
              className="w-full bg-slate-100 pl-8 pr-4 py-1.5 rounded-full text-xs focus:outline-none focus:ring-1 focus:ring-teal-500"
            />
          </div>
        </div>

        {/* CENTER TABS (DESKTOP) */}
        <nav className="hidden md:flex items-center space-x-2 lg:space-x-6 text-xs font-semibold text-gray-600">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex flex-col items-center pb-1 pt-1 px-3 transition border-b-2 ${
                  isActive
                    ? "text-teal-700 border-teal-600 font-bold"
                    : "border-transparent hover:text-teal-600"
                }`}
              >
                <Icon className="w-4 h-4 mb-0.5" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </nav>

        {/* HEADER ACTIONS */}
        <div className="flex items-center space-x-2 md:space-x-3">
          <button className="relative w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center hover:bg-slate-200 transition">
            <Bell className="w-4 h-4 text-gray-600" />
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
              2
            </span>
          </button>

          <button className="relative w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center border border-teal-500 hover:bg-slate-200 transition">
            <MessageSquare className="w-4 h-4 text-teal-600" />
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
              7
            </span>
          </button>

          <img
            src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80"
            alt="Avatar"
            className="w-8 h-8 rounded-full object-cover cursor-pointer ring-1 ring-teal-500"
          />
        </div>
      </header>

      {/* ================= MAIN CONTENT ================= */}
      <div className="flex-1 w-full max-w-[1440px] mx-auto grid grid-cols-12 gap-4 lg:gap-6 p-3 md:p-6 pb-20 md:pb-6">
        {/* LEFT SIDEBAR (DESKTOP) */}
        <aside className="hidden lg:block lg:col-span-3 space-y-4">
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-200 text-xs">
            <p className="font-bold text-gray-500 mb-3 uppercase tracking-wider text-[10px]">
              Lối tắt của bạn
            </p>

            <div className="space-y-2">
              {groups.map((group) => (
                <div
                  key={group.id}
                  className="flex items-center space-x-3 cursor-pointer hover:bg-slate-50 p-2 rounded-lg transition"
                >
                  <img
                    src={group.img}
                    alt={group.name}
                    className="w-8 h-8 rounded-lg object-cover shrink-0"
                  />
                  <span className="font-medium text-gray-700 leading-tight">
                    {group.name}
                  </span>
                </div>
              ))}

              <div className="flex items-center space-x-3 cursor-pointer hover:bg-slate-50 p-2 rounded-lg transition">
                <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-700 font-bold flex items-center justify-center text-[10px] shrink-0 border border-teal-100">
                  EXP
                </div>
                <span className="font-medium text-gray-700 leading-tight">
                  EXP GUPPY (Kinh nghiệm nuôi Guppy)
                </span>
              </div>
            </div>

            <button className="flex items-center space-x-2 text-teal-600 font-semibold mt-4 text-xs hover:underline">
              <ChevronDown className="w-3.5 h-3.5" />
              <span>Xem thêm</span>
            </button>
          </div>
        </aside>

        {/* FEED / CENTER CONTAINER */}
        <main className="col-span-12 lg:col-span-6 space-y-4">
          {/* STORIES (VISIBLE ON ALL SCREENS) */}
          <div className="grid grid-cols-4 gap-2">
            {stories.map((story, index) => (
              <div
                key={index}
                className="h-28 sm:h-36 rounded-xl overflow-hidden relative shadow-sm border border-gray-200 group cursor-pointer"
              >
                <img
                  src={story.img}
                  alt={story.label}
                  className="w-full h-full object-cover group-hover:scale-105 transition duration-300"
                />
                <span className="absolute bottom-1.5 left-1.5 text-[10px] font-bold text-white bg-black/50 px-1.5 py-0.5 rounded backdrop-blur-sm">
                  {story.label}
                </span>
              </div>
            ))}
          </div>

          {/* CREATE POST */}
          <div className="bg-white p-3.5 rounded-xl shadow-sm border border-gray-200 text-xs">
            <div className="flex items-center space-x-2.5 mb-2.5">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100"
                alt="Avatar"
                className="w-8 h-8 rounded-full object-cover"
              />
              <span className="font-semibold text-gray-800">Tạo bài viết</span>
            </div>

            <input
              type="text"
              placeholder="Bạn đang nghĩ gì thế?..."
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs focus:outline-none focus:bg-white mb-2.5 focus:border-teal-500"
            />

            <div className="flex items-center justify-between pt-2 border-t border-gray-100">
              <div className="flex space-x-4 text-gray-500 text-xs">
                <button className="hover:text-teal-600 flex items-center space-x-1">
                  <ImageIcon className="w-4 h-4 text-green-500" />
                  <span>Ảnh</span>
                </button>
                <button className="hover:text-teal-600 flex items-center space-x-1">
                  <Video className="w-4 h-4 text-red-500" />
                  <span>Video</span>
                </button>
                <button className="hover:text-teal-600 flex items-center space-x-1">
                  <Share2 className="w-4 h-4 text-blue-500" />
                  <span>Chia sẻ</span>
                </button>
              </div>

              <button className="bg-teal-700 hover:bg-teal-800 text-white font-medium px-4 py-1.5 rounded-md text-xs transition">
                Đăng
              </button>
            </div>
          </div>

          {/* FEED */}
          {isLoading && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 text-center text-sm text-gray-500">
              Đang tải bảng tin...
            </div>
          )}

          {isError && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 text-center text-sm text-red-500">
              Không thể tải bảng tin.
            </div>
          )}

          {!isLoading && !isError && posts.length === 0 && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 text-center text-sm text-gray-500">
              Chưa có bài viết nào.
            </div>
          )}

          {/* POST */}
          {posts.map((post) => (
            <article
              key={post.id}
              className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden text-xs"
            >
              <div className="p-3.5">
                {/* POST CONTENT */}
                <div className="grid grid-cols-1 sm:grid-cols-2 bg-slate-50 rounded-lg overflow-hidden border border-gray-200">
                  {/* MEDIA */}
                  {post.mediaList?.length > 0 && post.mediaList[0].url ? (
                    <img
                      src={post.mediaList[0].url}
                      alt=""
                      className="w-full h-40 sm:h-auto object-cover"
                    />
                  ) : (
                    <div className="w-full h-40 sm:h-auto bg-slate-100 flex items-center justify-center text-gray-400">
                      No image
                    </div>
                  )}

                  {/* CONTENT */}
                  <div className="p-3.5 flex flex-col justify-center bg-gray-50">
                    <p className="font-bold text-gray-800 text-sm leading-snug">
                      {post.content}
                    </p>
                  </div>
                </div>

                {/* AUTHOR */}
                <p className="text-gray-500 text-[11px] mt-2.5">
                  {post.author.fullName || post.author.username}
                  {" · "}@{post.author.username}
                </p>
              </div>

              {/* ACTIONS */}
              <div className="px-3.5 py-2.5 border-t border-gray-100 flex items-center justify-between text-gray-500 text-xs">
                <div className="flex space-x-5">
                  <button className="hover:text-teal-600 flex items-center space-x-1">
                    <ThumbsUp className="w-4 h-4" />
                    <span>{post.totalReactions}</span>
                  </button>

                  <button className="hover:text-teal-600 flex items-center space-x-1">
                    <MessageSquare className="w-4 h-4" />
                    <span>{post.commentCount}</span>
                  </button>

                  <button className="hover:text-teal-600 flex items-center space-x-1">
                    <Share2 className="w-4 h-4" />
                    <span>{post.shareCount}</span>
                  </button>
                </div>

                <CircleCheck className="w-4 h-4 text-teal-600" />
              </div>
            </article>
          ))}

        </main>

        {/* RIGHT SIDEBAR (DESKTOP) */}
        <aside className="hidden lg:block lg:col-span-3 space-y-4">
          <div className="bg-gradient-to-br from-pink-50 to-purple-50 p-3.5 rounded-xl border border-purple-100">
            <p className="font-bold text-gray-500 text-[10px] uppercase mb-2">
              Được tài trợ
            </p>
            <div className="grid grid-cols-2 gap-2">
              <div className="bg-white p-2 rounded-lg border border-purple-100">
                <img
                  src="https://images.unsplash.com/photo-1526947425960-945c6e72858f?w=150"
                  alt="Product"
                  className="h-16 w-full object-cover rounded mb-1.5"
                />
                <p className="font-semibold text-[10px] truncate">
                  Ra Mắt Sản Phẩm Mới
                </p>
                <p className="text-[8px] text-gray-400">connect.com</p>
              </div>

              <div className="bg-white p-2 rounded-lg border border-purple-100">
                <img
                  src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=150"
                  alt="Writers"
                  className="h-16 w-full object-cover rounded mb-1.5"
                />
                <p className="font-semibold text-[10px] truncate">
                  Voice of Writers
                </p>
                <p className="text-[8px] text-gray-400">trusted_by...</p>
              </div>
            </div>
          </div>

          <div className="bg-white p-3.5 rounded-xl shadow-sm border border-gray-200">
            <p className="font-bold text-gray-500 text-[10px] uppercase mb-2.5">
              Xu hướng
            </p>
            <ul className="space-y-2 text-xs">
              <li className="text-teal-700 font-medium hover:underline cursor-pointer">
                #LapTrinhVien
              </li>
              <li className="text-teal-700 font-medium hover:underline cursor-pointer">
                #U23VietNam
              </li>
              <li className="text-teal-700 font-medium hover:underline cursor-pointer">
                #GuppyFish
              </li>
            </ul>
          </div>
        </aside>
      </div>

      {/* ================= FLOATING CHAT BOX (FIXED BOTTOM RIGHT) ================= */}
      {chatOpen ? (
        <div className="fixed bottom-0 right-4 md:right-8 w-72 bg-white rounded-t-xl shadow-2xl border border-gray-200 overflow-hidden text-xs z-40">
          <div className="bg-teal-700 text-white px-3 py-2.5 flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <div className="w-6 h-6 rounded-full bg-teal-800 border border-teal-500 flex items-center justify-center text-[10px]">
                G
              </div>
              <div>
                <p className="font-bold leading-tight">Quốc Tín</p>
                <p className="text-[8px] text-teal-200">Tech Lead k22</p>
              </div>
            </div>

            <div className="flex space-x-2 text-slate-200">
              <button
                onClick={() => setChatOpen(false)}
                className="hover:text-white"
              >
                <Minus className="w-3.5 h-3.5" />
              </button>
              <button
                onClick={() => setChatOpen(false)}
                className="hover:text-white"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>

          <div className="p-3 h-48 bg-slate-50 flex flex-col space-y-2 overflow-y-auto">
            <div className="self-end bg-teal-600 text-white px-2.5 py-1.5 rounded-lg rounded-tr-none text-[11px] max-w-[80%]">
              Rảnh xem tin nhắn nha Tín
            </div>
            <div className="self-start flex items-start space-x-1.5 max-w-[85%]">
              <div className="w-5 h-5 rounded-full bg-gray-300 text-[8px] flex items-center justify-center font-bold shrink-0">
                QT
              </div>
              <div className="bg-gray-200 text-gray-800 px-2.5 py-1 rounded-lg rounded-tl-none text-[10px]">
                Ok bro
              </div>
            </div>
            <div className="text-center text-[8px] text-gray-400 my-0.5">
              13:35
            </div>
            <div className="self-end bg-teal-600 text-white px-2.5 py-1.5 rounded-lg rounded-tr-none text-[10px] max-w-[85%]">
              Thôi chơi ơi nay mệt biếng ghê?
            </div>
          </div>

          <div className="p-2 bg-white border-t border-gray-200 flex items-center space-x-1.5 text-gray-500">
            <Paperclip className="w-4 h-4 cursor-pointer hover:text-teal-600" />
            <ImageIcon className="w-4 h-4 cursor-pointer hover:text-teal-600" />
            <input
              type="text"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Aa"
              className="flex-1 bg-slate-100 rounded-full px-3 py-1 text-[11px] focus:outline-none"
            />
            <LikeIcon className="w-4 h-4 text-teal-600 cursor-pointer" />
          </div>
        </div>
      ) : (
        <button
          onClick={() => setChatOpen(true)}
          className="fixed bottom-4 right-4 md:right-8 bg-teal-700 text-white p-3 rounded-full shadow-xl hover:bg-teal-800 transition z-40"
        >
          <MessageCircle className="w-5 h-5" />
        </button>
      )}

      {/* ================= MOBILE BOTTOM NAVIGATION ================= */}
      <div className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-2 flex items-center justify-around text-gray-500 z-30">
        <button
          onClick={() => setActiveTab("home")}
          className={`flex flex-col items-center text-[10px] ${
            activeTab === "home" ? "text-teal-700 font-bold" : ""
          }`}
        >
          <Home className="w-5 h-5 mb-0.5" />
          <span>Trang chủ</span>
        </button>

        <button
          onClick={() => setActiveTab("explore")}
          className={`flex flex-col items-center text-[10px] ${
            activeTab === "explore" ? "text-teal-700 font-bold" : ""
          }`}
        >
          <Compass className="w-5 h-5 mb-0.5" />
          <span>Khám phá</span>
        </button>

        <button className="w-10 h-10 -mt-5 rounded-full bg-teal-700 text-white flex items-center justify-center shadow-lg border-2 border-white">
          <Plus className="w-5 h-5" />
        </button>

        <button
          onClick={() => setActiveTab("notif")}
          className={`flex flex-col items-center text-[10px] ${
            activeTab === "notif" ? "text-teal-700 font-bold" : ""
          }`}
        >
          <Bell className="w-5 h-5 mb-0.5" />
          <span>Thông báo</span>
        </button>

        <button
          onClick={() => setActiveTab("profile")}
          className={`flex flex-col items-center text-[10px] ${
            activeTab === "profile" ? "text-teal-700 font-bold" : ""
          }`}
        >
          <User className="w-5 h-5 mb-0.5" />
          <span>Cá nhân</span>
        </button>
      </div>
    </div>
  );
}

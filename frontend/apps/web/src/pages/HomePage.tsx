import React, { useState } from "react";
import {
  Home,
  Compass,
  MessageSquare,
  Users,
  Bell,
  User,
  Search,
  Image as ImageIcon,
  Smile,
  Share2,
  ThumbsUp,
  MoreHorizontal,
  X,
  Send,
  Minus,
  ExternalLink,
} from "lucide-react";

export function HomePage() {
  const [activeTab, setActiveTab] = useState("home");
  const [chatOpen, setChatOpen] = useState(true);
  const [message, setMessage] = useState("");

  const groups = [
    {
      id: 1,
      name: "Hội Cá Vàng - Goldfish Việt Nam",
      img: "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=100&auto=format&fit=crop&q=60",
    },
    {
      id: 2,
      name: "Hội chơi cá Longfin - L144 Bristlenose Pleco",
      img: "https://images.unsplash.com/photo-1535591273668-578e31182c4f?w=100&auto=format&fit=crop&q=60",
    },
    {
      id: 3,
      name: "Hội Cá Chép Phụng Tản - Giao lưu chia sẻ kinh nghiệm",
      img: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=100&auto=format&fit=crop&q=60",
    },
    {
      id: 4,
      name: "Hội Cá Bảy Màu TPHCM (Guppy)",
      img: "https://images.unsplash.com/photo-1524704654690-b56c05c78a00?w=100&auto=format&fit=crop&q=60",
    },
  ];

  return (
    <div className="min-h-screen bg-[#F0F2F5] text-slate-800 flex flex-col">
      {/* 1. TOP HEADER */}
      <header className="sticky top-0 z-40 bg-white border-b border-slate-200 px-6 py-2.5 flex items-center justify-between shadow-sm">
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-2">
            <div className="w-9 h-9 rounded-full bg-teal-600 flex items-center justify-center text-white font-bold text-lg shadow-sm">
              CH
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-teal-600 to-cyan-600 bg-clip-text text-transparent">
              ConnectHub
            </span>
          </div>
          <div className="relative w-64 lg:w-80">
            <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Search..."
              className="w-full pl-9 pr-4 py-1.5 bg-slate-100 rounded-full text-sm outline-none focus:ring-2 focus:ring-teal-500/20"
            />
          </div>
        </div>

        <div className="hidden md:flex items-center bg-slate-50 border border-slate-200 rounded-2xl p-1 gap-1">
          {[
            { key: "home", label: "Trang chủ", icon: Home },
            { key: "msg", label: "Tin nhắn", icon: MessageSquare },
            { key: "notif", label: "Thông báo", icon: Bell },
            { key: "explore", label: "Khám phá", icon: Compass },
            { key: "profile", label: "Cá nhân", icon: User },
          ].map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex items-center gap-2 px-4 py-1.5 rounded-xl text-xs font-semibold transition ${
                  isActive
                    ? "bg-teal-600 text-white shadow-sm"
                    : "text-slate-600 hover:bg-slate-200/60"
                }`}
              >
                <Icon className="w-4 h-4" />
                {tab.label}
              </button>
            );
          })}
        </div>

        <div className="flex items-center gap-3">
          <button className="p-2 bg-slate-100 rounded-full hover:bg-slate-200 text-slate-600 relative">
            <Bell className="w-4 h-4" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-rose-500 rounded-full" />
          </button>
          <img
            src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60"
            alt="Avatar"
            className="w-8 h-8 rounded-full object-cover border border-teal-500 cursor-pointer"
          />
        </div>
      </header>

      {/* 2. MAIN LAYOUT */}
      <div className="max-w-7xl w-full mx-auto px-4 py-4 grid grid-cols-12 gap-5 flex-1">
        {/* CỘT TRÁI */}
        <aside className="hidden lg:block col-span-3 space-y-4">
          <div className="bg-white rounded-2xl p-3 border border-slate-200 shadow-sm space-y-1">
            {[
              { label: "Home", icon: Home, active: true },
              { label: "Explore", icon: Compass },
              { label: "Messages", icon: MessageSquare },
              { label: "Groups", icon: Users },
              { label: "Notifications", icon: Bell },
              { label: "Profile", icon: User },
            ].map((item, idx) => {
              const Icon = item.icon;
              return (
                <button
                  key={idx}
                  className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-semibold transition ${
                    item.active
                      ? "bg-gradient-to-r from-teal-600 to-cyan-700 text-white shadow-sm"
                      : "text-slate-600 hover:bg-slate-50"
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {item.label}
                </button>
              );
            })}
          </div>

          <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
              Lối tắt của bạn
            </h3>
            <div className="space-y-3">
              {groups.map((g) => (
                <div
                  key={g.id}
                  className="flex items-center gap-2.5 cursor-pointer group"
                >
                  <img
                    src={g.img}
                    alt={g.name}
                    className="w-8 h-8 rounded-lg object-cover"
                  />
                  <span className="text-xs font-medium text-slate-700 group-hover:text-teal-600 line-clamp-1">
                    {g.name}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </aside>

        {/* CỘT GIỮA - FEED */}
        <main className="col-span-12 lg:col-span-6 space-y-4">
          <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm">
            <div className="flex items-center gap-3 mb-3">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60"
                alt="Avatar"
                className="w-9 h-9 rounded-full object-cover"
              />
              <input
                type="text"
                placeholder="Tạo bài viết mới..."
                className="flex-1 bg-slate-100 rounded-xl px-4 py-2 text-sm outline-none focus:bg-slate-50 border border-transparent focus:border-teal-500/30"
              />
            </div>
            <div className="flex items-center justify-between border-t border-slate-100 pt-3">
              <div className="flex items-center gap-3">
                <button className="flex items-center gap-1 text-xs font-medium text-slate-500 hover:text-teal-600">
                  <ImageIcon className="w-4 h-4 text-emerald-500" /> Ảnh/Video
                </button>
                <button className="flex items-center gap-1 text-xs font-medium text-slate-500 hover:text-teal-600">
                  <Smile className="w-4 h-4 text-amber-500" /> Cảm xúc
                </button>
              </div>
              <button className="px-5 py-1.5 bg-teal-600 hover:bg-teal-700 text-white rounded-xl text-xs font-bold transition">
                Đăng
              </button>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <img
                  src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop&q=60"
                  alt="Author"
                  className="w-9 h-9 rounded-full object-cover"
                />
                <div>
                  <h4 className="text-xs font-bold text-slate-800">
                    Group K22: connecthub.com/groups/k22devs
                  </h4>
                  <p className="text-[11px] text-slate-400">2 giờ trước</p>
                </div>
              </div>
              <MoreHorizontal className="w-4 h-4 text-slate-400 cursor-pointer" />
            </div>

            <div className="px-4 pb-3">
              <p className="text-sm font-medium text-slate-800 mb-2">
                Câu chuyện về các lập trình viên trẻ ngành Công nghệ thông
                tin... 💻🔥
              </p>
            </div>

            <img
              src="https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800&auto=format&fit=crop&q=60"
              alt="Post"
              className="w-full h-64 object-cover"
            />

            <div className="px-4 py-2.5 flex items-center justify-between border-t border-slate-100 text-xs text-slate-500">
              <div className="flex items-center gap-4">
                <button className="flex items-center gap-1.5 hover:text-teal-600 font-semibold">
                  <ThumbsUp className="w-4 h-4" /> 124 Thích
                </button>
                <button className="flex items-center gap-1.5 hover:text-teal-600 font-semibold">
                  <MessageSquare className="w-4 h-4" /> 42 Bình luận
                </button>
              </div>
              <button className="flex items-center gap-1.5 hover:text-teal-600 font-semibold">
                <Share2 className="w-4 h-4" /> Chia sẻ
              </button>
            </div>
          </div>
        </main>

        {/* CỘT PHẢI */}
        <aside className="hidden lg:block col-span-3 space-y-4">
          <div className="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
              Được tài trợ
            </h3>
            <div className="flex items-center gap-3">
              <img
                src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=120&auto=format&fit=crop&q=60"
                alt="Ad"
                className="w-16 h-16 rounded-xl object-cover"
              />
              <div className="space-y-1">
                <h4 className="text-xs font-bold text-slate-800 line-clamp-1">
                  Ra mắt khóa học Spring Boot
                </h4>
                <p className="text-[11px] text-slate-400">connecthub.edu.vn</p>
                <a
                  href="#ad"
                  className="text-[11px] text-teal-600 font-semibold flex items-center gap-1"
                >
                  Xem ngay <ExternalLink className="w-3 h-3" />
                </a>
              </div>
            </div>
          </div>
        </aside>
      </div>

      {/* 3. FLOATING CHAT BOX */}
      {chatOpen && (
        <div className="fixed bottom-4 right-8 w-72 bg-white rounded-2xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col z-50">
          <div className="bg-teal-700 text-white px-3.5 py-2.5 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 bg-emerald-400 rounded-full" />
              <span className="text-xs font-bold">Nhóm Dev K22</span>
            </div>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setChatOpen(false)}
                className="hover:bg-teal-600 p-1 rounded"
              >
                <Minus className="w-3.5 h-3.5" />
              </button>
              <button
                onClick={() => setChatOpen(false)}
                className="hover:bg-teal-600 p-1 rounded"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>

          <div className="p-3 h-52 overflow-y-auto space-y-2 bg-slate-50 text-xs">
            <div className="bg-teal-100 text-teal-900 p-2 rounded-xl rounded-tl-none self-start max-w-[85%]">
              Hôm nay mấy giờ họp đồ án vậy mọi người?
            </div>
            <div className="bg-slate-200 text-slate-800 p-2 rounded-xl rounded-tr-none ml-auto max-w-[85%]">
              Tầm 8h tối nha!
            </div>
          </div>

          <div className="p-2 border-t border-slate-100 bg-white flex items-center gap-1.5">
            <input
              type="text"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Nhập tin nhắn..."
              className="flex-1 px-3 py-1.5 bg-slate-100 rounded-xl text-xs outline-none"
            />
            <button className="p-1.5 bg-teal-600 text-white rounded-xl hover:bg-teal-700">
              <Send className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

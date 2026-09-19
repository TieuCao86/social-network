import { useState, useRef, useEffect } from "react";
import {
  Home,
  Video,
  ShoppingBag,
  Users,
  Gamepad2,
  MessageSquare,
  Bell,
  Search,
  Settings,
  HelpCircle,
  AlertCircle,
  Moon,
  LogOut,
  ChevronRight,
} from "lucide-react";

import { useNavigate } from "react-router-dom";

import logoImg from "../../assets/logo.png";

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
}

export function Navbar({ activeTab, setActiveTab }: NavbarProps) {
  const navigate = useNavigate();

  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setUserMenuOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const tabs = [
    { key: "home", label: "Trang chủ", icon: Home },
    { key: "watch", label: "Phim", icon: Video },
    { key: "shop", label: "Mua sắm", icon: ShoppingBag },
    { key: "groups", label: "Nhóm", icon: Users },
    { key: "games", label: "Trò chơi", icon: Gamepad2 },
  ];

  const handleGoHome = () => {
    setActiveTab("home");
    navigate("/");
  };

  // Hàm xử lý khi nhấn Enter ở ô tìm kiếm
  const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault(); // Ngăn form submit mặc định của trình duyệt
      if (searchQuery.trim()) {
        navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
      }
    }
  };
  return (
    <header className="bg-white border-b border-gray-200 px-4 md:px-8 py-2.5 grid grid-cols-3 items-center sticky top-0 z-30 shadow-sm w-full">
      {/* 1. CỘT TRÁI: Logo & Tìm kiếm */}
      <div className="flex items-center space-x-4 md:space-x-6 justify-start">
        <div
          className="flex items-center cursor-pointer shrink-0"
          onClick={handleGoHome}
        >
          <img
            src={logoImg}
            alt="Logo"
            className="h-12 md:h-16 w-auto object-contain"
          />
        </div>

        <div className="relative w-36 sm:w-56 md:w-64">
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (searchQuery.trim()) {
                navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
              }
            }}
            className="relative w-36 sm:w-56 md:w-64"
          >
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm..."
              className="w-full bg-slate-100 pl-8 pr-4 py-1.5 rounded-full text-xs focus:outline-none focus:ring-1 focus:ring-teal-500"
            />
          </form>
        </div>
      </div>

      {/* 2. CỘT GIỮA: Các Tabs được canh giữa tuyệt đối */}
      <nav className="hidden md:flex items-center justify-center space-x-1 lg:space-x-2 text-xs font-semibold text-gray-600">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;

          return (
            <button
              key={tab.key}
              onClick={() => {
                if (tab.key === "home") {
                  handleGoHome();
                } else {
                  setActiveTab(tab.key);
                }
              }}
              className={`flex flex-col items-center pb-1 pt-1 px-4 transition border-b-2 rounded-lg hover:bg-gray-50 ${
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

      {/* 3. CỘT PHẢI: Thông báo, Tin nhắn & Avatar Menu */}
      <div className="flex items-center space-x-2 md:space-x-3 justify-end">
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

        <div className="relative" ref={menuRef}>
          <img
            src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80"
            alt="Avatar"
            onClick={() => setUserMenuOpen(!userMenuOpen)}
            className="w-8 h-8 rounded-full object-cover cursor-pointer ring-1 ring-teal-500 hover:opacity-90 transition"
          />

          {userMenuOpen && (
            <div className="absolute right-0 top-10 w-80 bg-white rounded-2xl shadow-2xl border border-gray-100 p-3 text-sm z-50">
              <div
                onClick={() => {
                  setUserMenuOpen(false);
                  navigate("/profile");
                }}
                className="bg-white shadow-sm border border-gray-200 rounded-xl p-3 mb-2 hover:bg-gray-50 cursor-pointer transition"
              >
                <div className="flex items-center space-x-3 mb-3">
                  <img
                    src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80"
                    alt="Cao Quốc Trung"
                    className="w-10 h-10 rounded-full object-cover"
                  />

                  <span className="font-bold text-gray-900 text-sm">
                    Cao Quốc Trung
                  </span>
                </div>

                <hr className="border-gray-100 my-2" />

                <div className="flex items-center justify-center space-x-2 text-gray-700 font-semibold text-xs py-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition">
                  <Users className="w-4 h-4" />
                  <span>Xem tất cả trang cá nhân</span>
                </div>
              </div>

              <div className="space-y-1 text-gray-700">
                <div className="flex items-center justify-between p-2 hover:bg-gray-100 rounded-xl cursor-pointer transition">
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
                      <Settings className="w-5 h-5 text-gray-800" />
                    </div>
                    <span className="font-medium text-xs">
                      Cài đặt và quyền riêng tư
                    </span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-gray-500" />
                </div>

                <div className="flex items-center justify-between p-2 hover:bg-gray-100 rounded-xl cursor-pointer transition">
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
                      <HelpCircle className="w-5 h-5 text-gray-800" />
                    </div>
                    <span className="font-medium text-xs">
                      Trợ giúp và hỗ trợ
                    </span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-gray-500" />
                </div>

                <div className="flex items-center justify-between p-2 hover:bg-gray-100 rounded-xl cursor-pointer transition">
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
                      <AlertCircle className="w-5 h-5 text-gray-800" />
                    </div>
                    <div>
                      <p className="font-medium text-xs leading-tight">
                        Báo cáo sự cố
                      </p>
                      <p className="text-[10px] text-gray-400">CTRL B</p>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between p-2 hover:bg-gray-100 rounded-xl cursor-pointer transition">
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
                      <Moon className="w-5 h-5 text-gray-800" />
                    </div>
                    <span className="font-medium text-xs">
                      Màn hình và trợ năng
                    </span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-gray-500" />
                </div>

                <div className="flex items-center justify-between p-2 hover:bg-gray-100 rounded-xl cursor-pointer transition">
                  <div className="flex items-center space-x-3">
                    <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
                      <LogOut className="w-5 h-5 text-gray-800" />
                    </div>
                    <span className="font-medium text-xs">Đăng xuất</span>
                  </div>
                </div>
              </div>

              <hr className="border-gray-100 my-2" />

              <div className="px-2 text-[10px] text-gray-400 leading-relaxed">
                Quyền riêng tư · Điều khoản · Quảng cáo · Lựa chọn quảng cáo ·
                Cookie · Xem thêm
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

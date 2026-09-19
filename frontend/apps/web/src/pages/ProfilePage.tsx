import { useState } from "react";
import {
  Camera,
  Plus,
  Pencil,
  MoreHorizontal,
  Home,
  MapPin,
  Heart,
  User,
  Briefcase,
  GraduationCap,
  Video,
  Image as ImageIcon,
  Flag,
  Sliders,
  Settings,
  ChevronDown,
} from "lucide-react";

import { useUserPosts } from "@social/shared";

import { PostCard } from "../components/post/PostCard";
import { Navbar } from "../components/ui/Navbar";

import { auth } from "../api/client";

export function ProfilePage() {
  const [activeTab, setActiveTab] = useState("profile");

  const { data: userData } = auth.useMe();

  const authorId = userData?.userId ?? "";

  const { data: postsData, isLoading: postsLoading } = useUserPosts(authorId);

  const posts = postsData?.pages.flatMap((page) => page.content) ?? [];

  console.log("userData =", userData);
  console.log("authorId =", authorId);
  console.log("postsData =", postsData);
  console.log("posts =", posts);

  return (
    <div className="min-h-screen w-full bg-slate-100 flex flex-col text-gray-800 relative">
      {/* Navbar full width */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />

      {/* Main Content Container bounded to max-w-5xl */}
      <div className="flex-1 w-full max-w-5xl mx-auto bg-white shadow min-h-screen">
        {/* Cover Photo Section */}
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

        {/* Profile Info Header */}
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

                <p className="text-gray-600 text-sm mt-1">Chỉ yêu mình em AT</p>
              </div>
            </div>

            {/* Action Buttons */}
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

          {/* Profile Navigation Tabs */}
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
        </div>

        {/* Content Section */}
        <div className="bg-slate-100 p-4 flex flex-col md:flex-row gap-4">
          {/* Left Column */}
          <div className="w-full md:w-5/12 space-y-4">
            {/* Intro Card */}
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

            {/* Work Card */}
            <div className="bg-white p-4 rounded-xl shadow-sm">
              <h2 className="text-lg font-bold mb-3 text-gray-900">Công việc</h2>

              <div className="flex items-center space-x-3 text-sm text-gray-700">
                <Briefcase className="w-5 h-5 text-gray-500 shrink-0" />
                <span>Cao Lãnh</span>
              </div>

              <button className="w-full bg-gray-200 text-gray-800 font-semibold py-1.5 rounded-lg mt-4 hover:bg-gray-300 text-sm transition">
                Thêm công việc
              </button>
            </div>

            {/* Education Card */}
            <div className="bg-white p-4 rounded-xl shadow-sm">
              <h2 className="text-lg font-bold mb-3 text-gray-900">Giáo dục</h2>

              <div className="flex items-center space-x-3 text-sm text-gray-700">
                <GraduationCap className="w-5 h-5 text-gray-500 shrink-0" />

                <span>THPT TP Cao Lãnh Confessions</span>
              </div>
            </div>
          </div>

          {/* Right Column */}
          <div className="w-full md:w-7/12 space-y-4">
            {/* Create Post Box */}
            <div className="bg-white p-4 rounded-xl shadow-sm">
              <div className="flex space-x-3 pb-3 border-b border-gray-200">
                <img
                  src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&q=80"
                  alt="Avatar"
                  className="w-10 h-10 rounded-full object-cover"
                />

                <input
                  type="text"
                  placeholder="Bạn đang nghĩ gì?"
                  className="bg-slate-100 rounded-full px-4 py-2 flex-grow focus:outline-none hover:bg-slate-200 cursor-pointer text-sm"
                />
              </div>

              <div className="flex justify-between pt-3">
                <button className="flex items-center space-x-2 text-gray-600 hover:bg-gray-100 px-2 md:px-4 py-2 rounded-lg font-semibold text-xs md:text-sm flex-1 justify-center transition">
                  <Video className="w-5 h-5 text-red-500" />
                  <span>Video trực tiếp</span>
                </button>

                <button className="flex items-center space-x-2 text-gray-600 hover:bg-gray-100 px-2 md:px-4 py-2 rounded-lg font-semibold text-xs md:text-sm flex-1 justify-center transition">
                  <ImageIcon className="w-5 h-5 text-green-500" />
                  <span>Ảnh/video</span>
                </button>

                <button className="flex items-center space-x-2 text-gray-600 hover:bg-gray-100 px-2 md:px-4 py-2 rounded-lg font-semibold text-xs md:text-sm flex-1 justify-center transition">
                  <Flag className="w-5 h-5 text-blue-500" />
                  <span>Sự kiện trong đời</span>
                </button>
              </div>
            </div>

            {/* Feed Control Filter */}
            <div className="bg-white p-4 rounded-xl shadow-sm flex items-center justify-between">
              <h3 className="font-bold text-base text-gray-900">Bài viết</h3>

              <div className="flex space-x-2">
                <button className="bg-gray-200 text-gray-800 px-3 py-1.5 rounded-lg font-semibold text-xs md:text-sm flex items-center space-x-1.5 hover:bg-gray-300 transition">
                  <Sliders className="w-4 h-4" />
                  <span>Bộ lọc</span>
                </button>

                <button className="bg-gray-200 text-gray-800 px-3 py-1.5 rounded-lg font-semibold text-xs md:text-sm flex items-center space-x-1.5 hover:bg-gray-300 transition">
                  <Settings className="w-4 h-4" />
                  <span>Quản lý bài viết</span>
                </button>
              </div>
            </div>

            {/* User Posts */}
            {postsLoading ? (
              <div className="bg-white rounded-xl shadow-sm p-8 text-center text-gray-500">
                Đang tải bài viết...
              </div>
            ) : posts.length === 0 ? (
              <div className="bg-white rounded-xl shadow-sm p-8 text-center text-gray-500">
                Chưa có bài viết nào.
              </div>
            ) : (
              <div className="space-y-4">
                {posts.map((post) => (
                  <PostCard key={post.postId} post={post} />
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
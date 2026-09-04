import { useState } from "react";

import { useCreatePost } from "@social/shared";

export function CreatePost() {
  const [content, setContent] = useState("");

  const createPost = useCreatePost();

  const handleCreatePost = () => {
    const trimmedContent = content.trim();

    if (!trimmedContent) {
      return;
    }

    createPost.mutate(
      {
        content: trimmedContent,
        visibility: "PUBLIC",
      },
      {
        onSuccess: (response) => {
          if (response.success) {
            setContent("");
            console.log("Tạo bài viết thành công", response.data);
          }
        },
        onError: (error) => {
          console.error("Tạo bài viết thất bại", error);
        },
      },
    );
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-3.5">
      <div className="flex items-center space-x-2.5 mb-3">
        <img
          src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100"
          alt="Avatar"
          className="w-8 h-8 rounded-full object-cover"
        />

        <span className="font-semibold text-gray-800">Tạo bài viết</span>
      </div>

      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="Bạn đang nghĩ gì?"
        className="w-full border border-gray-200 rounded-lg p-3 text-sm outline-none focus:border-teal-500 resize-none"
        rows={3}
        disabled={createPost.isPending}
      />

      <div className="flex justify-end mt-3">
        <button
          onClick={handleCreatePost}
          disabled={!content.trim() || createPost.isPending}
          className="px-4 py-2 bg-teal-600 text-white rounded-lg text-sm hover:bg-teal-700 disabled:opacity-50"
        >
          {createPost.isPending ? "Đang đăng..." : "Đăng bài"}
        </button>
      </div>
    </div>
  );
}

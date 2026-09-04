import { CircleCheck, MessageSquare, Share2, ThumbsUp } from "lucide-react";

import type { PostResponse } from "@social/shared";

interface PostCardProps {
  post: PostResponse;
}

export function PostCard({ post }: PostCardProps) {
  const firstMedia = post.mediaList?.[0];

  return (
    <article className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden text-xs">
      <div className="p-3.5">
        {/* POST CONTENT */}
        <div className="grid grid-cols-1 sm:grid-cols-2 bg-slate-50 rounded-lg overflow-hidden border border-gray-200">
          {/* MEDIA */}
          {firstMedia ? (
            <div className="w-full h-40 sm:h-auto bg-slate-100 flex items-center justify-center text-gray-400">
              {/* Chưa có File API nên chưa thể lấy URL */}
              <span>{firstMedia.type === "IMAGE" ? "Image" : "Video"}</span>
            </div>
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
  );
}

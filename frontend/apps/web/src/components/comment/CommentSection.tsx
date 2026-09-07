import { MessageSquare } from "lucide-react";

import { useComments } from "@social/shared";

interface CommentSectionProps {
  postId: string;
  commentCount: number;
}

export function CommentSection({
  postId,
  commentCount,
}: CommentSectionProps) {

  const {
    data,
    isLoading,
    isError,
  } = useComments(postId, true);

  const comments = data?.content ?? [];

  return (
    <div className="border-t border-gray-100 bg-slate-50 p-3.5">

      {/* ========================================================
          HEADER
      ======================================================== */}

      <div className="flex items-center gap-2 mb-3">

        <MessageSquare className="w-4 h-4 text-teal-600" />

        <span className="font-semibold text-gray-700">
          Bình luận
        </span>

        <span className="text-gray-400">
          ({commentCount})
        </span>

      </div>


      {/* ========================================================
          LOADING
      ======================================================== */}

      {isLoading && (
        <div className="text-center text-gray-400 py-4">
          Đang tải bình luận...
        </div>
      )}


      {/* ========================================================
          ERROR
      ======================================================== */}

      {isError && (
        <div className="text-center text-red-400 py-4">
          Không thể tải bình luận.
        </div>
      )}


      {/* ========================================================
          EMPTY
      ======================================================== */}

      {!isLoading && !isError && comments.length === 0 && (
        <div className="text-center text-gray-400 py-4">
          Chưa có bình luận nào.
        </div>
      )}


      {/* ========================================================
          COMMENT LIST
      ======================================================== */}

      {!isLoading && !isError && comments.length > 0 && (
        <div className="space-y-3">

          {comments.map((comment) => (

            <div
              key={comment.id}
              className="flex gap-2.5"
            >

              {/* AVATAR */}

              <div className="flex-shrink-0">

                <div className="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center text-teal-700 text-xs font-semibold">
                  {(
                    comment.author.fullName ||
                    comment.author.username
                  )
                    .charAt(0)
                    .toUpperCase()}
                </div>

              </div>


              {/* COMMENT CONTENT */}

              <div className="min-w-0 flex-1">

                <div className="bg-white border border-gray-200 rounded-lg px-3 py-2">

                  {/* AUTHOR */}

                  <div className="flex items-center gap-1.5">

                    <span className="font-semibold text-gray-800 text-xs">
                      {comment.author.fullName ||
                        comment.author.username}
                    </span>

                    <span className="text-[10px] text-gray-400">
                      @{comment.author.username}
                    </span>

                  </div>


                  {/* TEXT */}

                  {comment.content && (
                    <p className="text-gray-700 text-xs mt-1 whitespace-pre-wrap break-words">
                      {comment.content}
                    </p>
                  )}

                </div>


                {/* COMMENT META */}

                <div className="flex items-center gap-3 mt-1 px-1">

                  <span className="text-[10px] text-gray-400">
                    {new Date(
                      comment.createdAt
                    ).toLocaleString("vi-VN")}
                  </span>

                  {comment.replyCount > 0 && (
                    <button
                      type="button"
                      className="text-[10px] font-medium text-gray-500 hover:text-teal-600"
                    >
                      {comment.replyCount} phản hồi
                    </button>
                  )}

                </div>

              </div>

            </div>
          ))}

        </div>
      )}

    </div>
  );
}
import { useState } from "react";
import { MessageSquare, ThumbsUp, ThumbsDown, ChevronDown } from "lucide-react";

import {
  useInfiniteComments,
  useInfiniteReplies,
  useCreateComment,
  formatRelativeTime,
} from "@social/shared";

interface CommentSectionProps {
  postId: string;
  commentCount: number;
}

// ========================================================
// REPLY LIST
// ========================================================

function ReplyList({ commentId }: { commentId: string }) {
  const {
    data,
    isLoading,
    isError,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
  } = useInfiniteReplies(commentId, 10);

  const replies = data?.pages.flatMap((page) => page.content) ?? [];

  if (isLoading) {
    return (
      <div className="mt-3 ml-2 text-sm text-gray-500">
        Đang tải phản hồi...
      </div>
    );
  }

  if (isError) {
    return (
      <div className="mt-3 ml-2 text-sm text-red-500">
        Không thể tải phản hồi.
      </div>
    );
  }

  if (replies.length === 0) {
    return (
      <div className="mt-3 ml-2 text-sm text-gray-500">Chưa có phản hồi.</div>
    );
  }

  return (
    <div className="mt-3 ml-2 pl-4 border-l-2 border-gray-100 space-y-4">
      {replies.map((reply) => (
        <div key={reply.id} className="flex gap-3">
          {/* AVATAR */}
          <div className="flex-shrink-0">
            {reply.author.avatarUrl ? (
              <img
                src={reply.author.avatarUrl}
                alt="avatar"
                className="w-8 h-8 rounded-full object-cover"
              />
            ) : (
              <div className="w-8 h-8 rounded-full bg-gray-900 flex items-center justify-center text-white text-xs font-semibold">
                {(reply.author.fullName || reply.author.username)
                  .charAt(0)
                  .toUpperCase()}
              </div>
            )}
          </div>

          {/* BODY */}
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-1.5 mb-1">
              <span className="font-semibold text-gray-900 text-sm">
                {reply.author.fullName || reply.author.username}
              </span>

              <span className="text-[13px] text-gray-500">
                · {formatRelativeTime(reply.createdAt)}
              </span>
            </div>

            {reply.content && (
              <p className="text-gray-900 text-sm whitespace-pre-wrap break-words leading-relaxed">
                {reply.content}
              </p>
            )}

            {/* ACTION */}
            <div className="flex items-center gap-1 mt-1">
              <button
                type="button"
                className="flex items-center justify-center w-7 h-7 rounded-full hover:bg-gray-100 text-gray-600"
              >
                <ThumbsUp className="w-3.5 h-3.5" />
              </button>

              <button
                type="button"
                className="flex items-center justify-center w-7 h-7 rounded-full hover:bg-gray-100 text-gray-600"
              >
                <ThumbsDown className="w-3.5 h-3.5" />
              </button>

              <button
                type="button"
                className="text-xs font-semibold text-gray-600 hover:bg-gray-100 px-2.5 py-1 rounded-full"
              >
                Trả lời
              </button>
            </div>
          </div>
        </div>
      ))}

      {/* LOAD MORE REPLIES */}
      {hasNextPage && (
        <div className="flex justify-center pt-1">
          <button
            type="button"
            onClick={() => fetchNextPage()}
            disabled={isFetchingNextPage}
            className="text-xs font-semibold text-gray-600 hover:text-blue-600"
          >
            {isFetchingNextPage ? "Đang tải..." : "Xem thêm phản hồi"}
          </button>
        </div>
      )}
    </div>
  );
}

export function CommentSection({ postId, commentCount }: CommentSectionProps) {
  // ========================================================
  // STATE & MUTATION
  // ========================================================

  const [content, setContent] = useState("");

  // Lưu comment nào đang mở replies
  const [expandedReplies, setExpandedReplies] = useState<Set<string>>(
    new Set(),
  );

  const createComment = useCreateComment();

  // ========================================================
  // QUERIES
  // ========================================================

  const {
    data,
    isLoading,
    isError,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
  } = useInfiniteComments(postId, 10);

  const comments = data?.pages.flatMap((page) => page.content) ?? [];

  // ========================================================
  // HANDLERS
  // ========================================================

  const handleSubmit = () => {
    const value = content.trim();

    if (!value || createComment.isPending) {
      return;
    }

    createComment.mutate(
      {
        postId,
        payload: {
          content: value,
          parentId: null,
          mediaList: [],
        },
      },
      {
        onSuccess: () => {
          setContent("");
        },
      },
    );
  };

  // ========================================================
  // TOGGLE REPLIES
  // ========================================================

  const toggleReplies = (commentId: string) => {
    setExpandedReplies((prev) => {
      const next = new Set(prev);

      if (next.has(commentId)) {
        next.delete(commentId);
      } else {
        next.add(commentId);
      }

      return next;
    });
  };

  return (
    <div className="border-t border-gray-100 bg-white p-4">
      {/* ========================================================
          HEADER
      ======================================================== */}

      <div className="flex items-center gap-2 mb-4">
        <MessageSquare className="w-5 h-5 text-gray-700" />

        <span className="font-semibold text-gray-900">Bình luận</span>

        <span className="text-gray-500">({commentCount})</span>
      </div>

      {/* ========================================================
          INPUT
      ======================================================== */}

      <div className="flex gap-3 mb-6">
        <input
          type="text"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              handleSubmit();
            }
          }}
          placeholder="Viết bình luận..."
          maxLength={5000}
          disabled={createComment.isPending}
          className="flex-1 rounded-full border border-gray-300 bg-gray-50 px-4 py-2.5 text-sm outline-none focus:border-gray-500 focus:bg-white transition-colors"
        />

        <button
          type="button"
          onClick={handleSubmit}
          disabled={createComment.isPending || !content.trim()}
          className="rounded-full bg-blue-600 px-5 py-2.5 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50 transition-colors"
        >
          {createComment.isPending ? "Đang gửi..." : "Gửi"}
        </button>
      </div>

      {/* ========================================================
          LOADING
      ======================================================== */}

      {isLoading && (
        <div className="text-center text-gray-500 py-6 text-sm">
          Đang tải bình luận...
        </div>
      )}

      {isError && (
        <div className="text-center text-red-500 py-6 text-sm">
          Không thể tải bình luận.
        </div>
      )}

      {!isLoading && !isError && comments.length === 0 && (
        <div className="text-center text-gray-500 py-6 text-sm">
          Chưa có bình luận nào.
        </div>
      )}

      {/* ========================================================
          COMMENT LIST
      ======================================================== */}

      {!isLoading && !isError && comments.length > 0 && (
        <div className="space-y-6">
          {comments.map((comment) => {
            const isRepliesOpen = expandedReplies.has(comment.id);

            return (
              <div key={comment.id}>
                {/* COMMENT */}
                <div className="flex gap-3.5">
                  {/* AVATAR */}
                  <div className="flex-shrink-0">
                    {comment.author.avatarUrl ? (
                      <img
                        src={comment.author.avatarUrl}
                        alt="avatar"
                        className="w-10 h-10 rounded-full object-cover"
                      />
                    ) : (
                      <div className="w-10 h-10 rounded-full bg-gray-900 flex items-center justify-center text-white text-sm font-semibold">
                        {(comment.author.fullName || comment.author.username)
                          .charAt(0)
                          .toUpperCase()}
                      </div>
                    )}
                  </div>

                  {/* COMMENT BODY */}
                  <div className="min-w-0 flex-1">
                    {/* AUTHOR + TIME */}
                    <div className="flex items-center gap-1.5 mb-1">
                      <span className="font-semibold text-gray-900 text-sm">
                        {comment.author.fullName || comment.author.username}
                      </span>

                      <span className="text-[13px] text-gray-500">
                        · {formatRelativeTime(comment.createdAt)}
                      </span>
                    </div>

                    {/* CONTENT */}
                    {comment.content && (
                      <p className="text-gray-900 text-[15px] whitespace-pre-wrap break-words leading-relaxed">
                        {comment.content}
                      </p>
                    )}

                    {/* ACTION BAR */}
                    <div className="flex items-center gap-1 mt-2">
                      <button
                        type="button"
                        className="flex items-center justify-center w-8 h-8 rounded-full hover:bg-gray-100 text-gray-700 transition-colors"
                      >
                        <ThumbsUp className="w-4 h-4" />
                      </button>

                      <button
                        type="button"
                        className="flex items-center justify-center w-8 h-8 rounded-full hover:bg-gray-100 text-gray-700 transition-colors"
                      >
                        <ThumbsDown className="w-4 h-4" />
                      </button>

                      <button
                        type="button"
                        className="text-xs font-semibold text-gray-700 hover:bg-gray-100 px-3 py-1.5 rounded-full transition-colors ml-1"
                      >
                        Trả lời
                      </button>
                    </div>

                    {/* ==================================================
                          REPLIES BUTTON
                      ================================================== */}

                    {comment.replyCount > 0 && (
                      <div className="mt-1">
                        <button
                          type="button"
                          onClick={() => toggleReplies(comment.id)}
                          className="flex items-center gap-2 px-3 py-1.5 -ml-3 text-[13px] font-semibold text-gray-600 hover:bg-blue-50 hover:text-blue-600 rounded-full transition-colors"
                        >
                          <ChevronDown
                            className={`w-4 h-4 transition-transform ${
                              isRepliesOpen ? "rotate-180" : ""
                            }`}
                          />

                          {isRepliesOpen
                            ? "Ẩn phản hồi"
                            : `Xem ${comment.replyCount} phản hồi`}
                        </button>
                      </div>
                    )}
                  </div>
                </div>

                {/* ==================================================
                      REPLY LIST
                  ================================================== */}

                {isRepliesOpen && <ReplyList commentId={comment.id} />}
              </div>
            );
          })}

          {/* ==================================================
                LOAD MORE COMMENTS
            ================================================== */}

          {hasNextPage && (
            <div className="flex justify-center pt-4">
              <button
                type="button"
                onClick={() => fetchNextPage()}
                disabled={isFetchingNextPage}
                className="rounded-full border border-gray-300 px-6 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:bg-gray-100 disabled:text-gray-400 transition-colors"
              >
                {isFetchingNextPage ? "Đang tải..." : "Xem thêm bình luận"}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

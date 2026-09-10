import { useState } from "react";
import { ThumbsUp, ThumbsDown, ChevronDown } from "lucide-react";

import { formatRelativeTime, useCreateComment } from "@social/shared";

import type { CommentResponse } from "@social/shared";

import { ReplyList } from "./ReplyList";

interface CommentItemProps {
  comment: CommentResponse;
  postId: string;
}

export function CommentItem({ comment, postId }: CommentItemProps) {
  const [isRepliesOpen, setIsRepliesOpen] = useState(false);
  const [isReplying, setIsReplying] = useState(false);
  const [replyContent, setReplyContent] = useState("");

  const createComment = useCreateComment();

  const authorName = comment.author.fullName || comment.author.username;

  // ============================================================
  // REPLY
  // ============================================================
  const handleReplySubmit = () => {
    const value = replyContent.trim();

    if (!value || createComment.isPending) {
      return;
    }

    createComment.mutate(
      {
        postId,
        payload: {
          content: value,

          // Quan trọng:
          // Reply sẽ có parentId là comment/reply hiện tại
          parentId: comment.id,

          mediaList: [],
        },
      },
      {
        onSuccess: () => {
          setReplyContent("");
          setIsReplying(false);

          // Mở danh sách reply sau khi gửi
          setIsRepliesOpen(true);
        },
      },
    );
  };

  return (
    <div>
      {/* ========================================================
          COMMENT / REPLY BODY
          ======================================================== */}
      <div className="flex gap-3.5">
        {/* AVATAR */}
        <div className="shrink-0">
          {comment.author.avatarFileId ? (
            <img
              src={comment.author.avatarFileId}
              alt="avatar"
              className="w-10 h-10 rounded-full object-cover"
            />
          ) : (
            <div className="w-10 h-10 rounded-full bg-gray-900 flex items-center justify-center text-white text-sm font-semibold">
              {authorName.charAt(0).toUpperCase()}
            </div>
          )}
        </div>

        {/* BODY */}
        <div className="min-w-0 flex-1">
          {/* AUTHOR + TIME */}
          <div className="flex items-center gap-1.5 mb-1">
            <span className="font-semibold text-gray-900 text-sm">
              {authorName}
            </span>

            <span className="text-[13px] text-gray-500">
              · {formatRelativeTime(comment.createdAt)}
            </span>
          </div>

          {/* CONTENT */}
          {comment.content && (
            <p className="text-gray-900 text-[15px] whitespace-pre-wrap wrap-break-word leading-relaxed">
              {comment.content}
            </p>
          )}

          {/* ====================================================
              ACTION BAR
              ==================================================== */}
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

            {/* REPLY */}
            <button
              type="button"
              onClick={() => {
                setIsReplying((prev) => !prev);
              }}
              className="text-xs font-semibold text-gray-700 hover:bg-gray-100 px-3 py-1.5 rounded-full transition-colors ml-1"
            >
              {isReplying ? "Hủy" : "Trả lời"}
            </button>
          </div>

          {/* ====================================================
              REPLY INPUT
              ==================================================== */}
          {isReplying && (
            <div className="flex gap-2 mt-3">
              <input
                type="text"
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleReplySubmit();
                  }

                  if (e.key === "Escape") {
                    setIsReplying(false);
                    setReplyContent("");
                  }
                }}
                placeholder={`Trả lời ${authorName}...`}
                maxLength={5000}
                disabled={createComment.isPending}
                autoFocus
                className="flex-1 rounded-full border border-gray-300 bg-gray-50 px-4 py-2 text-sm outline-none focus:border-gray-500 focus:bg-white transition-colors"
              />

              <button
                type="button"
                onClick={handleReplySubmit}
                disabled={createComment.isPending || !replyContent.trim()}
                className="rounded-full bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50 transition-colors"
              >
                {createComment.isPending ? "Đang gửi..." : "Gửi"}
              </button>
            </div>
          )}

          {/* ====================================================
              VIEW REPLIES
              ==================================================== */}
          {comment.replyCount > 0 && (
            <div className="mt-1">
              <button
                type="button"
                onClick={() => setIsRepliesOpen((prev) => !prev)}
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

      {/* ========================================================
          REPLY LIST
          ======================================================== */}

      {isRepliesOpen && <ReplyList commentId={comment.id} postId={postId} />}
    </div>
  );
}

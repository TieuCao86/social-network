import { useState } from "react";
import { MessageSquare } from "lucide-react";

import { useInfiniteComments, useCreateComment } from "@social/shared";

import { CommentItem } from "./CommentItem";

interface CommentSectionProps {
  postId: string;
  commentCount: number;
}

export function CommentSection({ postId, commentCount }: CommentSectionProps) {
  const [content, setContent] = useState("");

  const createComment = useCreateComment();

  const {
    data,
    isLoading,
    isError,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
  } = useInfiniteComments(postId, 10);

  const comments = data?.pages.flatMap((page) => page.content) ?? [];

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

  return (
    <div className="border-t border-gray-100 bg-white p-4">
      {/* HEADER */}
      <div className="flex items-center gap-2 mb-4">
        <MessageSquare className="w-5 h-5 text-gray-700" />

        <span className="font-semibold text-gray-900">Bình luận</span>

        <span className="text-gray-500">({commentCount})</span>
      </div>

      {/* INPUT COMMENT */}
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

      {/* LOADING */}
      {isLoading && (
        <div className="text-center text-gray-500 py-6 text-sm">
          Đang tải bình luận...
        </div>
      )}

      {/* ERROR */}
      {isError && (
        <div className="text-center text-red-500 py-6 text-sm">
          Không thể tải bình luận.
        </div>
      )}

      {/* EMPTY */}
      {!isLoading && !isError && comments.length === 0 && (
        <div className="text-center text-gray-500 py-6 text-sm">
          Chưa có bình luận nào.
        </div>
      )}

      {/* COMMENT LIST */}
      {!isLoading && !isError && comments.length > 0 && (
        <div className="space-y-6">
          {comments.map((comment) => (
            <CommentItem key={comment.id} comment={comment} postId={postId} />
          ))}

          {/* LOAD MORE */}
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

import { useInfiniteReplies } from "@social/shared";

import { CommentItem } from "./CommentItem";

interface ReplyListProps {
  commentId: string;
  postId: string;
}

export function ReplyList({ commentId, postId }: ReplyListProps) {
  const {
    data,
    isLoading,
    isError,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
  } = useInfiniteReplies(commentId, 10);

  const replies = data?.pages.flatMap((page) => page.content) ?? [];

  // ============================================================
  // LOADING
  // ============================================================
  if (isLoading) {
    return (
      <div className="mt-3 ml-8 text-sm text-gray-500">
        Đang tải phản hồi...
      </div>
    );
  }

  // ============================================================
  // ERROR
  // ============================================================
  if (isError) {
    return (
      <div className="mt-3 ml-8 text-sm text-red-500">
        Không thể tải phản hồi.
      </div>
    );
  }

  // ============================================================
  // EMPTY
  // ============================================================
  if (replies.length === 0) {
    return (
      <div className="mt-3 ml-8 text-sm text-gray-500">Chưa có phản hồi.</div>
    );
  }

  return (
    <div className="mt-3 ml-8 pl-4 border-l-2 border-gray-100 space-y-4">
      {/* ========================================================
          REPLIES
          ======================================================== */}

      {replies.map((reply) => (
        <CommentItem key={reply.id} comment={reply} postId={postId} />
      ))}

      {/* ========================================================
          LOAD MORE
          ======================================================== */}

      {hasNextPage && (
        <div className="flex justify-center pt-1">
          <button
            type="button"
            onClick={() => fetchNextPage()}
            disabled={isFetchingNextPage}
            className="text-xs font-semibold text-gray-600 hover:text-blue-600 disabled:text-gray-400"
          >
            {isFetchingNextPage ? "Đang tải..." : "Xem thêm phản hồi"}
          </button>
        </div>
      )}
    </div>
  );
}

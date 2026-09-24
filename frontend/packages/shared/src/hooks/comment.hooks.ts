import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";

import type { ApiClient } from "../api/api-client";
import { createCommentService } from "../services/comment.service";
import { queryKeys } from "../constants/queryKeys";

import type {
  CommentCreateRequest,
  CommentUpdateRequest,
} from "../types/comment";

export const createCommentHooks = (client: ApiClient) => {
  const commentService = createCommentService(client);

  /**
   * ============================================================
   * GET COMMENTS
   * ============================================================
   */
  const useInfiniteComments = (postId: string, size = 10) => {
    return useInfiniteQuery({
      queryKey: queryKeys.comments.byPost(postId),
      enabled: !!postId,
      initialPageParam: 0,

      queryFn: async ({ pageParam }) => {
        const response = await commentService.getComments(
          postId,
          pageParam,
          size,
        );

        if (!response.success || !response.data) {
          throw new Error(
            response.message || "Không lấy được danh sách bình luận",
          );
        }

        return response.data;
      },

      getNextPageParam: (lastPage) => {
        if (lastPage.last) {
          return undefined;
        }

        return lastPage.number + 1;
      },
    });
  };

  /**
   * ============================================================
   * GET REPLIES
   * ============================================================
   */
  const useInfiniteReplies = (commentId: string, size = 10) => {
    return useInfiniteQuery({
      queryKey: queryKeys.comments.replies(commentId),
      enabled: !!commentId,
      initialPageParam: 0,

      queryFn: async ({ pageParam }) => {
        const response = await commentService.getReplies(
          commentId,
          pageParam,
          size,
        );

        if (!response.success || !response.data) {
          throw new Error(
            response.message || "Không lấy được danh sách phản hồi",
          );
        }

        return response.data;
      },

      getNextPageParam: (lastPage) => {
        if (lastPage.last) {
          return undefined;
        }

        return lastPage.number + 1;
      },
    });
  };

  /**
   * ============================================================
   * COUNT COMMENTS
   * ============================================================
   */
  const useCountComments = (postId: string) => {
    return useQuery({
      queryKey: queryKeys.comments.countByPost(postId),
      enabled: !!postId,

      queryFn: async () => {
        const response = await commentService.countComments(postId);

        if (!response.success) {
          throw new Error(response.message || "Không đếm được số bình luận");
        }

        return response.data ?? 0;
      },
    });
  };

  /**
   * ============================================================
   * COUNT REPLIES
   * ============================================================
   */
  const useCountReplies = (commentId: string) => {
    return useQuery({
      queryKey: queryKeys.comments.countReplies(commentId),
      enabled: !!commentId,

      queryFn: async () => {
        const response = await commentService.countReplies(commentId);

        if (!response.success) {
          throw new Error(response.message || "Không đếm được số phản hồi");
        }

        return response.data ?? 0;
      },
    });
  };

  /**
   * ============================================================
   * CREATE COMMENT / REPLY
   * ============================================================
   */
  const useCreateComment = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: ({
        postId,
        payload,
      }: {
        postId: string;
        payload: CommentCreateRequest;
      }) => commentService.createComment(postId, payload),

      onSuccess: (_response, variables) => {
        // Reload danh sách comment
        queryClient.invalidateQueries({
          queryKey: queryKeys.comments.byPost(variables.postId),
        });

        // Reload tổng số comment
        queryClient.invalidateQueries({
          queryKey: queryKeys.comments.countByPost(variables.postId),
        });

        // Nếu là reply
        if (variables.payload.parentId) {
          queryClient.invalidateQueries({
            queryKey: queryKeys.comments.replies(variables.payload.parentId),
          });

          queryClient.invalidateQueries({
            queryKey: queryKeys.comments.countReplies(
              variables.payload.parentId,
            ),
          });
        }

        // Cập nhật commentCount của post
        queryClient.invalidateQueries({
          queryKey: queryKeys.posts.feed(),
        });

        queryClient.invalidateQueries({
          queryKey: queryKeys.posts.detail(variables.postId),
        });
      },
    });
  };

  /**
   * ============================================================
   * UPDATE COMMENT
   * ============================================================
   */
  const useUpdateComment = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: ({
        commentId,
        payload,
      }: {
        commentId: string;
        payload: CommentUpdateRequest;
      }) => commentService.updateComment(commentId, payload),

      onSuccess: (response) => {
        const comment = response.data;

        if (!comment) {
          return;
        }

        // Cập nhật comment gốc
        queryClient.invalidateQueries({
          queryKey: queryKeys.comments.byPost(comment.postId),
        });

        // Cập nhật reply
        if (comment.parentId) {
          queryClient.invalidateQueries({
            queryKey: queryKeys.comments.replies(comment.parentId),
          });
        }
      },
    });
  };

  /**
   * ============================================================
   * DELETE COMMENT
   * ============================================================
   */
  const useDeleteComment = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (commentId: string) =>
        commentService.deleteComment(commentId),

      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.comments.all,
        });

        queryClient.invalidateQueries({
          queryKey: queryKeys.posts.feed(),
        });
      },
    });
  };

  return {
    useInfiniteComments,
    useInfiniteReplies,
    useCountComments,
    useCountReplies,
    useCreateComment,
    useUpdateComment,
    useDeleteComment,
  };
};

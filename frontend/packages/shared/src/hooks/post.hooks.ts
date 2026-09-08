import {
  useInfiniteQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import { postService } from "../services/post.service";
import { queryKeys } from "../constants/queryKeys";

import type { ApiResponse } from "../types/api";

import type {
  PostCreateRequest,
  ReactionRequest,
  ReactionResponse,
} from "../types/post";

/**
 * Lấy danh sách bài viết trên feed
 */
export function useFeed() {
  return useInfiniteQuery({
    queryKey: queryKeys.posts.feed(),

    initialPageParam: 0,

    queryFn: async ({ pageParam }) => {
      const response = await postService.getFeed(pageParam, 10);

      if (!response.success || !response.data) {
        throw new Error(response.message || "Không lấy được feed");
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
}

/**
 * Tạo bài viết
 */
export function useCreatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: PostCreateRequest) => postService.createPost(payload),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: queryKeys.posts.feed(),
      });
    },
  });
}

/**
 * Xóa bài viết
 */
export function useDeletePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (postId: string) => postService.deletePost(postId),

    onSuccess: (_response, postId) => {
      queryClient.invalidateQueries({
        queryKey: queryKeys.posts.feed(),
      });

      queryClient.invalidateQueries({
        queryKey: queryKeys.posts.detail(postId),
      });
    },
  });
}

/**
 * Thêm / đổi / xóa reaction của bài viết
 */
export function useReactToPost() {
  const queryClient = useQueryClient();

  return useMutation<
    ApiResponse<ReactionResponse>,
    Error,
    {
      postId: string;
      payload: ReactionRequest;
    }
  >({
    mutationFn: ({ postId, payload }) =>
      postService.reactToPost(postId, payload),

    onSuccess: (_response, variables) => {
      queryClient.invalidateQueries({
        queryKey: queryKeys.posts.feed(),
      });

      queryClient.invalidateQueries({
        queryKey: queryKeys.posts.detail(variables.postId),
      });

      queryClient.invalidateQueries({
        queryKey: queryKeys.reactions.byPost(variables.postId),
      });
    },
  });
}

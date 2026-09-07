import { getApiClient } from "../api/api-client";

import type {
  ApiResponse,
  PageResponse,
} from "../types/api";

import type {
  CommentResponse,
} from "../types/comment";

export const commentService = {

  getComments: async (
    postId: string,
    page = 0,
    size = 10,
  ): Promise<
    ApiResponse<PageResponse<CommentResponse>>
  > => {
    return getApiClient().get<
      PageResponse<CommentResponse>
    >(
      `/api/comments/posts/${postId}`,
      {
        params: {
          page,
          size,
        },
      },
    );
  },

  getReplies: async (
    commentId: string,
    page = 0,
    size = 10,
  ): Promise<
    ApiResponse<PageResponse<CommentResponse>>
  > => {
    return getApiClient().get<
      PageResponse<CommentResponse>
    >(
      `/api/comments/${commentId}/replies`,
      {
        params: {
          page,
          size,
        },
      },
    );
  },

  countComments: async (
    postId: string,
  ): Promise<ApiResponse<number>> => {
    return getApiClient().get<number>(
      `/api/comments/posts/${postId}/count`,
    );
  },

  countReplies: async (
    commentId: string,
  ): Promise<ApiResponse<number>> => {
    return getApiClient().get<number>(
      `/api/comments/${commentId}/replies/count`,
    );
  },
};
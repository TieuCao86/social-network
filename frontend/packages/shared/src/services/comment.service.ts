import { getApiClient } from "../api/api-client";
import type { ApiResponse, PageResponse } from "../types/api";
import type {
  CommentCreateRequest,
  CommentResponse,
  CommentUpdateRequest,
} from "../types/comment";

export const commentService = {
  // ============================================================
  // CREATE COMMENT / REPLY
  // ============================================================
  createComment: async (
    postId: string,
    payload: CommentCreateRequest,
  ): Promise<ApiResponse<CommentResponse>> => {
    return getApiClient().post<CommentResponse>(
      `/api/comments/posts/${postId}`,
      payload,
    );
  },

  // ============================================================
  // GET COMMENTS
  // ============================================================
  getComments: async (
    postId: string,
    page = 0,
    size = 10,
  ): Promise<ApiResponse<PageResponse<CommentResponse>>> => {
    return getApiClient().get<PageResponse<CommentResponse>>(
      `/api/comments/posts/${postId}`,
      { params: { page, size } },
    );
  },

  // ============================================================
  // GET REPLIES
  // ============================================================
  getReplies: async (
    commentId: string,
    page = 0,
    size = 10,
  ): Promise<ApiResponse<PageResponse<CommentResponse>>> => {
    return getApiClient().get<PageResponse<CommentResponse>>(
      `/api/comments/${commentId}/replies`,
      { params: { page, size } },
    );
  },

  // ============================================================
  // COUNT COMMENTS
  // ============================================================
  countComments: async (postId: string): Promise<ApiResponse<number>> => {
    return getApiClient().get<number>(`/api/comments/posts/${postId}/count`);
  },

  // ============================================================
  // COUNT REPLIES
  // ============================================================
  countReplies: async (commentId: string): Promise<ApiResponse<number>> => {
    return getApiClient().get<number>(
      `/api/comments/${commentId}/replies/count`,
    );
  },

  // ============================================================
  // UPDATE COMMENT
  // ============================================================
  updateComment: async (
    commentId: string,
    payload: CommentUpdateRequest,
  ): Promise<ApiResponse<CommentResponse>> => {
    return getApiClient().put<CommentResponse>(
      `/api/comments/${commentId}`,
      payload,
    );
  },

  // ============================================================
  // DELETE COMMENT
  // ============================================================
  deleteComment: async (commentId: string): Promise<ApiResponse<void>> => {
    return getApiClient().delete<void>(`/api/comments/${commentId}`);
  },
};

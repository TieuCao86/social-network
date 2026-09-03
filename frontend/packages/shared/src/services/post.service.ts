import { getApiClient } from "../api/api-client";
import type { ApiResponse, PageResponse } from "../types/api";
import type {
  PostResponse,
  PostCreateRequest,
  ReactionRequest,
  ReactionResponse,
  ReactionUserResponse,
  ReactionType,
} from "../types/post";

export const postService = {
  // GET /api/posts/feed
  getFeed: async (
    page = 0,
    size = 10,
  ): Promise<ApiResponse<PageResponse<PostResponse>>> => {
    return getApiClient().get<PageResponse<PostResponse>>("/api/posts/feed", {
      params: {
        page,
        size,
      },
    });
  },

  // GET /api/posts/{id}
  getPostById: async (postId: string): Promise<ApiResponse<PostResponse>> => {
    return getApiClient().get<PostResponse>(`/api/posts/${postId}`);
  },

  // POST /api/posts
  createPost: async (
    payload: PostCreateRequest,
  ): Promise<ApiResponse<PostResponse>> => {
    return getApiClient().post<PostResponse>("/api/posts", payload);
  },

  // GET /api/posts/user/{authorId}
  getUserPosts: async (
    authorId: string,
    page = 0,
    size = 10,
  ): Promise<ApiResponse<PageResponse<PostResponse>>> => {
    return getApiClient().get<PageResponse<PostResponse>>(
      `/api/posts/user/${authorId}`,
      {
        params: {
          page,
          size,
        },
      },
    );
  },

  // DELETE /api/posts/{id}
  deletePost: async (postId: string): Promise<ApiResponse<void>> => {
    return getApiClient().delete<void>(`/api/posts/${postId}`);
  },

  // POST /api/posts/{id}/reactions
  reactToPost: async (
    postId: string,
    payload: ReactionRequest,
  ): Promise<ApiResponse<ReactionResponse>> => {
    return getApiClient().post<ReactionResponse>(
      `/api/posts/${postId}/reactions`,
      payload,
    );
  },

  // GET /api/posts/{id}/reactions
  getPostReactions: async (
    postId: string,
    type?: ReactionType,
    page = 0,
    size = 20,
  ): Promise<ApiResponse<PageResponse<ReactionUserResponse>>> => {
    return getApiClient().get<PageResponse<ReactionUserResponse>>(
      `/api/posts/${postId}/reactions`,
      {
        params: {
          page,
          size,
          ...(type ? { type } : {}),
        },
      },
    );
  },
};

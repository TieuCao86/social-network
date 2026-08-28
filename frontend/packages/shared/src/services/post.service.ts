import { createApiClient } from '../api/client';
import { ApiResponse } from '../types/api';
import {
  PostResponse,
  PostCreateRequest,
  ReactionRequest,
  ReactionResponse,
  ReactionUserResponse,
  ReactionType,
} from '../types/post';

export const postService = {
  // Lấy danh sách Newsfeed / Posts
  getPosts: async (page = 0, size = 10): Promise<ApiResponse<PostResponse[]>> => {
    const response = await apiClient.get<ApiResponse<PostResponse[]>>('/posts', {
      params: { page, size },
    });
    return response.data;
  },

  // Xem chi tiết 1 bài viết
  getPostById: async (postId: string): Promise<ApiResponse<PostResponse>> => {
    const response = await apiClient.get<ApiResponse<PostResponse>>(`/posts/${postId}`);
    return response.data;
  },

  // Tạo bài viết mới
  createPost: async (payload: PostCreateRequest): Promise<ApiResponse<PostResponse>> => {
    const response = await apiClient.post<ApiResponse<PostResponse>>('/posts', payload);
    return response.data;
  },

  // Thả hoặc đổi Reaction bài viết
  reactToPost: async (postId: string, payload: ReactionRequest): Promise<ApiResponse<ReactionResponse>> => {
    const response = await apiClient.post<ApiResponse<ReactionResponse>>(
      `/posts/${postId}/reactions`,
      payload
    );
    return response.data;
  },

  // Hủy thả reaction
  removeReaction: async (postId: string): Promise<ApiResponse<ReactionResponse>> => {
    const response = await apiClient.delete<ApiResponse<ReactionResponse>>(`/posts/${postId}/reactions`);
    return response.data;
  },

  // Lấy danh sách người dùng đã thả reaction
  getPostReactions: async (
    postId: string,
    type?: ReactionType
  ): Promise<ApiResponse<ReactionUserResponse[]>> => {
    const response = await apiClient.get<ApiResponse<ReactionUserResponse[]>>(
      `/posts/${postId}/reactions`,
      { params: { type } }
    );
    return response.data;
  },
};
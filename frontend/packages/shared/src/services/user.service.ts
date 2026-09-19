import type { ApiClient } from "../api/api-client";

import type { PageResponse, UserResponse, UserSearchResponse } from "../types";

export const createUserService = (client: ApiClient) => ({
  getProfile: async (): Promise<UserResponse> => {
    const res = await client.get<UserResponse>("/api/users/profile");

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không tìm thấy thông tin người dùng");
    }

    return res.data;
  },

  searchUsers: async (
    keyword: string,
    page = 0,
    size = 10,
  ): Promise<PageResponse<UserSearchResponse>> => {
    const res = await client.get<PageResponse<UserSearchResponse>>(
      "/api/users/search",
      {
        params: {
          q: keyword,
          page,
          size,
        },
      },
    );

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không thể tìm kiếm người dùng");
    }

    return res.data;
  },
});

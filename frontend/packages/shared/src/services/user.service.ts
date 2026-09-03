import type { ApiClient } from "../api/api-client";

import type { UserResponse } from "../types";

export const createUserService = (client: ApiClient) => ({
  getProfile: async (): Promise<UserResponse> => {
    const res = await client.get<UserResponse>("/api/users/profile");

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không tìm thấy thông tin người dùng");
    }

    return res.data;
  },
});

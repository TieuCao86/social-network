import type { ApiClient } from "../api/api-client";

import type { LoginRequest, LoginResponse, UserResponse } from "../types";

export const createAuthService = (client: ApiClient) => ({
  login: async (body: LoginRequest): Promise<LoginResponse> => {
    const res = await client.post<LoginResponse>("/api/auth/login", body);

    if (!res.success || !res.data) {
      throw new Error(res.message || "Đăng nhập thất bại");
    }

    return res.data;
  },

  getMe: async (): Promise<UserResponse> => {
    const res = await client.get<UserResponse>("/api/users/me");

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không tìm thấy thông tin người dùng");
    }

    return res.data;
  },

  logout: async (): Promise<void> => {
    await client.post<null>("/api/auth/logout");
  },
});

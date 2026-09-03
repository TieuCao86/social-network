import type { ApiClient } from "../api/api-client";

import type { LoginRequest, LoginResponse } from "../types";

export const createAuthService = (client: ApiClient) => ({
  login: async (body: LoginRequest): Promise<LoginResponse> => {
    const res = await client.post<LoginResponse>("/api/auth/login", body);

    if (!res.success || !res.data) {
      throw new Error(res.message || "Đăng nhập thất bại");
    }

    return res.data;
  },

  logout: async (): Promise<void> => {
    const res = await client.post<null>("/api/auth/logout");

    if (!res.success) {
      throw new Error(res.message || "Đăng xuất thất bại");
    }
  },
});

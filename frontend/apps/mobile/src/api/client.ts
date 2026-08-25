import { createApiClient } from "@social/shared";
import * as SecureStore from "expo-secure-store";

export const apiClient = createApiClient(
  process.env.EXPO_PUBLIC_API_URL || "http://localhost:8080",
);

// Request Interceptor: Gắn client type và Bearer token
apiClient.interceptors.request.use(
  async (config) => {
    config.headers.set("X-Client-Type", "MOBILE");

    const token = await SecureStore.getItemAsync("access_token");
    if (token) {
      config.headers.set("Authorization", `Bearer ${token}`);
    }

    return config;
  },
  (error) => Promise.reject(error),
);

// Response Interceptor: Unwrap data & Xử lý 401
apiClient.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    // Không xóa token nếu lỗi 401 xuất phát từ chính API đăng nhập (nhập sai mật khẩu)
    const isLoginRequest = error.config?.url?.includes("/auth/login");

    if (error.response?.status === 401 && !isLoginRequest) {
      await SecureStore.deleteItemAsync("access_token");
      // TODO: Điều hướng về màn hình Login (ví dụ: router.replace("/login"))
    }

    return Promise.reject(error.response?.data || error);
  },
);

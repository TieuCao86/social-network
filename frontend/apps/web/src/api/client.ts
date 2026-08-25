import { createApiClient } from "@social/shared";

export const apiClient = createApiClient(
  import.meta.env.VITE_API_URL || "http://localhost:8080",
  {
    withCredentials: true,
  },
);

// Request: Gắn định danh WEB
apiClient.interceptors.request.use(
  (config) => {
    config.headers.set("X-Client-Type", "WEB");
    return config;
  },
  (error) => Promise.reject(error),
);

// Response: Unwrap data và xử lý 401 an toàn
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const isAuthEndpoint =
      error.config?.url?.includes("/auth/login") ||
      error.config?.url?.includes("/auth/register");

    // Chỉ redirect khi phiên hết hạn ở trang khác, không redirect khi login sai mật khẩu
    if (
      error.response?.status === 401 &&
      !isAuthEndpoint &&
      window.location.pathname !== "/login"
    ) {
      window.location.href = "/login";
    }

    return Promise.reject(error.response?.data || error);
  },
);

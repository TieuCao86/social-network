import {
  createApiClient,
  createAxiosApiClient,
  setApiClient,
} from "@social/shared";

import { tokenStorage } from "./tokenStorage";

// 1. Tạo Axios instance
const axiosClient = createApiClient(
  process.env.EXPO_PUBLIC_API_URL || "http://localhost:8080",
);

// 2. Request interceptor
axiosClient.interceptors.request.use(
  async (config) => {
    config.headers.set("X-Client-Type", "MOBILE");

    const token = await tokenStorage.getItem("access_token");

    if (token) {
      config.headers.set("Authorization", `Bearer ${token}`);
    }

    return config;
  },
  (error) => Promise.reject(error),
);

// 3. Response interceptor
axiosClient.interceptors.response.use(
  (response) => response,

  async (error) => {
    const isLoginRequest = error.config?.url?.includes("/auth/login");

    if (error.response?.status === 401 && !isLoginRequest) {
      await tokenStorage.deleteItem("access_token");
    }

    return Promise.reject(error.response?.data || error);
  },
);

// 4. AxiosInstance → ApiClient
export const apiClient = createAxiosApiClient(axiosClient);

// 5. Đăng ký cho Shared
setApiClient(apiClient);

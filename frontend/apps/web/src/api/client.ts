import {
  createApiClient,
  createAxiosApiClient,
  setApiClient,
} from "@social/shared";

const axiosClient = createApiClient(
  import.meta.env.VITE_API_URL || "http://localhost:8080",
  {
    withCredentials: true,
  },
);

// Request Interceptor
axiosClient.interceptors.request.use(
  (config) => {
    config.headers.set("X-Client-Type", "WEB");

    return config;
  },
  (error) => Promise.reject(error),
);

// Response Interceptor
axiosClient.interceptors.response.use(
  (response) => response,

  (error) => {
    const isAuthEndpoint =
      error.config?.url?.includes("/auth/login") ||
      error.config?.url?.includes("/auth/register");

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

// AxiosInstance → ApiClient
export const apiClient = createAxiosApiClient(axiosClient);

// Đăng ký client cho Shared
setApiClient(apiClient);

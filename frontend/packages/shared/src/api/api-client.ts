import type { AxiosRequestConfig } from "axios";
import type { ApiResponse } from "../types";

export interface ApiClient {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>>;

  post<T>(
    url: string,
    data?: unknown,
    config?: AxiosRequestConfig,
  ): Promise<ApiResponse<T>>;

  put<T>(
    url: string,
    data?: unknown,
    config?: AxiosRequestConfig,
  ): Promise<ApiResponse<T>>;

  delete<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>>;
}

let activeClient: ApiClient | null = null;

export function setApiClient(client: ApiClient): void {
  activeClient = client;
}

export function getApiClient(): ApiClient {
  if (!activeClient) {
    throw new Error(
      "ApiClient chưa được đăng ký! Hãy gọi setApiClient(...) ở Web (main.tsx) hoặc Mobile (App.tsx) trước.",
    );
  }

  return activeClient;
}

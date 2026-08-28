import type { AxiosInstance, AxiosRequestConfig } from "axios";

import type { ApiResponse } from "../types";
import type { ApiClient } from "./api-client";

export function createAxiosApiClient(axiosClient: AxiosInstance): ApiClient {
  return {
    async get<T>(
      url: string,
      config?: AxiosRequestConfig,
    ): Promise<ApiResponse<T>> {
      const response = await axiosClient.get<ApiResponse<T>>(url, config);

      return response.data;
    },

    async post<T>(
      url: string,
      data?: unknown,
      config?: AxiosRequestConfig,
    ): Promise<ApiResponse<T>> {
      const response = await axiosClient.post<ApiResponse<T>>(
        url,
        data,
        config,
      );

      return response.data;
    },

    async put<T>(
      url: string,
      data?: unknown,
      config?: AxiosRequestConfig,
    ): Promise<ApiResponse<T>> {
      const response = await axiosClient.put<ApiResponse<T>>(url, data, config);

      return response.data;
    },

    async delete<T>(
      url: string,
      config?: AxiosRequestConfig,
    ): Promise<ApiResponse<T>> {
      const response = await axiosClient.delete<ApiResponse<T>>(url, config);

      return response.data;
    },
  };
}

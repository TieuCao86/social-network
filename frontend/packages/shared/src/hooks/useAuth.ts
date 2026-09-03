import { useMutation, useQuery, type QueryClient } from "@tanstack/react-query";

import type { ApiClient } from "../api/api-client";

import { createAuthService } from "../services/auth.service";
import { createUserService } from "../services/user.service";

import type { LoginRequest, LoginResponse, UserResponse } from "../types";

export interface AuthStorageAdapter {
  getToken?: () => Promise<string | null> | string | null;

  setToken?: (token: string) => Promise<void> | void;

  removeToken?: () => Promise<void> | void;
}

export interface UseAuthOptions {
  client: QueryClient;

  /**
   * Mobile dùng storage để lưu access token.
   * Web dùng HttpOnly Cookie nên không cần.
   */
  storage?: AuthStorageAdapter;

  onLoginSuccess?: (data: LoginResponse) => Promise<void> | void;

  onLogoutSuccess?: () => Promise<void> | void;
}

export const authQueryKeys = {
  all: ["auth"] as const,

  me: () => [...authQueryKeys.all, "me"] as const,
};

export function createUseAuth(apiClient: ApiClient) {
  const authService = createAuthService(apiClient);
  const userService = createUserService(apiClient);

  return function useAuth(options: UseAuthOptions) {
    const queryClient = options.client;

    // =========================
    // 1. Current User / Profile
    // =========================

    const userQuery = useQuery(
      {
        queryKey: authQueryKeys.me(),

        queryFn: async (): Promise<UserResponse | null> => {
          // Mobile: kiểm tra access token trước
          if (options.storage?.getToken) {
            const token = await options.storage.getToken();

            if (!token) {
              return null;
            }
          }

          try {
            return await userService.getProfile();
          } catch {
            // Mobile: token hết hạn / không hợp lệ
            if (options.storage?.removeToken) {
              await options.storage.removeToken();
            }

            return null;
          }
        },

        staleTime: 5 * 60 * 1000,
        retry: false,
      },
      queryClient,
    );

    // =========================
    // 2. Login
    // =========================

    const loginMutation = useMutation(
      {
        mutationFn: async (
          credentials: LoginRequest,
        ): Promise<LoginResponse> => {
          const result = await authService.login(credentials);

          // Mobile: lưu access token
          if (result.accessToken && options.storage?.setToken) {
            await options.storage.setToken(result.accessToken);
          }

          // Platform-specific callback
          if (options.onLoginSuccess) {
            await options.onLoginSuccess(result);
          }

          return result;
        },

        onSuccess: async () => {
          // Sau login, lấy profile thật từ server
          await queryClient.invalidateQueries({
            queryKey: authQueryKeys.me(),
          });
        },
      },
      queryClient,
    );

    // =========================
    // 3. Logout
    // =========================

    const logoutMutation = useMutation(
      {
        mutationFn: async (): Promise<void> => {
          try {
            await authService.logout();
          } finally {
            // Mobile: xóa access token
            if (options.storage?.removeToken) {
              await options.storage.removeToken();
            }

            // Platform-specific callback
            if (options.onLogoutSuccess) {
              await options.onLogoutSuccess();
            }
          }
        },

        onSuccess: async () => {
          queryClient.setQueryData(authQueryKeys.me(), null);

          await queryClient.removeQueries({
            queryKey: authQueryKeys.all,
          });
        },
      },
      queryClient,
    );

    return {
      // =========================
      // User
      // =========================

      user: userQuery.data ?? null,

      isLoading: userQuery.isLoading,

      isAuthenticated: !!userQuery.data,

      // =========================
      // Login
      // =========================

      login: loginMutation.mutateAsync,

      isLoggingIn: loginMutation.isPending,

      loginError: loginMutation.error,

      // =========================
      // Logout
      // =========================

      logout: logoutMutation.mutateAsync,

      isLoggingOut: logoutMutation.isPending,
    };
  };
}

import {
  useMutation,
  useQuery,
  useQueryClient,
  QueryClient,
} from "@tanstack/react-query";
import type { ApiClient } from "../api/api-client";
import { createAuthService } from "../services/auth.service";
import type { LoginRequest, LoginResponse, UserResponse } from "../types";

export interface AuthStorageAdapter {
  getToken?: () => Promise<string | null> | string | null;
  setToken?: (token: string) => Promise<void> | void;
  removeToken?: () => Promise<void> | void;
}

export interface UseAuthOptions {
  storage?: AuthStorageAdapter;
  onLoginSuccess?: (data: LoginResponse) => Promise<void> | void;
  onLogoutSuccess?: () => Promise<void> | void;
  client?: QueryClient;
}

export const authQueryKeys = {
  all: ["auth"] as const,
  me: () => [...authQueryKeys.all, "me"] as const,
};

export function createUseAuth(client: ApiClient) {
  const authService = createAuthService(client);

  return function useAuth(options?: UseAuthOptions) {
    // Tự động lấy queryClient từ options hoặc fallback về hook useQueryClient()
    let queryClient: QueryClient;
    try {
      queryClient = options?.client ?? useQueryClient();
    } catch {
      queryClient = options?.client!;
    }

    // 1. Query Profile
    const userQuery = useQuery(
      {
        queryKey: authQueryKeys.me(),
        queryFn: async (): Promise<UserResponse | null> => {
          if (options?.storage?.getToken) {
            const token = await options.storage.getToken();
            if (!token) return null;
          }

          try {
            return await authService.getMe();
          } catch {
            if (options?.storage?.removeToken) {
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

    // 2. Mutation Login
    const loginMutation = useMutation(
      {
        mutationFn: async (
          credentials: LoginRequest,
        ): Promise<LoginResponse> => {
          const result = await authService.login(credentials);

          if (result.accessToken && options?.storage?.setToken) {
            await options.storage.setToken(result.accessToken);
          }

          if (options?.onLoginSuccess) {
            await options.onLoginSuccess(result);
          }

          return result;
        },
        onSuccess: async () => {
          await queryClient?.invalidateQueries({
            queryKey: authQueryKeys.me(),
          });
        },
      },
      queryClient,
    );

    // 3. Mutation Logout
    const logoutMutation = useMutation(
      {
        mutationFn: async (): Promise<void> => {
          try {
            await authService.logout();
          } finally {
            if (options?.storage?.removeToken) {
              await options.storage.removeToken();
            }
            if (options?.onLogoutSuccess) {
              await options.onLogoutSuccess();
            }
          }
        },
        onSuccess: () => {
          queryClient?.setQueryData(authQueryKeys.me(), null);
          queryClient?.removeQueries({
            queryKey: authQueryKeys.all,
          });
        },
      },
      queryClient,
    );

    return {
      user: userQuery.data ?? null,
      isLoading: userQuery.isLoading,
      isAuthenticated: !!userQuery.data,

      login: loginMutation.mutateAsync,
      isLoggingIn: loginMutation.isPending,
      loginError: loginMutation.error,

      logout: logoutMutation.mutateAsync,
      isLoggingOut: logoutMutation.isPending,
    };
  };
}

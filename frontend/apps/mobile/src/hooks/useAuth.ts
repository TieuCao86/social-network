import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as SecureStore from "expo-secure-store";
import {
  createAuthService,
  queryKeys,
  type LoginRequest,
  type UserResponse,
} from "@social/shared";
import { apiClient } from "@/api/client";

// Khởi tạo service gắn với axios client của mobile
const authService = createAuthService(apiClient);

export function useAuth() {
  const queryClient = useQueryClient();

  // 1. Query lấy thông tin profile (chỉ chạy khi tồn tại access_token)
  const userQuery = useQuery({
    queryKey: queryKeys.auth.me(),
    queryFn: async () => {
      const token = await SecureStore.getItemAsync("access_token");
      if (!token) return null;
      return authService.getMe();
    },
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

  // 2. Mutation Đăng nhập
  const loginMutation = useMutation({
    mutationFn: async (data: LoginRequest) => {
      const result = await authService.login(data);
      if (result.accessToken) {
        await SecureStore.setItemAsync("access_token", result.accessToken);
      }
      return result;
    },
    onSuccess: async () => {
      // Refetch lại query profile ngay sau khi lưu token
      await queryClient.invalidateQueries({
        queryKey: queryKeys.auth.me(),
      });
    },
  });

  // 3. Mutation Đăng xuất
  const logoutMutation = useMutation({
    mutationFn: async () => {
      try {
        await authService.logout();
      } finally {
        await SecureStore.deleteItemAsync("access_token");
      }
    },
    onSuccess: () => {
      queryClient.removeQueries({
        queryKey: queryKeys.auth.all,
      });
    },
  });

  return {
    user: (userQuery.data as UserResponse) ?? null,
    isLoading: userQuery.isLoading,
    isAuthenticated: !!userQuery.data,

    // Login
    login: loginMutation.mutateAsync,
    isLoggingIn: loginMutation.isPending,
    loginError: loginMutation.error,

    // Logout
    logout: logoutMutation.mutateAsync,
    isLoggingOut: logoutMutation.isPending,
  };
}

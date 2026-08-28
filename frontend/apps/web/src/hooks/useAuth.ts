import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAuthService,
  queryKeys,
  type LoginRequest,
  type UserResponse,
} from "@social/shared";
import { apiClient } from "../api/client";

// Khởi tạo service gắn với axios client của web
const authService = createAuthService(apiClient);

export function useAuth() {
  const queryClient = useQueryClient();

  // 1. Query lấy thông tin profile hiện tại qua HttpOnly Cookie
  const userQuery = useQuery({
    queryKey: queryKeys.auth.me(),
    queryFn: authService.getMe,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

  // 2. Mutation Đăng nhập
  const loginMutation = useMutation({
    mutationFn: (data: LoginRequest) => authService.login(data),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: queryKeys.auth.me(),
      });
    },
  });

  // 3. Mutation Đăng xuất
  const logoutMutation = useMutation({
    mutationFn: authService.logout,
    onSuccess: () => {
      // Xóa cache user và các query liên quan đến auth
      queryClient.setQueryData(queryKeys.auth.me(), null);
      queryClient.removeQueries({
        queryKey: queryKeys.auth.all,
      });
      window.location.href = "/login";
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

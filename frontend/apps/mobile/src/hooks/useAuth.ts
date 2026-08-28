import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createAuthService,
  queryKeys,
  type UserResponse,
} from "@social/shared";
import { apiClient } from "@/api/client";
import { mobileStorage } from "../utils/storage";

const authService = createAuthService(apiClient);

export function useAuth() {
  const queryClient = useQueryClient();

  // 1. Query lấy profile (Hoạt động tốt cả trên Expo Web và Native)
  const userQuery = useQuery({
    queryKey: queryKeys.auth.me(),
    queryFn: async () => {
      const token = await mobileStorage.getItem("access_token");
      if (!token) return null;
      try {
        return await authService.getMe();
      } catch (err) {
        // Nếu token hết hạn (401), xóa token lưu trữ
        await mobileStorage.deleteItem("access_token");
        return null;
      }
    },
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

  // 2. Mutation Đăng nhập (Map dữ liệu an toàn)
  const loginMutation = useMutation({
    mutationFn: async (data: any) => {
      // Map linh hoạt trường username/phoneOrEmail để khớp với backend
      const payload = {
        username: data.username || data.phoneOrEmail,
        phoneOrEmail: data.phoneOrEmail || data.username,
        password: data.password,
      };

      const result = await authService.login(payload as any);

      // Lưu Access Token an toàn
      const token = result?.accessToken || (result as any)?.token;
      if (token) {
        await mobileStorage.setItem("access_token", token);
      }

      return result;
    },
    onSuccess: async () => {
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
      } catch {
        // Bỏ qua lỗi mạng khi logout
      } finally {
        await mobileStorage.deleteItem("access_token");
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

    login: loginMutation.mutateAsync,
    isLoggingIn: loginMutation.isPending,
    loginError: loginMutation.error,

    logout: logoutMutation.mutateAsync,
    isLoggingOut: logoutMutation.isPending,
  };
}

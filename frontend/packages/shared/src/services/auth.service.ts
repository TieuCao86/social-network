import type { AxiosInstance } from "axios";
import type { 
  ApiResponse, 
  LoginRequest, 
  LoginResponse, 
  UserResponse 
} from "../types";

export const createAuthService = (client: AxiosInstance) => ({
  login: async (body: LoginRequest): Promise<LoginResponse> => {
    const res = await client.post<any, ApiResponse<LoginResponse>>(
      "/api/auth/login",
      body
    );
    if (!res.data) {
      throw new Error(res.message || "Đăng nhập thất bại");
    }
    return res.data;
  },

  getMe: async (): Promise<UserResponse> => {
    const res = await client.get<any, ApiResponse<UserResponse>>(
      "/api/users/me"
    );
    if (!res.data) {
      throw new Error(res.message || "Không tìm thấy thông tin người dùng");
    }
    return res.data;
  },

  logout: async (): Promise<void> => {
    await client.post<any, ApiResponse<null>>("/api/auth/logout");
  },
});
import type { UserResponse } from "./user";

export interface LoginRequest {
  phoneOrEmail: string;
  password: string;
}

export interface LoginResponse {
  userId: string;
  username: string;
  accessToken?: string;
}

export interface RegisterRequest {
  email?: string;
  phone?: string;
  password: string;
  username: string;
}

export interface AuthState {
  user: UserResponse | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export type UserStatus =
  | "ACTIVE"
  | "INACTIVE"
  | "BLOCKED";

export type UserRole =
  | "USER"
  | "MODERATOR"
  | "ADMIN";

export interface UserResponse {
  userId: string;
  username: string;
  email: string | null;
  phone: string | null;
  status: UserStatus;
  role: UserRole;
  emailVerified: boolean;
  phoneVerified: boolean;
}
export type UserStatus = "ACTIVE" | "INACTIVE" | "BLOCKED";

export type UserRole = "USER" | "MODERATOR" | "ADMIN";

export type RelationshipStatus =
  | "NONE"
  | "REQUEST_SENT"
  | "REQUEST_RECEIVED"
  | "FRIENDS"
  | "BLOCKING"
  | "BLOCKED_BY"
  | "FOLLOWING"
  | "FOLLOWED_BY"
  | "FOLLOWING_EACH_OTHER";

export interface UserResponse {
  userId: string;
  username: string;
  email: string | null;
  phone: string | null;

  status: UserStatus;
  role: UserRole;

  emailVerified: boolean;
  phoneVerified: boolean;

  fullName: string | null;
  avatarFileId: string | null;
}

export interface UserSearchResponse {
  userId: string;
  username: string;
  fullName: string | null;
  avatarFileId: string | null;
  relationshipStatus: RelationshipStatus;
}

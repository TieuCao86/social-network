export type PostVisibility = "PUBLIC" | "FRIENDS" | "PRIVATE";

export type PostStatus = "ACTIVE" | "ARCHIVED" | "DELETED";

export type MediaType = "IMAGE" | "VIDEO" | "AUDIO" | "DOCUMENT";

export const ReactionType = {
  LIKE: "LIKE",
  LOVE: "LOVE",
  HAHA: "HAHA",
  WOW: "WOW",
  SAD: "SAD",
  ANGRY: "ANGRY",
} as const;

export type ReactionType = (typeof ReactionType)[keyof typeof ReactionType];

export interface PostAuthorResponse {
  id: string;
  username: string;
  fullName: string | null;
  avatarFileId: string | null;
}

export interface MediaItemResponse {
  id: string;
  fileId: string;
  type: "IMAGE" | "VIDEO";
  sortOrder: number;
  reactionCount: number;
}

export interface PostResponse {
  id: string;
  author: PostAuthorResponse;
  content: string;
  visibility: PostVisibility;
  totalReactions: number;
  commentCount: number;
  shareCount: number;
  currentUserReaction: ReactionType | null;
  topReactions: ReactionType[];
  mediaList: MediaItemResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface ReactionResponse {
  reacted: boolean;
  currentUserReaction: ReactionType | null;
  totalReactions: number;
  reactionCounts: Partial<Record<ReactionType, number>>;
}

export interface ReactionUserResponse {
  id: string;
  userId: string;
  username: string;
  email: string;
  reactionType: ReactionType;
  createdAt: string;
}

export interface PostMediaRequest {
  fileId: string;
  type: MediaType;
}

export interface PostCreateRequest {
  content?: string;
  visibility?: PostVisibility;
  mediaList?: PostMediaRequest[];
}

export interface ReactionRequest {
  type: ReactionType;
}

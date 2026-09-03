export type PostVisibility =
  | "PUBLIC"
  | "FRIENDS"
  | "PRIVATE";

export type PostStatus =
  | "ACTIVE"
  | "ARCHIVED"
  | "DELETED";

export type MediaType =
  | "IMAGE"
  | "VIDEO"
  | "AUDIO"
  | "DOCUMENT";

export type ReactionType =
  | "LIKE"
  | "LOVE"
  | "HAHA"
  | "WOW"
  | "SAD"
  | "ANGRY";


// ================================
// Post Author
// ================================

export interface PostAuthorResponse {
  id: string;

  username: string;

  fullName: string | null;

  avatarFileId: string | null;
}


// ================================
// Media Response
// ================================

export interface MediaItemResponse {
  id: string;
  fileId: string;
  type: "IMAGE" | "VIDEO";
  sortOrder: number;
  reactionCount: number;
}


// ================================
// Post Response
// ================================

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


// ================================
// Reaction Response
// ================================

export interface ReactionResponse {
  reacted: boolean;

  currentUserReaction: ReactionType | null;

  totalReactions: number;

  reactionCounts: Partial<
    Record<ReactionType, number>
  >;
}


// ================================
// Reaction User Response
// ================================

export interface ReactionUserResponse {
  id: string;

  userId: string;

  username: string;

  email: string;

  reactionType: ReactionType;

  createdAt: string;
}


// ================================
// Request Payloads
// ================================

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
  type?: ReactionType;
}

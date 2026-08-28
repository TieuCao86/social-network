// Enums đồng bộ với Java Enums
export type PostVisibility = 'PUBLIC' | 'FRIENDS' | 'PRIVATE';
export type PostStatus = 'ACTIVE' | 'ARCHIVED' | 'DELETED';
export type MediaType = 'IMAGE' | 'VIDEO' | 'AUDIO' | 'DOCUMENT';
export type ReactionType = 'LIKE' | 'LOVE' | 'HAHA' | 'WOW' | 'SAD' | 'ANGRY';

// Response DTOs
export interface MediaItemResponse {
  id: string;
  fileId: string;
  type: MediaType;
  sortOrder: number;
  reactionCount: number;
  url?: string; // URL tải file hiển thị trên UI
}

export interface PostResponse {
  id: string;
  authorId: string;
  content: string;
  visibility: PostVisibility;
  totalReactions: number;
  commentCount: number;
  shareCount: number;
  currentUserReaction?: ReactionType | null;
  topReactions?: ReactionType[];
  mediaList?: MediaItemResponse[];
  createdAt: string;
  updatedAt: string;
  // Bổ sung author để render UI nếu backend join dữ liệu
  author?: {
    id: string;
    username: string;
    avatarUrl?: string;
  };
}

export interface ReactionResponse {
  reacted: boolean;
  currentUserReaction?: ReactionType | null;
  totalReactions: number;
  reactionCounts?: Partial<Record<ReactionType, number>>;
}

export interface ReactionUserResponse {
  id: string;
  userId: string;
  username: string;
  email: string;
  reactionType: ReactionType;
  createdAt: string;
}

// Request Payloads
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
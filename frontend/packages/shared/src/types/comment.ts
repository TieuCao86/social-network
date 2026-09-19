export type CommentMediaType =
  | "IMAGE"
  | "VIDEO"
  | "GIF";

export type CommentStatus =
  | "ACTIVE"
  | "DELETED"
  | "HIDDEN"
  | "FLAGGED";

export interface CommentAuthorResponse {
  userId: string;
  username: string;
  fullName: string | null;
  avatarFileId: string | null;
}

export interface CommentMediaResponse {
  mediaId: string;
  fileId: string;
  type: CommentMediaType;
  sortOrder: number;
}

export interface CommentResponse {
  commentId: string;
  postId: string;
  author: CommentAuthorResponse;
  parentId: string | null;
  content: string | null;
  status: CommentStatus;
  mediaList: CommentMediaResponse[];
  replyCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CommentMediaRequest {
  fileId: string;
  type: CommentMediaType;
}

export interface CommentCreateRequest {
  content?: string;
  parentId?: string | null;
  mediaList?: CommentMediaRequest[];
}

export interface CommentUpdateRequest {
  content?: string;
  mediaList?: CommentMediaRequest[];
}
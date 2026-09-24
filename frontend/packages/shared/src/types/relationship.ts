import type { RelationshipStatus } from "./user";

export type FriendshipStatus =
  | "PENDING"
  | "ACCEPTED"
  | "BLOCKED";

export interface RelationshipResponse {
  userId: string;
  relationshipStatus: RelationshipStatus;
}

export interface FriendshipResponse {
  friendshipId: string;
  requesterId: string;
  addresseeId: string;
  status: FriendshipStatus;
  createdAt: string;
  updatedAt: string;
}

export interface FollowResponse {
  followId: string;
  followerId: string;
  followingId: string;
  createdAt: string;
  updatedAt: string;
}
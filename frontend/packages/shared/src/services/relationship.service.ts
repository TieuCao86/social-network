import type { ApiClient } from "../api/api-client";
import type {
  RelationshipResponse,
  FriendshipResponse,
  FollowResponse,
} from "../types/relationship";

export const createRelationshipService = (client: ApiClient) => ({
  // ============================================================
  // RELATIONSHIP
  // ============================================================

  getRelationship: async (
    targetUserId: string,
  ): Promise<RelationshipResponse> => {
    const res = await client.get<RelationshipResponse>(
      `/api/relationships/${targetUserId}`,
    );

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không thể lấy trạng thái quan hệ");
    }

    return res.data;
  },

  // ============================================================
  // FRIENDSHIP
  // ============================================================

  sendFriendRequest: async (
    targetUserId: string,
  ): Promise<FriendshipResponse> => {
    const res = await client.post<FriendshipResponse>(
      `/api/relationships/friends/${targetUserId}`,
    );

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không thể gửi lời mời kết bạn");
    }

    return res.data;
  },

  acceptFriendRequest: async (
    requesterId: string,
  ): Promise<FriendshipResponse> => {
    const res = await client.post<FriendshipResponse>(
      `/api/relationships/friends/${requesterId}/accept`,
    );

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không thể chấp nhận lời mời kết bạn");
    }

    return res.data;
  },

  rejectFriendRequest: async (requesterId: string): Promise<void> => {
    const res = await client.delete(
      `/api/relationships/friends/${requesterId}/request`,
    );

    if (!res.success) {
      throw new Error(res.message || "Không thể từ chối lời mời kết bạn");
    }
  },

  cancelFriendRequest: async (targetUserId: string): Promise<void> => {
    const res = await client.delete(
      `/api/relationships/friends/${targetUserId}/cancel`,
    );

    if (!res.success) {
      throw new Error(res.message || "Không thể hủy lời mời kết bạn");
    }
  },

  unfriend: async (friendId: string): Promise<void> => {
    const res = await client.delete(`/api/relationships/friends/${friendId}`);

    if (!res.success) {
      throw new Error(res.message || "Không thể hủy kết bạn");
    }
  },

  // ============================================================
  // FOLLOW
  // ============================================================

  follow: async (targetUserId: string): Promise<FollowResponse> => {
    const res = await client.post<FollowResponse>(
      `/api/relationships/follows/${targetUserId}`,
    );

    if (!res.success || !res.data) {
      throw new Error(res.message || "Không thể theo dõi người dùng");
    }

    return res.data;
  },

  unfollow: async (targetUserId: string): Promise<void> => {
    const res = await client.delete(
      `/api/relationships/follows/${targetUserId}`,
    );

    if (!res.success) {
      throw new Error(res.message || "Không thể bỏ theo dõi người dùng");
    }
  },
});

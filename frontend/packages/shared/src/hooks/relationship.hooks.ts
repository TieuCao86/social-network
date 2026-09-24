import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import type { ApiClient } from "../api/api-client";
import { createRelationshipService } from "../services/relationship.service";
import { queryKeys } from "../constants/queryKeys";

export const createRelationshipHooks = (client: ApiClient) => {
  const relationshipService = createRelationshipService(client);

  // ============================================================
  // GET RELATIONSHIP
  // ============================================================

  const useRelationship = (targetUserId: string) => {
    return useQuery({
      queryKey: queryKeys.relationships.detail(targetUserId),
      enabled: !!targetUserId,

      queryFn: () => relationshipService.getRelationship(targetUserId),
    });
  };

  // ============================================================
  // SEND FRIEND REQUEST
  // ============================================================

  const useSendFriendRequest = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (targetUserId: string) =>
        relationshipService.sendFriendRequest(targetUserId),

      onSuccess: (_response, targetUserId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(targetUserId),
        });
      },
    });
  };

  // ============================================================
  // ACCEPT FRIEND REQUEST
  // ============================================================

  const useAcceptFriendRequest = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (requesterId: string) =>
        relationshipService.acceptFriendRequest(requesterId),

      onSuccess: (_response, requesterId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(requesterId),
        });
      },
    });
  };

  // ============================================================
  // REJECT FRIEND REQUEST
  // ============================================================

  const useRejectFriendRequest = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (requesterId: string) =>
        relationshipService.rejectFriendRequest(requesterId),

      onSuccess: (_response, requesterId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(requesterId),
        });
      },
    });
  };

  // ============================================================
  // CANCEL FRIEND REQUEST
  // ============================================================

  const useCancelFriendRequest = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (targetUserId: string) =>
        relationshipService.cancelFriendRequest(targetUserId),

      onSuccess: (_response, targetUserId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(targetUserId),
        });
      },
    });
  };

  // ============================================================
  // UNFRIEND
  // ============================================================

  const useUnfriend = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (friendId: string) => relationshipService.unfriend(friendId),

      onSuccess: (_response, friendId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(friendId),
        });
      },
    });
  };

  // ============================================================
  // FOLLOW
  // ============================================================

  const useFollow = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (targetUserId: string) =>
        relationshipService.follow(targetUserId),

      onSuccess: (_response, targetUserId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(targetUserId),
        });
      },
    });
  };

  // ============================================================
  // UNFOLLOW
  // ============================================================

  const useUnfollow = () => {
    const queryClient = useQueryClient();

    return useMutation({
      mutationFn: (targetUserId: string) =>
        relationshipService.unfollow(targetUserId),

      onSuccess: (_response, targetUserId) => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.relationships.detail(targetUserId),
        });
      },
    });
  };

  return {
    useRelationship,
    useSendFriendRequest,
    useAcceptFriendRequest,
    useRejectFriendRequest,
    useCancelFriendRequest,
    useUnfriend,
    useFollow,
    useUnfollow,
  };
};

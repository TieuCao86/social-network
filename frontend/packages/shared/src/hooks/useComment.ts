import { useQuery } from "@tanstack/react-query";

import { commentService } from "../services/comment.service";

export function useComments(
  postId: string,
  enabled = true,
) {
  return useQuery({
    queryKey: ["comments", "post", postId],

    queryFn: async () => {
      const response =
        await commentService.getComments(
          postId,
          0,
          10,
        );

      if (!response.data) {
        throw new Error(
          "Không có dữ liệu bình luận",
        );
      }

      return response.data;
    },

    enabled: enabled && !!postId,
  });
}
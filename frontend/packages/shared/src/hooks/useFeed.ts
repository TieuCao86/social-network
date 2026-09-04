import {
  useInfiniteQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import { postService } from "../services/post.service";
import type { PostCreateRequest } from "../types/post";

export function useFeed() {
  return useInfiniteQuery({
    queryKey: ["posts", "feed"],

    initialPageParam: 0,

    queryFn: async ({ pageParam }) => {
      const response = await postService.getFeed(pageParam, 10);

      if (!response.data) {
        throw new Error("Không có dữ liệu feed");
      }

      return response.data;
    },

    getNextPageParam: (lastPage) => {
      if (lastPage.last) {
        return undefined;
      }

      return lastPage.number + 1;
    },
  });
}

export function useCreatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: PostCreateRequest) => postService.createPost(payload),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["posts", "feed"],
      });
    },
  });
}

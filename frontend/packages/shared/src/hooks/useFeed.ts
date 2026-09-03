import { useInfiniteQuery } from "@tanstack/react-query";
import { postService } from "../services/post.service";

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
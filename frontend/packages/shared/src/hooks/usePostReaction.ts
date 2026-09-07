import { useMutation } from "@tanstack/react-query";

import { postService } from "../services/post.service";
import type { ReactionRequest } from "../types/post";

export function useReactToPost() {
  return useMutation({
    mutationFn: ({
      postId,
      payload,
    }: {
      postId: string;
      payload: ReactionRequest;
    }) => postService.reactToPost(postId, payload),
  });
}
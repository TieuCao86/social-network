export const queryKeys = {
  auth: {
    all: ["auth"] as const,
    me: () => [...queryKeys.auth.all, "me"] as const,
  },

  users: {
    all: ["users"] as const,
    detail: (userId: string) => [...queryKeys.users.all, "detail", userId] as const,
    profile: (username: string) => [...queryKeys.users.all, "profile", username] as const,
  },

  posts: {
    all: ["posts"] as const,
    lists: () => [...queryKeys.posts.all, "list"] as const,
    list: (params?: Record<string, any>) => [...queryKeys.posts.lists(), { params }] as const,
    detail: (postId: string) => [...queryKeys.posts.all, "detail", postId] as const,
    userPosts: (userId: string) => [...queryKeys.posts.all, "user", userId] as const,
  },

  comments: {
    all: ["comments"] as const,
    byPost: (postId: string, params?: Record<string, any>) =>
      [...queryKeys.comments.all, "post", postId, { params }] as const,
  },

  reactions: {
    all: ["reactions"] as const,
    byPost: (postId: string) => [...queryKeys.reactions.all, "post", postId] as const,
    byComment: (commentId: string) => [...queryKeys.reactions.all, "comment", commentId] as const,
  },
};
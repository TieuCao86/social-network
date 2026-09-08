import { useEffect, useState } from "react";
import { CircleCheck, MessageSquare, Share2, ThumbsUp } from "lucide-react";

import {
  ReactionType,
  useReactToPost,
  useCountComments,
  type PostResponse,
} from "@social/shared";

import { CommentSection } from "../comment/CommentSection";

interface PostCardProps {
  post: PostResponse;
}

const reactions = [
  {
    type: ReactionType.LIKE,
    emoji: "👍",
    label: "Like",
    color: "text-teal-600",
  },
  {
    type: ReactionType.LOVE,
    emoji: "❤️",
    label: "Love",
    color: "text-red-500",
  },
  {
    type: ReactionType.HAHA,
    emoji: "😂",
    label: "Haha",
    color: "text-yellow-500",
  },
  {
    type: ReactionType.WOW,
    emoji: "😮",
    label: "Wow",
    color: "text-yellow-600",
  },
  {
    type: ReactionType.SAD,
    emoji: "😢",
    label: "Sad",
    color: "text-blue-500",
  },
  {
    type: ReactionType.ANGRY,
    emoji: "😡",
    label: "Angry",
    color: "text-orange-600",
  },
];

export function PostCard({ post }: PostCardProps) {
  const firstMedia = post.mediaList?.[0];

  const [showReactions, setShowReactions] = useState(false);
  const [showComments, setShowComments] = useState(false);

  const [reactionTimer, setReactionTimer] = useState<ReturnType<
    typeof setTimeout
  > | null>(null);

  const [userReaction, setUserReaction] = useState<ReactionType | undefined>(
    post.currentUserReaction ?? undefined,
  );

  const [reactionCount, setReactionCount] = useState(post.totalReactions ?? 0);

  // ============================================================
  // COMMENT COUNT
  // ============================================================

  const { data: currentCommentCount } = useCountComments(post.id);

  const displayedCommentCount = currentCommentCount ?? post.commentCount ?? 0;

  // ============================================================
  // SYNC POST REACTION
  // ============================================================

  useEffect(() => {
    setUserReaction(post.currentUserReaction ?? undefined);
    setReactionCount(post.totalReactions ?? 0);
  }, [post.id, post.currentUserReaction, post.totalReactions]);

  // Cleanup timer khi component unmount
  useEffect(() => {
    return () => {
      if (reactionTimer) {
        clearTimeout(reactionTimer);
      }
    };
  }, [reactionTimer]);

  const reactToPost = useReactToPost();

  const activeReaction = reactions.find(
    (reaction) => reaction.type === userReaction,
  );

  // ============================================================
  // REACTION HOVER
  // ============================================================

  const handleReactionEnter = () => {
    if (reactionTimer) {
      clearTimeout(reactionTimer);
      setReactionTimer(null);
    }

    setShowReactions(true);
  };

  const handleReactionLeave = () => {
    const timer = setTimeout(() => {
      setShowReactions(false);
      setReactionTimer(null);
    }, 200);

    setReactionTimer(timer);
  };

  // ============================================================
  // REACTION
  // ============================================================

  const handleReaction = (type: ReactionType) => {
    setShowReactions(false);

    if (reactionTimer) {
      clearTimeout(reactionTimer);
      setReactionTimer(null);
    }

    const isRemoving = userReaction === type;

    const previousReaction = userReaction;
    const previousCount = reactionCount;

    const nextReaction = isRemoving ? undefined : type;

    // Optimistic UI
    setUserReaction(nextReaction);

    setReactionCount((prev) => {
      if (isRemoving) {
        return Math.max(0, prev - 1);
      }

      if (!previousReaction) {
        return prev + 1;
      }

      return prev;
    });

    // API
    reactToPost.mutate(
      {
        postId: post.id,
        payload: {
          type,
        },
      },
      {
        onSuccess: (response) => {
          const data = response.data;

          if (!data) {
            return;
          }

          setUserReaction(data.currentUserReaction ?? undefined);
          setReactionCount(data.totalReactions ?? 0);
        },

        onError: () => {
          setUserReaction(previousReaction);
          setReactionCount(previousCount);
        },
      },
    );
  };

  const handleMainButtonClick = () => {
    handleReaction(userReaction ?? ReactionType.LIKE);
  };

  // ============================================================
  // COMMENT
  // ============================================================

  const handleCommentClick = () => {
    setShowComments((prev) => !prev);
  };

  return (
    <article className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden text-xs">
      {/* ========================================================
          POST CONTENT
      ======================================================== */}

      <div className="p-3.5">
        <div className="grid grid-cols-1 sm:grid-cols-2 bg-slate-50 rounded-lg overflow-hidden border border-gray-200">
          {/* MEDIA */}

          {firstMedia ? (
            <div className="w-full h-40 sm:h-auto bg-slate-100 flex items-center justify-center text-gray-400">
              <span>{firstMedia.type === "IMAGE" ? "Image" : "Video"}</span>
            </div>
          ) : (
            <div className="w-full h-40 sm:h-auto bg-slate-100 flex items-center justify-center text-gray-400">
              No image
            </div>
          )}

          {/* CONTENT */}

          <div className="p-3.5 flex flex-col justify-center bg-gray-50">
            <p className="font-bold text-gray-800 text-sm leading-snug">
              {post.content}
            </p>
          </div>
        </div>

        {/* AUTHOR */}

        <p className="text-gray-500 text-[11px] mt-2.5">
          {post.author.fullName || post.author.username}
          {" · "}@{post.author.username}
        </p>
      </div>

      {/* ========================================================
          ACTION BAR
      ======================================================== */}

      <div className="px-3.5 py-2.5 border-t border-gray-100 flex items-center justify-between text-gray-500 text-xs">
        <div className="flex space-x-5">
          {/* ====================================================
              REACTION
          ==================================================== */}

          <div
            className="relative"
            onMouseEnter={handleReactionEnter}
            onMouseLeave={handleReactionLeave}
          >
            {showReactions && (
              <div className="absolute bottom-full left-0 z-20 pb-2">
                <div className="flex items-center gap-1.5 bg-white border border-gray-200 shadow-xl rounded-full px-2.5 py-1.5 animate-in fade-in zoom-in-95 duration-150">
                  {reactions.map((reaction) => (
                    <button
                      key={reaction.type}
                      type="button"
                      title={reaction.label}
                      onClick={() => handleReaction(reaction.type)}
                      className={`text-2xl leading-none transition-transform duration-150 hover:scale-125 ${
                        String(userReaction).toUpperCase() ===
                        String(reaction.type).toUpperCase()
                          ? "scale-125"
                          : ""
                      }`}
                    >
                      {reaction.emoji}
                    </button>
                  ))}
                </div>
              </div>
            )}

            <button
              type="button"
              disabled={reactToPost.isPending}
              onClick={handleMainButtonClick}
              className={`flex items-center space-x-1.5 transition-colors font-medium ${
                activeReaction
                  ? activeReaction.color
                  : "text-gray-500 hover:text-teal-600"
              }`}
            >
              {activeReaction ? (
                <span className="text-base leading-none">
                  {activeReaction.emoji}
                </span>
              ) : (
                <ThumbsUp className="w-4 h-4" />
              )}

              <span>{reactionCount}</span>
            </button>
          </div>

          {/* ====================================================
              COMMENT
          ==================================================== */}

          <button
            type="button"
            onClick={handleCommentClick}
            className={`flex items-center space-x-1 transition-colors ${
              showComments ? "text-teal-600 font-medium" : "hover:text-teal-600"
            }`}
          >
            <MessageSquare className="w-4 h-4" />

            <span>{displayedCommentCount}</span>
          </button>

          {/* ====================================================
              SHARE
          ==================================================== */}

          <button
            type="button"
            className="hover:text-teal-600 flex items-center space-x-1 transition-colors"
          >
            <Share2 className="w-4 h-4" />

            <span>{post.shareCount}</span>
          </button>
        </div>

        {/* VERIFIED */}

        <CircleCheck className="w-4 h-4 text-teal-600" />
      </div>

      {/* ========================================================
          COMMENT SECTION
      ======================================================== */}

      {showComments && (
        <CommentSection postId={post.id} commentCount={displayedCommentCount} />
      )}
    </article>
  );
}

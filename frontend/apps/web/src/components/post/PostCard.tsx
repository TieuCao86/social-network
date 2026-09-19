import { useEffect, useState } from "react";
import {
  Globe,
  MessageSquare,
  MoreHorizontal,
  Play,
  Share2,
} from "lucide-react";

import {
  ReactionType,
  useReactToPost,
  useCountComments,
  type PostResponse,
} from "@social/shared";

import { CommentSection } from "../comment/CommentSection";
import { formatRelativeTime } from "@social/shared";

interface PostCardProps {
  post: PostResponse;
}

const reactions = [
  { type: ReactionType.LIKE, emoji: "👍", label: "Thích", color: "text-teal-600" },
  { type: ReactionType.LOVE, emoji: "❤️", label: "Yêu thích", color: "text-red-500" },
  { type: ReactionType.HAHA, emoji: "😂", label: "Haha", color: "text-yellow-500" },
  { type: ReactionType.WOW, emoji: "😮", label: "Wow", color: "text-yellow-600" },
  { type: ReactionType.SAD, emoji: "😢", label: "Buồn", color: "text-blue-500" },
  { type: ReactionType.ANGRY, emoji: "😡", label: "Phẫn nộ", color: "text-orange-600" },
];

export function PostCard({ post }: PostCardProps) {
  const firstMedia = post.mediaList?.[0];

  const [showReactions, setShowReactions] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [reactionTimer, setReactionTimer] = useState<ReturnType<typeof setTimeout> | null>(null);

  const [userReaction, setUserReaction] = useState<ReactionType | undefined>(
    post.currentUserReaction ?? undefined,
  );
  const [reactionCount, setReactionCount] = useState(post.totalReactions ?? 0);

  const { data: currentCommentCount } = useCountComments(post.postId);
  const displayedCommentCount = currentCommentCount ?? post.commentCount ?? 0;

  useEffect(() => {
    setUserReaction(post.currentUserReaction ?? undefined);
    setReactionCount(post.totalReactions ?? 0);
  }, [post.postId, post.currentUserReaction, post.totalReactions]);

  useEffect(() => {
    return () => {
      if (reactionTimer) clearTimeout(reactionTimer);
    };
  }, [reactionTimer]);

  const reactToPost = useReactToPost();

  const activeReaction = reactions.find((r) => r.type === userReaction);

  const topReactions = post.topReactions
    ?.slice(0, 3)
    .map((type) => reactions.find((r) => r.type === type))
    .filter(Boolean);

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

    setUserReaction(nextReaction);
    setReactionCount((prev) => {
      if (isRemoving) return Math.max(0, prev - 1);
      if (!previousReaction) return prev + 1;
      return prev;
    });

    reactToPost.mutate(
      { postId: post.postId, payload: { type } },
      {
        onSuccess: (response) => {
          const data = response.data;
          if (!data) return;
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

  return (
    <article className="bg-white rounded-xl shadow-sm overflow-hidden text-xs">
      {/* HEADER */}
      <div className="p-4 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <img
            src={
              post.author.userId ||
              "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&q=80"
            }
            alt={post.author.fullName || post.author.username}
            className="w-10 h-10 rounded-full object-cover"
          />
          <div>
            <h4 className="font-bold text-sm text-gray-900">
              {post.author.fullName || post.author.username}
            </h4>
            <div className="flex items-center space-x-1 text-xs text-gray-500">
              <span>{formatRelativeTime(post.createdAt)}</span>
              <span>·</span>
              <Globe className="w-3 h-3" />
            </div>
          </div>
        </div>
        <button type="button" className="text-gray-500 hover:bg-gray-100 p-2 rounded-full transition">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>

      {/* CONTENT */}
      {post.content && <div className="px-4 text-sm pb-3 text-gray-800">{post.content}</div>}

      {/* MEDIA */}
      {firstMedia && (
        <div className="bg-black relative h-72 flex items-center justify-center">
          {firstMedia.type === "IMAGE" ? (
            <img src={firstMedia.url} alt="Post media" className="w-full h-full object-cover" />
          ) : (
            <>
              <video src={firstMedia.url} className="w-full h-full object-cover" />
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="w-16 h-16 bg-red-600 rounded-full flex items-center justify-center text-white text-xl shadow-lg cursor-pointer hover:bg-red-700 transition">
                  <Play className="w-6 h-6 ml-1 fill-white" />
                </div>
              </div>
            </>
          )}
        </div>
      )}

      {/* UNIFIED TOP BAR: STATS & INTERACTIVE BUTTONS */}
      <div className="px-3 py-2 flex items-center justify-between text-xs border-b border-gray-200 relative">
        {/* Left: Interactive Buttons (Like with Reaction Hover, Comment, Share) */}
        <div className="flex items-center gap-1 sm:gap-2">
          {/* LIKE BUTTON WITH REACTION HOVER POPUP */}
          <div
            className="relative"
            onMouseEnter={handleReactionEnter}
            onMouseLeave={handleReactionLeave}
          >
            {showReactions && (
              <div className="absolute bottom-full left-0 mb-2 bg-white shadow-xl rounded-full px-2 py-1.5 flex items-center gap-2 border border-gray-200 z-20">
                {reactions.map((r) => (
                  <button
                    key={r.type}
                    type="button"
                    onClick={() => handleReaction(r.type)}
                    className="text-2xl hover:scale-125 transition-transform duration-150 p-1"
                    title={r.label}
                  >
                    {r.emoji}
                  </button>
                ))}
              </div>
            )}

            <button
              type="button"
              onClick={() => handleReaction(userReaction ?? ReactionType.LIKE)}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-semibold hover:bg-gray-100 transition ${
                activeReaction ? activeReaction.color : "text-gray-600"
              }`}
            >
              <span className="text-base">{activeReaction ? activeReaction.emoji : "👍"}</span>
              <span>{activeReaction ? activeReaction.label : "Thích"}</span>
              <span className="ml-1 text-gray-500 font-normal">({reactionCount})</span>
            </button>
          </div>

          {/* COMMENT BUTTON */}
          <button
            type="button"
            onClick={() => setShowComments((prev) => !prev)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-semibold text-gray-600 hover:bg-gray-100 transition"
          >
            <MessageSquare className="w-4 h-4" />
            <span>Bình luận</span>
            <span className="text-gray-500 font-normal">({displayedCommentCount})</span>
          </button>

          {/* SHARE BUTTON */}
          <button
            type="button"
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg font-semibold text-gray-600 hover:bg-gray-100 transition"
          >
            <Share2 className="w-4 h-4" />
            <span>Chia sẻ</span>
            <span className="text-gray-500 font-normal">({post.shareCount ?? 0})</span>
          </button>
        </div>

        {/* Right: Top Reactions Preview Icons */}
        <div className="flex items-center pr-1">
          {topReactions?.map((reaction, index) =>
            reaction ? (
              <span
                key={reaction.type}
                title={reaction.label}
                className={`text-base leading-none ${index > 0 ? "-ml-1" : ""}`}
              >
                {reaction.emoji}
              </span>
            ) : null,
          )}
        </div>
      </div>

      {/* COMMENTS SECTION */}
      {showComments && (
        <CommentSection postId={post.postId} commentCount={displayedCommentCount} />
      )}
    </article>
  );
}
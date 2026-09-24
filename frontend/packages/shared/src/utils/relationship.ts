import type { RelationshipStatus } from "../types/user";

export const getRelationshipText = (
  status: RelationshipStatus | null,
): string => {
  switch (status) {
    case "FRIENDS":
      return "Bạn bè";

    case "REQUEST_SENT":
      return "Đã gửi lời mời";

    case "REQUEST_RECEIVED":
      return "Phản hồi";

    case "FOLLOWING":
      return "Đang theo dõi";

    case "FOLLOWED_BY":
      return "Theo dõi";

    case "FOLLOWING_EACH_OTHER":
      return "Đang theo dõi nhau";

    case "BLOCKING":
      return "Đã chặn";

    case "BLOCKED_BY":
      return "Bị chặn";

    case "NONE":
    default:
      return "Thêm bạn mới";
  }
};
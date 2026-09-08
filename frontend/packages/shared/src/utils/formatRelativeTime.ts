export function formatRelativeTime(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();

  const diffMs = now.getTime() - date.getTime();

  // Nếu thời gian trong tương lai
  if (diffMs < 0) {
    return "vừa xong";
  }

  const diffSeconds = Math.floor(diffMs / 1000);
  const diffMinutes = Math.floor(diffSeconds / 60);
  const diffHours = Math.floor(diffMinutes / 60);
  const diffDays = Math.floor(diffHours / 24);
  const diffWeeks = Math.floor(diffDays / 7);
  const diffMonths = Math.floor(diffDays / 30);
  const diffYears = Math.floor(diffDays / 365);

  // < 1 phút
  if (diffSeconds < 60) {
    return "vừa xong";
  }

  // < 1 giờ
  if (diffMinutes < 60) {
    return `${diffMinutes} phút trước`;
  }

  // < 1 ngày
  if (diffHours < 24) {
    return `${diffHours} giờ trước`;
  }

  // < 7 ngày
  if (diffDays < 7) {
    return `${diffDays} ngày trước`;
  }

  // >= 7 ngày -> tuần
  if (diffDays < 30) {
    return `${diffWeeks} tuần trước`;
  }

  // >= 30 ngày -> tháng
  if (diffDays < 365) {
    return `${diffMonths} tháng trước`;
  }

  // >= 365 ngày -> năm
  return `${diffYears} năm trước`;
}

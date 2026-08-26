export const APP_THEME = {
  colors: {
    primaryStart: "#6366F1", // Indigo
    primaryEnd: "#14B8A6", // Teal
    primaryHoverStart: "#4F46E5",
    primaryHoverEnd: "#0D9488",
    disabled: "#94A3B8",
    white: "#FFFFFF",
    border: "#E2E8F0",
    textMuted: "#64748B",
  },
  gradients: {
    primary: "linear-gradient(135deg, #6366F1 0%, #14B8A6 100%)",
    primaryHover: "linear-gradient(135deg, #4F46E5 0%, #0D9488 100%)",
    primaryTuple: ["#6366F1", "#14B8A6"] as const,
  },
  borderRadius: {
    sm: 8,
    md: 12,
    lg: 14,
    full: 9999,
  },
};

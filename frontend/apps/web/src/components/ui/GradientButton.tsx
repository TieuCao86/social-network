import type { ButtonHTMLAttributes } from "react";

interface GradientButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement> {
  isLoading?: boolean;
}

export function GradientButton({
  children,
  isLoading,
  disabled,
  className = "",
  ...props
}: GradientButtonProps) {
  return (
    <button
      disabled={disabled || isLoading}
      className={`
        w-full
        py-3.5 px-6
        rounded-2xl
        text-white
        font-bold text-sm sm:text-base
        tracking-wider uppercase
        bg-linear-to-r from-[#6366F1] via-[#06B6D4] to-[#10B981]
        shadow-lg shadow-teal-500/25
        hover:shadow-xl hover:opacity-95
        active:scale-[0.99]
        transition-all duration-200
        disabled:opacity-60
        disabled:cursor-not-allowed
        flex items-center justify-center
        ${className}
      `}
      {...props}
    >
      {isLoading && (
        <span className="inline-block w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
      )}
      {children}
    </button>
  );
}
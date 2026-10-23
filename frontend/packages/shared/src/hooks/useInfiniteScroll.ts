import { useEffect, useRef } from "react";

interface UseInfiniteScrollOptions {
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  fetchNextPage: () => Promise<unknown>;
}

export function useInfiniteScroll({
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
}: UseInfiniteScrollOptions) {
  const loadMoreRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const element = loadMoreRef.current;
    if (!element) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (
          entry.isIntersecting &&
          hasNextPage &&
          !isFetchingNextPage
        ) {
          void fetchNextPage();
        }
      },
      { threshold: 0.1 },
    );

    observer.observe(element);
    return () => observer.disconnect();
  }, [fetchNextPage, hasNextPage, isFetchingNextPage]);

  return loadMoreRef;
}
import { createUseAuth } from "@social/shared";

import { apiClient } from "../api/client";
import { queryClient } from "../api/queryClient";

const useSharedAuth = createUseAuth(apiClient);

export function useAuth() {
  return useSharedAuth({
    client: queryClient,

    onLogoutSuccess: () => {
      window.location.href = "/login";
    },
  });
}

import { createUseAuth } from "@social/shared";
import { apiClient } from "../api/client";

const useSharedAuth = createUseAuth(apiClient);

export function useAuth() {
  return useSharedAuth({
    onLogoutSuccess: () => {
      window.location.href = "/login";
    },
  });
}
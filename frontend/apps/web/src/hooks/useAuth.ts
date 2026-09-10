import { createUseAuth } from "@social/shared";

import { apiClient } from "../api/client";

const authHooks = createUseAuth(apiClient);

export function useAuth() {
  return authHooks;
}
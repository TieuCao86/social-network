import { createUseAuth } from "@social/shared";

import { apiClient } from "../api/client";
import { queryClient } from "../api/queryClient";
import { mobileStorage } from "../utils/storage";

const useSharedAuth = createUseAuth(apiClient);

export function useAuth() {
  return useSharedAuth({
    client: queryClient,

    storage: {
      getToken: () => mobileStorage.getItem("access_token"),

      setToken: (token) => mobileStorage.setItem("access_token", token),

      removeToken: () => mobileStorage.deleteItem("access_token"),
    },
  });
}

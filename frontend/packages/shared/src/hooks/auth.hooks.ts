import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import type { ApiClient } from "../api/api-client";
import { createAuthService } from "../services/auth.service";
import { createUserService } from "../services/user.service";
import { queryKeys } from "../constants/queryKeys";

import type { LoginRequest, LoginResponse, UserResponse } from "../types";

const authEnabledKey = ["auth", "enabled"] as const;

export const createUseAuth = (client: ApiClient) => {
  const authService = createAuthService(client);
  const userService = createUserService(client);

  const useMe = () => {
    const queryClient = useQueryClient();

    const authEnabled =
      queryClient.getQueryData<boolean>(authEnabledKey) ?? true;

    return useQuery<UserResponse>({
      queryKey: queryKeys.auth.me(),
      queryFn: () => userService.getProfile(),
      enabled: authEnabled,
      retry: false,
    });
  };

  const useLogin = () => {
    const queryClient = useQueryClient();

    return useMutation<LoginResponse, Error, LoginRequest>({
      mutationFn: (body) => authService.login(body),

      onSuccess: async () => {
        queryClient.setQueryData(authEnabledKey, true);

        await queryClient.invalidateQueries({
          queryKey: queryKeys.auth.me(),
        });
      },
    });
  };

  const useLogout = () => {
    const queryClient = useQueryClient();

    return useMutation<void, Error>({
      mutationFn: () => authService.logout(),

      onSuccess: async () => {
        queryClient.setQueryData(authEnabledKey, false);

        await queryClient.cancelQueries({
          queryKey: queryKeys.auth.me(),
        });

        queryClient.removeQueries({
          queryKey: queryKeys.auth.me(),
        });
      },
    });
  };

  return { useMe, useLogin, useLogout };
};

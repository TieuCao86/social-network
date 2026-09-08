import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import type { ApiClient } from "../api/api-client";
import { createAuthService } from "../services/auth.service";
import { createUserService } from "../services/user.service";
import { queryKeys } from "../constants/queryKeys";

import type { LoginRequest, LoginResponse, UserResponse } from "../types";

export const createUseAuth = (client: ApiClient) => {
  const authService = createAuthService(client);
  const userService = createUserService(client);

  const useMe = () => {
    return useQuery<UserResponse>({
      queryKey: queryKeys.auth.me(),
      queryFn: () => userService.getProfile(),
      retry: false,
    });
  };

  const useLogin = () => {
    const queryClient = useQueryClient();

    return useMutation<LoginResponse, Error, LoginRequest>({
      mutationFn: (body) => authService.login(body),

      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: queryKeys.auth.me(),
        });
      },
    });
  };

  const useLogout = () => {
    const queryClient = useQueryClient();

    return useMutation<void, Error>({
      mutationFn: () => authService.logout(),

      onSuccess: () => {
        queryClient.removeQueries({
          queryKey: queryKeys.auth.me(),
        });
      },
    });
  };

  return {
    useMe,
    useLogin,
    useLogout,
  };
};

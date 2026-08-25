import * as SecureStore from "expo-secure-store";

export const tokenStorage = {
  get: () => SecureStore.getItemAsync("access_token"),
  set: (token: string) => SecureStore.setItemAsync("access_token", token),
  remove: () => SecureStore.deleteItemAsync("access_token"),
};
import { Platform } from "react-native";
import * as SecureStore from "expo-secure-store";

export const mobileStorage = {
  async getItem(key: string): Promise<string | null> {
    if (Platform.OS === "web") {
      return typeof window !== "undefined" ? localStorage.getItem(key) : null;
    }
    try {
      return await SecureStore.getItemAsync(key);
    } catch {
      return null;
    }
  },

  async setItem(key: string, value: string): Promise<void> {
    if (Platform.OS === "web") {
      if (typeof window !== "undefined") localStorage.setItem(key, value);
      return;
    }
    try {
      await SecureStore.setItemAsync(key, value);
    } catch (err) {
      console.error("Lỗi SecureStore setItem:", err);
    }
  },

  async deleteItem(key: string): Promise<void> {
    if (Platform.OS === "web") {
      if (typeof window !== "undefined") localStorage.removeItem(key);
      return;
    }
    try {
      await SecureStore.deleteItemAsync(key);
    } catch (err) {
      console.error("Lỗi SecureStore deleteItem:", err);
    }
  },
};
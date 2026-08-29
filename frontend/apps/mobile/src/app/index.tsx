import React from "react";
import { View, ActivityIndicator, StyleSheet } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { LoginPage } from "@/pages/LoginPage";
import { MobileHomePage } from "../pages/HomePage";

export default function HomeScreen() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#0d9488" />
      </View>
    );
  }

  // Tự động chuyển đổi giữa trang Login và trang Feed chính
  return isAuthenticated ? <MobileHomePage /> : <LoginPage />;
}

const styles = StyleSheet.create({
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F8FAFC",
  },
});
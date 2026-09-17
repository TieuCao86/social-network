import { View, ActivityIndicator, StyleSheet } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { LoginPage } from "@/pages/LoginPage";
import { MobileHomePage } from "../pages/HomePage";

export default function HomeScreen() {
  const { useMe } = useAuth();

  const { data: user, isLoading } = useMe();

  const isAuthenticated = !!user;

  if (isLoading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#0d9488" />
      </View>
    );
  }

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

import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Image,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Feather } from "@expo/vector-icons";
import { loginSchema, type LoginFormData } from "@social/shared";
import { useAuth } from "../hooks/useAuth";

// Import logo dạng đứng
import logoImg_D from "../../assets/logo_D.png";

export function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const { login, isLoggingIn } = useAuth();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      phoneOrEmail: "",
      password: "",
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data);
    } catch (err: any) {
      Alert.alert("Lỗi đăng nhập", err.message || "Vui lòng thử lại sau.");
    }
  };

  return (
    <LinearGradient
      colors={["#eaf7f5", "#ddf3f0", "#d4efe9"]}
      style={styles.container}
    >
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
        >
          {/* Logo phía trên */}
          <View style={styles.logoContainer}>
            <Image
              source={logoImg_D}
              style={styles.logoImage}
              resizeMode="contain"
            />
          </View>

          {/* Khung Form */}
          <View style={styles.formContainer}>
            {/* Input Email / Phone */}
            <View style={styles.inputGroup}>
              <Controller
                control={control}
                name="phoneOrEmail"
                render={({ field: { onChange, value } }) => (
                  <View style={styles.inputWrapper}>
                    <TextInput
                      placeholder="Email / Phone"
                      placeholderTextColor="#94A3B8"
                      value={value}
                      onChangeText={onChange}
                      autoCapitalize="none"
                      style={styles.input}
                    />
                  </View>
                )}
              />
              {errors.phoneOrEmail && (
                <Text style={styles.errorText}>
                  {errors.phoneOrEmail.message}
                </Text>
              )}
            </View>

            {/* Input Password */}
            <View style={styles.inputGroup}>
              <Controller
                control={control}
                name="password"
                render={({ field: { onChange, value } }) => (
                  <View style={styles.inputWrapper}>
                    <TextInput
                      placeholder="Password"
                      placeholderTextColor="#94A3B8"
                      secureTextEntry={!showPassword}
                      value={value}
                      onChangeText={onChange}
                      style={styles.input}
                    />
                    <TouchableOpacity
                      onPress={() => setShowPassword(!showPassword)}
                      hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
                    >
                      <Feather
                        name={showPassword ? "eye" : "eye-off"}
                        size={20}
                        color="#94A3B8"
                      />
                    </TouchableOpacity>
                  </View>
                )}
              />
              {errors.password && (
                <Text style={styles.errorText}>{errors.password.message}</Text>
              )}
            </View>

            {/* Nút Gradient SIGN IN */}
            <TouchableOpacity
              onPress={handleSubmit(onSubmit)}
              disabled={isLoggingIn}
              activeOpacity={0.85}
              style={styles.btnContainer}
            >
              <LinearGradient
                colors={["#6366F1", "#06B6D4", "#10B981"]}
                start={{ x: 0, y: 0 }}
                end={{ x: 1, y: 0 }}
                style={styles.gradientBtn}
              >
                <Text style={styles.btnText}>
                  {isLoggingIn ? "SIGNING IN..." : "SIGN IN"}
                </Text>
              </LinearGradient>
            </TouchableOpacity>

            {/* Forgot Password */}
            <TouchableOpacity style={styles.forgotBtn}>
              <Text style={styles.forgotText}>Forgot Password?</Text>
            </TouchableOpacity>

            {/* Create account / Sign up */}
            <View style={styles.signupRow}>
              <Text style={styles.signupLabel}>Don't have an account? </Text>
              <TouchableOpacity>
                <Text style={styles.signupLink}>Sign Up</Text>
              </TouchableOpacity>
            </View>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  flex: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: "center",
    paddingHorizontal: 32,
    paddingTop: Platform.OS === "ios" ? 50 : 30,
    paddingBottom: 40,
  },
  logoContainer: {
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 40,
    overflow: "visible",
  },
  logoImage: {
    width: 380,
    height: 280,
    transform: [{ scale: 1.45 }],
  },
  formContainer: {
    width: "100%",
  },
  inputGroup: {
    marginBottom: 16,
  },
  inputWrapper: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 16,
    paddingHorizontal: 18,
    height: 54,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.03,
    shadowRadius: 4,
    elevation: 1,
  },
  input: {
    flex: 1,
    fontSize: 15,
    color: "#1E293B",
  },
  errorText: {
    fontSize: 12,
    color: "#E11D48",
    marginTop: 4,
    marginLeft: 6,
  },
  btnContainer: {
    marginTop: 8,
    borderRadius: 16,
    overflow: "hidden",
    shadowColor: "#06B6D4",
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 4,
  },
  gradientBtn: {
    height: 52,
    justifyContent: "center",
    alignItems: "center",
  },
  btnText: {
    color: "#FFFFFF",
    fontSize: 15,
    fontWeight: "700",
    letterSpacing: 1,
  },
  forgotBtn: {
    alignItems: "center",
    marginTop: 20,
  },
  forgotText: {
    fontSize: 13,
    color: "#64748B",
    fontWeight: "500",
  },
  signupRow: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: 24,
  },
  signupLabel: {
    fontSize: 13,
    color: "#64748B",
  },
  signupLink: {
    fontSize: 13,
    color: "#0284C7",
    fontWeight: "700",
  },
});

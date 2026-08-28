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
  ActivityIndicator,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Feather } from "@expo/vector-icons";
import { loginSchema, type LoginFormData } from "@social/shared";
import { useAuth } from "../hooks/useAuth";

// Import logo dạng đứng
import logoImg_D from "../../assets/logo_D.png";

// Helper thông báo hoạt động trên cả Native và Expo Web
const showToastOrAlert = (title: string, message: string) => {
  if (Platform.OS === "web") {
    window.alert(`${title}: ${message}`);
  } else {
    Alert.alert(title, message);
  }
};

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

  // Xử lý khi Form hợp lệ
  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data);
      showToastOrAlert("Đăng nhập thành công", "Chào mừng bạn quay trở lại!");
    } catch (err: any) {
      showToastOrAlert(
        "Đăng nhập thất bại",
        err?.message || "Vui lòng kiểm tra lại tài khoản hoặc kết nối mạng.",
      );
    }
  };

  // Xử lý khi validation thất bại (Người dùng để trống hoặc nhập sai định dạng)
  const onInvalid = (formErrors: typeof errors) => {
    const firstErrorMessage =
      formErrors.phoneOrEmail?.message ||
      formErrors.password?.message ||
      "Vui lòng điền đầy đủ thông tin đăng nhập.";
    showToastOrAlert("Lỗi nhập liệu", firstErrorMessage);
  };

  return (
    <LinearGradient
      colors={["#eaf7f5", "#ddf3f0", "#d4efe9"]}
      style={styles.container}
    >
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
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
                render={({ field: { onChange, onBlur, value } }) => (
                  <View
                    style={[
                      styles.inputWrapper,
                      errors.phoneOrEmail && styles.inputErrorBorder,
                    ]}
                  >
                    <Feather
                      name="mail"
                      size={18}
                      color="#94A3B8"
                      style={styles.inputIcon}
                    />
                    <TextInput
                      placeholder="Email / Phone"
                      placeholderTextColor="#94A3B8"
                      value={value}
                      onBlur={onBlur}
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
                render={({ field: { onChange, onBlur, value } }) => (
                  <View
                    style={[
                      styles.inputWrapper,
                      errors.password && styles.inputErrorBorder,
                    ]}
                  >
                    <Feather
                      name="lock"
                      size={18}
                      color="#94A3B8"
                      style={styles.inputIcon}
                    />
                    <TextInput
                      placeholder="Password"
                      placeholderTextColor="#94A3B8"
                      secureTextEntry={!showPassword}
                      value={value}
                      onBlur={onBlur}
                      onChangeText={onChange}
                      autoCapitalize="none"
                      style={styles.input}
                    />
                    <TouchableOpacity
                      onPress={() => setShowPassword(!showPassword)}
                      hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
                    >
                      <Feather
                        name={showPassword ? "eye" : "eye-off"}
                        size={18}
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
              onPress={handleSubmit(onSubmit, onInvalid)}
              disabled={isLoggingIn}
              activeOpacity={0.85}
              style={[styles.btnContainer, isLoggingIn && { opacity: 0.7 }]}
            >
              <LinearGradient
                colors={["#6366F1", "#06B6D4", "#10B981"]}
                start={{ x: 0, y: 0 }}
                end={{ x: 1, y: 0 }}
                style={styles.gradientBtn}
              >
                {isLoggingIn ? (
                  <ActivityIndicator color="#FFFFFF" size="small" />
                ) : (
                  <Text style={styles.btnText}>SIGN IN</Text>
                )}
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
    paddingHorizontal: 28,
    paddingTop: Platform.OS === "ios" ? 40 : 20,
    paddingBottom: 30,
  },
  logoContainer: {
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 20,
  },
  logoImage: {
    width: 260,
    height: 160,
  },
  formContainer: {
    width: "100%",
  },
  inputGroup: {
    marginBottom: 14,
  },
  inputWrapper: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 16,
    paddingHorizontal: 16,
    height: 52,
    ...Platform.select({
      web: {
        boxShadow: "0 2px 4px 0 rgba(0, 0, 0, 0.04)",
      },
      default: {
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.04,
        shadowRadius: 2,
        elevation: 1,
      },
    }),
  },
  inputErrorBorder: {
    borderColor: "#F43F5E",
  },
  inputIcon: {
    marginRight: 10,
  },
  input: {
    flex: 1,
    fontSize: 14,
    color: "#1E293B",
  },
  errorText: {
    fontSize: 12,
    color: "#E11D48",
    marginTop: 4,
    marginLeft: 6,
  },
  btnContainer: {
    marginTop: 10,
    borderRadius: 16,
    overflow: "hidden",
    ...Platform.select({
      web: {
        boxShadow: "0 6px 14px 0 rgba(6, 182, 212, 0.25)",
      },
      default: {
        shadowColor: "#06B6D4",
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.25,
        shadowRadius: 6,
        elevation: 3,
      },
    }),
  },
  gradientBtn: {
    height: 50,
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
    marginTop: 18,
  },
  forgotText: {
    fontSize: 13,
    color: "#64748B",
    fontWeight: "500",
  },
  signupRow: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: 22,
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

import {
  TouchableOpacity,
  Text,
  StyleSheet,
  ActivityIndicator,
  type TouchableOpacityProps,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { APP_THEME } from "@social/shared";

interface GradientButtonProps extends TouchableOpacityProps {
  isLoading?: boolean;
  children: string;
}

export function GradientButton({
  children,
  isLoading,
  disabled,
  style,
  ...props
}: GradientButtonProps) {
  return (
    <TouchableOpacity
      disabled={disabled || isLoading}
      activeOpacity={0.8}
      style={[
        styles.container,
        disabled && styles.disabled,
        style,
      ]}
      {...props}
    >
      <LinearGradient
        colors={APP_THEME.gradients.primaryTuple}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 0 }}
        style={styles.gradient}
      >
        {isLoading ? (
          <ActivityIndicator color={APP_THEME.colors.white} />
        ) : (
          <Text style={styles.text}>{children}</Text>
        )}
      </LinearGradient>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    width: "100%",
    borderRadius: APP_THEME.borderRadius.lg,
    shadowColor: APP_THEME.colors.primaryEnd,
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 4,
  },

  gradient: {
    paddingVertical: 14,
    borderRadius: APP_THEME.borderRadius.lg,
    alignItems: "center",
    justifyContent: "center",
  },

  text: {
    color: APP_THEME.colors.white,
    fontSize: 14,
    fontWeight: "700",
    letterSpacing: 0.5,
  },

  disabled: {
    opacity: 0.5,
  },
});
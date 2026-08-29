import React, { useEffect } from "react";
import { QueryClientProvider } from "@tanstack/react-query";
import { Slot } from "expo-router";
import * as SplashScreen from "expo-splash-screen";
import { queryClient } from "../api/queryClient";

SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  useEffect(() => {
    SplashScreen.hideAsync();
  }, []);

  return (
    <QueryClientProvider client={queryClient}>
      <Slot />
    </QueryClientProvider>
  );
}
import React, { useState } from "react";
import {
  StyleSheet,
  View,
  Text,
  Image,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  Platform,
  FlatList,
} from "react-native";
import { Feather } from "@expo/vector-icons";

const STORIES = [
  { id: "1", name: "Sonya", img: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=60" },
  { id: "2", name: "Explore", img: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=60" },
  { id: "3", name: "Stories", img: "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&auto=format&fit=crop&q=60" },
];

export function MobileHomePage() {
  const [topTab, setTopTab] = useState("home");
  const [bottomTab, setBottomTab] = useState("Home");

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* Top Header */}
      <View style={styles.topHeader}>
        <TouchableOpacity style={styles.iconCircle}>
          <Feather name="menu" size={20} color="#0f766e" />
        </TouchableOpacity>
        <View style={styles.brandRow}>
          <View style={styles.brandLogo}>
            <Text style={styles.brandLogoText}>CH</Text>
          </View>
          <Text style={styles.brandTitle}>ConnectHub</Text>
        </View>
        <TouchableOpacity style={styles.iconCircle}>
          <Feather name="message-circle" size={20} color="#0f766e" />
        </TouchableOpacity>
      </View>

      {/* Top Tabs */}
      <View style={styles.tabsContainer}>
        {[
          { key: "home", label: "Trang chủ", icon: "home" },
          { key: "msg", label: "Tin nhắn", icon: "message-square" },
          { key: "notif", label: "Thông báo", icon: "bell" },
          { key: "explore", label: "Khám phá", icon: "compass" },
          { key: "profile", label: "Cá nhân", icon: "user" },
        ].map((tab) => {
          const isActive = topTab === tab.key;
          return (
            <TouchableOpacity
              key={tab.key}
              style={[styles.tabItem, isActive && styles.activeTabItem]}
              onPress={() => setTopTab(tab.key)}
            >
              <Feather
                name={tab.icon as any}
                size={16}
                color={isActive ? "#0d9488" : "#64748b"}
              />
              <Text style={[styles.tabLabel, isActive && styles.activeTabLabel]}>
                {tab.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>

      {/* Feed */}
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.feedContent}>
        <View style={styles.storySection}>
          <FlatList
            horizontal
            showsHorizontalScrollIndicator={false}
            data={STORIES}
            keyExtractor={(item) => item.id}
            contentContainerStyle={{ paddingHorizontal: 12 }}
            renderItem={({ item }) => (
              <TouchableOpacity style={styles.storyCard}>
                <Image source={{ uri: item.img }} style={styles.storyImage} />
                <View style={styles.storyOverlay} />
                <Text style={styles.storyName}>{item.name}</Text>
              </TouchableOpacity>
            )}
          />
        </View>

        <View style={styles.postCard}>
          <View style={styles.postHeader}>
            <Image
              source={{ uri: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop&q=60" }}
              style={styles.authorAvatar}
            />
            <View style={styles.postAuthorMeta}>
              <Text style={styles.postAuthorName}>Group K22: Lập trình viên</Text>
              <Text style={styles.postTime}>Vừa xong</Text>
            </View>
            <Feather name="more-horizontal" size={18} color="#94a3b8" />
          </View>
          <Text style={styles.postBodyText}>
            Câu chuyện về các lập trình viên trẻ ngành Công nghệ thông tin... 💻🔥
          </Text>
          <Image
            source={{ uri: "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800&auto=format&fit=crop&q=60" }}
            style={styles.postImage}
          />
        </View>
      </ScrollView>

      {/* Bottom Nav */}
      <View style={styles.bottomBar}>
        {[
          { key: "Home", label: "Home", icon: "home" },
          { key: "Explore", label: "Explore", icon: "compass" },
          { key: "Create", label: "Create", icon: "plus" },
          { key: "Notification", label: "Notifications", icon: "bell" },
          { key: "Profile", label: "Profile", icon: "user" },
        ].map((item) => {
          const isCreate = item.key === "Create";
          const isActive = bottomTab === item.key;

          if (isCreate) {
            return (
              <TouchableOpacity
                key={item.key}
                style={styles.createBtn}
                onPress={() => setBottomTab(item.key)}
              >
                <Feather name="plus" size={24} color="#ffffff" />
              </TouchableOpacity>
            );
          }

          return (
            <TouchableOpacity
              key={item.key}
              style={styles.bottomTabItem}
              onPress={() => setBottomTab(item.key)}
            >
              <Feather
                name={item.icon as any}
                size={20}
                color={isActive ? "#0d9488" : "#94a3b8"}
              />
              <Text style={[styles.bottomTabLabel, isActive && styles.activeBottomTabLabel]}>
                {item.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: "#F8FAFC" },
  topHeader: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 10,
    backgroundColor: "#ffffff",
    borderBottomWidth: 1,
    borderBottomColor: "#f1f5f9",
  },
  iconCircle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: "#f0fdfa",
    alignItems: "center",
    justifyContent: "center",
  },
  brandRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  brandLogo: {
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: "#0d9488",
    alignItems: "center",
    justifyContent: "center",
  },
  brandLogoText: { color: "#fff", fontWeight: "bold", fontSize: 14 },
  brandTitle: { fontSize: 17, fontWeight: "800", color: "#0f766e" },
  tabsContainer: {
    flexDirection: "row",
    backgroundColor: "#ffffff",
    paddingVertical: 6,
    borderBottomWidth: 1,
    borderBottomColor: "#e2e8f0",
    justifyContent: "space-around",
  },
  tabItem: { alignItems: "center", paddingVertical: 4, paddingHorizontal: 8 },
  activeTabItem: { borderBottomWidth: 2, borderBottomColor: "#0d9488" },
  tabLabel: { fontSize: 10, fontWeight: "600", color: "#64748b", marginTop: 2 },
  activeTabLabel: { color: "#0d9488", fontWeight: "700" },
  feedContent: { paddingBottom: 70 },
  storySection: { paddingVertical: 12 },
  storyCard: {
    width: 90,
    height: 130,
    borderRadius: 14,
    overflow: "hidden",
    marginRight: 10,
  },
  storyImage: { width: "100%", height: "100%", resizeMode: "cover" },
  storyOverlay: { ...StyleSheet.absoluteFill, backgroundColor: "rgba(0,0,0,0.25)" },
  storyName: { position: "absolute", bottom: 6, left: 8, color: "#fff", fontSize: 11, fontWeight: "700" },
  postCard: {
    backgroundColor: "#ffffff",
    borderRadius: 16,
    marginHorizontal: 12,
    marginBottom: 12,
    padding: 12,
    borderWidth: 1,
    borderColor: "#f1f5f9",
    ...Platform.select({
      web: { boxShadow: "0 2px 4px 0 rgba(0,0,0,0.03)" },
      default: { elevation: 1 },
    }),
  },
  postHeader: { flexDirection: "row", alignItems: "center", marginBottom: 10 },
  authorAvatar: { width: 36, height: 36, borderRadius: 18 },
  postAuthorMeta: { flex: 1, marginLeft: 10 },
  postAuthorName: { fontSize: 13, fontWeight: "700", color: "#1e293b" },
  postTime: { fontSize: 10, color: "#94a3b8" },
  postBodyText: { fontSize: 13, color: "#334155", marginBottom: 10, lineHeight: 18 },
  postImage: { width: "100%", height: 200, borderRadius: 12, marginBottom: 10 },
  bottomBar: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    height: 60,
    backgroundColor: "#ffffff",
    borderTopWidth: 1,
    borderTopColor: "#e2e8f0",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-around",
    paddingBottom: Platform.OS === "ios" ? 10 : 0,
  },
  bottomTabItem: { alignItems: "center", justifyContent: "center" },
  bottomTabLabel: { fontSize: 10, color: "#94a3b8", fontWeight: "500", marginTop: 2 },
  activeBottomTabLabel: { color: "#0d9488", fontWeight: "700" },
  createBtn: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: "#0d9488",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 14,
    elevation: 4,
  },
});
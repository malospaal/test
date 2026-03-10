import React from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, radius, spacing, type } from "../theme/tokens";

type Props = {
  habitTitle: string;
  planLabel: string;
  onPressSettings?: () => void;
};

export function HomeHeader({ habitTitle, planLabel, onPressSettings }: Props) {
  return (
    <View style={styles.header}>
      <View style={styles.left}>
        <View style={styles.topRow}>
          <Text style={styles.kicker}>Today</Text>
          <View style={styles.planPill}>
            <Text style={styles.planText}>{planLabel}</Text>
          </View>
        </View>
        <Text style={styles.title} numberOfLines={1}>
          {habitTitle}
        </Text>
      </View>

      <Pressable
        onPress={onPressSettings}
        hitSlop={8}
        style={({ pressed }) => [styles.settingsButton, pressed && styles.settingsButtonPressed]}
        accessibilityRole="button"
        accessibilityLabel="Open settings"
      >
        <Ionicons name="settings-outline" size={20} color={colors.textPrimary} />
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    gap: spacing.md,
  },
  left: {
    flex: 1,
    gap: spacing.xs,
  },
  topRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: spacing.xs,
  },
  kicker: {
    fontSize: type.caption,
    letterSpacing: 0.3,
    textTransform: "uppercase",
    color: colors.textSecondary,
    fontWeight: "700",
  },
  planPill: {
    height: 24,
    borderRadius: radius.full,
    backgroundColor: colors.primarySoft,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: spacing.xs,
    alignItems: "center",
    justifyContent: "center",
  },
  planText: {
    fontSize: type.caption,
    color: colors.primary,
    fontWeight: "700",
  },
  title: {
    fontSize: type.h2,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  settingsButton: {
    width: 40,
    height: 40,
    borderRadius: radius.full,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: "center",
    justifyContent: "center",
  },
  settingsButtonPressed: {
    opacity: 0.8,
  },
});

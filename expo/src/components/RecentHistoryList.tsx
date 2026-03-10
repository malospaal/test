import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { HistoryItem } from "../types/habit";
import { colors, radius, spacing, type } from "../theme/tokens";
import { SectionCard } from "./SectionCard";

type Props = {
  items: HistoryItem[];
};

export function RecentHistoryList({ items }: Props) {
  return (
    <SectionCard>
      <Text style={styles.title}>Recent history</Text>
      <View style={styles.list}>
        {items.map((item) => (
          <View key={item.key} style={styles.row}>
            <Text style={styles.day}>{item.label}</Text>
            <Text style={[styles.status, item.done ? styles.done : styles.missed]}>
              {item.done ? "Done" : "Missed"}
            </Text>
          </View>
        ))}
      </View>
    </SectionCard>
  );
}

const styles = StyleSheet.create({
  title: {
    fontSize: type.h3,
    fontWeight: "700",
    color: colors.textPrimary,
    marginBottom: spacing.xs,
  },
  list: {
    gap: spacing.xs,
  },
  row: {
    height: 40,
    borderRadius: radius.sm,
    backgroundColor: colors.surfaceSoft,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: spacing.md,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  day: {
    color: colors.textPrimary,
    fontSize: type.body,
    fontWeight: "600",
  },
  status: {
    fontSize: type.caption,
    fontWeight: "700",
  },
  done: {
    color: colors.success,
  },
  missed: {
    color: colors.textSecondary,
  },
});

import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { colors, radius, spacing, type } from "../theme/tokens";

type Props = {
  label: string;
  value: string;
};

export function StatTile({ label, value }: Props) {
  return (
    <View style={styles.tile}>
      <Text style={styles.value}>{value}</Text>
      <Text style={styles.label}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  tile: {
    flex: 1,
    minHeight: 96,
    borderRadius: radius.md,
    backgroundColor: colors.surfaceSoft,
    borderWidth: 1,
    borderColor: colors.border,
    padding: spacing.md,
    justifyContent: "center",
    gap: spacing.xs,
  },
  value: {
    fontSize: type.h3,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  label: {
    fontSize: type.caption,
    color: colors.textSecondary,
    lineHeight: 18,
  },
});

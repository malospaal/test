import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { DayPoint } from "../types/habit";
import { colors, radius, spacing, type } from "../theme/tokens";
import { SectionCard } from "./SectionCard";

type Props = {
  points: DayPoint[];
};

export function ProgressChart7d({ points }: Props) {
  return (
    <SectionCard>
      <Text style={styles.title}>7 day progress</Text>
      <View style={styles.row}>
        {points.map((point) => (
          <View key={point.key} style={[styles.col, point.isToday && styles.colToday]}>
            <View style={styles.track}>
              <View
                style={[
                  styles.bar,
                  point.done ? styles.barDone : styles.barMissed,
                  point.isToday && styles.barToday,
                ]}
              />
            </View>
            <Text style={[styles.label, point.isToday && styles.labelToday]}>{point.label}</Text>
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
    marginBottom: spacing.md,
  },
  row: {
    flexDirection: "row",
    gap: spacing.xs,
  },
  col: {
    flex: 1,
    alignItems: "center",
    paddingVertical: spacing.xs,
    borderRadius: radius.sm,
  },
  colToday: {
    backgroundColor: colors.primarySoft,
  },
  track: {
    width: "100%",
    height: 56,
    borderRadius: radius.sm,
    backgroundColor: colors.surfaceSoft,
    justifyContent: "flex-end",
    overflow: "hidden",
  },
  bar: {
    width: "100%",
  },
  barDone: {
    height: 40,
    backgroundColor: colors.primary,
  },
  barMissed: {
    height: 16,
    backgroundColor: colors.border,
  },
  barToday: {
    borderTopWidth: 1,
    borderTopColor: "rgba(255,255,255,0.32)",
  },
  label: {
    marginTop: spacing.xs,
    fontSize: type.caption,
    color: colors.textSecondary,
    fontWeight: "600",
  },
  labelToday: {
    color: colors.primary,
    fontWeight: "700",
  },
});

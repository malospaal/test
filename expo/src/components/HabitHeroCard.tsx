import React, { useEffect, useRef } from "react";
import { Animated, Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, radius, spacing, type } from "../theme/tokens";
import { SectionCard } from "./SectionCard";

type Props = {
  habitTitle: string;
  dateLabel: string;
  isDoneToday: boolean;
  onPressDone: () => void;
};

export function HabitHeroCard({
  habitTitle,
  dateLabel,
  isDoneToday,
  onPressDone,
}: Props) {
  const scale = useRef(new Animated.Value(1)).current;
  const doneOpacity = useRef(new Animated.Value(isDoneToday ? 1 : 0)).current;

  useEffect(() => {
    if (isDoneToday) {
      Animated.parallel([
        Animated.sequence([
          Animated.timing(scale, {
            toValue: 1.02,
            duration: 140,
            useNativeDriver: true,
          }),
          Animated.spring(scale, {
            toValue: 1,
            speed: 14,
            bounciness: 6,
            useNativeDriver: true,
          }),
        ]),
        Animated.timing(doneOpacity, {
          toValue: 1,
          duration: 220,
          useNativeDriver: true,
        }),
      ]).start();
      return;
    }

    Animated.timing(doneOpacity, {
      toValue: 0,
      duration: 140,
      useNativeDriver: true,
    }).start();
  }, [doneOpacity, isDoneToday, scale]);

  return (
    <SectionCard style={styles.card}>
      <Text style={styles.habitTitle}>{habitTitle}</Text>
      <Text style={styles.date}>{dateLabel}</Text>
      <Text style={styles.question}>Did you complete it today?</Text>

      <Animated.View style={[styles.ctaWrap, { transform: [{ scale }] }]}>
        <Pressable
          onPress={onPressDone}
          disabled={isDoneToday}
          style={({ pressed }) => [
            styles.cta,
            isDoneToday && styles.ctaDone,
            pressed && !isDoneToday && styles.ctaPressed,
          ]}
          accessibilityRole="button"
          accessibilityLabel={isDoneToday ? "Habit completed for today" : "Mark habit done today"}
        >
          <Text style={styles.ctaText}>{isDoneToday ? "Completed Today" : "Mark as Done"}</Text>
        </Pressable>
      </Animated.View>

      <Animated.View style={[styles.doneState, { opacity: doneOpacity }]}>
        <Ionicons name="checkmark-circle" size={18} color={colors.success} />
        <Text style={styles.doneStateText}>Great consistency. Keep your streak alive.</Text>
      </Animated.View>
    </SectionCard>
  );
}

const styles = StyleSheet.create({
  card: {
    gap: spacing.xs,
  },
  habitTitle: {
    fontSize: type.h2,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  date: {
    fontSize: type.caption,
    color: colors.textSecondary,
  },
  question: {
    marginTop: spacing.xs,
    fontSize: type.h3,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  ctaWrap: {
    marginTop: spacing.xs,
  },
  cta: {
    height: 56,
    borderRadius: radius.md,
    backgroundColor: colors.primary,
    alignItems: "center",
    justifyContent: "center",
  },
  ctaDone: {
    backgroundColor: colors.success,
  },
  ctaPressed: {
    opacity: 0.86,
  },
  ctaText: {
    color: "#FFFFFF",
    fontSize: type.button,
    fontWeight: "700",
  },
  doneState: {
    minHeight: 24,
    marginTop: spacing.xs,
    flexDirection: "row",
    alignItems: "center",
    gap: spacing.xs,
  },
  doneStateText: {
    flex: 1,
    color: colors.textSecondary,
    fontSize: type.caption,
    fontWeight: "600",
  },
});

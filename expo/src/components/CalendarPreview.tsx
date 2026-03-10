import React from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { DayCompletionMap } from "../types/habit";
import { buildMonthGrid, monthYear, toISODate } from "../utils/date";
import { colors, radius, spacing, type } from "../theme/tokens";
import { SectionCard } from "./SectionCard";

type Props = {
  selectedDate: Date;
  completionByDate: DayCompletionMap;
  onSelectDate: (date: Date) => void;
  onJumpToday: () => void;
};

const WEEK_LABELS = ["M", "T", "W", "T", "F", "S", "S"];

export function CalendarPreview({
  selectedDate,
  completionByDate,
  onSelectDate,
  onJumpToday,
}: Props) {
  const monthRows = buildMonthGrid(selectedDate);
  const selectedKey = toISODate(selectedDate);
  const todayKey = toISODate(new Date());

  return (
    <SectionCard>
      <View style={styles.top}>
        <View>
          <Text style={styles.title}>Calendar</Text>
          <Text style={styles.month}>{monthYear(selectedDate)}</Text>
        </View>
        <Pressable
          onPress={onJumpToday}
          style={({ pressed }) => [styles.todayButton, pressed && styles.todayButtonPressed]}
        >
          <Text style={styles.todayText}>Today</Text>
        </Pressable>
      </View>

      <View style={styles.weekRow}>
        {WEEK_LABELS.map((label, index) => (
          <Text key={`${label}-${index}`} style={styles.weekCell}>
            {label}
          </Text>
        ))}
      </View>

      <View style={styles.grid}>
        {monthRows.map((row, rowIndex) => (
          <View key={`row-${rowIndex}`} style={styles.dayRow}>
            {row.map((date, colIndex) => {
              if (!date) {
                return <View key={`empty-${rowIndex}-${colIndex}`} style={styles.dayCell} />;
              }

              const key = toISODate(date);
              const isSelected = key === selectedKey;
              const isToday = key === todayKey;
              const isDone = !!completionByDate[key];

              return (
                <Pressable
                  key={key}
                  onPress={() => onSelectDate(date)}
                  style={[
                    styles.dayCell,
                    isToday && styles.dayCellToday,
                    isSelected && styles.dayCellSelected,
                  ]}
                >
                  <Text
                    style={[
                      styles.dayText,
                      isSelected && styles.dayTextSelected,
                      isToday && !isSelected && styles.dayTextToday,
                    ]}
                  >
                    {date.getDate()}
                  </Text>
                  <View
                    style={[
                      styles.dot,
                      isDone && styles.dotDone,
                      isDone && isSelected && styles.dotDoneSelected,
                    ]}
                  />
                </Pressable>
              );
            })}
          </View>
        ))}
      </View>
    </SectionCard>
  );
}

const styles = StyleSheet.create({
  top: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: spacing.xs,
  },
  title: {
    fontSize: type.h3,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  month: {
    marginTop: spacing.xs,
    fontSize: type.caption,
    color: colors.textSecondary,
    fontWeight: "600",
  },
  todayButton: {
    height: 32,
    borderRadius: radius.full,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceSoft,
    paddingHorizontal: spacing.md,
    alignItems: "center",
    justifyContent: "center",
  },
  todayButtonPressed: {
    opacity: 0.85,
  },
  todayText: {
    color: colors.textPrimary,
    fontSize: type.caption,
    fontWeight: "700",
  },
  weekRow: {
    flexDirection: "row",
    marginBottom: spacing.xs,
  },
  weekCell: {
    flex: 1,
    textAlign: "center",
    fontSize: type.caption,
    color: colors.textSecondary,
    fontWeight: "700",
  },
  grid: {
    gap: spacing.xs,
  },
  dayRow: {
    flexDirection: "row",
    gap: spacing.xs,
  },
  dayCell: {
    flex: 1,
    aspectRatio: 1,
    minHeight: 40,
    borderRadius: radius.sm,
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: "transparent",
  },
  dayCellToday: {
    borderColor: colors.primary,
  },
  dayCellSelected: {
    backgroundColor: colors.primary,
    borderColor: colors.primary,
  },
  dayText: {
    fontSize: type.caption,
    color: colors.textPrimary,
    fontWeight: "600",
  },
  dayTextToday: {
    color: colors.primary,
    fontWeight: "700",
  },
  dayTextSelected: {
    color: "#FFFFFF",
    fontWeight: "700",
  },
  dot: {
    marginTop: spacing.xs,
    width: 4,
    height: 4,
    borderRadius: radius.full,
    backgroundColor: "transparent",
  },
  dotDone: {
    backgroundColor: colors.success,
  },
  dotDoneSelected: {
    backgroundColor: "#FFFFFF",
  },
});

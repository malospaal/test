import React, { useMemo, useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet } from "react-native";
import { colors, spacing } from "../theme/tokens";
import { DayCompletionMap, DayPoint, Habit, HistoryItem } from "../types/habit";
import {
  buildLastNDays,
  calculate30DayCompletionRate,
  calculateStreak,
  countTotalCompletions,
  formatLongDate,
  toISODate,
  weekdayShort,
} from "../utils/date";
import { CalendarPreview } from "../components/CalendarPreview";
import { HabitHeroCard } from "../components/HabitHeroCard";
import { HomeHeader } from "../components/HomeHeader";
import { ProgressChart7d } from "../components/ProgressChart7d";
import { RecentHistoryList } from "../components/RecentHistoryList";
import { StatsRow } from "../components/StatsRow";

const ACTIVE_HABIT: Habit = {
  id: "habit-1",
  title: "Morning Meditation",
};

export default function HomeScreen() {
  const [selectedDate, setSelectedDate] = useState(() => {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    return date;
  });
  const [completionByDate, setCompletionByDate] = useState<DayCompletionMap>({});

  const today = useMemo(() => {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    return date;
  }, []);

  const todayKey = toISODate(today);
  const isDoneToday = !!completionByDate[todayKey];

  const streak = useMemo(() => calculateStreak(completionByDate), [completionByDate]);
  const completion30Day = useMemo(
    () => calculate30DayCompletionRate(completionByDate),
    [completionByDate]
  );
  const totalCompletions = useMemo(
    () => countTotalCompletions(completionByDate),
    [completionByDate]
  );

  const chart7Days: DayPoint[] = useMemo(
    () =>
      buildLastNDays(7).map((date) => {
        const key = toISODate(date);
        return {
          key,
          label: weekdayShort(date),
          done: !!completionByDate[key],
          isToday: key === todayKey,
        };
      }),
    [completionByDate, todayKey]
  );

  const recentHistory: HistoryItem[] = useMemo(
    () =>
      buildLastNDays(5)
        .reverse()
        .map((date) => {
          const key = toISODate(date);
          return {
            key,
            label: formatLongDate(date),
            done: !!completionByDate[key],
          };
        }),
    [completionByDate]
  );

  const onMarkDoneToday = () => {
    setCompletionByDate((prev) => ({ ...prev, [todayKey]: true }));
  };

  const onJumpToday = () => setSelectedDate(today);

  return (
    <SafeAreaView style={styles.safe}>
      <ScrollView
        style={styles.screen}
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <HomeHeader habitTitle={ACTIVE_HABIT.title} planLabel="PRO" onPressSettings={() => {}} />

        <HabitHeroCard
          habitTitle={ACTIVE_HABIT.title}
          dateLabel={formatLongDate(today)}
          isDoneToday={isDoneToday}
          onPressDone={onMarkDoneToday}
        />

        <StatsRow
          streak={streak}
          completion30Day={completion30Day}
          totalCompletions={totalCompletions}
        />

        <ProgressChart7d points={chart7Days} />
        <RecentHistoryList items={recentHistory} />

        <CalendarPreview
          selectedDate={selectedDate}
          completionByDate={completionByDate}
          onSelectDate={setSelectedDate}
          onJumpToday={onJumpToday}
        />
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: colors.bg,
  },
  screen: {
    flex: 1,
  },
  content: {
    paddingHorizontal: spacing.md,
    paddingTop: spacing.md,
    paddingBottom: spacing.xxl,
    gap: spacing.md,
  },
});

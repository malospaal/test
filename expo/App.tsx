import "react-native-gesture-handler";
import React, { useState } from "react";
import { SafeAreaView, View, Pressable, Text, StyleSheet } from "react-native";
import HomeScreen from "./src/screens/HomeScreen";
import HabitsScreen, { HabitItem } from "./src/screens/HabitsScreen";
import CreateHabitScreen from "./src/features/create-habit/CreateHabitScreen";
import { CreateHabitPayload } from "./src/features/create-habit/types";

type ScreenKey = "home" | "habits" | "create";

const navTheme = {
  bg: "#F7F9F8",
  text: "#131B18",
  textMuted: "#5C6A64",
  primary: "#1E6D5F",
  primarySoft: "#E8F3F0",
  border: "#E1E7E4",
};

const INITIAL_HABITS: HabitItem[] = [
  {
    id: "1",
    emoji: "🧘",
    name: "Meditation",
    frequency: "Every day",
    streak: 6,
    completionRate: 80,
    isActive: true,
    order: 0,
  },
  {
    id: "2",
    emoji: "💧",
    name: "Water intake",
    frequency: "5x/week",
    streak: 3,
    completionRate: 62,
    isActive: true,
    order: 1,
  },
];

function frequencyLabel(payload: CreateHabitPayload): string {
  if (payload.frequencyType === "daily") return "Every day";
  if (payload.frequencyType === "times_per_week") {
    return `${payload.timesPerWeek ?? 0}x/week`;
  }
  const d = payload.daysOfWeek ?? [];
  const labels = d.map((n) => ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][n - 1]);
  return labels.join(", ") || "Custom";
}

export default function App() {
  const [screen, setScreen] = useState<ScreenKey>("home");
  const [habits, setHabits] = useState<HabitItem[]>(INITIAL_HABITS);

  const onSaveHabit = async (payload: CreateHabitPayload) => {
    const newHabit: HabitItem = {
      id: `habit_${Date.now()}`,
      emoji: payload.emoji,
      name: payload.name,
      frequency: frequencyLabel(payload),
      streak: 0,
      completionRate: 0,
      isActive: true,
      order: habits.length,
    };

    setHabits((prev) => [newHabit, ...prev]);
    setScreen("habits");
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.topNav}>
        <NavItem label="Home" active={screen === "home"} onPress={() => setScreen("home")} />
        <NavItem
          label="Habits"
          active={screen === "habits"}
          onPress={() => setScreen("habits")}
        />
        <NavItem
          label="Create"
          active={screen === "create"}
          onPress={() => setScreen("create")}
        />
      </View>

      <View style={styles.content}>
        {screen === "home" && <HomeScreen />}

        {screen === "habits" && (
          <HabitsScreen
            habits={habits}
            onAddHabit={() => setScreen("create")}
            onEditHabit={() => {}}
            onArchiveHabit={(habit) =>
              setHabits((prev) =>
                prev.map((h) => (h.id === habit.id ? { ...h, isActive: false } : h))
              )
            }
            onDeleteHabit={(habit) => setHabits((prev) => prev.filter((h) => h.id !== habit.id))}
            onReorderHabits={(reordered) => setHabits(reordered)}
          />
        )}

        {screen === "create" && (
          <CreateHabitScreen onCancel={() => setScreen("habits")} onSave={onSaveHabit} />
        )}
      </View>
    </SafeAreaView>
  );
}

function NavItem({
  label,
  active,
  onPress,
}: {
  label: string;
  active: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} style={[styles.navItem, active && styles.navItemActive]}>
      <Text style={[styles.navText, active && styles.navTextActive]}>{label}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: navTheme.bg },
  topNav: {
    flexDirection: "row",
    gap: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    backgroundColor: navTheme.bg,
    borderBottomWidth: 1,
    borderBottomColor: navTheme.border,
  },
  navItem: {
    height: 36,
    paddingHorizontal: 14,
    borderRadius: 999,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "transparent",
  },
  navItemActive: {
    backgroundColor: navTheme.primarySoft,
  },
  navText: {
    color: navTheme.textMuted,
    fontWeight: "600",
  },
  navTextActive: {
    color: navTheme.primary,
  },
  content: { flex: 1 },
});

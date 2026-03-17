import React, { useEffect, useMemo, useRef, useState } from "react";
import {
  Animated,
  FlatList,
  LayoutAnimation,
  PanResponder,
  Platform,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  UIManager,
  useColorScheme,
  View,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { GestureHandlerRootView, Swipeable } from "react-native-gesture-handler";

export type HabitItem = {
  id: string;
  emoji?: string;
  name: string;
  frequency: string;
  streak?: number;
  completionRate?: number;
  isActive?: boolean;
  reminderTime?: string;
  reminderLabel?: string;
  reminderEnabled?: boolean;
  order?: number;
};

type HabitsScreenProps = {
  habits: HabitItem[];
  onAddHabit: () => void;
  onOpenHabit?: (habit: HabitItem) => void;
  onEditHabit: (habit: HabitItem) => void;
  onArchiveHabit: (habit: HabitItem) => void;
  onDeleteHabit: (habit: HabitItem) => void;
  onReorderHabits?: (habits: HabitItem[]) => void;
};

type ScreenTheme = ReturnType<typeof buildTheme>;
type ScreenStyles = ReturnType<typeof createStyles>;

export default function HabitsScreen({
  habits,
  onAddHabit,
  onEditHabit,
  onDeleteHabit,
  onReorderHabits,
}: HabitsScreenProps) {
  const colorScheme = useColorScheme();
  const theme = useMemo(() => buildTheme(colorScheme === "dark"), [colorScheme]);
  const styles = useMemo(() => createStyles(theme), [theme]);

  const [orderedHabits, setOrderedHabits] = useState<HabitItem[]>(() => sortHabits(habits));

  useEffect(() => {
    setOrderedHabits(sortHabits(habits));
  }, [habits]);

  useEffect(() => {
    if (Platform.OS === "android" && UIManager.setLayoutAnimationEnabledExperimental) {
      UIManager.setLayoutAnimationEnabledExperimental(true);
    }
  }, []);

  const moveHabit = (fromIndex: number, toIndex: number) => {
    setOrderedHabits((prev) => {
      if (
        fromIndex < 0 ||
        toIndex < 0 ||
        fromIndex >= prev.length ||
        toIndex >= prev.length ||
        fromIndex === toIndex
      ) {
        return prev;
      }

      const next = [...prev];
      const [moved] = next.splice(fromIndex, 1);
      next.splice(toIndex, 0, moved);
      LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);

      const persisted = next.map((habit, idx) => ({ ...habit, order: idx }));
      onReorderHabits?.(persisted);
      return persisted;
    });
  };

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaView style={styles.safe}>
        <View style={styles.container}>
          <Header styles={styles} />

          <FlatList
            data={orderedHabits}
            keyExtractor={(item) => item.id}
            showsVerticalScrollIndicator={false}
            contentContainerStyle={[
              styles.listContent,
              orderedHabits.length === 0 && styles.emptyListContent,
            ]}
            renderItem={({ item, index }) => (
              <HabitRow
                habit={item}
                index={index}
                total={orderedHabits.length}
                theme={theme}
                styles={styles}
                onMove={moveHabit}
                onEdit={() => onEditHabit(item)}
                onDelete={() => onDeleteHabit(item)}
              />
            )}
            ListEmptyComponent={
              <EmptyHabitsState onAddHabit={onAddHabit} styles={styles} theme={theme} />
            }
          />
        </View>

        <Pressable style={styles.fab} onPress={onAddHabit}>
          <Ionicons name="add" size={24} color="#FFFFFF" />
        </Pressable>
      </SafeAreaView>
    </GestureHandlerRootView>
  );
}

function Header({ styles }: { styles: ScreenStyles }) {
  return (
    <View style={styles.header}>
      <Text style={styles.title}>Habits</Text>
    </View>
  );
}

function HabitRow({
  habit,
  index,
  total,
  onMove,
  theme,
  styles,
  onEdit,
  onDelete,
}: {
  habit: HabitItem;
  index: number;
  total: number;
  onMove: (fromIndex: number, toIndex: number) => void;
  theme: ScreenTheme;
  styles: ScreenStyles;
  onEdit: () => void;
  onDelete: () => void;
}) {
  const scaleAnim = useRef(new Animated.Value(1)).current;
  const shadowAnim = useRef(new Animated.Value(0)).current;
  const accumulatedDyRef = useRef(0);
  const lastMoveYRef = useRef<number | null>(null);

  const reminderText =
    habit.reminderEnabled === false ? null : habit.reminderTime ?? habit.reminderLabel;
  const secondaryLine = reminderText ? `${habit.frequency} • ${reminderText}` : habit.frequency;

  const resetDragState = () => {
    Animated.parallel([
      Animated.spring(scaleAnim, {
        toValue: 1,
        speed: 20,
        bounciness: 4,
        useNativeDriver: true,
      }),
      Animated.timing(shadowAnim, {
        toValue: 0,
        duration: 120,
        useNativeDriver: false,
      }),
    ]).start();
    accumulatedDyRef.current = 0;
    lastMoveYRef.current = null;
  };

  const panResponder = useMemo(
    () =>
      PanResponder.create({
        onStartShouldSetPanResponder: () => true,
        onMoveShouldSetPanResponder: (_, gesture) => Math.abs(gesture.dy) > 3,
        onPanResponderGrant: () => {
          accumulatedDyRef.current = 0;
          lastMoveYRef.current = null;
          Animated.parallel([
            Animated.timing(scaleAnim, {
              toValue: 1.02,
              duration: 120,
              useNativeDriver: true,
            }),
            Animated.timing(shadowAnim, {
              toValue: 1,
              duration: 120,
              useNativeDriver: false,
            }),
          ]).start();
        },
        onPanResponderMove: (_, gestureState) => {
          if (lastMoveYRef.current == null) {
            lastMoveYRef.current = gestureState.moveY;
            return;
          }

          const delta = gestureState.moveY - lastMoveYRef.current;
          lastMoveYRef.current = gestureState.moveY;
          accumulatedDyRef.current += delta;

          if (accumulatedDyRef.current > 40 && index < total - 1) {
            onMove(index, index + 1);
            accumulatedDyRef.current = 0;
          } else if (accumulatedDyRef.current < -40 && index > 0) {
            onMove(index, index - 1);
            accumulatedDyRef.current = 0;
          }
        },
        onPanResponderRelease: resetDragState,
        onPanResponderTerminate: resetDragState,
      }),
    [index, onMove, scaleAnim, shadowAnim, total]
  );

  const animatedCardStyle = {
    transform: [{ scale: scaleAnim }],
    shadowOpacity: shadowAnim.interpolate({
      inputRange: [0, 1],
      outputRange: [0.04, 0.14],
    }),
    elevation: shadowAnim.interpolate({
      inputRange: [0, 1],
      outputRange: [1, 5],
    }),
  };

  return (
    <Swipeable
      overshootRight={false}
      renderRightActions={() => (
        <View style={styles.swipeActions}>
          <SwipeAction label="Edit" color={theme.colors.primary} onPress={onEdit} styles={styles} />
          <SwipeAction
            label="Delete"
            color={theme.colors.danger}
            onPress={onDelete}
            styles={styles}
          />
        </View>
      )}
    >
      <Animated.View style={[styles.card, animatedCardStyle]}>
        <View style={styles.rowPressable}>
          <View {...panResponder.panHandlers} style={styles.dragHandle}>
            <Ionicons name="reorder-three-outline" size={18} color={theme.colors.textSecondary} />
          </View>
          <View style={styles.emojiWrap}>
            <Text style={styles.emoji}>{habit.emoji || "•"}</Text>
          </View>
          <View style={styles.content}>
            <Text style={styles.habitName} numberOfLines={1}>
              {habit.name}
            </Text>
            <Text style={styles.secondaryLine} numberOfLines={1}>
              {secondaryLine}
            </Text>
          </View>
        </View>
      </Animated.View>
    </Swipeable>
  );
}

function SwipeAction({
  label,
  color,
  onPress,
  styles,
}: {
  label: string;
  color: string;
  onPress: () => void;
  styles: ScreenStyles;
}) {
  return (
    <Pressable style={[styles.swipeActionBtn, { backgroundColor: color }]} onPress={onPress}>
      <Text style={styles.swipeActionText}>{label}</Text>
    </Pressable>
  );
}

function EmptyHabitsState({
  onAddHabit,
  styles,
  theme,
}: {
  onAddHabit: () => void;
  styles: ScreenStyles;
  theme: ScreenTheme;
}) {
  return (
    <View style={styles.emptyWrap}>
      <View style={styles.emptyIcon}>
        <Ionicons name="leaf-outline" size={28} color={theme.colors.primary} />
      </View>
      <Text style={styles.emptyTitle}>No habits yet</Text>
      <Text style={styles.emptyText}>
        Create your first micro habit and build momentum with small daily wins.
      </Text>
      <Pressable style={styles.emptyCta} onPress={onAddHabit}>
        <Text style={styles.emptyCtaText}>Create habit</Text>
      </Pressable>
    </View>
  );
}

function sortHabits(items: HabitItem[]): HabitItem[] {
  if (items.some((item) => typeof item.order === "number")) {
    return [...items].sort((a, b) => (a.order ?? Number.MAX_SAFE_INTEGER) - (b.order ?? Number.MAX_SAFE_INTEGER));
  }
  return [...items];
}

function buildTheme(isDark: boolean) {
  return {
    isDark,
    colors: isDark
      ? {
          bg: "#0F1513",
          surface: "#161F1C",
          textPrimary: "#E7EFEC",
          textSecondary: "#A9B6B1",
          border: "#293430",
          primary: "#62B88E",
          primarySoft: "#21463A",
          warning: "#CFA24B",
          danger: "#E58D8D",
        }
      : {
          bg: "#F7F9F8",
          surface: "#FFFFFF",
          textPrimary: "#131B18",
          textSecondary: "#5C6A64",
          border: "#E1E7E4",
          primary: "#1E6D5F",
          primarySoft: "#E8F3F0",
          warning: "#A06B1D",
          danger: "#B54747",
        },
    spacing: { xs: 8, sm: 12, md: 16, lg: 24, xl: 32 },
    radius: { md: 14, lg: 16, xl: 22, pill: 999 },
  };
}

function createStyles(theme: ScreenTheme) {
  return StyleSheet.create({
    safe: { flex: 1, backgroundColor: theme.colors.bg },
    container: { flex: 1, paddingHorizontal: theme.spacing.md },
    header: {
      height: 58,
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "space-between",
    },
    title: { fontSize: 28, fontWeight: "700", color: theme.colors.textPrimary },

    listContent: { paddingBottom: 130, gap: theme.spacing.xs },
    emptyListContent: { flexGrow: 1, justifyContent: "center" },

    card: {
      backgroundColor: theme.colors.surface,
      borderRadius: theme.radius.lg,
      paddingHorizontal: theme.spacing.sm,
      paddingVertical: 10,
      borderWidth: 1,
      borderColor: theme.colors.border,
      marginBottom: theme.spacing.xs,
      shadowColor: "#0D1B14",
      shadowOpacity: 0.04,
      shadowRadius: 6,
      shadowOffset: { width: 0, height: 2 },
      elevation: 1,
    },
    rowPressable: { flexDirection: "row", alignItems: "center", gap: theme.spacing.sm },
    dragHandle: {
      width: 28,
      height: 28,
      borderRadius: 8,
      alignItems: "center",
      justifyContent: "center",
    },
    emojiWrap: {
      width: 34,
      height: 34,
      borderRadius: 10,
      backgroundColor: theme.colors.primarySoft,
      alignItems: "center",
      justifyContent: "center",
    },
    emoji: { fontSize: 18 },
    content: { flex: 1 },
    habitName: { fontSize: 16, fontWeight: "700", color: theme.colors.textPrimary },
    secondaryLine: { marginTop: 2, fontSize: 13, color: theme.colors.textSecondary },

    swipeActions: { flexDirection: "row", marginBottom: theme.spacing.xs },
    swipeActionBtn: {
      width: 78,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: theme.radius.md,
      marginLeft: theme.spacing.xs,
    },
    swipeActionText: { color: "#FFFFFF", fontSize: 12, fontWeight: "700" },

    fab: {
      position: "absolute",
      right: 20,
      bottom: 92,
      width: 56,
      height: 56,
      borderRadius: 28,
      backgroundColor: theme.colors.primary,
      alignItems: "center",
      justifyContent: "center",
      shadowColor: "#000000",
      shadowOpacity: theme.isDark ? 0.32 : 0.2,
      shadowRadius: 10,
      shadowOffset: { width: 0, height: 4 },
      elevation: 6,
    },

    emptyWrap: { alignItems: "center", paddingHorizontal: theme.spacing.lg },
    emptyIcon: {
      width: 68,
      height: 68,
      borderRadius: theme.radius.xl,
      backgroundColor: theme.colors.primarySoft,
      alignItems: "center",
      justifyContent: "center",
    },
    emptyTitle: {
      marginTop: theme.spacing.md,
      fontSize: 20,
      fontWeight: "700",
      color: theme.colors.textPrimary,
    },
    emptyText: {
      marginTop: theme.spacing.xs,
      textAlign: "center",
      color: theme.colors.textSecondary,
      fontSize: 14,
      lineHeight: 20,
    },
    emptyCta: {
      marginTop: theme.spacing.md,
      backgroundColor: theme.colors.primary,
      borderRadius: theme.radius.pill,
      paddingHorizontal: theme.spacing.lg,
      paddingVertical: 10,
    },
    emptyCtaText: { color: "#FFFFFF", fontWeight: "700", fontSize: 14 },
  });
}

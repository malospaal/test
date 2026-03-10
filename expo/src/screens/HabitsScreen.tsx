import React, { useMemo, useState } from "react";
import {
  FlatList,
  Modal,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { GestureHandlerRootView, Swipeable } from "react-native-gesture-handler";

export type HabitItem = {
  id: string;
  emoji?: string;
  name: string;
  frequency: string;
  streak: number;
  completionRate: number;
  isActive: boolean;
};

type HabitsScreenProps = {
  habits: HabitItem[];
  onAddHabit: () => void;
  onOpenHabit?: (habit: HabitItem) => void;
  onEditHabit: (habit: HabitItem) => void;
  onArchiveHabit: (habit: HabitItem) => void;
  onDeleteHabit: (habit: HabitItem) => void;
};

const theme = {
  colors: {
    bg: "#F7F9F8",
    surface: "#FFFFFF",
    surfaceMuted: "#F1F4F3",
    textPrimary: "#131B18",
    textSecondary: "#5C6A64",
    border: "#E1E7E4",
    primary: "#1E6D5F",
    primarySoft: "#E8F3F0",
    success: "#2E8E68",
    warning: "#A06B1D",
    danger: "#B54747",
  },
  spacing: { xs: 8, sm: 12, md: 16, lg: 24, xl: 32 },
  radius: { sm: 10, md: 14, lg: 18, xl: 22, pill: 999 },
};

export default function HabitsScreen({
  habits,
  onAddHabit,
  onOpenHabit,
  onEditHabit,
  onArchiveHabit,
  onDeleteHabit,
}: HabitsScreenProps) {
  const [actionHabit, setActionHabit] = useState<HabitItem | null>(null);

  const sortedHabits = useMemo(
    () => [...habits].sort((a, b) => Number(b.isActive) - Number(a.isActive)),
    [habits]
  );

  const renderItem = ({ item }: { item: HabitItem }) => (
    <HabitCard
      habit={item}
      onPress={() => onOpenHabit?.(item)}
      onLongPress={() => setActionHabit(item)}
      onEdit={() => onEditHabit(item)}
      onArchive={() => onArchiveHabit(item)}
      onDelete={() => onDeleteHabit(item)}
    />
  );

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaView style={styles.safe}>
        <View style={styles.container}>
          <Header onAddHabit={onAddHabit} />

          <FlatList
            data={sortedHabits}
            keyExtractor={(item) => item.id}
            renderItem={renderItem}
            showsVerticalScrollIndicator={false}
            contentContainerStyle={[
              styles.listContent,
              habits.length === 0 && styles.emptyListContent,
            ]}
            ListEmptyComponent={<EmptyHabitsState onAddHabit={onAddHabit} />}
          />
        </View>

        <HabitActionsSheet
          visible={!!actionHabit}
          habit={actionHabit}
          onClose={() => setActionHabit(null)}
          onEdit={(h) => {
            onEditHabit(h);
            setActionHabit(null);
          }}
          onArchive={(h) => {
            onArchiveHabit(h);
            setActionHabit(null);
          }}
          onDelete={(h) => {
            onDeleteHabit(h);
            setActionHabit(null);
          }}
        />
      </SafeAreaView>
    </GestureHandlerRootView>
  );
}

function Header({ onAddHabit }: { onAddHabit: () => void }) {
  return (
    <View style={styles.header}>
      <Text style={styles.title}>Habits</Text>
      <Pressable style={styles.addButton} onPress={onAddHabit}>
        <Ionicons name="add" size={18} color="#fff" />
        <Text style={styles.addButtonText}>Add</Text>
      </Pressable>
    </View>
  );
}

function HabitCard({
  habit,
  onPress,
  onLongPress,
  onEdit,
  onArchive,
  onDelete,
}: {
  habit: HabitItem;
  onPress?: () => void;
  onLongPress?: () => void;
  onEdit: () => void;
  onArchive: () => void;
  onDelete: () => void;
}) {
  return (
    <Swipeable
      overshootRight={false}
      renderRightActions={() => (
        <View style={styles.swipeActions}>
          <SwipeAction label="Edit" color={theme.colors.primary} onPress={onEdit} />
          <SwipeAction label="Archive" color={theme.colors.warning} onPress={onArchive} />
          <SwipeAction label="Delete" color={theme.colors.danger} onPress={onDelete} />
        </View>
      )}
    >
      <Pressable style={styles.card} onPress={onPress} onLongPress={onLongPress}>
        <View style={styles.cardTop}>
          <View style={styles.leftInfo}>
            <View style={styles.emojiWrap}>
              <Text style={styles.emoji}>{habit.emoji || "•"}</Text>
            </View>
            <View>
              <Text style={styles.habitName}>{habit.name}</Text>
              <Text style={styles.frequency}>{habit.frequency}</Text>
            </View>
          </View>

          <View
            style={[
              styles.statusPill,
              habit.isActive ? styles.statusActive : styles.statusInactive,
            ]}
          >
            <Text
              style={[
                styles.statusText,
                { color: habit.isActive ? theme.colors.success : theme.colors.textSecondary },
              ]}
            >
              {habit.isActive ? "Active" : "Inactive"}
            </Text>
          </View>
        </View>

        <View style={styles.metricsRow}>
          <Metric label="Streak" value={`${habit.streak}d`} />
          <Metric label="Rate" value={`${habit.completionRate}%`} />
        </View>

        <View style={styles.progressTrack}>
          <View style={[styles.progressFill, { width: `${habit.completionRate}%` }]} />
        </View>
      </Pressable>
    </Swipeable>
  );
}

function SwipeAction({
  label,
  color,
  onPress,
}: {
  label: string;
  color: string;
  onPress: () => void;
}) {
  return (
    <Pressable style={[styles.swipeActionBtn, { backgroundColor: color }]} onPress={onPress}>
      <Text style={styles.swipeActionText}>{label}</Text>
    </Pressable>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.metric}>
      <Text style={styles.metricValue}>{value}</Text>
      <Text style={styles.metricLabel}>{label}</Text>
    </View>
  );
}

function EmptyHabitsState({ onAddHabit }: { onAddHabit: () => void }) {
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

function HabitActionsSheet({
  visible,
  habit,
  onClose,
  onEdit,
  onArchive,
  onDelete,
}: {
  visible: boolean;
  habit: HabitItem | null;
  onClose: () => void;
  onEdit: (habit: HabitItem) => void;
  onArchive: (habit: HabitItem) => void;
  onDelete: (habit: HabitItem) => void;
}) {
  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <Pressable style={styles.sheetOverlay} onPress={onClose}>
        <Pressable style={styles.sheet} onPress={(e) => e.stopPropagation()}>
          <Text style={styles.sheetTitle}>{habit?.name}</Text>

          <Pressable style={styles.sheetItem} onPress={() => habit && onEdit(habit)}>
            <Text style={styles.sheetItemText}>Edit</Text>
          </Pressable>
          <Pressable style={styles.sheetItem} onPress={() => habit && onArchive(habit)}>
            <Text style={styles.sheetItemText}>Archive</Text>
          </Pressable>
          <Pressable style={styles.sheetItem} onPress={() => habit && onDelete(habit)}>
            <Text style={[styles.sheetItemText, { color: theme.colors.danger }]}>Delete</Text>
          </Pressable>

          <Pressable style={styles.sheetCancel} onPress={onClose}>
            <Text style={styles.sheetCancelText}>Cancel</Text>
          </Pressable>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: theme.colors.bg },
  container: { flex: 1, paddingHorizontal: theme.spacing.md },
  header: {
    height: 64,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  title: { fontSize: 28, fontWeight: "700", color: theme.colors.textPrimary },
  addButton: {
    height: 38,
    borderRadius: theme.radius.pill,
    backgroundColor: theme.colors.primary,
    paddingHorizontal: theme.spacing.sm,
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
  },
  addButtonText: { color: "#fff", fontSize: 14, fontWeight: "700" },

  listContent: { paddingBottom: theme.spacing.xl, gap: theme.spacing.sm },
  emptyListContent: { flexGrow: 1, justifyContent: "center" },

  card: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.radius.lg,
    padding: theme.spacing.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    marginBottom: theme.spacing.sm,
  },
  cardTop: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  leftInfo: { flexDirection: "row", alignItems: "center", gap: theme.spacing.sm },
  emojiWrap: {
    width: 40,
    height: 40,
    borderRadius: theme.radius.md,
    backgroundColor: theme.colors.primarySoft,
    alignItems: "center",
    justifyContent: "center",
  },
  emoji: { fontSize: 20 },
  habitName: { fontSize: 17, fontWeight: "700", color: theme.colors.textPrimary },
  frequency: { marginTop: 2, fontSize: 13, color: theme.colors.textSecondary },

  statusPill: {
    borderRadius: theme.radius.pill,
    paddingHorizontal: 10,
    paddingVertical: 5,
  },
  statusActive: { backgroundColor: "#E5F4EE" },
  statusInactive: { backgroundColor: theme.colors.surfaceMuted },
  statusText: { fontSize: 12, fontWeight: "700" },

  metricsRow: { flexDirection: "row", gap: theme.spacing.sm, marginTop: theme.spacing.md },
  metric: {
    flex: 1,
    backgroundColor: theme.colors.surfaceMuted,
    borderRadius: theme.radius.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  metricValue: { fontSize: 18, fontWeight: "700", color: theme.colors.textPrimary },
  metricLabel: { marginTop: 2, fontSize: 12, color: theme.colors.textSecondary },

  progressTrack: {
    marginTop: theme.spacing.sm,
    height: 8,
    borderRadius: theme.radius.pill,
    backgroundColor: theme.colors.surfaceMuted,
    overflow: "hidden",
  },
  progressFill: { height: "100%", backgroundColor: theme.colors.primary },

  swipeActions: { flexDirection: "row", marginBottom: theme.spacing.sm },
  swipeActionBtn: {
    width: 78,
    justifyContent: "center",
    alignItems: "center",
    borderRadius: theme.radius.md,
    marginLeft: theme.spacing.xs,
  },
  swipeActionText: { color: "#fff", fontSize: 12, fontWeight: "700" },

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
  emptyCtaText: { color: "#fff", fontWeight: "700", fontSize: 14 },

  sheetOverlay: {
    flex: 1,
    backgroundColor: "rgba(15,23,32,0.35)",
    justifyContent: "flex-end",
  },
  sheet: {
    backgroundColor: theme.colors.surface,
    borderTopLeftRadius: theme.radius.xl,
    borderTopRightRadius: theme.radius.xl,
    padding: theme.spacing.md,
    gap: theme.spacing.xs,
  },
  sheetTitle: {
    fontSize: 16,
    fontWeight: "700",
    color: theme.colors.textPrimary,
    marginBottom: theme.spacing.xs,
  },
  sheetItem: {
    height: 48,
    borderRadius: theme.radius.md,
    backgroundColor: theme.colors.surfaceMuted,
    justifyContent: "center",
    paddingHorizontal: theme.spacing.md,
  },
  sheetItemText: { color: theme.colors.textPrimary, fontSize: 15, fontWeight: "600" },
  sheetCancel: {
    marginTop: theme.spacing.xs,
    height: 48,
    borderRadius: theme.radius.md,
    justifyContent: "center",
    alignItems: "center",
  },
  sheetCancelText: { color: theme.colors.textSecondary, fontSize: 15, fontWeight: "600" },
});

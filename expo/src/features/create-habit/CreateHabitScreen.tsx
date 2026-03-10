import React, { useMemo, useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, Text, View, Pressable, Switch } from "react-native";
import { tokens } from "./tokens";
import { useCreateHabitForm } from "./useCreateHabitForm";
import { validateCreateHabit } from "./validation";
import { CreateHabitPayload, FrequencyType, TrackingType } from "./types";
import {
  FieldError,
  FieldLabel,
  HabitPreviewCard,
  InlinePickerTrigger,
  SaveButton,
  Section,
  SegmentedControl,
  SimplePickerModal,
  TextField,
  Chip,
  ColorSwatches,
} from "./components";

const COLORS = ["#1E6D5F", "#335C81", "#7C5C99", "#C97A57", "#3A7D44", "#7A7A7A"];
const EMOJIS = ["✨", "💪", "📚", "🧘", "🏃", "💧", "🛌", "🥗", "🧠", "📝", "🎯", "🫶"];
const DAY_LABELS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

type Props = {
  onCancel?: () => void;
  onSave?: (payload: CreateHabitPayload) => Promise<void> | void;
};

export default function CreateHabitScreen({ onCancel, onSave }: Props) {
  const { state, setField, toggleDay, setSaving, setShowErrors, toggleAdvanced } =
    useCreateHabitForm();

  const [emojiPickerOpen, setEmojiPickerOpen] = useState(false);
  const [timePickerOpen, setTimePickerOpen] = useState(false);
  const [datePickerOpen, setDatePickerOpen] = useState(false);

  const errors = useMemo(() => validateCreateHabit(state), [state]);
  const isValid = Object.keys(errors).length === 0;
  const showErrors = state.showErrors;

  const trackingText = getTrackingText(state.trackingType, state.targetCount, state.targetMinutes);
  const frequencyText = getFrequencyText(state.frequencyType, state.daysOfWeek, state.timesPerWeek);

  async function handleSave() {
    setShowErrors(true);
    if (!isValid) return;

    const payload: CreateHabitPayload = {
      name: state.name.trim(),
      emoji: state.emoji,
      colorHex: state.colorHex,
      trackingType: state.trackingType,
      targetCount: state.trackingType === "count" ? Number(state.targetCount) : undefined,
      targetMinutes: state.trackingType === "duration" ? Number(state.targetMinutes) : undefined,
      frequencyType: state.frequencyType,
      daysOfWeek: state.frequencyType === "days_of_week" ? state.daysOfWeek : undefined,
      timesPerWeek: state.frequencyType === "times_per_week" ? Number(state.timesPerWeek) : undefined,
      reminderTime: state.reminderEnabled ? state.reminderTime : undefined,
      startDateISO: state.startDateISO,
    };

    try {
      setSaving(true);
      if (onSave) {
        await onSave(payload);
      } else {
        await new Promise((r) => setTimeout(r, 900));
      }
    } finally {
      setSaving(false);
    }
  }

  const timeOptions = useMemo(() => buildTimeOptions(), []);
  const dateOptions = useMemo(() => buildDateOptions(30), []);

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.root}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Create Habit</Text>
          {onCancel ? (
            <Pressable onPress={onCancel}>
              <Text style={styles.cancelText}>Cancel</Text>
            </Pressable>
          ) : null}
        </View>

        <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
          <Section title="Basic setup" subtitle="Start simple. You can adjust later.">
            <FieldLabel label="Habit name" />
            <TextField
              value={state.name}
              onChangeText={(v) => setField("name", v)}
              placeholder="e.g. Morning meditation"
            />
            <FieldError error={showErrors ? errors.name : undefined} />

            <View style={styles.row2}>
              <View style={{ flex: 1 }}>
                <FieldLabel label="Icon / Emoji" />
                <InlinePickerTrigger
                  label="Selected"
                  value={state.emoji}
                  onPress={() => setEmojiPickerOpen(true)}
                />
              </View>

              <View style={{ flex: 1 }}>
                <FieldLabel label="Color" />
                <ColorSwatches
                  colors={COLORS}
                  value={state.colorHex}
                  onChange={(c) => setField("colorHex", c)}
                />
              </View>
            </View>

            <FieldLabel label="Tracking type" />
            <SegmentedControl<TrackingType>
              value={state.trackingType}
              onChange={(v) => setField("trackingType", v)}
              options={[
                { label: "Yes / No", value: "yes_no" },
                { label: "Count", value: "count" },
                { label: "Duration", value: "duration" },
              ]}
            />

            {state.trackingType === "count" ? (
              <View style={{ marginTop: tokens.spacing.sm }}>
                <FieldLabel label="Target count" />
                <TextField
                  value={state.targetCount}
                  onChangeText={(v) => setField("targetCount", v.replace(/[^\d]/g, ""))}
                  placeholder="e.g. 10"
                  keyboardType="numeric"
                />
                <FieldError error={showErrors ? errors.targetCount : undefined} />
              </View>
            ) : null}

            {state.trackingType === "duration" ? (
              <View style={{ marginTop: tokens.spacing.sm }}>
                <FieldLabel label="Target minutes" />
                <TextField
                  value={state.targetMinutes}
                  onChangeText={(v) => setField("targetMinutes", v.replace(/[^\d]/g, ""))}
                  placeholder="e.g. 20"
                  keyboardType="numeric"
                />
                <FieldError error={showErrors ? errors.targetMinutes : undefined} />
              </View>
            ) : null}
          </Section>

          <Section title="Schedule" subtitle="Choose how often this habit should happen.">
            <FieldLabel label="Goal frequency" />
            <SegmentedControl<FrequencyType>
              value={state.frequencyType}
              onChange={(v) => setField("frequencyType", v)}
              options={[
                { label: "Every day", value: "daily" },
                { label: "Days", value: "days_of_week" },
                { label: "X / week", value: "times_per_week" },
              ]}
            />

            {state.frequencyType === "days_of_week" ? (
              <>
                <View style={styles.daysWrap}>
                  {DAY_LABELS.map((label, idx) => {
                    const day = idx + 1;
                    const selected = state.daysOfWeek.includes(day);
                    return (
                      <Chip
                        key={day}
                        label={label}
                        selected={selected}
                        onPress={() => toggleDay(day)}
                      />
                    );
                  })}
                </View>
                <FieldError error={showErrors ? errors.daysOfWeek : undefined} />
              </>
            ) : null}

            {state.frequencyType === "times_per_week" ? (
              <View style={{ marginTop: tokens.spacing.sm }}>
                <FieldLabel label="Times per week" />
                <View style={styles.stepper}>
                  <Pressable
                    style={styles.stepBtn}
                    onPress={() =>
                      setField("timesPerWeek", String(Math.max(1, Number(state.timesPerWeek || 1) - 1)))
                    }
                  >
                    <Text style={styles.stepBtnText}>-</Text>
                  </Pressable>
                  <Text style={styles.stepValue}>{state.timesPerWeek}</Text>
                  <Pressable
                    style={styles.stepBtn}
                    onPress={() =>
                      setField("timesPerWeek", String(Math.min(7, Number(state.timesPerWeek || 1) + 1)))
                    }
                  >
                    <Text style={styles.stepBtnText}>+</Text>
                  </Pressable>
                </View>
                <FieldError error={showErrors ? errors.timesPerWeek : undefined} />
              </View>
            ) : null}
          </Section>

          <Section title="Advanced options" subtitle="Optional settings">
            <Pressable onPress={toggleAdvanced} style={styles.advancedToggle}>
              <Text style={styles.advancedToggleText}>
                {state.isAdvancedOpen ? "Hide advanced" : "Show advanced"}
              </Text>
            </Pressable>

            {state.isAdvancedOpen ? (
              <View style={{ marginTop: tokens.spacing.sm }}>
                <View style={styles.reminderRow}>
                  <Text style={styles.reminderLabel}>Enable reminder</Text>
                  <Switch
                    value={state.reminderEnabled}
                    onValueChange={(v) => setField("reminderEnabled", v)}
                  />
                </View>

                {state.reminderEnabled ? (
                  <>
                    <InlinePickerTrigger
                      label="Reminder time"
                      value={state.reminderTime}
                      onPress={() => setTimePickerOpen(true)}
                    />
                    <FieldError error={showErrors ? errors.reminderTime : undefined} />
                  </>
                ) : null}

                <View style={{ marginTop: tokens.spacing.sm }}>
                  <InlinePickerTrigger
                    label="Start date"
                    value={state.startDateISO}
                    onPress={() => setDatePickerOpen(true)}
                  />
                  <FieldError error={showErrors ? errors.startDateISO : undefined} />
                </View>
              </View>
            ) : null}
          </Section>

          <Section title="Preview">
            <HabitPreviewCard
              emoji={state.emoji}
              colorHex={state.colorHex}
              name={state.name}
              trackingText={trackingText}
              frequencyText={frequencyText}
            />
          </Section>
        </ScrollView>

        <View style={styles.footer}>
          <SaveButton
            title="Save Habit"
            disabled={!isValid}
            loading={state.isSaving}
            onPress={handleSave}
          />
        </View>
      </View>

      <SimplePickerModal
        visible={emojiPickerOpen}
        title="Choose icon"
        options={EMOJIS.map((e) => ({ label: e, value: e }))}
        onSelect={(v) => setField("emoji", v)}
        onClose={() => setEmojiPickerOpen(false)}
      />

      <SimplePickerModal
        visible={timePickerOpen}
        title="Reminder time"
        options={timeOptions}
        onSelect={(v) => setField("reminderTime", v)}
        onClose={() => setTimePickerOpen(false)}
      />

      <SimplePickerModal
        visible={datePickerOpen}
        title="Start date"
        options={dateOptions}
        onSelect={(v) => setField("startDateISO", v)}
        onClose={() => setDatePickerOpen(false)}
      />
    </SafeAreaView>
  );
}

function getTrackingText(type: TrackingType, count: string, mins: string) {
  if (type === "yes_no") return "Completion: Yes / No";
  if (type === "count") return `Completion: ${count || "0"} times`;
  return `Completion: ${mins || "0"} minutes`;
}

function getFrequencyText(type: FrequencyType, days: number[], timesPerWeek: string) {
  if (type === "daily") return "Frequency: Every day";
  if (type === "times_per_week") return `Frequency: ${timesPerWeek || "0"} times/week`;
  const labels = days.map((d) => ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][d - 1]);
  return `Frequency: ${labels.join(", ") || "No days selected"}`;
}

function buildTimeOptions() {
  const opts: Array<{ label: string; value: string }> = [];
  for (let h = 0; h < 24; h += 1) {
    for (let m = 0; m < 60; m += 30) {
      const hh = String(h).padStart(2, "0");
      const mm = String(m).padStart(2, "0");
      const value = `${hh}:${mm}`;
      opts.push({ label: value, value });
    }
  }
  return opts;
}

function buildDateOptions(daysForward: number) {
  const opts: Array<{ label: string; value: string }> = [];
  const base = new Date();
  base.setHours(0, 0, 0, 0);

  for (let i = 0; i < daysForward; i += 1) {
    const d = new Date(base);
    d.setDate(base.getDate() + i);
    const iso = d.toISOString().slice(0, 10);
    const label = d.toLocaleDateString(undefined, {
      weekday: "short",
      month: "short",
      day: "numeric",
    });
    opts.push({ label, value: iso });
  }
  return opts;
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: tokens.colors.bg },
  root: { flex: 1 },
  header: {
    paddingHorizontal: tokens.spacing.md,
    paddingTop: tokens.spacing.sm,
    paddingBottom: tokens.spacing.xs,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: "700",
    color: tokens.colors.textPrimary,
  },
  cancelText: {
    color: tokens.colors.textSecondary,
    fontSize: 15,
    fontWeight: "600",
  },
  content: {
    paddingHorizontal: tokens.spacing.md,
    paddingBottom: 120,
    gap: tokens.spacing.sm,
  },
  row2: {
    marginTop: tokens.spacing.sm,
    flexDirection: "row",
    gap: tokens.spacing.sm,
  },
  daysWrap: {
    marginTop: tokens.spacing.sm,
    flexDirection: "row",
    flexWrap: "wrap",
    gap: tokens.spacing.xs,
  },
  stepper: {
    height: 46,
    borderRadius: tokens.radius.md,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: tokens.spacing.xs,
    backgroundColor: tokens.colors.surface,
  },
  stepBtn: {
    width: 36,
    height: 36,
    borderRadius: tokens.radius.sm,
    backgroundColor: tokens.colors.surfaceMuted,
    alignItems: "center",
    justifyContent: "center",
  },
  stepBtnText: { fontSize: 18, fontWeight: "700", color: tokens.colors.textPrimary },
  stepValue: { fontSize: 18, fontWeight: "700", color: tokens.colors.textPrimary },

  advancedToggle: {
    height: 42,
    borderRadius: tokens.radius.md,
    backgroundColor: tokens.colors.surfaceMuted,
    alignItems: "center",
    justifyContent: "center",
  },
  advancedToggleText: { color: tokens.colors.textPrimary, fontWeight: "600" },
  reminderRow: {
    height: 46,
    marginBottom: tokens.spacing.sm,
    borderRadius: tokens.radius.md,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    backgroundColor: tokens.colors.surface,
    paddingHorizontal: tokens.spacing.sm,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  reminderLabel: { color: tokens.colors.textPrimary, fontSize: 14, fontWeight: "600" },

  footer: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 0,
    padding: tokens.spacing.md,
    backgroundColor: "rgba(247,249,248,0.95)",
    borderTopWidth: 1,
    borderTopColor: tokens.colors.border,
  },
});

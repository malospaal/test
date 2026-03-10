import React from "react";
import {
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import { tokens } from "./tokens";

export function Section({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle?: string;
  children: React.ReactNode;
}) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {subtitle ? <Text style={styles.sectionSubtitle}>{subtitle}</Text> : null}
      <View style={{ marginTop: tokens.spacing.sm }}>{children}</View>
    </View>
  );
}

export function FieldLabel({ label }: { label: string }) {
  return <Text style={styles.label}>{label}</Text>;
}

export function FieldError({ error }: { error?: string }) {
  if (!error) return null;
  return <Text style={styles.error}>{error}</Text>;
}

export function TextField({
  value,
  onChangeText,
  placeholder,
  keyboardType,
}: {
  value: string;
  onChangeText: (v: string) => void;
  placeholder?: string;
  keyboardType?: "default" | "numeric";
}) {
  return (
    <TextInput
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      keyboardType={keyboardType}
      style={styles.input}
      placeholderTextColor={tokens.colors.textSecondary}
    />
  );
}

export function SegmentedControl<T extends string>({
  options,
  value,
  onChange,
}: {
  options: Array<{ label: string; value: T }>;
  value: T;
  onChange: (v: T) => void;
}) {
  return (
    <View style={styles.segmentWrap}>
      {options.map((opt) => {
        const active = opt.value === value;
        return (
          <Pressable
            key={opt.value}
            onPress={() => onChange(opt.value)}
            style={[styles.segmentItem, active && styles.segmentItemActive]}
          >
            <Text style={[styles.segmentText, active && styles.segmentTextActive]}>
              {opt.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}

export function Chip({
  label,
  selected,
  onPress,
}: {
  label: string;
  selected: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      style={[styles.chip, selected && styles.chipSelected]}
    >
      <Text style={[styles.chipText, selected && styles.chipTextSelected]}>{label}</Text>
    </Pressable>
  );
}

export function ColorSwatches({
  colors,
  value,
  onChange,
}: {
  colors: string[];
  value: string;
  onChange: (c: string) => void;
}) {
  return (
    <View style={styles.colorRow}>
      {colors.map((c) => {
        const selected = c === value;
        return (
          <Pressable
            key={c}
            onPress={() => onChange(c)}
            style={[
              styles.colorDotWrap,
              selected && { borderColor: tokens.colors.textPrimary },
            ]}
          >
            <View style={[styles.colorDot, { backgroundColor: c }]} />
          </Pressable>
        );
      })}
    </View>
  );
}

export function InlinePickerTrigger({
  label,
  value,
  onPress,
}: {
  label: string;
  value: string;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} style={styles.pickerTrigger}>
      <Text style={styles.pickerLabel}>{label}</Text>
      <Text style={styles.pickerValue}>{value}</Text>
    </Pressable>
  );
}

export function SimplePickerModal({
  visible,
  title,
  options,
  onSelect,
  onClose,
}: {
  visible: boolean;
  title: string;
  options: Array<{ label: string; value: string }>;
  onSelect: (v: string) => void;
  onClose: () => void;
}) {
  return (
    <Modal transparent visible={visible} animationType="fade" onRequestClose={onClose}>
      <Pressable style={styles.modalOverlay} onPress={onClose}>
        <Pressable style={styles.modalCard} onPress={(e) => e.stopPropagation()}>
          <Text style={styles.modalTitle}>{title}</Text>
          <ScrollView style={{ maxHeight: 320 }}>
            {options.map((o) => (
              <Pressable
                key={o.value}
                style={styles.modalOption}
                onPress={() => {
                  onSelect(o.value);
                  onClose();
                }}
              >
                <Text style={styles.modalOptionText}>{o.label}</Text>
              </Pressable>
            ))}
          </ScrollView>
          <Pressable onPress={onClose} style={styles.modalCloseBtn}>
            <Text style={styles.modalCloseText}>Close</Text>
          </Pressable>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

export function HabitPreviewCard({
  emoji,
  colorHex,
  name,
  frequencyText,
  trackingText,
}: {
  emoji: string;
  colorHex: string;
  name: string;
  frequencyText: string;
  trackingText: string;
}) {
  return (
    <View style={styles.previewCard}>
      <View style={[styles.previewIcon, { backgroundColor: colorHex + "22" }]}>
        <Text style={{ fontSize: 20 }}>{emoji}</Text>
      </View>
      <View style={{ flex: 1 }}>
        <Text style={styles.previewName}>{name || "Your new habit"}</Text>
        <Text style={styles.previewMeta}>{trackingText}</Text>
        <Text style={styles.previewMeta}>{frequencyText}</Text>
      </View>
      <View style={[styles.previewAccent, { backgroundColor: colorHex }]} />
    </View>
  );
}

export function SaveButton({
  title,
  disabled,
  loading,
  onPress,
}: {
  title: string;
  disabled: boolean;
  loading: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      disabled={disabled || loading}
      style={[
        styles.saveBtn,
        (disabled || loading) && { backgroundColor: "#9CB5AE" },
      ]}
    >
      {loading ? (
        <ActivityIndicator color="#fff" />
      ) : (
        <Text style={styles.saveBtnText}>{title}</Text>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  section: {
    backgroundColor: tokens.colors.surface,
    borderRadius: tokens.radius.lg,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    padding: tokens.spacing.md,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "700",
    color: tokens.colors.textPrimary,
  },
  sectionSubtitle: {
    marginTop: 4,
    color: tokens.colors.textSecondary,
    fontSize: 13,
  },
  label: {
    marginBottom: 6,
    color: tokens.colors.textPrimary,
    fontSize: 13,
    fontWeight: "600",
  },
  error: {
    marginTop: 6,
    color: tokens.colors.danger,
    fontSize: 12,
  },
  input: {
    height: 46,
    borderRadius: tokens.radius.md,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    paddingHorizontal: tokens.spacing.sm,
    color: tokens.colors.textPrimary,
    backgroundColor: tokens.colors.surface,
  },
  segmentWrap: {
    flexDirection: "row",
    backgroundColor: tokens.colors.surfaceMuted,
    borderRadius: tokens.radius.md,
    padding: 4,
  },
  segmentItem: {
    flex: 1,
    height: 38,
    borderRadius: tokens.radius.sm,
    alignItems: "center",
    justifyContent: "center",
  },
  segmentItemActive: { backgroundColor: tokens.colors.surface },
  segmentText: { color: tokens.colors.textSecondary, fontWeight: "600", fontSize: 13 },
  segmentTextActive: { color: tokens.colors.textPrimary },

  chip: {
    height: 34,
    paddingHorizontal: 12,
    borderRadius: tokens.radius.pill,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: tokens.colors.surface,
  },
  chipSelected: {
    backgroundColor: tokens.colors.accentSoft,
    borderColor: tokens.colors.accent,
  },
  chipText: { color: tokens.colors.textSecondary, fontSize: 13, fontWeight: "600" },
  chipTextSelected: { color: tokens.colors.accent },

  colorRow: { flexDirection: "row", gap: tokens.spacing.sm },
  colorDotWrap: {
    width: 34,
    height: 34,
    borderRadius: tokens.radius.pill,
    borderWidth: 2,
    borderColor: "transparent",
    alignItems: "center",
    justifyContent: "center",
  },
  colorDot: { width: 26, height: 26, borderRadius: tokens.radius.pill },

  pickerTrigger: {
    height: 46,
    borderRadius: tokens.radius.md,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    paddingHorizontal: tokens.spacing.sm,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  pickerLabel: { color: tokens.colors.textSecondary, fontSize: 13 },
  pickerValue: { color: tokens.colors.textPrimary, fontWeight: "600", fontSize: 14 },

  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(19,27,24,0.35)",
    justifyContent: "center",
    padding: tokens.spacing.md,
  },
  modalCard: {
    backgroundColor: tokens.colors.surface,
    borderRadius: tokens.radius.lg,
    padding: tokens.spacing.md,
  },
  modalTitle: { fontSize: 18, fontWeight: "700", color: tokens.colors.textPrimary },
  modalOption: {
    height: 44,
    justifyContent: "center",
    borderBottomWidth: 1,
    borderBottomColor: tokens.colors.border,
  },
  modalOptionText: { color: tokens.colors.textPrimary, fontSize: 15 },
  modalCloseBtn: {
    marginTop: tokens.spacing.sm,
    height: 42,
    borderRadius: tokens.radius.md,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: tokens.colors.surfaceMuted,
  },
  modalCloseText: { color: tokens.colors.textPrimary, fontWeight: "600" },

  previewCard: {
    backgroundColor: tokens.colors.surface,
    borderRadius: tokens.radius.lg,
    borderWidth: 1,
    borderColor: tokens.colors.border,
    padding: tokens.spacing.md,
    flexDirection: "row",
    alignItems: "center",
    gap: tokens.spacing.sm,
  },
  previewIcon: {
    width: 44,
    height: 44,
    borderRadius: tokens.radius.md,
    alignItems: "center",
    justifyContent: "center",
  },
  previewName: { color: tokens.colors.textPrimary, fontSize: 16, fontWeight: "700" },
  previewMeta: { color: tokens.colors.textSecondary, fontSize: 13, marginTop: 2 },
  previewAccent: {
    width: 4,
    alignSelf: "stretch",
    borderRadius: tokens.radius.pill,
  },

  saveBtn: {
    height: 52,
    borderRadius: tokens.radius.md,
    backgroundColor: tokens.colors.accent,
    alignItems: "center",
    justifyContent: "center",
  },
  saveBtnText: { color: "#fff", fontSize: 16, fontWeight: "700" },
});

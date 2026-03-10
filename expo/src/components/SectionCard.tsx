import React from "react";
import { View, StyleSheet, ViewProps } from "react-native";
import { colors, radius, spacing, shadows } from "../theme/tokens";

type Props = ViewProps & {
  children: React.ReactNode;
};

export function SectionCard({ children, style, ...rest }: Props) {
  return (
    <View style={[styles.card, style]} {...rest}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.md,
    borderWidth: 1,
    borderColor: colors.border,
    ...shadows.card,
  },
});

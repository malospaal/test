import {
  darkColors,
  elevation,
  icon,
  lightColors,
  radius,
  spacing,
  typography,
} from "./tokens";

export type ThemeMode = "light" | "dark";

export const createTheme = (mode: ThemeMode = "light") => {
  const colors = mode === "light" ? lightColors : darkColors;

  return {
    mode,
    colors,
    typography,
    spacing,
    radius,
    elevation,
    icon,

    components: {
      button: {
        height: {
          sm: 36,
          md: 44,
          lg: 52,
        },
        variants: {
          primary: {
            bg: colors.accent.primary,
            bgPressed: colors.accent.primaryPressed,
            text: colors.accent.onAccent,
            border: "transparent",
          },
          secondary: {
            bg: colors.background.surface,
            bgPressed: colors.background.surfaceMuted,
            text: colors.text.primary,
            border: colors.border.default,
          },
          ghost: {
            bg: "transparent",
            bgPressed: colors.background.surfaceMuted,
            text: colors.text.secondary,
            border: "transparent",
          },
          danger: {
            bg: colors.status.danger,
            bgPressed: colors.status.danger,
            text: "#FFFFFF",
            border: "transparent",
          },
          disabled: {
            bg: colors.background.surfaceMuted,
            text: colors.text.disabled,
            border: colors.border.subtle,
          },
        },
      },

      card: {
        variants: {
          elevated: {
            bg: colors.background.surfaceElevated,
            border: colors.border.subtle,
            elevation: elevation.md,
            radius: radius.lg,
          },
          outlined: {
            bg: colors.background.surface,
            border: colors.border.default,
            elevation: elevation.none,
            radius: radius.lg,
          },
          subtle: {
            bg: colors.background.surfaceMuted,
            border: "transparent",
            elevation: elevation.none,
            radius: radius.md,
          },
        },
      },

      input: {
        height: 48,
        bg: colors.background.surface,
        text: colors.text.primary,
        placeholder: colors.text.tertiary,
        borderDefault: colors.border.default,
        borderFocus: colors.border.focus,
        borderError: colors.status.danger,
        borderDisabled: colors.border.subtle,
        radius: radius.md,
      },
    },
  };
};

export type AppTheme = ReturnType<typeof createTheme>;

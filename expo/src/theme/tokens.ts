export const colors = {
  bg: "#F6F8F7",
  surface: "#FFFFFF",
  surfaceSoft: "#F0F4F2",
  textPrimary: "#0F1720",
  textSecondary: "#5F6B72",
  border: "#E4EBE7",
  primary: "#1F7A62",
  primarySoft: "#DFF3EC",
  success: "#2E9D74",
  warning: "#D4A72C",
  danger: "#C14C4C",
};

export const spacing = {
  xxs: 4,
  xs: 8,
  sm: 12,
  md: 16,
  lg: 20,
  xl: 24,
  xxl: 32,
};

export const radius = {
  sm: 10,
  md: 14,
  lg: 18,
  xl: 24,
  full: 999,
};

export const type = {
  h1: 28,
  h2: 22,
  h3: 18,
  body: 15,
  caption: 13,
  button: 16,
};

export const shadows = {
  card: {
    shadowColor: "#0D1B14",
    shadowOpacity: 0.08,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 6 },
    elevation: 3,
  } as const,
};

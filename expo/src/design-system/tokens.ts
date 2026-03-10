export const primitives = {
  teal: {
    900: "#103A33",
    800: "#165046",
    700: "#1E6D5F",
    600: "#2A8A78",
    500: "#48A996",
    200: "#CFE7E1",
    100: "#E8F3F0",
  },
  slate: {
    950: "#0E1412",
    900: "#131B18",
    800: "#1B2521",
    700: "#2E3A35",
    600: "#4D5B55",
    500: "#6C7A74",
    400: "#95A39D",
    300: "#C8D1CD",
    200: "#E1E7E4",
    100: "#F1F4F3",
    50: "#F7F9F8",
    0: "#FFFFFF",
  },
  red: {
    700: "#B54747",
    100: "#F8E8E8",
  },
  amber: {
    700: "#A06B1D",
    100: "#FAF1DF",
  },
  green: {
    700: "#2E8E68",
    100: "#E5F4EE",
  },
};

export const lightColors = {
  background: {
    canvas: "#F7F9F8",
    surface: "#FFFFFF",
    surfaceMuted: "#F1F4F3",
    surfaceElevated: "#FFFFFF",
  },
  text: {
    primary: "#131B18",
    secondary: "#4D5B55",
    tertiary: "#6C7A74",
    inverse: "#FFFFFF",
    disabled: "#95A39D",
  },
  border: {
    subtle: "#E1E7E4",
    default: "#C8D1CD",
    strong: "#95A39D",
    focus: "#2A8A78",
  },
  accent: {
    primary: "#1E6D5F",
    primaryPressed: "#165046",
    primarySoft: "#E8F3F0",
    onAccent: "#FFFFFF",
  },
  status: {
    success: "#2E8E68",
    successSoft: "#E5F4EE",
    warning: "#A06B1D",
    warningSoft: "#FAF1DF",
    danger: "#B54747",
    dangerSoft: "#F8E8E8",
    neutral: "#4D5B55",
    neutralSoft: "#F1F4F3",
  },
  chart: {
    primary: "#1E6D5F",
    secondary: "#48A996",
    muted: "#C8D1CD",
    grid: "#E1E7E4",
    positive: "#2E8E68",
    negative: "#B54747",
  },
  calendar: {
    dayText: "#131B18",
    dayMuted: "#95A39D",
    selectedBg: "#1E6D5F",
    selectedText: "#FFFFFF",
    todayRing: "#2A8A78",
    completedDot: "#2E8E68",
    disabledText: "#C8D1CD",
    rangeBg: "#E8F3F0",
  },
};

export const darkColors = {
  background: {
    canvas: "#0E1412",
    surface: "#131B18",
    surfaceMuted: "#1B2521",
    surfaceElevated: "#1F2B27",
  },
  text: {
    primary: "#EAF1EE",
    secondary: "#B9C4BF",
    tertiary: "#95A39D",
    inverse: "#0E1412",
    disabled: "#6C7A74",
  },
  border: {
    subtle: "#2E3A35",
    default: "#4D5B55",
    strong: "#6C7A74",
    focus: "#48A996",
  },
  accent: {
    primary: "#48A996",
    primaryPressed: "#2A8A78",
    primarySoft: "#1B2521",
    onAccent: "#0E1412",
  },
  status: {
    success: "#5AB58A",
    successSoft: "#173126",
    warning: "#D2A85A",
    warningSoft: "#332814",
    danger: "#E07A7A",
    dangerSoft: "#331A1A",
    neutral: "#B9C4BF",
    neutralSoft: "#1B2521",
  },
  chart: {
    primary: "#48A996",
    secondary: "#66C3B0",
    muted: "#4D5B55",
    grid: "#2E3A35",
    positive: "#5AB58A",
    negative: "#E07A7A",
  },
  calendar: {
    dayText: "#EAF1EE",
    dayMuted: "#6C7A74",
    selectedBg: "#48A996",
    selectedText: "#0E1412",
    todayRing: "#66C3B0",
    completedDot: "#5AB58A",
    disabledText: "#4D5B55",
    rangeBg: "#1B2521",
  },
};

export const typography = {
  fontFamily: {
    heading: "System",
    body: "System",
    mono: "Courier",
  },
  fontWeight: {
    regular: "400",
    medium: "500",
    semibold: "600",
    bold: "700",
  },
  size: {
    display: 32,
    h1: 28,
    h2: 22,
    h3: 18,
    bodyLg: 16,
    body: 15,
    bodySm: 14,
    caption: 12,
    overline: 11,
  },
  lineHeight: {
    display: 40,
    h1: 34,
    h2: 28,
    h3: 24,
    bodyLg: 24,
    body: 22,
    bodySm: 20,
    caption: 16,
    overline: 14,
  },
};

export const spacing = {
  0: 0,
  0.5: 4,
  1: 8,
  1.5: 12,
  2: 16,
  3: 24,
  4: 32,
  5: 40,
  6: 48,
  8: 64,
};

export const radius = {
  none: 0,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
  pill: 999,
};

export const elevation = {
  none: {
    shadowColor: "#000000",
    shadowOpacity: 0,
    shadowRadius: 0,
    shadowOffset: { width: 0, height: 0 },
    elevation: 0,
  },
  sm: {
    shadowColor: "#0F1720",
    shadowOpacity: 0.06,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 1,
  },
  md: {
    shadowColor: "#0F1720",
    shadowOpacity: 0.1,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 6 },
    elevation: 3,
  },
  lg: {
    shadowColor: "#0F1720",
    shadowOpacity: 0.14,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 10 },
    elevation: 6,
  },
};

export const icon = {
  xs: 16,
  sm: 20,
  md: 24,
  lg: 28,
  xl: 32,
  touchTargetMin: 44,
};

export type TrackingType = "yes_no" | "count" | "duration";
export type FrequencyType = "daily" | "days_of_week" | "times_per_week";

export type FormErrors = Partial<{
  name: string;
  targetCount: string;
  targetMinutes: string;
  daysOfWeek: string;
  timesPerWeek: string;
  reminderTime: string;
  startDateISO: string;
}>;

export type CreateHabitFormState = {
  name: string;
  emoji: string;
  colorHex: string;

  trackingType: TrackingType;
  targetCount: string;
  targetMinutes: string;

  frequencyType: FrequencyType;
  daysOfWeek: number[];
  timesPerWeek: string;

  reminderEnabled: boolean;
  reminderTime: string;
  startDateISO: string;

  isAdvancedOpen: boolean;
  isSaving: boolean;
  showErrors: boolean;
};

export type CreateHabitPayload = {
  name: string;
  emoji: string;
  colorHex: string;
  trackingType: TrackingType;
  targetCount?: number;
  targetMinutes?: number;
  frequencyType: FrequencyType;
  daysOfWeek?: number[];
  timesPerWeek?: number;
  reminderTime?: string;
  startDateISO: string;
};

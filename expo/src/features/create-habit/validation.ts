import { CreateHabitFormState, FormErrors } from "./types";

const hhmmRegex = /^([01]\d|2[0-3]):([0-5]\d)$/;
const isoDateRegex = /^\d{4}-\d{2}-\d{2}$/;

export function validateCreateHabit(state: CreateHabitFormState): FormErrors {
  const errors: FormErrors = {};

  if (!state.name.trim()) {
    errors.name = "Habit name is required.";
  } else if (state.name.trim().length < 2) {
    errors.name = "Habit name is too short.";
  }

  if (state.trackingType === "count") {
    const n = Number(state.targetCount);
    if (!Number.isInteger(n) || n <= 0) {
      errors.targetCount = "Enter a valid target count.";
    }
  }

  if (state.trackingType === "duration") {
    const n = Number(state.targetMinutes);
    if (!Number.isInteger(n) || n <= 0) {
      errors.targetMinutes = "Enter valid target minutes.";
    }
  }

  if (state.frequencyType === "days_of_week" && state.daysOfWeek.length === 0) {
    errors.daysOfWeek = "Select at least one day.";
  }

  if (state.frequencyType === "times_per_week") {
    const n = Number(state.timesPerWeek);
    if (!Number.isInteger(n) || n < 1 || n > 7) {
      errors.timesPerWeek = "Times per week must be between 1 and 7.";
    }
  }

  if (state.reminderEnabled && !hhmmRegex.test(state.reminderTime)) {
    errors.reminderTime = "Reminder time is invalid.";
  }

  if (!isoDateRegex.test(state.startDateISO)) {
    errors.startDateISO = "Start date is invalid.";
  }

  return errors;
}

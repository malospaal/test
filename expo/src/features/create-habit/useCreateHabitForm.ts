import { useReducer } from "react";
import { CreateHabitFormState } from "./types";

type Action =
  | { type: "setField"; key: keyof CreateHabitFormState; value: any }
  | { type: "toggleDay"; day: number }
  | { type: "setSaving"; value: boolean }
  | { type: "setShowErrors"; value: boolean }
  | { type: "toggleAdvanced" };

const todayISO = new Date().toISOString().slice(0, 10);

const initialState: CreateHabitFormState = {
  name: "",
  emoji: "✨",
  colorHex: "#1E6D5F",

  trackingType: "yes_no",
  targetCount: "1",
  targetMinutes: "10",

  frequencyType: "daily",
  daysOfWeek: [1, 2, 3, 4, 5],
  timesPerWeek: "3",

  reminderEnabled: false,
  reminderTime: "08:00",
  startDateISO: todayISO,

  isAdvancedOpen: false,
  isSaving: false,
  showErrors: false,
};

function reducer(state: CreateHabitFormState, action: Action): CreateHabitFormState {
  switch (action.type) {
    case "setField":
      return { ...state, [action.key]: action.value };
    case "toggleDay": {
      const exists = state.daysOfWeek.includes(action.day);
      const next = exists
        ? state.daysOfWeek.filter((d) => d !== action.day)
        : [...state.daysOfWeek, action.day].sort((a, b) => a - b);
      return { ...state, daysOfWeek: next };
    }
    case "setSaving":
      return { ...state, isSaving: action.value };
    case "setShowErrors":
      return { ...state, showErrors: action.value };
    case "toggleAdvanced":
      return { ...state, isAdvancedOpen: !state.isAdvancedOpen };
    default:
      return state;
  }
}

export function useCreateHabitForm() {
  const [state, dispatch] = useReducer(reducer, initialState);

  function setField<K extends keyof CreateHabitFormState>(
    key: K,
    value: CreateHabitFormState[K]
  ) {
    dispatch({ type: "setField", key, value });
  }

  return {
    state,
    setField,
    toggleDay: (day: number) => dispatch({ type: "toggleDay", day }),
    setSaving: (value: boolean) => dispatch({ type: "setSaving", value }),
    setShowErrors: (value: boolean) => dispatch({ type: "setShowErrors", value }),
    toggleAdvanced: () => dispatch({ type: "toggleAdvanced" }),
  };
}

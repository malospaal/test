export type Habit = {
  id: string;
  title: string;
};

export type DayCompletionMap = Record<string, boolean>;

export type DayPoint = {
  key: string;
  label: string;
  done: boolean;
  isToday?: boolean;
};

export type HistoryItem = {
  key: string;
  label: string;
  done: boolean;
};

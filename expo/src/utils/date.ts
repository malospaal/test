import { DayCompletionMap } from "../types/habit";

const pad = (n: number) => String(n).padStart(2, "0");

export const toISODate = (date: Date) =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

export const formatLongDate = (date: Date) =>
  date.toLocaleDateString(undefined, {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });

export const weekdayShort = (date: Date) =>
  date.toLocaleDateString(undefined, { weekday: "short" });

export const monthYear = (date: Date) =>
  date.toLocaleDateString(undefined, { month: "long", year: "numeric" });

export const daysAgo = (n: number) => {
  const d = new Date();
  d.setHours(0, 0, 0, 0);
  d.setDate(d.getDate() - n);
  return d;
};

export const buildLastNDays = (n: number) => {
  const arr: Date[] = [];
  for (let i = n - 1; i >= 0; i -= 1) arr.push(daysAgo(i));
  return arr;
};

export const countTotalCompletions = (map: DayCompletionMap) =>
  Object.values(map).filter(Boolean).length;

export const calculateStreak = (map: DayCompletionMap) => {
  let streak = 0;
  for (let i = 0; i < 3650; i += 1) {
    const key = toISODate(daysAgo(i));
    if (map[key]) streak += 1;
    else break;
  }
  return streak;
};

export const calculate30DayCompletionRate = (map: DayCompletionMap) => {
  const last30 = buildLastNDays(30).map(toISODate);
  const done = last30.filter((k) => map[k]).length;
  return Math.round((done / 30) * 100);
};

export const buildMonthGrid = (forDate: Date) => {
  const year = forDate.getFullYear();
  const month = forDate.getMonth();

  const first = new Date(year, month, 1);
  const last = new Date(year, month + 1, 0);

  const firstWeekday = (first.getDay() + 6) % 7;
  const totalDays = last.getDate();

  const cells: Array<Date | null> = [];
  for (let i = 0; i < firstWeekday; i += 1) cells.push(null);
  for (let day = 1; day <= totalDays; day += 1) {
    cells.push(new Date(year, month, day));
  }
  while (cells.length % 7 !== 0) cells.push(null);

  const rows: Array<Array<Date | null>> = [];
  for (let i = 0; i < cells.length; i += 7) rows.push(cells.slice(i, i + 7));
  return rows;
};

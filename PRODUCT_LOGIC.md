# Micro Habit — Product Logic (Single Source of Truth)

## 1. Purpose
Этот документ фиксирует текущую бизнес-логику приложения `Micro Habit` и служит обязательной базой для разработки.

Цели:
- сохранить целостность продукта при изменениях;
- избежать расхождений между экранами и слоями (UI, ViewModel, Repository, Notifications);
- ускорить проверку edge-cases и регрессий.

Область действия:
- Android-приложение на Kotlin + Jetpack Compose;
- локальное хранение в `SharedPreferences`;
- локальные уведомления через `AlarmManager`.

## 2. Product Principles
- Основной loop: открыть приложение → отметить прогресс → получить reward → закрыть.
- Tracker = экран действий.
- Habit Screen = экран глубокой аналитики/мотивации.
- Все вычисления прогресса и расписания должны идти из единых правил `HabitRepository`.
- UI не должен дублировать бизнес-логику вычислений.

## 3. Core Domain Model
Источник: `app/src/main/java/com/example/microhabit/data/HabitRepository.kt`.

### 3.1 HabitTask
Поля:
- `id: String`
- `title: String`
- `emoji: String`
- `colorHex: String`
- `trackingType: TrackingType` (`YES_NO`, `COUNT`, `DURATION`)
- `dailyTarget: Int`
- `unitLabel: String`
- `frequency: TaskFrequency` (`DAILY`, `SELECTED_DAYS`, `TIMES_PER_WEEK`)
- `timesPerWeek: Int` (для `TIMES_PER_WEEK`)
- `reminderHour: Int`
- `reminderMinute: Int`
- `reminderEnabled: Boolean`
- `startDate: LocalDate` (обязательная)
- `endDate: LocalDate?` (опциональная, inclusive)
- `customDays: Set<Int>` (1..7, для `SELECTED_DAYS`)
- `isArchived: Boolean`

### 3.2 Lifecycle States
`HabitLifecycleState`:
- `ACTIVE`
- `COMPLETED`
- `ARCHIVED`

Правила:
- `ARCHIVED`: `isArchived = true`.
- `COMPLETED`: `isArchived = false`, `endDate != null`, `today > endDate`.
- `ACTIVE`: не архивирована и не completed.

## 4. Scheduling Rules
Источник: `HabitRepository.isScheduledOn`, `isScheduledByFrequency`.

День считается запланированным, если:
- `date >= startDate`
- `endDate == null` или `date <= endDate`
- и выполняется правило частоты:
  - `DAILY`: всегда true
  - `SELECTED_DAYS`: `date.dayOfWeek.value in customDays`
  - `TIMES_PER_WEEK`: true (недельная цель)

Следствия:
- После `endDate` все дни считаются `not scheduled`.
- До `startDate` день `not scheduled`.

## 5. Tracking Types and Day Completion
Источник: `completionPercentByValue`, `isCompletedByValue`, `setDayValue`.

### 5.1 YES_NO
- Значение дня: `0` или `>=1`.
- Завершено: `value >= 1`.

### 5.2 COUNT
- Завершено по порогу `minimumCompletionPercent` (из Settings).
- `completionPercent = value / target * 100`.
- Может превышать цель (`value > target`).

### 5.3 DURATION
- Завершено по порогу `minimumCompletionPercent` (из Settings).
- `completionPercent = value / target * 100`.
- Завершено, если `completionPercent >= minimumCompletionPercent`.

### 5.4 Partial
- Только для `COUNT` и `DURATION`.
- `value > 0`, но не достигнут completion rule.

## 6. Completion Threshold
Источник: `HabitRepository.getMinimumCompletionPercent`, `setMinimumCompletionPercent`.

- Диапазон хранения: `1..100`.
- Значение по умолчанию: `100`.
- Используется для `COUNT` и `DURATION` как условие completed.
- Применяется единообразно в completion-логике, partial-состоянии, виджетах и аналитике.
- Для `YES_NO` порог не применяется.

## 7. Streak Logic
Источник: `calculateStreak`, `bestStreak`, `streakHistory`.

### 7.1 Daily-like (DAILY / SELECTED_DAYS)
- Идёт назад от `fromDate`.
- Учитывает только scheduled дни.
- Streak увеличивается, если день completed или сохранён streak saver.
- Для сегодняшнего незавершённого scheduled дня streak не обрывается сразу.

### 7.2 TIMES_PER_WEEK
- Считается по неделям.
- Неделя streak-valid, если completions в неделе `>= timesPerWeek`.

### 7.3 Best Streak / History
- Best streak = исторический максимум серии:
  - для daily-like: максимум по дням;
  - для `TIMES_PER_WEEK`: максимум по непрерывным валидным неделям.
- История streak — сегменты завершённых серий.

## 8. Streak Saver
Источник: `MainViewModel` + `HabitRepository`.

Правила:
- Начисление: 1 saver за каждые 7 дней прогресса streak milestone.
- Диалог показывается при пропуске вчерашнего scheduled дня (не для weekly режима).
- Saver можно применить только к последнему пропуску и только один раз на дату.
- Применение saver предотвращает reset streak для этой даты.

## 9. Completion Rate and Analytics
Источник: `completionRate`, `progressForLast30Days`, `last7Days`, `monthlyWeeklyProgress`, `weekdayConsistency`, `totalCompletions`, `totalTrackedValue`, `averageTrackedValue`.

Общее правило:
- Метрики учитывают только scheduled дни (с учётом `startDate` и `endDate`).

Особенности:
- `completionRate(days)`:
  - для `TIMES_PER_WEEK`: target масштабируется по effective days.
  - для остальных: completed/scheduled.
- Для `COUNT` и `DURATION` completed-дни в аналитике определяются через `minimumCompletionPercent`.
- `progressPercentForWidget` возвращает 0 для not scheduled.
- `totalCompletions` считает до `min(today, endDate)` для конечных привычек.

## 10. Habit End Date and Completion Flow
Источники: `HabitRepository`, `MainViewModel`, `MainActivity`.

### 10.1 End Date Behavior
- `endDate` optional.
- `endDate` inclusive.
- Если `today > endDate` и привычка не архивирована → `COMPLETED`.
- Completed habit не архивируется автоматически.

### 10.2 Completed Habit Prompt
После завершения привычки показывается одноразовый prompt:
- Заголовок: «Поздравляем! Привычка завершена.»
- Варианты:
  - продолжить привычку;
  - архивировать;
  - удалить.

Поведение:
- Продолжить привычку:
  - задать новую `endDate` (дата не раньше today и не раньше startDate),
  - или сделать бессрочной (`endDate = null`).
- Архивировать: `isArchived = true`.
- Удалить: удаляется привычка и все связанные данные.

Ограничение показа:
- Хранится маркер `completed_prompt_{taskId}` со значением endDate.
- Prompt показывается пока marker не соответствует текущему `endDate`.

## 11. Subscription and Free Plan Limits
Источник: `MainViewModel.canCreateTask`, `unarchiveTask`.

Текущий лимит Free:
- `FREE_ACTIVE_HABIT_LIMIT = 1`.

Правила:
- Create новой привычки на Free: только если active count < 1.
- Unarchive:
  - если возвращаемая привычка станет `ACTIVE` и active count уже на лимите Free, операция блокируется;
  - для blocked сценария UI открывает paywall.
- Если привычка после unarchive останется `COMPLETED`, лимит активных не нарушается.
- Продление completed привычки (`endDate` update) не считается созданием новой привычки.

Навигация paywall в этом сценарии:
- из Habits при blocked unarchive вызывается `onUpgrade`;
- возврат из paywall остаётся в контексте Habits.

## 12. Screen Responsibilities
Источник: `MainActivity.kt`.

### 12.1 Tracker (Action Screen)
- Показывает только `ACTIVE` привычки.
- Основное действие: completion/value update на выбранную дату.
- Содержит:
  - Hero control (адаптивный по tracking type),
  - streak tiles,
  - 7-day chart,
  - calendar.
- Ссылка `Подробнее →` ведёт в `HabitDetail`.

### 12.2 Habit Detail (Progress Screen)
- Без daily action-кнопок.
- Без mini weekly widget.
- Содержит:
  - большой progress ring,
  - streak + weekly completion,
  - level block,
  - analytics/insights/notes.

### 12.3 Habits List
- Разделы:
  - Active habits,
  - Completed habits,
  - Archived habits.
- Карточка показывает статус (`Active/Completed/Archived`) и reminder state.

### 12.4 Create/Edit Habit
Текущий порядок ключевых полей:
1. Tracking type
2. Frequency
3. Start date (mandatory, prefilled today)
4. End date (toggle optional)
5. Reminders

Advanced settings блок удалён, поля перенесены в основной поток формы.
Start date отображается облегчённой строкой в одну линию:
- label (локализованный `Start date`);
- текущее значение даты;
- действие `Edit`.
На узких экранах строка может корректно переноситься в компактный двухстрочный вариант.
Формат даты должен использовать текущую locale приложения (`localized medium date`), без хардкода формата под один язык.

## 13. Reminder System
Источник: `notifications/HabitReminderScheduler.kt`.

Правила:
- Локальные уведомления через `AlarmManager`.
- Напоминания планируются только если:
  - notifications enabled;
  - task active (`isHabitActive`);
  - reminderEnabled.
- Для задач с `endDate`:
  - после последнего допустимого reminder-времени напоминание отменяется;
  - `shouldRemindOn` учитывает start/end date.
- При изменении/архиве/удалении sync/cancel выполняется через ViewModel.

## 14. Calendar States (Semantic)
Состояния дня:
- completed
- partial
- missed
- not scheduled
- future

Базовое правило:
- `not scheduled` после `endDate` обязательно.
- В календарном UI `FUTURE` имеет визуальный приоритет над `NOT_SCHEDULED` для будущих дат.
- `MISSED` применяется только к scheduled-датам **до** сегодня, которые не были завершены.
- Scheduled-дата = сегодня и не завершена отображается как отдельный today-pending UX слой (не как `MISSED`).

## 15. Data Persistence and Deletion Rules
При удалении привычки удаляются:
- сама запись задачи;
- day progress keys (`done_...`, `value_...`);
- notes;
- streak saver data;
- saved missed dates;
- completed prompt marker.

## 16. Localization
Поддерживаемые языки:
- EN, CS, DE, FR, ES, IT, RU, UK.

Требование:
- новые user-facing строки должны быть добавлены минимум в `AppLocalization.kt` + `UkTranslations.kt` (и не оставаться хардкодом).

## 17. Critical Invariants (Must Not Break)
- Единственный источник расписания: `isScheduledOn`.
- Все экраны/виджеты должны использовать согласованные computed values из Repository/ViewModel.
- Tracker должен работать только с `ACTIVE` привычками.
- Completed привычка не равна archived привычке.
- Метрики и streak не должны учитывать дни вне schedule window (`startDate..endDate`).
- Free limit нельзя обходить через archive/unarchive.

## 18. Implementation Checklist for Any New Feature
Перед изменениями:
1. Проверить влияния на `Tracker`, `HabitDetail`, `Habits`, `Calendar`, `Analytics`.
2. Проверить lifecycle (`ACTIVE/COMPLETED/ARCHIVED`) и schedule window.
3. Проверить reminders (sync/cancel) на все переходы состояния.
4. Проверить free/pro ограничения.
5. Проверить локализацию строк.

После изменений:
1. Прогнать `:app:compileDebugKotlin`.
2. Проверить edge-cases:
   - no habits / one habit / many habits;
   - archived/completed/active mix;
   - past/today/future даты;
   - смена языка/темы;
   - create/edit/delete/unarchive/continue flows.

## 19. Maintenance Rule (Mandatory)
Этот файл должен обновляться при любом изменении:
- бизнес-правил,
- вычислений,
- состояний привычек,
- ключевых UI-flow между экранами,
- подписки/ограничений,
- уведомлений и календарной логики.

Перед началом новой задачи разработчик обязан:
- прочитать `PRODUCT_LOGIC.md`,
- сверить задачу с инвариантами,
- реализовать изменения согласованно во всех затронутых частях приложения.

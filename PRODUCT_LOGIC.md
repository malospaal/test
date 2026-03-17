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
- Каноническое значение по умолчанию в Repository и стартовое UI-состояние должны быть согласованы (`100`).
- Значение хранится в `SharedPreferences` и сохраняется между обычными рестартами приложения и app updates; не гарантируется после `clear data` / uninstall / `delete account`.

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
- Manual override на not scheduled дате сохраняет day value/mark, но не меняет schedule-статус даты; scheduled-only метрики остаются основанными на `isScheduledOn`.

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

Текущая реализованная UI-форма prompt:
- Базовый action-dialog (без отдельного rich-celebration экрана).
- Контент: заголовок + название привычки + действия Continue / Archive / Delete.
- Диалог продолжения открывается отдельным шагом (выбор новой endDate или перевод в бессрочную привычку).

Поведение:
- Продолжить привычку:
  - задать новую `endDate` (дата не раньше today и не раньше startDate),
  - или сделать бессрочной (`endDate = null`).
- Архивировать: `isArchived = true`.
- Удалить: удаляется привычка и все связанные данные.

Ограничение показа:
- Хранится маркер `completed_prompt_{taskId}` со значением endDate.
- Prompt показывается пока marker не соответствует текущему `endDate`.
- На практике prompt появляется при следующем refresh состояния (обычно при следующем входе/возврате в приложение после наступления completed-состояния).

Future scope (не часть текущего канонического поведения):
- richer motivational celebration content;
- отображение дополнительных stats в prompt/celebration UI;
- share CTA / share flow.

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
- Continue completed habit (через completed-prompt) и edit completed habit с изменением `endDate` следуют тому же правилу:
  - это не трактуется как create/unarchive операция;
  - может реактивировать привычку в `ACTIVE` без применения create-style лимитного guard;
  - это текущая намеренная продуктовая семантика, а не accidental bypass create/unarchive flow.

Навигация paywall в этом сценарии:
- из Habits при blocked unarchive вызывается `onUpgrade`;
- закрытие/возврат из paywall возвращает пользователя в сохранённый `previousPage`; для blocked unarchive это контекст Habits.

## 12. Screen Responsibilities
Источник: `MainActivity.kt`.

### 12.1 Tracker (Action Screen)
- Показывает только `ACTIVE` привычки.
- Основное действие: completion/value update на выбранную дату.
- Содержит:
  - компактный heading над selector-строкой с динамическим количеством активных привычек (`N active habits` / локализованный plural, для `0` — отдельный zero-state label);
  - видимый горизонтальный selector активных привычек в виде pill-таблеток (emoji + title) с явным selected state;
  - первый элемент selector-строки = компактный `+` action-tile (Create habit), всегда доступен как отдельный первый элемент;
  - `+` tile является action-only контролом (без текстового label внутри tile), визуально отделён от selected habit состояния;
  - selector имеет правый gradient fade-индикатор, когда список можно прокрутить вправо (`canScrollForward`);
  - под selector отображается одноразовый swipe-hint (`← → ...`) до первого реального скролла пользователем;
  - флаг показа swipe-hint хранится локально (`pref_selector_hint_shown`) и не показывается повторно после первого скролла;
  - единый `HeroCard` для выбранной привычки (адаптивный по tracking type), который объединяет:
    - заголовок (emoji + title),
    - progress ring:
      - для `YES_NO` — недельная стабильность `completedThisWeek / scheduledThisWeek` (ISO неделя, пн-вс);
      - для `COUNT/DURATION` — `value/target`;
    - streak/meta строку (`streak`, `best streak`, `30-day completion`) или zero-streak fallback,
    - compact 7-day mini-track;
  - для `COUNT` / `DURATION` внутри `HeroCard` отображаются:
    - linear progress bar по физическому прогрессу к target (`value/target`);
    - human-readable goal status (`to go` / `goal reached` / `beyond goal`);
    - completion-threshold (`minimumCompletionPercent`) продолжает влиять на completion-state через Repository и не меняет schedule/business семантику;
  - внутри `HeroCard` сохраняется rest-day UX для not scheduled даты (объяснение + optional next scheduled date + explicit `Mark anyway` override action);
  - отдельные standalone блоки streak tiles и 7-day chart на Tracker-экране не отображаются (их контекст перенесён в `HeroCard`);
  - calendar.
- Канонический default для Tracker при normal open/load: selected habit = первый `ACTIVE` habit.
- Переключение привычек выполняется через tiles selector-строки и обновляет текущий Tracker context.
- Внутри `HeroCard` доступно действие `Подробнее →` (нижний правый угол карточки), ведущее в `HabitDetail`.

### 12.2 Habit Detail (Progress Screen)
- Без daily action-кнопок.
- Без mini weekly widget.
- Без календаря.
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
Create/Edit открывается отдельным dialog-flow (`TaskEditorDialog`) и не является частью `HabitDetail`.

Визуальный top-to-bottom порядок формы может включать служебные блоки перед ядром конфигурации (например `Basic setup`, `Color`).

Порядок **ключевого блока конфигурации привычки**:
1. Tracking type
2. Frequency
3. Start date (mandatory, prefilled today)
4. End date (toggle optional)
5. Reminders

Дополнительно по поведению:
- Start date обязательна и по умолчанию = today при создании.
- End date опциональна и управляется toggle (`OFF` = `null`, `ON` = дата доступна для выбора).
- Reminders опциональны; запрос notification permission в Create/Edit инициируется только при сохранении, если reminder включён.

Advanced settings блок удалён, поля перенесены в основной поток формы.
Start date отображается облегчённой строкой в одну линию:
- label (локализованный `Start date`);
- текущее значение даты;
- действие `Edit`.
На узких экранах строка может корректно переноситься в компактный двухстрочный вариант.
Формат даты должен использовать текущую locale приложения (`localized medium date`), без хардкода формата под один язык.

### 12.5 Onboarding
- Onboarding реализован отдельным wizard-flow и активируется для нового пользователя (когда onboarding не завершён и привычек ещё нет).
- Структура шагов: `WELCOME` → `CATEGORY` → `TEMPLATE` → `SETUP` → `READY`.
- В onboarding присутствуют выбор категории и выбор шаблона.
- В `SETUP` onboarding доступны только 2 tracking type:
  - `YES_NO` (`Do once`);
  - `DURATION` (`Do N minutes`).
- `COUNT` в onboarding не показывается.
- Напоминания в onboarding опциональны; notification permission запрашивается только при создании привычки, если reminder включён.

### 12.6 Calendar (Global Overview Screen)
- `Calendar` — канонический global overview экран, отдельный от Tracker/Habit Detail.
- Базовый scope календаря по умолчанию:
  - включает `ACTIVE` и `COMPLETED` привычки;
  - исключает `ARCHIVED` привычки.
- В календаре поддерживаются фильтры:
  - `All habits` (default);
  - фильтр на одну конкретную привычку.
- Heatmap-семантика:
  - GitHub-style сетка по дням месяца;
  - интенсивность дня строится по `completed habit count` за дату (а не по completion ratio);
  - scheduled-контекст вычисляется по `isScheduledOn` и используется для корректной интерпретации дня.
- Tap по дню открывает breakdown на той же странице:
  - дата;
  - summary `completed / scheduled`;
  - список привычек из текущего filter-scope со статусом за выбранную дату (`completed`, `partial`, `missed`, `not scheduled`, `today pending`/`future`).
- Calendar не заменяет Tracker:
  - Tracker остаётся action-screen для одной выбранной `ACTIVE` привычки;
  - Habit Detail остаётся deep analytics экраном одной привычки.

### 12.7 Primary Navigation Shell
- Основная (primary) навигация реализована через нижнюю horizontal bottom bar (icons-only).
- Bottom bar содержит ровно 5 primary destination:
  - `Tracker`
  - `Habits`
  - `Analytics`
  - `Calendar`
  - `Account`
- Текстовые label в bottom bar не отображаются; выбранная вкладка определяется через визуальное выделение icon + индикатор.
- Drawer / burger menu не используется как primary navigation паттерн для основных экранов.
- Top app bar использует контекстные действия экрана (например, `Add` в Habits, `Today` в Calendar), без burger-кнопки primary навигации.
- На всех primary экранах (`Tracker`, `Habits`, `Analytics`, `Calendar`, `Account`) доступен глобальный shortcut в `Settings` через иконку `gear` в top-right.

### 12.8 Account Screen
- `Account` — primary destination для профиля и подписки (profile/plan screen), а не основной экран конфигурации приложения.
- В Account используются отдельные секции:
  - текущий план (`Plan`) + действие `Manage subscription`;
  - entry `App settings` (переход в `Settings`);
  - `Support` (`Help center`, `Contact support`);
  - `Data` (`Export data`, `Reset progress`, `Delete account`).
- `Delete account` в Account визуально оформляется как destructive action.
- Theme / Language / Notifications не настраиваются напрямую на Account-экране и вынесены в `Settings`.

### 12.9 Settings Screen
- `Settings` — отдельный экран системных настроек приложения (application configuration).
- Группы настроек:
  - `Appearance` (Theme),
  - `Language`,
  - `Notifications` (enable reminders + reminder time),
  - `Tracking` (completion threshold),
  - `Subscription` (manage subscription),
  - `Data` (export data, reset progress),
  - `Danger zone` (delete account).

## 13. Reminder System
Источник: `notifications/HabitReminderScheduler.kt`.

Правила:
- Локальные уведомления через `AlarmManager`.
- Напоминания планируются только если:
  - notifications enabled;
  - task active (`isHabitActive`);
  - reminderEnabled.
- Permission / delivery gate:
  - перед выполнением reminder-действий (включение reminders в Settings, сохранение Create/Edit с reminder, создание из onboarding с reminder) UI проверяет доступность доставки (`canDeliverNotifications`) и runtime permission (`POST_NOTIFICATIONS`, где применимо);
  - если доставка уже доступна, действие выполняется сразу.
- Denied / blocked flow:
  - при отказе в permission или системной блокировке уведомлений показывается диалог с предложением перейти в Settings (`Open Settings`);
  - используется redirect в notification settings, с fallback в app details settings.
- Resume / retry:
  - при возврате из Settings и восстановлении доступности доставки pending reminder-action автоматически повторяется.
- Consistency safeguard:
  - если app state хранит `notifications enabled = true`, но OS больше не разрешает доставку (`canDeliverNotifications = false`), глобальный флаг уведомлений сбрасывается в `false`.
- Для задач с `endDate`:
  - после последнего допустимого reminder-времени напоминание отменяется;
  - `shouldRemindOn` учитывает start/end date.
- При изменении/архиве/удалении sync/cancel выполняется через ViewModel.
- UI:
  - в Habits list карточки показывают статус напоминания (`Reminder: time` / `Reminder off`);
  - отображение времени напоминания использует формат устройства (12h/24h).

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
- Manual override на not scheduled дате не переводит дату в scheduled и не меняет семантическое состояние schedule-window.
- Для global heatmap различаются:
  - дни без scheduled привычек в текущем filter-scope;
  - дни со scheduled привычками, но без completed.

## 15. Data Persistence and Deletion Rules
### 15.1 Primary Runtime Storage
- Runtime-данные приложения хранятся локально в `SharedPreferences` (файл `habit_prefs`).
- Привычки хранятся в ключе `tasks_json` как JSON-массив задач.
- Прогресс по дням хранится ключами `done_...` и `value_...`.
- Связанные данные привычки (notes, streak saver, saved missed dates, completed prompt marker) хранятся отдельными pref-ключами с префиксами.
- Пользовательские настройки (plan, theme, language, notifications, default reminder, onboarding, minimum completion percent, selected task) также хранятся в тех же `SharedPreferences`.

### 15.2 Derived Analytics
- Аналитика и статистики не хранятся отдельной БД/таблицей.
- Метрики вычисляются on-demand из сырых сохранённых данных (задачи + day values/completions).

### 15.3 Export Storage
- Export данных формирует отдельный JSON-файл во внутреннем `filesDir` приложения.
- Export-файл хранится отдельно от runtime `SharedPreferences` и не заменяет основное runtime-хранилище.

### 15.4 Update Safety (Current Scope)
- При обычном in-place app update локальные `SharedPreferences` обычно сохраняются; соответственно, привычки, настройки и прогресс ожидаемо сохраняются.
- После package update напоминания пересинхронизируются через обработку `MY_PACKAGE_REPLACED` и при старте приложения.
- Гарантия в этом разделе относится к обычным app update-сценариям и не распространяется на `clear data`, uninstall или ручное удаление данных приложения.

### 15.5 Backup / Restore Reality
- В манифесте включено `android:allowBackup="true"`.
- Явные правила `fullBackupContent` / `dataExtractionRules` в текущей реализации не заданы.
- При backup/restore форма восстановленных локальных данных зависит от системного механизма Android; точный контроль restore-семантики кодом приложения сейчас ограничен.

### 15.6 Migration Strategy (Current Reality)
- Формального versioned migration framework (со schema version key и пошаговыми миграциями) в текущей реализации нет.
- Совместимость обеспечивается ad-hoc на уровне парсинга/нормализации в `HabitRepository` (например, legacy frequency значения, lenient date parsing, sane defaults).
- При невалидном/непарсируемом `tasks_json` чтение задач может fallback-нуться к пустому списку.
- Неизвестные поля task-объектов не гарантированно сохраняются после следующей перезаписи `tasks_json` текущим сериализатором.

### 15.7 Deletion Rules
При удалении привычки удаляются:
- сама запись задачи;
- day progress keys (`done_...`, `value_...`);
- notes;
- streak saver data;
- saved missed dates;
- completed prompt marker.

## 16. Localization
Языки в модели приложения (`AppLanguage`):
- EN, CS, DE, FR, ES, IT, RU, UK.

Текущий scope выбора в Settings (language selector):
- EN, RU, UK, DE, CS.
- Selector отображает названия языков в native форме (`languageNativeLabel`).

Покрытие переводов:
- наличие языка в модели не означает 100% покрытие всех строк;
- для части локалей (включая CS) покрытие может быть частичным, и отсутствующие ключи fallback-ятся в source string.

Требование:
- новые user-facing строки должны быть добавлены минимум в `AppLocalization.kt` + `UkTranslations.kt` (и не оставаться хардкодом).
- это правило относится и к user-visible notification strings (включая channel name/description).

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

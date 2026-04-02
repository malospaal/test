# CODEBASE_MAP

## 1. File index

## app/src/main/java/com/example/microhabit/ui/app/AppRoot.kt
Package: com.example.microhabit.ui.app  
Lines: ~928  
Responsibility: Root app composition and navigation flow (routing between all top-level screens, dialogs, paywall/account/template flows).

## app/src/main/java/com/example/microhabit/ui/app/BottomNav.kt
Package: com.example.microhabit.ui.app  
Lines: ~141  
Responsibility: Primary bottom navigation UI and nav item rendering.

## app/src/main/java/com/example/microhabit/ui/tracker/TrackerScreen.kt
Package: com.example.microhabit.ui.tracker  
Lines: ~420  
Responsibility: Tracker screen shell, selector, hero+calendar placement, streak overlay handling.

## app/src/main/java/com/example/microhabit/ui/tracker/HeroCard.kt
Package: com.example.microhabit.ui.tracker  
Lines: ~991  
Responsibility: Main tracker hero card behavior and state-rich interaction logic.

## app/src/main/java/com/example/microhabit/ui/tracker/HeroCardStats.kt
Package: com.example.microhabit.ui.tracker  
Lines: ~424  
Responsibility: Hero stats visuals (ring, stat tiles, 7-day chart bars).

## app/src/main/java/com/example/microhabit/ui/tracker/HeroCardInputs.kt
Package: com.example.microhabit.ui.tracker  
Lines: ~190  
Responsibility: Hero input controls (details/edit buttons, value numpad).

## app/src/main/java/com/example/microhabit/ui/tracker/HeroCardWeek.kt
Package: com.example.microhabit.ui.tracker  
Lines: ~218  
Responsibility: Hero mini-week dots and embedded calendar card.

## app/src/main/java/com/example/microhabit/ui/habits/HabitsScreen.kt
Package: com.example.microhabit.ui.habits  
Lines: ~272  
Responsibility: Habits list/edit/reorder screen and habit list metadata formatter.

## app/src/main/java/com/example/microhabit/ui/habits/TaskEditorDialog.kt
Package: com.example.microhabit.ui.habits  
Lines: ~604  
Responsibility: Full habit create/edit dialog flow and advanced task editor UI.

## app/src/main/java/com/example/microhabit/ui/habitdetail/HabitDetailScreen.kt
Package: com.example.microhabit.ui.habitdetail  
Lines: ~544  
Responsibility: Habit details page with level, stats, history, insights, notes.

## app/src/main/java/com/example/microhabit/ui/calendar/CalendarScreen.kt
Package: com.example.microhabit.ui.calendar  
Lines: ~282  
Responsibility: Calendar screen, global heat cells, calendar state helpers.

## app/src/main/java/com/example/microhabit/ui/analytics/AnalyticsPage.kt
Package: com.example.microhabit.ui.analytics  
Lines: ~184  
Responsibility: Analytics destination wrapper and analytics screen orchestration.

## app/src/main/java/com/example/microhabit/ui/account/AccountScreen.kt
Package: com.example.microhabit.ui.account  
Lines: ~637  
Responsibility: Account destination including premium card and account action groups.

## app/src/main/java/com/example/microhabit/ui/subscription/ManageSubscriptionScreen.kt
Package: com.example.microhabit.ui.subscription  
Lines: ~595  
Responsibility: Manage-subscription UI, plan switch rows, milestone preview, cancel sheet.

## app/src/main/java/com/example/microhabit/ui/paywall/PaywallScreen.kt
Package: com.example.microhabit.ui.paywall  
Lines: ~267  
Responsibility: Paywall destination, pricing cards, plan labels, premium feature rows.

## app/src/main/java/com/example/microhabit/ui/settings/SettingsScreen.kt
Package: com.example.microhabit.ui.settings  
Lines: ~376  
Responsibility: Settings page, section cards, and settings bottom-sheet selectors.

## app/src/main/java/com/example/microhabit/ui/onboarding/OnboardingWizard.kt
Package: com.example.microhabit.ui.onboarding  
Lines: ~673  
Responsibility: Full onboarding wizard and its header/progress/mini-calendar components.

## app/src/main/java/com/example/microhabit/ui/onboarding/TemplateFlow.kt
Package: com.example.microhabit.ui.onboarding  
Lines: ~390  
Responsibility: Category/template selection flow components for onboarding/template picker.

## app/src/main/java/com/example/microhabit/ui/onboarding/TemplateConfirm.kt
Package: com.example.microhabit.ui.onboarding  
Lines: ~472  
Responsibility: Template confirm step and parameter/frequency editing controls.

## app/src/main/java/com/example/microhabit/ui/shared/StreakOverlays.kt
Package: com.example.microhabit.ui.shared  
Lines: ~295  
Responsibility: Shared streak reward/milestone overlays and milestone utility.

## app/src/main/java/com/example/microhabit/ui/shared/SelectorsAndCards.kt
Package: com.example.microhabit.ui.shared  
Lines: ~568  
Responsibility: Shared selectors/chips/glass card components used across screens.

## app/src/main/java/com/example/microhabit/ui/shared/DateTimeHelpers.kt
Package: com.example.microhabit.ui.shared  
Lines: ~172  
Responsibility: Shared calendar header and date/time/settings helper utilities.

## app/src/main/java/com/example/microhabit/domain/subscription/BillingMapping.kt
Package: com.example.microhabit.domain.subscription  
Lines: ~20  
Responsibility: Billing-cycle mapping helpers for product ids and access source.

## app/src/main/java/com/example/microhabit/AppContracts.kt
Package: com.example.microhabit  
Lines: ~145  
Responsibility: App-wide enums/constants/contracts extracted from MainActivity.


## 2. Function -> file map

| Function | File | Notes |
|---|---|---|
| billingProductIdFor | domain/subscription/BillingMapping.kt | Billing cycle -> product id |
| proAccessSourceFor | domain/subscription/BillingMapping.kt | Billing cycle -> ProAccessSource |
| SettingsPage | ui/settings/SettingsScreen.kt | Settings destination |
| SelectionBottomSheet | ui/settings/SettingsScreen.kt | Generic settings option bottom sheet |
| CompletionThresholdBottomSheet | ui/settings/SettingsScreen.kt | Completion threshold slider sheet |
| DeleteAccountBottomSheet | ui/settings/SettingsScreen.kt | Destructive account-delete confirmation sheet |
| themeLabel | ui/settings/SettingsScreen.kt | Theme mode label helper |
| AnalyticsPage | ui/analytics/AnalyticsPage.kt | Analytics destination wrapper |
| PaywallPage | ui/paywall/PaywallScreen.kt | Paywall destination |
| PaywallPlanCard | ui/paywall/PaywallScreen.kt | Plan option card |
| planPriceLabel | ui/paywall/PaywallScreen.kt | Plan pricing text |
| PremiumFeatureRow | ui/paywall/PaywallScreen.kt | Feature bullet row |
| ManageSubscriptionScreen | ui/subscription/ManageSubscriptionScreen.kt | Manage subscription destination |
| ManageSubscriptionCard | ui/subscription/ManageSubscriptionScreen.kt | Active plan card |
| ManagePlanSwitchRow | ui/subscription/ManageSubscriptionScreen.kt | Plan switch card row |
| MilestonePreviewSheet | ui/subscription/ManageSubscriptionScreen.kt | Milestone preview bottom sheet |
| CancelSubscriptionSheet | ui/subscription/ManageSubscriptionScreen.kt | Cancel flow sheet |
| CancelSheetLossRow | ui/subscription/ManageSubscriptionScreen.kt | Loss-item row |
| PremiumPlan.displayName | ui/subscription/ManageSubscriptionScreen.kt | Extension label |
| managePlanName | ui/subscription/ManageSubscriptionScreen.kt | Plan name helper |
| managePlanPriceSummary | ui/subscription/ManageSubscriptionScreen.kt | Plan price helper |
| managePlanCtaLabel | ui/subscription/ManageSubscriptionScreen.kt | CTA label helper |
| managePlanHintText | ui/subscription/ManageSubscriptionScreen.kt | Hint text helper |
| AccountPage | ui/account/AccountScreen.kt | Account destination |
| AccountSectionLabel | ui/account/AccountScreen.kt | Section header |
| AccountActionCard | ui/account/AccountScreen.kt | Action group card |
| AccountActionRow | ui/account/AccountScreen.kt | Action row |
| CalendarScreen | ui/calendar/CalendarScreen.kt | Calendar destination |
| GlobalCalendarHeatCell | ui/calendar/CalendarScreen.kt | Heatmap cell |
| dayStateFor | ui/calendar/CalendarScreen.kt | Day state resolver |
| statusLabel | ui/calendar/CalendarScreen.kt | State label helper |
| localizedMonthYear | ui/calendar/CalendarScreen.kt | Month title helper |
| monthGrid | ui/calendar/CalendarScreen.kt | Calendar matrix helper |
| HabitDetailPage | ui/habitdetail/HabitDetailScreen.kt | Habit detail destination |
| levelInfoForStreak | ui/habitdetail/HabitDetailScreen.kt | Level thresholds helper |
| HabitDepthHero | ui/habitdetail/HabitDetailScreen.kt | Detail hero block |
| HabitLevelProgressCard | ui/habitdetail/HabitDetailScreen.kt | Level progress card |
| HabitDepthStats | ui/habitdetail/HabitDetailScreen.kt | Depth stats section |
| HabitStreakHistoryCard | ui/habitdetail/HabitDetailScreen.kt | Streak history section |
| HabitInsightsCard | ui/habitdetail/HabitDetailScreen.kt | Insights section |
| HabitNotesCard | ui/habitdetail/HabitDetailScreen.kt | Notes section |
| streakMilestoneTier | ui/shared/StreakOverlays.kt | Milestone config lookup |
| StreakRewardOverlay | ui/shared/StreakOverlays.kt | Shared streak overlay |
| StreakMilestoneScreen | ui/shared/StreakOverlays.kt | Milestone celebration screen |
| isStreakMilestone | ui/shared/StreakOverlays.kt | Milestone check helper |
| TaskSelector | ui/shared/SelectorsAndCards.kt | Shared task dropdown selector |
| HabitSelectorRow | ui/shared/SelectorsAndCards.kt | Shared habit pill row |
| HabitPill | ui/shared/SelectorsAndCards.kt | Habit chip |
| AddHabitTile | ui/shared/SelectorsAndCards.kt | Add-habit tile |
| FadeOverlay | ui/shared/SelectorsAndCards.kt | Selector edge fade |
| AllHabitsPill | ui/shared/SelectorsAndCards.kt | All-habits chip |
| SelectChip | ui/shared/SelectorsAndCards.kt | Generic selectable chip |
| GlassCard | ui/shared/SelectorsAndCards.kt | Shared elevated card |
| CalendarHeaderRow | ui/shared/DateTimeHelpers.kt | Shared calendar controls |
| showThemedTimePicker | ui/shared/DateTimeHelpers.kt | Theme-aware time picker |
| showThemedDatePicker | ui/shared/DateTimeHelpers.kt | Theme-aware date picker |
| openNotificationOrAppSettings | ui/shared/DateTimeHelpers.kt | Open settings helper |
| Context.findActivity | ui/shared/DateTimeHelpers.kt | Context->Activity resolver |
| formatTimeForDevice | ui/shared/DateTimeHelpers.kt | Device locale time formatter |
| HabitsPage | ui/habits/HabitsScreen.kt | Habits destination |
| buildHabitsMetaString | ui/habits/HabitsScreen.kt | Habit meta text builder |
| TaskEditorDialog | ui/habits/TaskEditorDialog.kt | Habit editor dialog |
| OnboardingCard | ui/onboarding/OnboardingWizard.kt | Empty-state onboarding card |
| OnboardingWizard | ui/onboarding/OnboardingWizard.kt | Full onboarding wizard |
| OnboardingHeader | ui/onboarding/OnboardingWizard.kt | Wizard header |
| OnboardingProgressDots | ui/onboarding/OnboardingWizard.kt | Wizard progress dots |
| OnboardingMiniCalendar | ui/onboarding/OnboardingWizard.kt | Mini calendar animation |
| OnboardingCategoryCard | ui/onboarding/TemplateFlow.kt | Category selector tile |
| OnboardingTemplateCard | ui/onboarding/TemplateFlow.kt | Template selector tile |
| HabitCategoryScreen | ui/onboarding/TemplateFlow.kt | Category picker screen |
| CategoryTile | ui/onboarding/TemplateFlow.kt | Category tile |
| templateCountLabel | ui/onboarding/TemplateFlow.kt | Template count pluralization |
| HabitTemplateScreen | ui/onboarding/TemplateFlow.kt | Template list screen |
| HabitTemplateConfirmScreen | ui/onboarding/TemplateConfirm.kt | Template confirm screen |
| TimesPerWeekStepper | ui/onboarding/TemplateConfirm.kt | Weekly frequency stepper |
| TemplateFrequencyOption | ui/onboarding/TemplateConfirm.kt | Frequency option row |
| TemplateParamRow | ui/onboarding/TemplateConfirm.kt | Confirm param row |
| templateTrackingTypeLabel | ui/onboarding/TemplateConfirm.kt | Tracking type label |
| templateMetaLabel | ui/onboarding/TemplateConfirm.kt | Template meta label |
| templateFrequencyLabel | ui/onboarding/TemplateConfirm.kt | Frequency label |
| templateFrequencyMetaLabel | ui/onboarding/TemplateConfirm.kt | Frequency meta label |
| selectedDaysShortLabel | ui/onboarding/TemplateConfirm.kt | Days summary label |
| TrackerPage | ui/tracker/TrackerScreen.kt | Tracker destination shell |
| TaskControlsRow | ui/tracker/TrackerScreen.kt | Hero controls row |
| TaskControlButton | ui/tracker/TrackerScreen.kt | Hero control button |
| formatHeroDate | ui/tracker/TrackerScreen.kt | Hero date label helper |
| activeHabitsCountLabel | ui/tracker/TrackerScreen.kt | Active habits pluralization |
| ProgressRing | ui/tracker/HeroCardStats.kt | Hero weekly ring |
| TrackerStreakRow | ui/tracker/HeroCardStats.kt | Streak tile row |
| StatsRow | ui/tracker/HeroCardStats.kt | Stats grid |
| StatTile | ui/tracker/HeroCardStats.kt | Stat tile |
| SevenDayChart | ui/tracker/HeroCardStats.kt | 7-day chart |
| DayBar | ui/tracker/HeroCardStats.kt | Chart bar cell |
| HeroDetailsButton | ui/tracker/HeroCardInputs.kt | Hero details CTA |
| EditValueButton | ui/tracker/HeroCardInputs.kt | Hero edit value CTA |
| ValueNumpad | ui/tracker/HeroCardInputs.kt | Numeric keypad |
| HeroMiniWeekRow | ui/tracker/HeroCardWeek.kt | 7-day dots row |
| DayDot | ui/tracker/HeroCardWeek.kt | Single day dot |
| CalendarCard | ui/tracker/HeroCardWeek.kt | Tracker calendar card |
| HeroCard | ui/tracker/HeroCard.kt | Main tracker hero card |
| pageTitle | ui/app/AppRoot.kt | App-page title mapping |
| HabitApp | ui/app/AppRoot.kt | Root app composition |
| PrimaryBottomNavigationBar | ui/app/BottomNav.kt | Bottom nav container |
| BottomNavigationIconItem | ui/app/BottomNav.kt | Bottom nav icon renderer |


## 3. Shared component registry

| Function | File | Used by |
|---|---|---|
| GlassCard | ui/shared/SelectorsAndCards.kt | Tracker, HabitDetail, Calendar, Analytics, Habits, Onboarding |
| HabitSelectorRow | ui/shared/SelectorsAndCards.kt | Tracker, Calendar, Analytics |
| TaskSelector | ui/shared/SelectorsAndCards.kt | Analytics (and shared selector flows) |
| SelectChip | ui/shared/SelectorsAndCards.kt | OnboardingWizard |
| TimesPerWeekStepper | ui/onboarding/TemplateConfirm.kt | TemplateConfirm, TaskEditorDialog |
| CalendarHeaderRow | ui/shared/DateTimeHelpers.kt | Tracker CalendarCard, Calendar screen |
| showThemedTimePicker | ui/shared/DateTimeHelpers.kt | AppRoot, TaskEditorDialog, OnboardingWizard |
| showThemedDatePicker | ui/shared/DateTimeHelpers.kt | AppRoot, TaskEditorDialog |
| openNotificationOrAppSettings | ui/shared/DateTimeHelpers.kt | AppRoot |
| formatTimeForDevice | ui/shared/DateTimeHelpers.kt | AppRoot, TaskEditorDialog, Onboarding, selectors/meta helpers |
| StreakRewardOverlay | ui/shared/StreakOverlays.kt | Tracker, HabitDetail, Onboarding |
| StreakMilestoneScreen | ui/shared/StreakOverlays.kt | AppRoot, ManageSubscription |
| isStreakMilestone | ui/shared/StreakOverlays.kt | Tracker, HabitDetail |


## 4. Screen -> file map

| Screen / destination | Primary file | Supporting files |
|---|---|---|
| Tracker | ui/tracker/TrackerScreen.kt | HeroCard.kt, HeroCardStats.kt, HeroCardInputs.kt, HeroCardWeek.kt |
| Habit detail | ui/habitdetail/HabitDetailScreen.kt | ui/shared/StreakOverlays.kt, ui/shared/SelectorsAndCards.kt |
| Habits | ui/habits/HabitsScreen.kt | ui/habits/TaskEditorDialog.kt, ui/shared/SelectorsAndCards.kt |
| Calendar | ui/calendar/CalendarScreen.kt | ui/shared/DateTimeHelpers.kt, ui/shared/SelectorsAndCards.kt |
| Analytics | ui/analytics/AnalyticsPage.kt | ui/analytics/AnalyticsScreen.kt, ui/shared/SelectorsAndCards.kt |
| Account | ui/account/AccountScreen.kt | ui/subscription/ManageSubscriptionScreen.kt |
| Manage Subscription | ui/subscription/ManageSubscriptionScreen.kt | ui/shared/StreakOverlays.kt |
| Paywall | ui/paywall/PaywallScreen.kt | — |
| Settings | ui/settings/SettingsScreen.kt | — |
| Onboarding wizard | ui/onboarding/OnboardingWizard.kt | ui/onboarding/TemplateFlow.kt, ui/onboarding/TemplateConfirm.kt |
| App root/navigation | ui/app/AppRoot.kt | ui/app/BottomNav.kt |


## 5. Key invariants (from PRODUCT_LOGIC.md)
- Single source of schedule truth: HabitRepository.isScheduledOn
- Tracker shows ACTIVE habits only
- Free limit: 3 active habits (FREE_ACTIVE_HABIT_LIMIT)
- Widget entitlement derived from SubscriptionState, not legacy pro_access_source
- Milestone shown once per (habitId, days) pair — tracked in SharedPreferences


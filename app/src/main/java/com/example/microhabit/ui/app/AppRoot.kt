package com.example.microhabit.ui.app

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.microhabit.*
import com.example.microhabit.R
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.ProAccessSource
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.domain.subscription.billingProductIdFor
import com.example.microhabit.domain.subscription.proAccessSourceFor
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.i18n.translate
import com.example.microhabit.notifications.HabitReminderScheduler
import com.example.microhabit.ui.onboarding.HabitCategoryScreen
import com.example.microhabit.ui.onboarding.HabitTemplateConfirmScreen
import com.example.microhabit.ui.onboarding.HabitTemplateScreen
import com.example.microhabit.ui.onboarding.OnboardingWizard
import com.example.microhabit.ui.habits.HabitsPage
import com.example.microhabit.ui.habits.TaskEditorDialog
import com.example.microhabit.ui.paywall.PaywallPage
import com.example.microhabit.ui.account.AccountPage
import com.example.microhabit.ui.analytics.AnalyticsPage
import com.example.microhabit.ui.analytics.AnalyticsScreen
import com.example.microhabit.ui.calendar.CalendarScreen
import com.example.microhabit.ui.create.CreateHabitTemplateCatalog
import com.example.microhabit.ui.create.TemplateCategory
import com.example.microhabit.ui.create.TemplateConfirmDraft
import com.example.microhabit.ui.habitdetail.HabitDetailPage
import com.example.microhabit.ui.settings.SettingsPage
import com.example.microhabit.ui.subscription.ManageSubscriptionScreen
import com.example.microhabit.ui.tracker.TrackerPage
import com.example.microhabit.ui.shared.StreakMilestoneScreen
import com.example.microhabit.ui.shared.findActivity
import com.example.microhabit.ui.shared.openNotificationOrAppSettings
import com.example.microhabit.ui.shared.showThemedDatePicker
import com.example.microhabit.ui.shared.showThemedTimePicker
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import kotlinx.coroutines.delay
@Composable
private fun pageTitle(page: AppPage): String {
    return when (page) {
        AppPage.TRACKER -> t("Tracker")
        AppPage.HABIT_DETAIL -> t("Habit details")
        AppPage.HABITS -> t("Habits")
        AppPage.ANALYTICS -> t("Analytics")
        AppPage.CALENDAR -> t("Calendar")
        AppPage.PAYWALL -> t("Premium")
        AppPage.ACCOUNT -> t("Account")
        AppPage.MANAGE_SUBSCRIPTION -> t("Manage subscription")
        AppPage.SETTINGS -> t("Settings")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitApp(state: HabitUiState, vm: MainViewModel) {
    var page by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    var previousPage by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    var settingsReturnPage by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    var trackerScrollToTopSignal by rememberSaveable { mutableStateOf(0) }
    var habitsScrollToTopSignal by rememberSaveable { mutableStateOf(0) }
    var selectedBilling by rememberSaveable { mutableStateOf(BillingCycle.YEARLY) }
    var paywallTrigger by rememberSaveable { mutableStateOf(PaywallTrigger.DEFAULT) }
    var showNotificationsBlockedDialog by rememberSaveable { mutableStateOf(false) }
    var pendingPermissionAction by remember { mutableStateOf<NotificationPermissionAction?>(null) }
    var pendingSettingsAction by remember { mutableStateOf<NotificationPermissionAction?>(null) }
    var highlightCompletionButton by rememberSaveable { mutableStateOf(false) }
    var showOnboardingWizard by rememberSaveable { mutableStateOf(false) }
    var onboardingDismissedSession by rememberSaveable { mutableStateOf(false) }
    var showContinueCompletedHabitDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteCompletedHabitConfirm by rememberSaveable { mutableStateOf(false) }
    var showTemplatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedTemplateId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTemplateCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var templateDraft by remember { mutableStateOf<TemplateConfirmDraft?>(null) }
    var pendingTemplateReminderPicker by remember { mutableStateOf<(() -> Unit)?>(null) }
    var habitsEditMode by rememberSaveable { mutableStateOf(false) }
    var activeMilestoneEvent by remember { mutableStateOf<StreakMilestoneEvent?>(null) }
    var milestoneQueueRefreshSignal by remember { mutableStateOf(0) }
    val semantic = AppTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val language = state.language
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val datePickerTheme = R.style.ThemeOverlay_MicroHabit_DatePicker
    val pickerActionColor = semantic.primary.toArgb()
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val runActionWithNotifications = { action: NotificationPermissionAction ->
        when (action) {
            NotificationPermissionAction.SAVE_EDITOR_REMINDER -> vm.saveEditorWithNotificationsEnabled()
            NotificationPermissionAction.PICK_TEMPLATE_REMINDER_TIME -> {
                pendingTemplateReminderPicker?.invoke()
                pendingTemplateReminderPicker = null
            }
        }
    }
    val showNotificationsBlocked = { action: NotificationPermissionAction? ->
        pendingPermissionAction = null
        pendingSettingsAction = action
        if (action == NotificationPermissionAction.PICK_TEMPLATE_REMINDER_TIME) {
            pendingTemplateReminderPicker = null
        }
        showNotificationsBlockedDialog = true
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val action = pendingPermissionAction
        pendingPermissionAction = null
        if (granted && HabitReminderScheduler.canDeliverNotifications(context)) {
            pendingSettingsAction = null
            if (action != null) runActionWithNotifications(action)
        } else {
            showNotificationsBlocked(action)
        }
    }

    val ensureNotificationPermissionAndRun = { action: NotificationPermissionAction ->
        when {
            HabitReminderScheduler.canDeliverNotifications(context) -> runActionWithNotifications(action)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !HabitReminderScheduler.hasRuntimeNotificationPermission(context) -> {
                pendingPermissionAction = action
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                showNotificationsBlocked(action)
            }
        }
    }

    val openSettingsPage = { from: AppPage ->
        settingsReturnPage = from
        previousPage = from
        page = AppPage.SETTINGS
    }
    val openPaywallFromCurrentPage: (PaywallTrigger) -> Unit = { trigger ->
        previousPage = page
        paywallTrigger = trigger
        page = AppPage.PAYWALL
    }
    val openTemplatePicker = {
        selectedTemplateCategory = null
        selectedTemplateId = null
        templateDraft = null
        showTemplatePicker = true
    }

    val pullPendingMilestoneIfIdle = {
        if (activeMilestoneEvent == null) {
            activeMilestoneEvent = StreakMilestoneQueue.peekPending(context)
        }
    }

    LaunchedEffect(state.isLoaded, state.tasks, state.allTasks) {
        val activity = context.findActivity() ?: return@LaunchedEffect
        val paywallTriggerExtra = activity.intent?.getStringExtra(EXTRA_OPEN_PAYWALL_TRIGGER)
        if (paywallTriggerExtra == EXTRA_PAYWALL_TRIGGER_WIDGETS) {
            activity.intent?.removeExtra(EXTRA_OPEN_PAYWALL_TRIGGER)
            previousPage = AppPage.TRACKER
            paywallTrigger = PaywallTrigger.WIDGETS
            page = AppPage.PAYWALL
            return@LaunchedEffect
        }
        val openHabitId = activity.intent?.getStringExtra(HabitReminderScheduler.EXTRA_OPEN_HABIT_ID)
            ?: return@LaunchedEffect
        activity.intent?.removeExtra(HabitReminderScheduler.EXTRA_OPEN_HABIT_ID)
        if (state.tasks.any { it.id == openHabitId }) {
            vm.selectTask(openHabitId)
        }
        previousPage = AppPage.TRACKER
        page = AppPage.TRACKER
    }

    DisposableEffect(lifecycleOwner, pendingSettingsAction) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.onHostResumed()
                milestoneQueueRefreshSignal += 1
            }
            if (
                event == Lifecycle.Event.ON_RESUME &&
                pendingSettingsAction != null &&
                HabitReminderScheduler.canDeliverNotifications(context)
            ) {
                val action = pendingSettingsAction
                pendingSettingsAction = null
                showNotificationsBlockedDialog = false
                if (action != null) runActionWithNotifications(action)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.isLoaded, state.onboardingCompleted, state.tasks.size, onboardingDismissedSession) {
        if (
            state.isLoaded &&
            !state.onboardingCompleted &&
            state.tasks.isEmpty() &&
            !onboardingDismissedSession
        ) {
            showOnboardingWizard = true
        }
    }

    LaunchedEffect(state.completedPromptTaskId) {
        if (state.completedPromptTaskId == null) {
            showContinueCompletedHabitDialog = false
            showDeleteCompletedHabitConfirm = false
        }
    }
    LaunchedEffect(page) {
        if (page != AppPage.HABITS) habitsEditMode = false
    }
    LaunchedEffect(milestoneQueueRefreshSignal, state.totalCompletions, state.streak) {
        delay(80)
        pullPendingMilestoneIfIdle()
    }

    CompositionLocalProvider(LocalAppLanguage provides state.language) {
        if (showOnboardingWizard) {
            OnboardingWizard(
                state = state,
                onSkip = {
                    onboardingDismissedSession = true
                    showOnboardingWizard = false
                    vm.setOnboardingCompleted(true)
                },
                onCreateHabit = { draft ->
                    vm.prepareOnboardingDraft(
                        name = draft.name,
                        category = draft.category,
                        template = draft.template,
                        trackingType = draft.trackingType,
                        dailyTarget = draft.dailyTarget,
                        unitLabel = draft.unitLabel,
                        frequency = draft.frequency,
                        customDays = draft.customDays,
                        reminderEnabled = draft.reminderEnabled,
                        reminderHour = draft.reminderHour,
                        reminderMinute = draft.reminderMinute
                    )
                    if (draft.reminderEnabled) {
                        ensureNotificationPermissionAndRun(NotificationPermissionAction.SAVE_EDITOR_REMINDER)
                    } else {
                        vm.saveEditor()
                    }
                },
                onHabitCreated = {
                    vm.setOnboardingCompleted(true)
                },
                onFinish = {
                    onboardingDismissedSession = true
                    showOnboardingWizard = false
                    highlightCompletionButton = true
                    page = AppPage.TRACKER
                }
            )
        } else {
            val selectedTaskTitle = state.tasks
                .firstOrNull { it.id == state.selectedTaskId }
                ?.let { "${it.emoji.ifBlank { "✨" }} ${it.title}" }
                ?: t("Habits")
            val primaryPages = remember {
                listOf(AppPage.TRACKER, AppPage.HABITS, AppPage.ANALYTICS, AppPage.CALENDAR, AppPage.ACCOUNT)
            }
            val isPrimaryPage = page in primaryPages

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = if (page == AppPage.HABIT_DETAIL) selectedTaskTitle else pageTitle(page),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            when (page) {
                                AppPage.HABIT_DETAIL -> {
                                    IconButton(onClick = { page = AppPage.TRACKER }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = t("Back")
                                        )
                                    }
                                }
                                AppPage.PAYWALL -> {
                                    IconButton(onClick = { page = previousPage }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = t("Back")
                                        )
                                    }
                                }
                                AppPage.MANAGE_SUBSCRIPTION -> {
                                    IconButton(onClick = { page = AppPage.ACCOUNT }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = t("Back")
                                        )
                                    }
                                }
                                AppPage.SETTINGS -> {
                                    IconButton(
                                        onClick = {
                                            page = when (settingsReturnPage) {
                                                AppPage.SETTINGS, AppPage.PAYWALL -> AppPage.TRACKER
                                                else -> settingsReturnPage
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = t("Back")
                                        )
                                    }
                                }
                                AppPage.HABITS -> {
                                    if (habitsEditMode) {
                                        Spacer(Modifier.width(48.dp))
                                    } else {
                                        val canAdd = vm.canCreateTask()
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TextButton(
                                                onClick = {
                                                    if (canAdd) {
                                                        openTemplatePicker()
                                                    } else {
                                                        openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = if (canAdd) t("Add") else t("Upgrade"),
                                                    color = semantic.primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            TextButton(onClick = { habitsEditMode = true }) {
                                                Text(
                                                    text = t("Edit"),
                                                    color = semantic.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                                else -> Unit
                            }
                        },
                        actions = {
                            val openSettings = { openSettingsPage(page) }
                            if (page == AppPage.HABITS) {
                                if (habitsEditMode) {
                                    TextButton(onClick = { habitsEditMode = false }) {
                                        Text(
                                            text = t("Done"),
                                            color = semantic.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    IconButton(onClick = openSettings) {
                                        Icon(
                                            imageVector = Icons.Rounded.Settings,
                                            contentDescription = t("Settings")
                                        )
                                    }
                                } else {
                                    IconButton(onClick = openSettings) {
                                        Icon(
                                            imageVector = Icons.Rounded.Settings,
                                            contentDescription = t("Settings")
                                        )
                                    }
                                }
                            } else if (page in primaryPages) {
                                IconButton(onClick = openSettings) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = t("Settings")
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = semantic.backgroundSurface
                        )
                    )
                },
                bottomBar = {
                    if (isPrimaryPage) {
                        PrimaryBottomNavigationBar(
                            currentPage = page,
                            onSelect = { destination, reselected ->
                                if (reselected) {
                                    when (destination) {
                                        AppPage.TRACKER -> {
                                            trackerScrollToTopSignal += 1
                                            vm.jumpToToday()
                                        }
                                        AppPage.HABITS -> {
                                            habitsScrollToTopSignal += 1
                                        }
                                        AppPage.CALENDAR -> {
                                            vm.jumpToToday()
                                        }
                                        else -> Unit
                                    }
                                } else {
                                    page = destination
                                }
                            }
                        )
                    }
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(semantic.backgroundCanvas)
                        .padding(padding)
                ) {
                    when (page) {
                        AppPage.TRACKER -> TrackerPage(
                            state = state,
                            vm = vm,
                            onOpenCreateHabit = openTemplatePicker,
                            onOpenDetails = { page = AppPage.HABIT_DETAIL },
                            highlightCompletionButton = highlightCompletionButton,
                            onHighlightConsumed = { highlightCompletionButton = false },
                            scrollToTopSignal = trackerScrollToTopSignal
                        )
                        AppPage.HABIT_DETAIL -> HabitDetailPage(
                            state = state,
                            vm = vm
                        )
                        AppPage.HABITS -> HabitsPage(
                            state = state,
                            vm = vm,
                            onCreateHabit = openTemplatePicker,
                            onUpgrade = {
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                            },
                            isEditMode = habitsEditMode,
                            onEditModeChange = { habitsEditMode = it },
                            scrollToTopSignal = habitsScrollToTopSignal
                        )
                        AppPage.ANALYTICS -> AnalyticsScreen(
                            state = state,
                            onSelectTask = vm::selectTask,
                            onUpgrade = {
                                openPaywallFromCurrentPage(PaywallTrigger.ANALYTICS)
                            }
                        )
                        AppPage.CALENDAR -> CalendarScreen(state = state, vm = vm)
                        AppPage.PAYWALL -> PaywallPage(
                            currentPlan = state.plan,
                            trigger = paywallTrigger,
                            selectedBilling = selectedBilling,
                            onSelectBilling = { selectedBilling = it },
                            onSubscribe = {
                                val source = when (billingProductIdFor(selectedBilling)) {
                                    PRODUCT_ID_MONTHLY -> ProAccessSource.MONTHLY
                                    PRODUCT_ID_YEARLY -> ProAccessSource.YEARLY
                                    PRODUCT_ID_LIFETIME -> ProAccessSource.LIFETIME
                                    else -> proAccessSourceFor(selectedBilling)
                                }
                                vm.setPlan(SubscriptionPlan.PRO)
                                vm.setProAccessSource(source)
                                Toast.makeText(
                                    context,
                                    when (selectedBilling) {
                                        BillingCycle.YEARLY -> translate(language, "Premium yearly activated (debug)")
                                        BillingCycle.MONTHLY -> translate(language, "Premium monthly activated (debug)")
                                        BillingCycle.LIFETIME -> translate(language, "Premium lifetime activated (debug)")
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onRestorePurchase = {
                                vm.setPlan(SubscriptionPlan.PRO)
                                vm.setProAccessSource(proAccessSourceFor(selectedBilling))
                                Toast.makeText(
                                    context,
                                    translate(language, "Purchases restored (debug)"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onClose = { page = previousPage }
                        )
                        AppPage.ACCOUNT -> AccountPage(
                            state = state,
                            onOpenPaywall = { trigger ->
                                openPaywallFromCurrentPage(trigger)
                            },
                            onOpenManageSubscription = {
                                page = AppPage.MANAGE_SUBSCRIPTION
                            },
                            onOpenSettings = {
                                openSettingsPage(AppPage.ACCOUNT)
                            },
                            onExportData = vm::exportData,
                            onResetProgress = vm::resetProgress,
                            onDeleteAccount = vm::deleteAccount
                        )
                        AppPage.MANAGE_SUBSCRIPTION -> ManageSubscriptionScreen(
                            state = state,
                            onOpenPaywall = { trigger ->
                                openPaywallFromCurrentPage(trigger)
                            },
                            onRestorePurchase = {
                                val source = state.proAccessSource
                                    .takeIf { it != ProAccessSource.NONE }
                                    ?: ProAccessSource.YEARLY
                                vm.setPlan(SubscriptionPlan.PRO)
                                vm.setProAccessSource(source)
                                Toast.makeText(
                                    context,
                                    translate(language, "Purchases restored (debug)"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onCancelSubscription = vm::cancelSubscription,
                            onRenewSubscription = vm::renewSubscription,
                            onSwitchPlan = vm::changeSubscriptionPlan,
                            onDebugForceFree = vm::debugForceFreePlan
                        )
                        AppPage.SETTINGS -> SettingsPage(
                            state = state,
                            onSetTheme = vm::setThemeMode,
                            onSetLanguage = vm::setLanguage,
                            onSetMinimumCompletionPercent = vm::setMinimumCompletionPercent,
                            onOpenPaywall = {
                                page = AppPage.MANAGE_SUBSCRIPTION
                            },
                            onExportData = vm::exportData,
                            onResetProgress = vm::resetProgress,
                            onDeleteAccount = vm::deleteAccount
                        )
                    }
                }
            }
        }

        if (showTemplatePicker) {
            val selectedCategory = selectedTemplateCategory
                ?.let { raw -> runCatching { TemplateCategory.valueOf(raw) }.getOrNull() }
                ?.takeIf { it != TemplateCategory.ALL }
            when {
                selectedCategory == null -> {
                    HabitCategoryScreen(
                        onCategorySelected = { category ->
                            if (!vm.canCreateTask()) {
                                showTemplatePicker = false
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                            } else {
                                selectedTemplateCategory = category.name
                                selectedTemplateId = null
                            }
                        },
                        onCreateCustom = {
                            if (!vm.canCreateTask()) {
                                showTemplatePicker = false
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                            } else {
                                showTemplatePicker = false
                                vm.openCreateTask()
                            }
                        },
                        onSkip = {
                            if (!vm.canCreateTask()) {
                                showTemplatePicker = false
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                            } else {
                                showTemplatePicker = false
                                vm.openCreateTask()
                            }
                        },
                        onDismiss = {
                            showTemplatePicker = false
                            selectedTemplateCategory = null
                            selectedTemplateId = null
                            templateDraft = null
                        }
                    )
                }

                selectedTemplateId == null -> {
                    HabitTemplateScreen(
                        category = selectedCategory,
                        onTemplateSelected = { template ->
                            if (!vm.canCreateTask()) {
                                showTemplatePicker = false
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                                return@HabitTemplateScreen
                            }
                            selectedTemplateId = template.id
                            templateDraft = TemplateConfirmDraft(
                                template = template,
                                habitName = translate(language, template.nameKey),
                                dailyTarget = template.dailyTarget.coerceAtLeast(1),
                                frequency = template.frequency,
                                customDays = template.defaultDays.ifEmpty { setOf(1, 2, 3, 4, 5) },
                                timesPerWeek = template.defaultTimesPerWeek.coerceIn(1, 7),
                                startDate = LocalDate.now(),
                                reminderEnabled = false,
                                reminderHour = DEFAULT_REMINDER_HOUR,
                                reminderMinute = DEFAULT_REMINDER_MINUTE
                            )
                        },
                        onCreateCustomHabit = {
                            if (!vm.canCreateTask()) {
                                showTemplatePicker = false
                                openPaywallFromCurrentPage(PaywallTrigger.HABIT_LIMIT)
                            } else {
                                showTemplatePicker = false
                                vm.openCreateTask()
                            }
                        },
                        onBack = {
                            selectedTemplateCategory = null
                            selectedTemplateId = null
                            templateDraft = null
                        }
                    )
                }

                else -> {
                    val selectedTemplate = CreateHabitTemplateCatalog.templatesFor(selectedCategory)
                        .firstOrNull { it.id == selectedTemplateId }
                    if (selectedTemplate == null) {
                        selectedTemplateId = null
                        templateDraft = null
                    } else {
                        val currentDraft = templateDraft ?: TemplateConfirmDraft(
                            template = selectedTemplate,
                            habitName = translate(language, selectedTemplate.nameKey),
                            dailyTarget = selectedTemplate.dailyTarget.coerceAtLeast(1),
                            frequency = selectedTemplate.frequency,
                            customDays = selectedTemplate.defaultDays.ifEmpty { setOf(1, 2, 3, 4, 5) },
                            timesPerWeek = selectedTemplate.defaultTimesPerWeek.coerceIn(1, 7),
                            startDate = LocalDate.now(),
                            reminderEnabled = false,
                            reminderHour = DEFAULT_REMINDER_HOUR,
                            reminderMinute = DEFAULT_REMINDER_MINUTE
                        )
                        templateDraft = currentDraft
                        HabitTemplateConfirmScreen(
                            initial = currentDraft,
                            onBack = { selectedTemplateId = null },
                            onStateChange = { templateDraft = it },
                            onCreateHabit = { draft ->
                                val prefill = TaskEditorPrefill(
                                    title = draft.habitName,
                                    emoji = draft.template.emoji,
                                    colorHex = draft.template.colorHex,
                                    trackingType = draft.template.trackingType,
                                    dailyTarget = draft.dailyTarget.coerceAtLeast(1),
                                    unitLabel = if (draft.template.unitLabelKey.isBlank()) "" else translate(language, draft.template.unitLabelKey),
                                    frequency = draft.frequency,
                                    customDays = draft.customDays,
                                    timesPerWeek = draft.timesPerWeek,
                                    startDate = draft.startDate,
                                    reminderEnabled = draft.reminderEnabled,
                                    reminderHour = draft.reminderHour,
                                    reminderMinute = draft.reminderMinute
                                )
                                showTemplatePicker = false
                                selectedTemplateCategory = null
                                selectedTemplateId = null
                                templateDraft = null
                                vm.openCreateTask(prefill)
                                if (draft.reminderEnabled) {
                                    ensureNotificationPermissionAndRun(NotificationPermissionAction.SAVE_EDITOR_REMINDER)
                                } else {
                                    vm.saveEditor()
                                }
                            },
                            onConfigureMore = { draft ->
                                val prefill = TaskEditorPrefill(
                                    title = draft.habitName,
                                    emoji = draft.template.emoji,
                                    colorHex = draft.template.colorHex,
                                    trackingType = draft.template.trackingType,
                                    dailyTarget = draft.dailyTarget.coerceAtLeast(1),
                                    unitLabel = if (draft.template.unitLabelKey.isBlank()) "" else translate(language, draft.template.unitLabelKey),
                                    frequency = draft.frequency,
                                    customDays = draft.customDays,
                                    timesPerWeek = draft.timesPerWeek,
                                    startDate = draft.startDate,
                                    reminderEnabled = draft.reminderEnabled,
                                    reminderHour = draft.reminderHour,
                                    reminderMinute = draft.reminderMinute
                                )
                                showTemplatePicker = false
                                selectedTemplateCategory = null
                                selectedTemplateId = null
                                templateDraft = null
                                vm.openCreateTask(prefill)
                            },
                            onPickStartDate = { startDate, onPicked ->
                                showThemedDatePicker(
                                    context = context,
                                    themeResId = datePickerTheme,
                                    initialDate = startDate,
                                    minDate = LocalDate.now(),
                                    actionColorArgb = pickerActionColor,
                                    onDateSet = { year, month, day ->
                                        onPicked(LocalDate.of(year, month + 1, day))
                                    }
                                )
                            },
                            onRequestReminderTime = { hour, minute, onPicked ->
                                pendingTemplateReminderPicker = {
                                    showThemedTimePicker(
                                        context = context,
                                        themeResId = pickerTheme,
                                        initialHour = hour,
                                        initialMinute = minute,
                                        is24HourView = is24HourView,
                                        actionColorArgb = pickerActionColor,
                                        onTimeSet = onPicked
                                    )
                                }
                                ensureNotificationPermissionAndRun(NotificationPermissionAction.PICK_TEMPLATE_REMINDER_TIME)
                            }
                        )
                    }
                }
            }
        }

        if (state.showEditor) {
            TaskEditorDialog(
                state = state,
                onDismiss = vm::closeEditor,
                vm = vm,
                onSaveRequest = {
                    if (state.editorReminderEnabled) {
                        ensureNotificationPermissionAndRun(NotificationPermissionAction.SAVE_EDITOR_REMINDER)
                    } else {
                        vm.saveEditor()
                    }
                }
            )
        }

        if (showNotificationsBlockedDialog) {
            AlertDialog(
                onDismissRequest = {
                    showNotificationsBlockedDialog = false
                    pendingSettingsAction = null
                },
                title = { Text(t("Notifications are disabled")) },
                text = { Text(t("Enable notifications in system settings to receive habit reminders.")) },
                confirmButton = {
                    Button(
                        onClick = {
                            showNotificationsBlockedDialog = false
                            val opened = openNotificationOrAppSettings(context)
                            if (!opened) {
                                pendingSettingsAction = null
                                Toast.makeText(
                                    context,
                                    translate(language, "Unable to open app settings."),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Text(t("Open Settings"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showNotificationsBlockedDialog = false
                        pendingSettingsAction = null
                    }) {
                        Text(t("Cancel"))
                    }
                }
            )
        }

        if (state.showStreakSaverDialog) {
            AlertDialog(
                onDismissRequest = vm::dismissStreakSaverDialog,
                title = { Text(t("Streak Saver")) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.x1)) {
                        Text(t("You missed yesterday.\nSave your streak?"))
                        Text(
                            text = tf("Streak savers: %d", state.streakSaverCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = vm::useStreakSaverForYesterday) {
                        Text(t("Use saver"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = vm::dismissStreakSaverDialog) {
                        Text(t("Cancel"))
                    }
                }
            )
        }

        if (state.completedPromptTaskId != null && !showContinueCompletedHabitDialog && !showDeleteCompletedHabitConfirm) {
            AlertDialog(
                onDismissRequest = vm::dismissCompletedHabitDialog,
                title = { Text(t("Congratulations! Habit completed.")) },
                text = { Text(state.completedPromptTaskTitle) },
                confirmButton = {
                    Button(onClick = { showContinueCompletedHabitDialog = true }) {
                        Text(t("Continue habit"))
                    }
                },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.x0_5)) {
                        TextButton(onClick = vm::archiveCompletedHabitFromDialog) {
                            Text(t("Archive"))
                        }
                        TextButton(onClick = { showDeleteCompletedHabitConfirm = true }) {
                            Text(t("Delete"), color = AppTheme.colors.danger)
                        }
                    }
                }
            )
        }

        if (state.completedPromptTaskId != null && showContinueCompletedHabitDialog) {
            AlertDialog(
                onDismissRequest = { showContinueCompletedHabitDialog = false },
                title = { Text(t("Continue habit")) },
                text = { Text(t("Choose how to continue this habit.")) },
                confirmButton = {
                    Button(
                        onClick = {
                            val initialDate = maxOf(
                                LocalDate.now(),
                                (state.completedPromptTaskEndDate ?: LocalDate.now()).plusDays(1)
                            )
                            showThemedDatePicker(
                                context = context,
                                themeResId = R.style.ThemeOverlay_MicroHabit_DatePicker,
                                initialDate = initialDate,
                                actionColorArgb = semantic.primary.toArgb(),
                                onDateSet = { year, month, day ->
                                    vm.continueCompletedHabitWithEndDate(LocalDate.of(year, month + 1, day))
                                }
                            )
                            showContinueCompletedHabitDialog = false
                        }
                    ) {
                        Text(t("Choose a new end date"))
                    }
                },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.x0_5)) {
                        TextButton(
                            onClick = {
                                vm.continueCompletedHabitIndefinite()
                                showContinueCompletedHabitDialog = false
                            }
                        ) {
                            Text(t("Make habit indefinite"))
                        }
                        TextButton(onClick = { showContinueCompletedHabitDialog = false }) {
                            Text(t("Cancel"))
                        }
                    }
                }
            )
        }

        if (state.completedPromptTaskId != null && showDeleteCompletedHabitConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteCompletedHabitConfirm = false },
                title = { Text(t("Delete habit?")) },
                text = { Text(t("This will permanently delete this habit and its completion history.")) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteCompletedHabitConfirm = false
                            vm.deleteCompletedHabitFromDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(t("Delete"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteCompletedHabitConfirm = false }) {
                        Text(t("Cancel"))
                    }
                }
            )
        }
        activeMilestoneEvent?.let { event ->
            StreakMilestoneScreen(
                habitId = event.habitId,
                days = event.days,
                onDismiss = {
                    StreakMilestoneQueue.markShownAndRemove(context, event)
                    activeMilestoneEvent = null
                    milestoneQueueRefreshSignal += 1
                }
            )
        }
    }
}







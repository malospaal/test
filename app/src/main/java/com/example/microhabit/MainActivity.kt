package com.example.microhabit

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.HabitTemplate
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.MAX_HABIT_TITLE_LENGTH
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.i18n.languageNativeLabel
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.i18n.translate
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.notifications.HabitReminderScheduler
import com.example.microhabit.ui.components.ChoiceOption
import com.example.microhabit.ui.components.CalendarDay
import com.example.microhabit.ui.components.CalendarDayState
import com.example.microhabit.ui.components.ColorSwatchPicker
import com.example.microhabit.ui.components.FeatureBulletRow
import com.example.microhabit.ui.components.FormSection
import com.example.microhabit.ui.components.HorizontalPercentBars
import com.example.microhabit.ui.components.HabitCard
import com.example.microhabit.ui.components.HabitCardModel
import com.example.microhabit.ui.components.AnalyticsMetricTile
import com.example.microhabit.ui.components.PlanComparisonRow
import com.example.microhabit.ui.components.PricingCardModel
import com.example.microhabit.ui.components.PricingPlanCard
import com.example.microhabit.ui.components.SingleSelectChips
import com.example.microhabit.ui.components.Stepper
import com.example.microhabit.ui.components.VerticalPercentBars
import com.example.microhabit.ui.components.WeekdaySelector
import com.example.microhabit.ui.components.SettingsDivider
import com.example.microhabit.ui.components.SettingsGroup
import com.example.microhabit.ui.components.SettingsRow
import com.example.microhabit.ui.components.SettingsSwitchRow
import com.example.microhabit.ui.components.parseColorHex
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.theme.MicroHabitTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class AppPage {
    TRACKER,
    HABIT_DETAIL,
    HABITS,
    ANALYTICS,
    CALENDAR,
    PAYWALL,
    ACCOUNT,
    SETTINGS
}

private enum class BillingCycle {
    MONTHLY,
    YEARLY
}

private enum class NotificationPermissionAction {
    ENABLE_REMINDERS,
    SAVE_EDITOR_REMINDER
}

private enum class OnboardingStep {
    WELCOME,
    CATEGORY,
    TEMPLATE,
    SETUP,
    READY
}

private data class StreakOverlayModel(
    val streak: Int,
    val milestone: Boolean
)

private data class OnboardingHabitDraft(
    val name: String,
    val category: HabitCategory,
    val template: HabitTemplate,
    val trackingType: TrackingType = TrackingType.YES_NO,
    val dailyTarget: Int = 1,
    val unitLabel: String = "",
    val frequency: TaskFrequency,
    val customDays: Set<Int>,
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = HabitRepository(applicationContext)
        val reminderScheduler = HabitReminderScheduler(applicationContext, repository)
        reminderScheduler.ensureNotificationChannel()
        reminderScheduler.syncAllReminders()

        setContent {
            val vm: MainViewModel = viewModel(
                factory = MainViewModel.Factory(repository, reminderScheduler)
            )
            val state by vm.state.collectAsState()

            MicroHabitTheme(themeMode = state.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HabitApp(state = state, vm = vm)
                }
            }
        }
    }
}

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
        AppPage.SETTINGS -> t("Settings")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitApp(state: HabitUiState, vm: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var page by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    var previousPage by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    var selectedBilling by rememberSaveable { mutableStateOf(BillingCycle.YEARLY) }
    var showNotificationsBlockedDialog by rememberSaveable { mutableStateOf(false) }
    var pendingPermissionAction by remember { mutableStateOf<NotificationPermissionAction?>(null) }
    var pendingSettingsAction by remember { mutableStateOf<NotificationPermissionAction?>(null) }
    var highlightCompletionButton by rememberSaveable { mutableStateOf(false) }
    var showOnboardingWizard by rememberSaveable { mutableStateOf(false) }
    var onboardingDismissedSession by rememberSaveable { mutableStateOf(false) }
    val semantic = AppTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val language = state.language
    val runActionWithNotifications = { action: NotificationPermissionAction ->
        when (action) {
            NotificationPermissionAction.ENABLE_REMINDERS -> vm.setNotificationsEnabled(true)
            NotificationPermissionAction.SAVE_EDITOR_REMINDER -> vm.saveEditorWithNotificationsEnabled()
        }
    }
    val showNotificationsBlocked = { action: NotificationPermissionAction? ->
        pendingPermissionAction = null
        pendingSettingsAction = action
        if (state.notificationsEnabled) {
            vm.setNotificationsEnabled(false)
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

    LaunchedEffect(state.notificationsEnabled) {
        if (
            state.notificationsEnabled &&
            !HabitReminderScheduler.canDeliverNotifications(context)
        ) {
            vm.setNotificationsEnabled(false)
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

    DisposableEffect(lifecycleOwner, pendingSettingsAction) {
        val observer = LifecycleEventObserver { _, event ->
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
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
                        current = page,
                        plan = state.plan,
                        onNavigate = {
                            if (it != AppPage.PAYWALL) previousPage = it
                            page = it
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        val selectedTaskTitle = state.tasks
                            .firstOrNull { it.id == state.selectedTaskId }
                            ?.let { "${it.emoji.ifBlank { "✨" }} ${it.title}" }
                            ?: t("Habits")
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    if (page == AppPage.HABIT_DETAIL) selectedTaskTitle else pageTitle(page),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                if (page == AppPage.HABIT_DETAIL) {
                                    IconButton(onClick = { page = AppPage.TRACKER }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = t("Back")
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Rounded.Menu, contentDescription = t("Menu"))
                                    }
                                }
                            },
                            actions = {
                                when (page) {
                                    AppPage.HABITS -> {
                                        val canAdd = state.plan == SubscriptionPlan.PRO || state.tasks.size < 1
                                        TextButton(onClick = {
                                            if (canAdd) {
                                                vm.openCreateTask()
                                            } else {
                                                previousPage = page
                                                page = AppPage.PAYWALL
                                            }
                                        }) {
                                            Text(if (canAdd) t("Add") else t("Upgrade"))
                                        }
                                    }
                                    else -> Unit
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = semantic.backgroundSurface
                            )
                        )
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
                                onOpenDetails = { page = AppPage.HABIT_DETAIL },
                                onOpenPaywall = {
                                    previousPage = AppPage.TRACKER
                                    page = AppPage.PAYWALL
                                },
                                highlightCompletionButton = highlightCompletionButton,
                                onHighlightConsumed = { highlightCompletionButton = false }
                            )
                            AppPage.HABIT_DETAIL -> HabitDetailPage(
                                state = state,
                                vm = vm
                            )
                            AppPage.HABITS -> HabitsPage(
                                state = state,
                                vm = vm,
                                onOpenHabit = {
                                    page = AppPage.HABIT_DETAIL
                                },
                                onUpgrade = {
                                    previousPage = AppPage.HABITS
                                    page = AppPage.PAYWALL
                                }
                            )
                            AppPage.ANALYTICS -> AnalyticsPage(
                                state = state,
                                onSelectTask = vm::selectTask
                            )
                            AppPage.CALENDAR -> CalendarScreen(state = state, vm = vm)
                            AppPage.PAYWALL -> PaywallPage(
                                currentPlan = state.plan,
                                selectedBilling = selectedBilling,
                                onSelectBilling = { selectedBilling = it },
                                onSubscribe = {
                                    vm.setPlan(SubscriptionPlan.PRO)
                                    Toast.makeText(
                                        context,
                                        if (selectedBilling == BillingCycle.YEARLY) {
                                            translate(language, "PRO yearly activated (debug)")
                                        } else {
                                            translate(language, "PRO monthly activated (debug)")
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onRestorePurchase = {
                                    vm.setPlan(SubscriptionPlan.PRO)
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
                                onSetPlan = vm::setPlan,
                                onOpenPaywall = {
                                    previousPage = AppPage.ACCOUNT
                                    page = AppPage.PAYWALL
                                }
                            )
                            AppPage.SETTINGS -> SettingsPage(
                                state = state,
                                onSetTheme = vm::setThemeMode,
                                onSetLanguage = vm::setLanguage,
                                onSetNotificationsEnabled = { enabled ->
                                    if (!enabled) {
                                        vm.setNotificationsEnabled(false)
                                    } else {
                                        ensureNotificationPermissionAndRun(NotificationPermissionAction.ENABLE_REMINDERS)
                                    }
                                },
                                onSetDefaultReminder = vm::setDefaultReminder,
                                onSetMinimumCompletionPercent = vm::setMinimumCompletionPercent,
                                onOpenPaywall = {
                                    previousPage = AppPage.SETTINGS
                                    page = AppPage.PAYWALL
                                },
                                onExportData = vm::exportData,
                                onResetProgress = vm::resetProgress,
                                onDeleteAccount = vm::deleteAccount
                            )
                        }
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
    }
}

@Composable
private fun DrawerContent(current: AppPage, plan: SubscriptionPlan, onNavigate: (AppPage) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val semantic = AppTheme.colors
    val spacing = AppTheme.spacing
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            Text(t("Micro-habit"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(
                if (plan == SubscriptionPlan.PRO) t("Plan: PRO") else t("Plan: Free"),
                color = semantic.textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            NavigationDrawerItem(
                label = { Text(t("Tracker")) },
                selected = current == AppPage.TRACKER,
                onClick = { onNavigate(AppPage.TRACKER) },
                icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Habits")) },
                selected = current == AppPage.HABITS,
                onClick = { onNavigate(AppPage.HABITS) },
                icon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Analytics")) },
                selected = current == AppPage.ANALYTICS,
                onClick = { onNavigate(AppPage.ANALYTICS) },
                icon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Calendar")) },
                selected = current == AppPage.CALENDAR,
                onClick = { onNavigate(AppPage.CALENDAR) },
                icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Premium")) },
                selected = current == AppPage.PAYWALL,
                onClick = { onNavigate(AppPage.PAYWALL) },
                icon = { Icon(Icons.Rounded.WorkspacePremium, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Account")) },
                selected = current == AppPage.ACCOUNT,
                onClick = { onNavigate(AppPage.ACCOUNT) },
                icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text(t("Settings")) },
                selected = current == AppPage.SETTINGS,
                onClick = { onNavigate(AppPage.SETTINGS) },
                icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
        }
    }
}

@Composable
private fun TrackerPage(
    state: HabitUiState,
    vm: MainViewModel,
    onOpenDetails: () -> Unit,
    onOpenPaywall: () -> Unit,
    highlightCompletionButton: Boolean,
    onHighlightConsumed: () -> Unit
) {
    val spacing = AppTheme.spacing
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }
    var pendingMilestone by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var milestoneCelebration by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var habitSwitchDirection by remember { mutableStateOf(0) }

    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        streakOverlay = null
        overlayVisible = false
        pendingMilestone = null
        milestoneCelebration = null
    }
    LaunchedEffect(state.totalCompletions, state.selectedDateDone, state.streak) {
        if (state.selectedDateDone && state.totalCompletions > previousTotalCompletions) {
            val model = StreakOverlayModel(
                streak = state.streak,
                milestone = isStreakMilestone(state.streak)
            )
            streakOverlay = model
            overlayVisible = true
            if (model.milestone) {
                pendingMilestone = model
            }
        }
        previousTotalCompletions = state.totalCompletions
    }
    LaunchedEffect(pendingMilestone) {
        val model = pendingMilestone ?: return@LaunchedEffect
        delay(260)
        milestoneCelebration = model
        pendingMilestone = null
    }
    LaunchedEffect(streakOverlay, overlayVisible) {
        if (overlayVisible && streakOverlay != null) {
            delay(1200)
            overlayVisible = false
        }
    }
    LaunchedEffect(highlightCompletionButton) {
        if (highlightCompletionButton) {
            delay(8000)
            onHighlightConsumed()
        }
    }

    fun switchToTask(taskId: String, direction: Int = 0) {
        if (taskId == state.selectedTaskId) return
        habitSwitchDirection = direction
        vm.selectTask(taskId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = spacing.x2, top = spacing.x1, end = spacing.x2, bottom = spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            if (state.tasks.isEmpty()) {
                item { OnboardingCard(vm, state) }
            } else {
                item {
                    TrackerHabitContextHeader(
                        tasks = state.tasks,
                        selectedTaskId = state.selectedTaskId,
                        onSelect = { taskId -> switchToTask(taskId, 0) },
                        onAddHabit = vm::openCreateTask
                    )
                }
                item {
                    fun switchHabitBy(delta: Int) {
                        if (state.tasks.size <= 1) return
                        val currentIndex = state.tasks
                            .indexOfFirst { it.id == state.selectedTaskId }
                            .let { if (it < 0) 0 else it }
                        val nextIndex = (currentIndex + delta + state.tasks.size) % state.tasks.size
                        val nextId = state.tasks[nextIndex].id
                        if (nextId != state.selectedTaskId) {
                            switchToTask(nextId, if (delta > 0) 1 else -1)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        AnimatedContent(
                            targetState = state.selectedTaskId,
                            transitionSpec = {
                                if (habitSwitchDirection == 0) {
                                    fadeIn(animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)) togetherWith
                                        fadeOut(animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing))
                                } else {
                                    val direction = if (habitSwitchDirection > 0) 1 else -1
                                    (slideInHorizontally(
                                        initialOffsetX = { fullWidth -> (fullWidth / 7) * direction },
                                        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
                                    ) + fadeIn(animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing))) togetherWith
                                        (slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> (-fullWidth / 9) * direction },
                                            animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
                                        ) + fadeOut(animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)))
                                }
                            },
                            label = "trackerHeroSwitch"
                        ) { taskId ->
                            val animatedTask = state.tasks.firstOrNull { it.id == taskId }
                            HeroCard(
                                task = animatedTask,
                                selectedDate = state.selectedDate,
                                done = state.selectedDateDone,
                                partial = state.selectedDatePartial,
                                scheduled = state.selectedDateScheduled,
                                selectedValue = state.selectedDateValue,
                                selectedTarget = state.selectedDateTarget,
                                selectedUnit = state.selectedDateUnit,
                                selectedCompletionPercent = state.selectedDateCompletionPercent,
                                minimumCompletionPercent = state.minimumCompletionPercent,
                                plan = state.plan,
                                durationTimerRunning = state.durationTimerRunning,
                                durationTimerElapsedSeconds = state.durationTimerElapsedSeconds,
                                onDone = vm::toggleSelectedDateDone,
                                onIncrementValue = vm::incrementSelectedDateValue,
                                onStartDurationTimer = vm::startDurationTimer,
                                onStopDurationTimerAndApply = vm::stopDurationTimerAndApply,
                                onOpenPaywall = onOpenPaywall,
                                highlightMarkButton = highlightCompletionButton,
                                onHighlightConsumed = onHighlightConsumed,
                                appThemeMode = state.themeMode,
                                swipeEnabled = state.tasks.size > 1,
                                onSwipeNext = { switchHabitBy(1) },
                                onSwipePrevious = { switchHabitBy(-1) }
                            )
                        }
                        Crossfade(
                            targetState = state.selectedTaskId,
                            animationSpec = tween(durationMillis = 150),
                            label = "trackerStatsCrossfade"
                        ) {
                            TrackerStreakRow(
                                streak = state.streak,
                                bestStreak = state.bestStreak
                            )
                        }
                        Crossfade(
                            targetState = state.selectedTaskId,
                            animationSpec = tween(durationMillis = 150),
                            label = "trackerWeekCrossfade"
                        ) {
                            SevenDayChart(
                                points = state.last7Days,
                                scheduled = state.last7DaysScheduled,
                                anchorDate = LocalDate.now()
                            )
                        }
                        Crossfade(
                            targetState = state.selectedTaskId,
                            animationSpec = tween(durationMillis = 150),
                            label = "trackerCalendarCrossfade"
                        ) { taskId ->
                            val animatedTask = state.tasks.firstOrNull { it.id == taskId }
                            CalendarCard(
                                month = state.currentMonth,
                                selectedDate = state.selectedDate,
                                selectedTask = animatedTask,
                                doneDates = state.doneDatesInCurrentMonth,
                                partialDates = state.partialDatesInCurrentMonth,
                                scheduledDates = state.scheduledDatesInCurrentMonth,
                                onMoveMonth = vm::moveMonth,
                                onToday = vm::jumpToToday,
                                onDateSelect = vm::selectDate
                            )
                        }
                        TextButton(
                            onClick = onOpenDetails,
                            modifier = Modifier.align(Alignment.End),
                            contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0)
                        ) {
                            Text(
                                text = t("More details →"),
                                style = MaterialTheme.typography.titleSmall,
                                color = AppTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = overlayVisible && streakOverlay != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = spacing.x1_5),
            enter = fadeIn(tween(180)) +
                slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(180, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(180)) +
                slideOutVertically(targetOffsetY = { -it / 3 }, animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.96f, animationSpec = tween(180, easing = FastOutSlowInEasing))
        ) {
            streakOverlay?.let { model ->
                StreakRewardOverlay(model = model)
            }
        }

        milestoneCelebration?.let { model ->
            MilestoneCelebrationDialog(
                model = model,
                currentStreak = state.streak,
                bestStreak = state.bestStreak,
                completion30Day = state.progressPercent,
                points = state.last7Days,
                onDismiss = { milestoneCelebration = null }
            )
        }
    }

}

@Composable
private fun HabitsPage(
    state: HabitUiState,
    vm: MainViewModel,
    onOpenHabit: () -> Unit,
    onUpgrade: () -> Unit
) {
    val context = LocalContext.current
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val canAdd = state.plan == SubscriptionPlan.PRO || state.tasks.size < 1
    var pendingDeleteTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        if (state.habits.isEmpty()) {
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        Text(
                            text = t("No habits yet"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = t("Create your first habit to start building momentum."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Button(
                            onClick = { if (canAdd) vm.openCreateTask() else onUpgrade() },
                            shape = RoundedCornerShape(AppTheme.radius.md)
                        ) {
                            Text(if (canAdd) t("Create habit") else t("Upgrade to PRO"))
                        }
                    }
                }
            }
        } else {
            val activeHabits = state.habits.filterNot { it.isArchived }
            val archivedHabits = state.habits.filter { it.isArchived }

            if (activeHabits.isNotEmpty()) {
                item {
                    Text(
                        text = t("Active habits"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                items(items = activeHabits, key = { it.id }) { habit ->
                    val reminderStatus = if (habit.reminderEnabled) {
                        tf("Reminder: %s", formatTimeForDevice(context, habit.reminderHour, habit.reminderMinute))
                    } else {
                        t("Reminder off")
                    }
                    HabitCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            trackingType = habit.trackingType,
                            streak = habit.streak,
                            frequency = habit.frequency,
                            reminderStatus = reminderStatus,
                            completionRate = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        onOpen = {
                            vm.selectTask(habit.id)
                            onOpenHabit()
                        },
                        onEdit = { vm.openEditTask(habit.id) },
                        onArchive = { vm.archiveTask(habit.id) },
                        onUnarchive = { vm.unarchiveTask(habit.id) },
                        onDelete = { pendingDeleteTaskId = habit.id }
                    )
                }
            }

            if (archivedHabits.isNotEmpty()) {
                item {
                    Text(
                        text = t("Archived habits"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                items(items = archivedHabits, key = { it.id }) { habit ->
                    val reminderStatus = if (habit.reminderEnabled) {
                        tf("Reminder: %s", formatTimeForDevice(context, habit.reminderHour, habit.reminderMinute))
                    } else {
                        t("Reminder off")
                    }
                    HabitCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            trackingType = habit.trackingType,
                            streak = habit.streak,
                            frequency = habit.frequency,
                            reminderStatus = reminderStatus,
                            completionRate = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        onOpen = { vm.openEditTask(habit.id) },
                        onEdit = { vm.openEditTask(habit.id) },
                        onArchive = { vm.archiveTask(habit.id) },
                        onUnarchive = { vm.unarchiveTask(habit.id) },
                        onDelete = { pendingDeleteTaskId = habit.id }
                    )
                }
            }
        }
    }

    pendingDeleteTaskId?.let { taskId ->
        AlertDialog(
            onDismissRequest = { pendingDeleteTaskId = null },
            title = { Text(t("Delete habit?")) },
            text = { Text(t("This will permanently delete this habit and its completion history.")) },
            confirmButton = {
                Button(
                    onClick = {
                        pendingDeleteTaskId = null
                        vm.deleteTask(taskId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(t("Delete"))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTaskId = null }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

@Composable
private fun HabitDetailPage(
    state: HabitUiState,
    vm: MainViewModel
) {
    val spacing = AppTheme.spacing
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    var noteDraft by remember(state.selectedTaskId, state.selectedTaskNote) {
        mutableStateOf(state.selectedTaskNote)
    }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }

    LaunchedEffect(state.selectedTaskId, state.selectedTaskNote) {
        noteDraft = state.selectedTaskNote
    }
    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        overlayVisible = false
        streakOverlay = null
    }
    LaunchedEffect(state.totalCompletions, state.streak) {
        if (state.totalCompletions > previousTotalCompletions) {
            streakOverlay = StreakOverlayModel(
                streak = state.streak,
                milestone = isStreakMilestone(state.streak)
            )
            overlayVisible = true
        }
        previousTotalCompletions = state.totalCompletions
    }
    LaunchedEffect(overlayVisible, streakOverlay) {
        if (overlayVisible && streakOverlay != null) {
            delay(1100)
            overlayVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedTask == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.x2),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                item {
                    GlassCard {
                        Text(
                            text = t("No active habit"),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.x2),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                item {
                    HabitDepthHero(
                        task = selectedTask,
                        streak = state.streak,
                        weeklyCompletion = state.completionRate7Day,
                        todayDone = state.todayDone,
                        todayScheduled = state.todayScheduled,
                        onCompleteToday = vm::completeSelectedHabitToday
                    )
                }
                item {
                    HabitLevelProgressCard(
                        streak = state.streak,
                        bestStreak = state.bestStreak
                    )
                }
                item {
                    HabitMiniCalendarCard(
                        month = state.currentMonth,
                        selectedDate = state.selectedDate,
                        doneDates = state.doneDatesInCurrentMonth,
                        partialDates = state.partialDatesInCurrentMonth,
                        scheduledDates = state.scheduledDatesInCurrentMonth,
                        onMoveMonth = vm::moveMonth,
                        onToday = vm::jumpToToday,
                        onDateSelect = vm::selectDate
                    )
                }
                item {
                    SevenDayChart(
                        points = state.last7Days,
                        scheduled = state.last7DaysScheduled,
                        anchorDate = LocalDate.now()
                    )
                }
                item {
                    HabitDepthStats(
                        trackingType = selectedTask.trackingType,
                        streak = state.streak,
                        bestStreak = state.bestStreak,
                        completion30Day = state.completionRate30Day,
                        totalCompletions = state.totalCompletions,
                        totalTrackedValue = state.totalTrackedValue,
                        averageTrackedValue = state.averageTrackedValue,
                        unitLabel = if (selectedTask.trackingType == TrackingType.DURATION) t("min") else selectedTask.unitLabel
                    )
                }
                item {
                    HabitStreakHistoryCard(
                        bestStreak = state.bestStreak,
                        history = state.streakHistory
                    )
                }
                item {
                    HabitInsightsCard(
                        mostConsistentWeekday = state.mostConsistentWeekday,
                        hardestWeekday = state.hardestWeekday,
                        completionConsistency = state.completionConsistency
                    )
                }
                item {
                    HabitNotesCard(
                        note = noteDraft,
                        onNoteChange = { value ->
                            noteDraft = value
                            vm.setSelectedTaskNote(value)
                        },
                        onSave = vm::saveSelectedTaskNote
                    )
                }
            }

            AnimatedVisibility(
                visible = overlayVisible && streakOverlay != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = spacing.x1_5),
                enter = fadeIn(tween(170)) + slideInVertically(initialOffsetY = { -it / 3 }, animationSpec = tween(170)),
                exit = fadeOut(tween(170)) + slideOutVertically(targetOffsetY = { -it / 4 }, animationSpec = tween(170))
            ) {
                streakOverlay?.let { model ->
                    StreakRewardOverlay(model = model)
                }
            }
        }
    }
}

private data class HabitLevelInfo(
    val achievedLevel: Int,
    val nextLevel: Int,
    val progressToNext: Float,
    val daysToNext: Int
)

private fun levelInfoForStreak(streak: Int): HabitLevelInfo {
    val value = streak.coerceAtLeast(0)
    val baseMilestones = listOf(7, 30, 60, 120)

    fun thresholdForLevel(level: Int): Int {
        if (level <= 0) return 0
        if (level <= baseMilestones.size) return baseMilestones[level - 1]
        return 120 + (level - baseMilestones.size) * 30
    }

    val achievedLevel = generateSequence(1) { it + 1 }
        .takeWhile { thresholdForLevel(it) <= value }
        .lastOrNull() ?: 0
    val nextLevel = achievedLevel + 1
    val currentThreshold = thresholdForLevel(achievedLevel)
    val nextThreshold = thresholdForLevel(nextLevel)
    val progressToNext = ((value - currentThreshold).toFloat() / (nextThreshold - currentThreshold).coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val daysToNext = (nextThreshold - value).coerceAtLeast(0)

    return HabitLevelInfo(
        achievedLevel = achievedLevel,
        nextLevel = nextLevel,
        progressToNext = progressToNext,
        daysToNext = daysToNext
    )
}

@Composable
private fun HabitDepthHero(
    task: HabitTask,
    streak: Int,
    weeklyCompletion: Int,
    todayDone: Boolean,
    todayScheduled: Boolean,
    onCompleteToday: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val animatedRing by animateFloatAsState(
        targetValue = (weeklyCompletion / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "habitsDepthRingProgress"
    )

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
            Text(
                text = "${task.emoji.ifBlank { "✨" }} ${task.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(spacing.x6 + spacing.x6),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = semantic.borderSubtle,
                        strokeWidth = 8.dp
                    )
                    CircularProgressIndicator(
                        progress = { animatedRing },
                        modifier = Modifier.fillMaxSize(),
                        color = semantic.success,
                        strokeWidth = 8.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedContent(targetState = streak, label = "habitsDepthStreakValue") { value ->
                            Text(
                                text = "🔥 $value",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = semantic.textPrimary
                            )
                        }
                        Text(
                            text = t("Current streak"),
                            style = MaterialTheme.typography.bodySmall,
                            color = semantic.textSecondary
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Text(
                        text = tf("%d day streak", streak),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = semantic.textPrimary
                    )
                    Text(
                        text = tf("Weekly completion: %d%%", weeklyCompletion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = semantic.textSecondary
                    )
                    Button(
                        onClick = onCompleteToday,
                        enabled = todayScheduled && !todayDone,
                        shape = RoundedCornerShape(radius.md),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayDone) semantic.success else semantic.primary
                        )
                    ) {
                        Text(
                            text = when {
                                todayDone -> t("Completed today")
                                todayScheduled -> t("Complete today")
                                else -> t("Not scheduled today")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitLevelProgressCard(
    streak: Int,
    bestStreak: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val info = remember(streak) { levelInfoForStreak(streak) }
    val animatedProgress by animateFloatAsState(
        targetValue = info.progressToNext,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "habitLevelProgress"
    )

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Level progress"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Text(
                text = tf("Level %d", info.achievedLevel),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = semantic.textPrimary
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(AppTheme.radius.full)),
                color = semantic.success,
                trackColor = semantic.backgroundSurfaceMuted
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tf("Best streak: %d", bestStreak),
                    style = MaterialTheme.typography.bodySmall,
                    color = semantic.textSecondary
                )
                Text(
                    text = if (info.daysToNext > 0) {
                        tf("%d days to level %d", info.daysToNext, info.nextLevel)
                    } else {
                        tf("Level %d unlocked", info.nextLevel)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = semantic.textSecondary
                )
            }
        }
    }
}

@Composable
private fun HabitMiniCalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    doneDates: Set<LocalDate>,
    partialDates: Set<LocalDate>,
    scheduledDates: Set<LocalDate>,
    onMoveMonth: (Long) -> Unit,
    onToday: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val locale = appLocale()
    val today = LocalDate.now()

    GlassCard(
        modifier = Modifier.height(352.dp),
        contentPadding = PaddingValues(spacing.x2)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Calendar"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            CalendarHeaderRow(
                monthLabel = localizedMonthYear(month, LocalAppLanguage.current, locale),
                isTodaySelected = selectedDate == today && month == YearMonth.now(),
                onPrev = { onMoveMonth(-1) },
                onToday = onToday,
                onNext = { onMoveMonth(1) }
            )
            val labels = weekdayLabels(LocalAppLanguage.current)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
            monthGrid(month).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val leadingEmpty = week.takeWhile { it == null }.size
                    val trailingEmpty = week.reversed().takeWhile { it == null }.size
                    repeat(leadingEmpty) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    week.filterNotNull().forEach { day ->
                        val state = dayStateFor(
                            date = day,
                            doneDates = doneDates,
                            partialDates = partialDates,
                            scheduledDates = scheduledDates,
                            today = today
                        )
                        MiniCalendarCell(
                            modifier = Modifier.weight(1f),
                            date = day,
                            state = state,
                            selected = day == selectedDate,
                            today = day == today,
                            onClick = { onDateSelect(day) }
                        )
                    }
                    repeat(trailingEmpty) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniCalendarCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    state: CalendarDayState,
    selected: Boolean,
    today: Boolean,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val background = when (state) {
        CalendarDayState.COMPLETED -> semantic.success.copy(alpha = 0.9f)
        CalendarDayState.PARTIAL -> semantic.successMuted.copy(alpha = 0.70f)
        CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.10f)
        CalendarDayState.NOT_SCHEDULED -> semantic.neutralMuted.copy(alpha = 0.35f)
        CalendarDayState.FUTURE -> semantic.backgroundSurfaceMuted.copy(alpha = 0.28f)
    }
    val textColor = when (state) {
        CalendarDayState.COMPLETED -> MaterialTheme.colorScheme.onPrimary
        CalendarDayState.PARTIAL -> semantic.success
        CalendarDayState.MISSED -> semantic.danger
        CalendarDayState.NOT_SCHEDULED -> semantic.textSecondary
        CalendarDayState.FUTURE -> semantic.textTertiary
    }

    Box(
        modifier = modifier
            .height(spacing.x4)
            .clip(RoundedCornerShape(radius.sm))
            .background(background)
            .border(
                width = if (selected || today) stroke.medium else stroke.thin,
                color = when {
                    selected -> semantic.primary
                    today -> semantic.calendarTodayRing
                    state == CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.45f)
                    else -> semantic.borderSubtle.copy(alpha = 0.45f)
                },
                shape = RoundedCornerShape(radius.sm)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (selected || today) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun HabitDepthStats(
    trackingType: TrackingType,
    streak: Int,
    bestStreak: Int,
    completion30Day: Int,
    totalCompletions: Int,
    totalTrackedValue: Int,
    averageTrackedValue: Int,
    unitLabel: String
) {
    val spacing = AppTheme.spacing

    GlassCard(contentPadding = PaddingValues(spacing.x1)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Stats"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Current streak"),
                    value = "${streak}d",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Best streak"),
                    value = "${bestStreak}d",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("30 day completion"),
                    value = "$completion30Day%",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Total completions"),
                    value = totalCompletions.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            if (trackingType != TrackingType.YES_NO) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    AnalyticsMetricTile(
                        label = t("Total value"),
                        value = if (unitLabel.isBlank()) {
                            totalTrackedValue.toString()
                        } else {
                            "$totalTrackedValue $unitLabel"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricTile(
                        label = t("Average per day"),
                        value = if (unitLabel.isBlank()) {
                            averageTrackedValue.toString()
                        } else {
                            "$averageTrackedValue $unitLabel"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitStreakHistoryCard(
    bestStreak: Int,
    history: List<Int>
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val previous = remember(bestStreak, history) {
        val normalized = history.filter { it > 0 }.sortedDescending()
        if (normalized.firstOrNull() == bestStreak) normalized.drop(1).take(3) else normalized.take(3)
    }

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Streak history"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Text(
                text = "🔥 ${bestStreak} ${t("days")}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = semantic.textPrimary
            )
            Text(
                text = t("Previous streaks"),
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
            if (previous.isEmpty()) {
                Text(
                    text = t("No streak history yet"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = semantic.textSecondary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    previous.forEach { value ->
                        Surface(
                            color = semantic.backgroundSurfaceMuted,
                            shape = RoundedCornerShape(AppTheme.radius.full)
                        ) {
                            Text(
                                text = "🔥 $value",
                                modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                                style = MaterialTheme.typography.bodyMedium,
                                color = semantic.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitInsightsCard(
    mostConsistentWeekday: Int?,
    hardestWeekday: Int?,
    completionConsistency: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val locale = appLocale()
    val noDataLabel = t("No data")

    fun weekdayLabelOrFallback(value: Int?): String {
        if (value == null || value !in 1..7) return noDataLabel
        return DayOfWeek.of(value).getDisplayName(TextStyle.SHORT, locale).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Insights"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Most consistent day"),
                    value = weekdayLabelOrFallback(mostConsistentWeekday),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Hardest day"),
                    value = weekdayLabelOrFallback(hardestWeekday),
                    modifier = Modifier.weight(1f)
                )
            }
            AnalyticsMetricTile(
                label = t("Completion consistency"),
                value = "$completionConsistency%",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HabitNotesCard(
    note: String,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = t("Habit notes"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = semantic.textPrimary
            )
            OutlinedTextField(
                value = note,
                onValueChange = { onNoteChange(it.take(180)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                label = { Text(t("Why did you miss today?")) },
                supportingText = {
                    Text(
                        text = "${note.length}/180",
                        color = semantic.textSecondary
                    )
                }
            )
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(AppTheme.radius.md),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(t("Save note"))
            }
        }
    }
}

@Composable
private fun AnalyticsPage(
    state: HabitUiState,
    onSelectTask: (String) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }

    if (selectedTask == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            if (state.tasks.isNotEmpty()) {
                item {
                    TaskSelector(
                        tasks = state.tasks,
                        selectedTaskId = state.selectedTaskId,
                        onSelect = onSelectTask
                    )
                }
            }
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        Text(
                            text = t("Analytics"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = t("Create and select a habit to view analytics."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
        return
    }

    val locale = appLocale()
    val chartAnchorDate = LocalDate.now()
    val weeklyValues = state.last7Days.map { it.coerceIn(0, 100) }
    val weeklyLabels = (6 downTo 0).map { offset ->
        chartAnchorDate.minusDays(offset.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    }
    val monthlyValues = if (state.monthlyProgress.isEmpty()) listOf(0, 0, 0, 0) else state.monthlyProgress
    val monthlyLabels = monthlyValues.indices.map { "${t("W")}${it + 1}" }
    val weekdayLabels = weekdayLabels(LocalAppLanguage.current)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            TaskSelector(
                tasks = state.tasks,
                selectedTaskId = state.selectedTaskId,
                onSelect = onSelectTask
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("Current streak"),
                    value = "${state.streak}d",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("Best streak"),
                    value = "${state.bestStreak}d",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                AnalyticsMetricTile(
                    label = t("7 day completion"),
                    value = "${state.completionRate7Day}%",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricTile(
                    label = t("30 day completion"),
                    value = "${state.completionRate30Day}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AnalyticsMetricTile(
                label = t("Total completions"),
                value = state.totalCompletions.toString(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Weekly completion chart"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    VerticalPercentBars(
                        values = weeklyValues,
                        labels = weeklyLabels,
                        highlightIndex = 6
                    )
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Monthly progress chart"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    VerticalPercentBars(
                        values = monthlyValues,
                        labels = monthlyLabels
                    )
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Weekday consistency"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    HorizontalPercentBars(
                        values = state.weekdayConsistency,
                        labels = weekdayLabels
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarScreen(state: HabitUiState, vm: MainViewModel) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val locale = appLocale()
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    val today = LocalDate.now()

    if (selectedTask == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        Text(
                            text = t("Calendar"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = t("Select or create a habit to view completion history."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
        return
    }

    val selectedState = dayStateFor(
        date = state.selectedDate,
        doneDates = state.doneDatesInCurrentMonth,
        partialDates = state.partialDatesInCurrentMonth,
        scheduledDates = state.scheduledDatesInCurrentMonth,
        today = today
    )
    val selectedIsTodayPending = state.selectedDate == today && !state.selectedDateDone

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            TaskSelector(
                tasks = state.tasks,
                selectedTaskId = state.selectedTaskId,
                onSelect = vm::selectTask
            )
        }

        item {
            GlassCard(modifier = Modifier.height(416.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    CalendarHeaderRow(
                        monthLabel = localizedMonthYear(state.currentMonth, state.language, locale),
                        isTodaySelected = state.selectedDate == today && state.currentMonth == YearMonth.now(),
                        onPrev = { vm.moveMonth(-1) },
                        onToday = vm::jumpToToday,
                        onNext = { vm.moveMonth(1) }
                    )

                    val weekdayLabels = weekdayLabels(LocalAppLanguage.current)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        weekdayLabels.forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    monthGrid(state.currentMonth).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            week.forEachIndexed { _, day ->
                                val dayState = dayStateFor(
                                    date = day,
                                    doneDates = state.doneDatesInCurrentMonth,
                                    partialDates = state.partialDatesInCurrentMonth,
                                    scheduledDates = state.scheduledDatesInCurrentMonth,
                                    today = today
                                )
                                val dayDate = day
                                val done = dayDate != null && dayDate in state.doneDatesInCurrentMonth
                                CalendarDay(
                                    modifier = Modifier.weight(1f),
                                    date = dayDate,
                                    state = dayState,
                                    selected = dayDate == state.selectedDate,
                                    today = dayDate == today,
                                    connectLeft = done && dayDate != null && dayDate.minusDays(1) in state.doneDatesInCurrentMonth,
                                    connectRight = done && dayDate != null && dayDate.plusDays(1) in state.doneDatesInCurrentMonth,
                                    enabled = dayDate != null,
                                    onClick = { dayDate?.let(vm::selectDate) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Completion details"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = state.selectedDate.format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = if (selectedIsTodayPending) t("Today") else statusLabel(selectedState, state.language),
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            selectedIsTodayPending -> colors.primary
                            else -> when (selectedState) {
                            CalendarDayState.COMPLETED -> colors.success
                            CalendarDayState.PARTIAL -> colors.success
                            CalendarDayState.MISSED -> colors.danger
                            CalendarDayState.NOT_SCHEDULED -> colors.textSecondary
                            CalendarDayState.FUTURE -> colors.textSecondary
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun dayStateFor(
    date: LocalDate?,
    doneDates: Set<LocalDate>,
    partialDates: Set<LocalDate>,
    scheduledDates: Set<LocalDate>,
    today: LocalDate
): CalendarDayState {
    if (date == null) return CalendarDayState.FUTURE
    if (date.isAfter(today)) return CalendarDayState.FUTURE
    if (date in doneDates) return CalendarDayState.COMPLETED
    if (date in partialDates) return CalendarDayState.PARTIAL
    if (date in scheduledDates && date.isBefore(today)) return CalendarDayState.MISSED
    return CalendarDayState.NOT_SCHEDULED
}

private fun statusLabel(state: CalendarDayState, language: AppLanguage): String {
    return when (state) {
        CalendarDayState.COMPLETED -> translate(language, "Completed")
        CalendarDayState.PARTIAL -> translate(language, "Partial")
        CalendarDayState.MISSED -> translate(language, "Missed")
        CalendarDayState.NOT_SCHEDULED -> translate(language, "Not scheduled")
        CalendarDayState.FUTURE -> translate(language, "Future")
    }
}

@Composable
private fun AccountPage(
    state: HabitUiState,
    onSetPlan: (SubscriptionPlan) -> Unit,
    onOpenPaywall: () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            GlassCard {
                Text(t("Account"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(spacing.x1))
                Text(
                    text = if (state.plan == SubscriptionPlan.PRO) {
                        t("You are on PRO. Manage options in Premium.")
                    } else {
                        t("You are on Free. Upgrade to unlock unlimited habits.")
                    },
                    color = semantic.textSecondary
                )
                Spacer(Modifier.height(spacing.x1))
                Button(onClick = onOpenPaywall, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.plan == SubscriptionPlan.PRO) t("Open Premium") else t("Upgrade to Premium"))
                }
            }
        }
        item {
            PlanCard(
                title = t("Free"),
                subtitle = t("1 habit"),
                selected = state.plan == SubscriptionPlan.FREE,
                actionLabel = if (state.plan == SubscriptionPlan.FREE) t("Current") else t("Choose"),
                onAction = { onSetPlan(SubscriptionPlan.FREE) }
            )
        }
        item {
            PlanCard(
                title = "PRO",
                subtitle = t("Unlimited habits"),
                selected = state.plan == SubscriptionPlan.PRO,
                actionLabel = if (state.plan == SubscriptionPlan.PRO) t("Current") else t("Choose PRO"),
                onAction = { onSetPlan(SubscriptionPlan.PRO) }
            )
        }
    }
}

@Composable
private fun SettingsPage(
    state: HabitUiState,
    onSetTheme: (AppThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetNotificationsEnabled: (Boolean) -> Unit,
    onSetDefaultReminder: (Int, Int) -> Unit,
    onSetMinimumCompletionPercent: (Int) -> Unit,
    onOpenPaywall: () -> Unit,
    onExportData: () -> Result<String>,
    onResetProgress: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val spacing = AppTheme.spacing
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showCompletionThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var completionPercentDraft by rememberSaveable(state.minimumCompletionPercent) {
        mutableStateOf(state.minimumCompletionPercent.coerceIn(50, 100))
    }
    val selectedHabit = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    val supportedLanguages = listOf(
        AppLanguage.EN,
        AppLanguage.RU,
        AppLanguage.UK,
        AppLanguage.DE,
        AppLanguage.CS
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            SettingsGroup(
                title = t("Account"),
                subtitle = t("Profile and app usage overview.")
            ) {
                SettingsRow(
                    title = t("Active habits"),
                    subtitle = t("Non-archived habits"),
                    value = state.tasks.size.toString()
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Current habit"),
                    subtitle = t("Selected for tracking today"),
                    value = selectedHabit?.title ?: t("Not selected")
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Appearance"),
                subtitle = t("Visual style of the app.")
            ) {
                SettingsRow(
                    title = t("Theme"),
                    subtitle = t("System, light or dark mode"),
                    value = themeLabel(state.themeMode, language),
                    onClick = { showThemeDialog = true }
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Language"),
                subtitle = t("App interface language.")
            ) {
                SettingsRow(
                    title = t("Language"),
                    subtitle = t("Choose your preferred locale"),
                    value = languageNativeLabel(state.language),
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Notifications"),
                subtitle = t("Daily reminders and nudges.")
            ) {
                SettingsSwitchRow(
                    title = t("Reminders"),
                    subtitle = t("Enable habit reminder notifications"),
                    checked = state.notificationsEnabled,
                    onCheckedChange = onSetNotificationsEnabled
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Reminder time"),
                    subtitle = t("Daily notification time"),
                    value = formatTimeForDevice(context, state.defaultReminderHour, state.defaultReminderMinute),
                    enabled = state.notificationsEnabled,
                    onClick = {
                        val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> onSetDefaultReminder(hour, minute) },
                            state.defaultReminderHour,
                            state.defaultReminderMinute,
                            is24HourView
                        ).show()
                    }
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Tracking"),
                subtitle = t("How much progress counts as completed.")
            ) {
                SettingsRow(
                    title = t("Minimum completion percent"),
                    subtitle = t("Used for count and duration habits"),
                    value = "${state.minimumCompletionPercent}%",
                    onClick = {
                        completionPercentDraft = state.minimumCompletionPercent.coerceIn(50, 100)
                        showCompletionThresholdDialog = true
                    }
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Subscription"),
                subtitle = t("Manage Free and PRO plans.")
            ) {
                SettingsRow(
                    title = t("Manage subscription"),
                    subtitle = if (state.plan == SubscriptionPlan.PRO) {
                        t("PRO active: unlimited habits")
                    } else {
                        t("Free plan: one active habit")
                    },
                    value = if (state.plan == SubscriptionPlan.PRO) "PRO" else t("FREE"),
                    onClick = onOpenPaywall
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Data & Privacy"),
                subtitle = t("Control your data and account lifecycle.")
            ) {
                SettingsRow(
                    title = t("Export data"),
                    subtitle = t("Save tasks and progress as JSON"),
                    onClick = {
                        val result = onExportData()
                        val message = result.fold(
                            onSuccess = { formatTranslate(language, "Data exported: %s", it) },
                            onFailure = {
                                formatTranslate(
                                    language,
                                    "Export failed: %s",
                                    it.message ?: translate(language, "Unknown error")
                                )
                            }
                        )
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Reset progress"),
                    subtitle = t("Clear completion history, keep habits"),
                    onClick = { showResetConfirm = true }
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Delete account"),
                    subtitle = t("Remove all habits and settings"),
                    destructive = true,
                    onClick = { showDeleteConfirm = true }
                )
            }
        }

        item {
            SettingsGroup(
                title = t("Support"),
                subtitle = t("Get help and send feedback.")
            ) {
                SettingsRow(
                    title = t("Help center"),
                    subtitle = t("Quick guidance for app features"),
                    onClick = {
                        Toast.makeText(
                            context,
                            translate(language, "Help center is not available in debug build."),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                SettingsDivider()
                SettingsRow(
                    title = t("Contact support"),
                    subtitle = t("Send us your feedback"),
                    onClick = {
                        Toast.makeText(
                            context,
                            translate(language, "Support contact will be connected in the next build."),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = t("Select theme"),
            options = listOf(
                AppThemeMode.SYSTEM to t("System"),
                AppThemeMode.LIGHT to t("Light"),
                AppThemeMode.DARK to t("Dark")
            ),
            selected = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = onSetTheme
        )
    }

    if (showLanguageDialog) {
        SelectionDialog(
            title = t("Select language"),
            options = supportedLanguages.map { it to languageNativeLabel(it) },
            selected = state.language,
            onDismiss = { showLanguageDialog = false },
            onSelect = onSetLanguage
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(t("Reset progress?")) },
            text = { Text(t("This will remove all completion history and keep your habits.")) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetProgress()
                        Toast.makeText(
                            context,
                            translate(language, "Progress reset."),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(t("Reset"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(t("Delete account?")) },
            text = { Text(t("This action removes all habits, progress and settings.")) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                        Toast.makeText(
                            context,
                            translate(language, "Account data deleted."),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(t("Delete"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }

    if (showCompletionThresholdDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionThresholdDialog = false },
            title = { Text(t("Minimum completion percent")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Used for count and duration habits"),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Stepper(
                        label = t("Completion threshold"),
                        value = completionPercentDraft,
                        min = 50,
                        max = 100,
                        onValueChange = { completionPercentDraft = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompletionThresholdDialog = false
                        onSetMinimumCompletionPercent(completionPercentDraft.coerceIn(50, 100))
                    }
                ) {
                    Text(t("Save changes"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionThresholdDialog = false }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

@Composable
private fun PaywallPage(
    currentPlan: SubscriptionPlan,
    selectedBilling: BillingCycle,
    onSelectBilling: (BillingCycle) -> Unit,
    onSubscribe: () -> Unit,
    onRestorePurchase: () -> Unit,
    onClose: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                            Text(
                                text = t("Premium"),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = t("Calm focus for consistent habit building."),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Rounded.Close, contentDescription = t("Close"))
                        }
                    }

                    Surface(
                        color = colors.primaryMuted,
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = if (currentPlan == SubscriptionPlan.PRO) {
                                t("You already have Premium access.")
                            } else {
                                t("Unlock unlimited habits and deeper insights.")
                            },
                            modifier = Modifier.padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Included with Premium"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    FeatureBulletRow(t("Unlimited active habits"))
                    FeatureBulletRow(t("Advanced analytics and consistency views"))
                    FeatureBulletRow(t("Priority support and early access updates"))
                    FeatureBulletRow(t("Future cross-device sync support"))
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Plan comparison"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        Text(
                            text = t("Feature"),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textSecondary
                        )
                        Text(
                            text = t("Free"),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textSecondary
                        )
                        Text(
                            text = t("Premium"),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textSecondary
                        )
                    }
                    PlanComparisonRow(
                        feature = t("Active habits"),
                        freeIncluded = true,
                        premiumIncluded = true
                    )
                    PlanComparisonRow(
                        feature = t("More than one habit"),
                        freeIncluded = false,
                        premiumIncluded = true
                    )
                    PlanComparisonRow(
                        feature = t("Advanced analytics"),
                        freeIncluded = false,
                        premiumIncluded = true
                    )
                    PlanComparisonRow(
                        feature = t("Priority support"),
                        freeIncluded = false,
                        premiumIncluded = true
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                PricingPlanCard(
                    model = PricingCardModel(
                        title = t("Monthly"),
                        priceLabel = "\$4.99 / month",
                        subtitle = t("Flexible monthly billing")
                    ),
                    selected = selectedBilling == BillingCycle.MONTHLY,
                    onClick = { onSelectBilling(BillingCycle.MONTHLY) }
                )
                PricingPlanCard(
                    model = PricingCardModel(
                        title = t("Yearly"),
                        priceLabel = "\$39.99 / year",
                        subtitle = t("Equivalent to \$3.33 / month"),
                        badge = t("Recommended")
                    ),
                    selected = selectedBilling == BillingCycle.YEARLY,
                    onClick = { onSelectBilling(BillingCycle.YEARLY) }
                )
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Button(
                        onClick = onSubscribe,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentPlan != SubscriptionPlan.PRO
                    ) {
                        Text(
                            if (currentPlan == SubscriptionPlan.PRO) {
                                t("Premium active")
                            } else {
                                tf(
                                    "Continue with %s",
                                    if (selectedBilling == BillingCycle.YEARLY) t("Yearly") else t("Monthly")
                                )
                            }
                        )
                    }
                    TextButton(
                        onClick = onRestorePurchase,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t("Restore purchase"))
                    }
                    Text(
                        text = t("Billing integration is shown in debug mode for now."),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}

private fun themeLabel(mode: AppThemeMode, language: AppLanguage): String {
    return when (mode) {
        AppThemeMode.SYSTEM -> translate(language, "System")
        AppThemeMode.LIGHT -> translate(language, "Light")
        AppThemeMode.DARK -> translate(language, "Dark")
    }
}

@Composable
private fun <T> SelectionDialog(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                options.forEach { option ->
                    val isSelected = option.first == selected
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppTheme.radius.md))
                            .clickable {
                                onSelect(option.first)
                                onDismiss()
                            },
                        color = if (isSelected) colors.primaryMuted else colors.backgroundSurfaceMuted
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.second,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = t("Selected"),
                                    tint = colors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(t("Close"))
            }
        }
    )
}

@Composable
private fun OnboardingCard(vm: MainViewModel, state: HabitUiState) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val canAdd = state.plan == SubscriptionPlan.PRO || state.tasks.size < 1
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(t("Create your first habit"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = t("Set up the basics first and add advanced options if needed."),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Button(
                onClick = vm::openCreateTask,
                enabled = canAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (canAdd) t("Create habit") else t("Upgrade to PRO"))
            }
        }
    }
}

@Composable
private fun OnboardingWizard(
    state: HabitUiState,
    onSkip: () -> Unit,
    onCreateHabit: (OnboardingHabitDraft) -> Unit,
    onHabitCreated: () -> Unit,
    onFinish: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke
    val radius = AppTheme.radius
    val context = LocalContext.current
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val pickerActionColor = colors.primary.toArgb()
    var step by rememberSaveable { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedCategory by rememberSaveable { mutableStateOf(HabitCategory.HEALTH) }
    var selectedTemplateId by rememberSaveable { mutableStateOf(HabitTemplateCatalog.templatesFor(HabitCategory.HEALTH).first().id) }
    var habitName by rememberSaveable { mutableStateOf("") }
    var onboardingTrackingType by rememberSaveable { mutableStateOf(TrackingType.YES_NO) }
    var onboardingDurationTarget by rememberSaveable { mutableStateOf(20) }
    var frequency by rememberSaveable { mutableStateOf(TaskFrequency.DAILY) }
    var customDays by rememberSaveable { mutableStateOf(listOf(1, 2, 3, 4, 5)) }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderHour by rememberSaveable(state.defaultReminderHour, state.defaultReminderMinute) {
        mutableStateOf(state.defaultReminderHour)
    }
    var reminderMinute by rememberSaveable(state.defaultReminderHour, state.defaultReminderMinute) {
        mutableStateOf(state.defaultReminderMinute)
    }
    var miniCalendarFill by remember(step) { mutableStateOf(0) }
    var templateRevealCount by remember(step, selectedCategory) { mutableStateOf(0) }
    var pendingCreate by remember { mutableStateOf(false) }
    var completionOverlayVisible by remember(step) { mutableStateOf(false) }
    var habitCreatedNotified by remember { mutableStateOf(false) }
    val templates = remember(selectedCategory) { HabitTemplateCatalog.templatesFor(selectedCategory) }
    val selectedTemplate = templates.firstOrNull { it.id == selectedTemplateId } ?: templates.first()
    val setupValid = remember(habitName, frequency, customDays, onboardingTrackingType, onboardingDurationTarget) {
        habitName.trim().isNotEmpty() &&
            (frequency != TaskFrequency.SELECTED_DAYS || customDays.isNotEmpty()) &&
            (onboardingTrackingType != TrackingType.DURATION || onboardingDurationTarget > 0)
    }

    LaunchedEffect(step) {
        if (step == OnboardingStep.WELCOME) {
            miniCalendarFill = 0
            repeat(5) { index ->
                delay(90)
                miniCalendarFill = index + 1
            }
        }
        if (step == OnboardingStep.READY) {
            completionOverlayVisible = true
        } else {
            completionOverlayVisible = false
        }
    }

    LaunchedEffect(step, selectedCategory) {
        if (step == OnboardingStep.TEMPLATE) {
            templateRevealCount = 0
            val total = templates.size
            repeat(total) { index ->
                delay(55)
                templateRevealCount = index + 1
            }
        }
    }

    LaunchedEffect(selectedCategory) {
        selectedTemplateId = HabitTemplateCatalog.templatesFor(selectedCategory).first().id
    }

    LaunchedEffect(pendingCreate, state.tasks.size) {
        if (pendingCreate && state.tasks.isNotEmpty()) {
            pendingCreate = false
            if (!habitCreatedNotified) {
                onHabitCreated()
                habitCreatedNotified = true
            }
            step = OnboardingStep.READY
        }
    }
    LaunchedEffect(pendingCreate) {
        if (pendingCreate) {
            delay(3500)
            if (state.tasks.isEmpty()) {
                pendingCreate = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            OnboardingHeader(
                onSkip = if (step == OnboardingStep.READY) null else onSkip
            )

            AnimatedContent(targetState = step, label = "onboardingStepTransition") { currentStep ->
                when (currentStep) {
                    OnboardingStep.WELCOME -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Micro Habit",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = t("Build powerful habits one day at a time"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                OnboardingMiniCalendar(filledDays = miniCalendarFill)
                                Button(
                                    onClick = { step = OnboardingStep.CATEGORY },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(radius.md)
                                ) {
                                    Text(t("Start"))
                                }
                            }
                        }
                    }

                    OnboardingStep.CATEGORY -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
                            ) {
                                Text(
                                    text = t("What do you want to improve?"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                HabitTemplateCatalog.categories.chunked(2).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                    ) {
                                        row.forEach { category ->
                                            OnboardingCategoryCard(
                                                modifier = Modifier.weight(1f),
                                                title = t(HabitTemplateCatalog.categoryTitleKey(category)),
                                                selected = selectedCategory == category,
                                                onClick = { selectedCategory = category }
                                            )
                                        }
                                        if (row.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.WELCOME },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = { step = OnboardingStep.TEMPLATE },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Next"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.TEMPLATE -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1)
                            ) {
                                Text(
                                    text = t("Pick a template"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                templates.forEachIndexed { index, template ->
                                    AnimatedVisibility(
                                        visible = index < templateRevealCount,
                                        enter = fadeIn(tween(180)) + slideInVertically(
                                            initialOffsetY = { it / 5 },
                                            animationSpec = tween(180)
                                        ),
                                        exit = fadeOut(tween(120))
                                    ) {
                                        OnboardingTemplateCard(
                                            title = t(template.titleKey),
                                            emoji = template.emoji,
                                            selected = template.id == selectedTemplateId,
                                            onClick = { selectedTemplateId = template.id }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.CATEGORY },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = {
                                            if (habitName.isBlank() && selectedTemplate.id != HabitTemplateCatalog.CUSTOM_TEMPLATE.id) {
                                                habitName = translate(state.language, selectedTemplate.titleKey)
                                                    .take(MAX_HABIT_TITLE_LENGTH)
                                            }
                                            step = OnboardingStep.SETUP
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Next"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.SETUP -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
                            ) {
                                Text(
                                    text = t("Set up your habit"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = habitName,
                                    onValueChange = { habitName = it.take(MAX_HABIT_TITLE_LENGTH) },
                                    label = { Text(t("Habit name")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                FormSection(title = t("Tracking type")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        listOf(
                                            Triple(
                                                TrackingType.YES_NO,
                                                t("Do once"),
                                                t("Just mark whether you did it today")
                                            ),
                                            Triple(
                                                TrackingType.DURATION,
                                                t("Do N minutes"),
                                                t("Set a daily time target")
                                            )
                                        ).forEach { (type, title, description) ->
                                            val selected = onboardingTrackingType == type
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(radius.md))
                                                    .clickable { onboardingTrackingType = type },
                                                shape = RoundedCornerShape(radius.md),
                                                color = if (selected) colors.primary.copy(alpha = 0.14f) else colors.backgroundSurfaceMuted,
                                                border = BorderStroke(
                                                    width = if (selected) stroke.medium else stroke.thin,
                                                    color = if (selected) colors.primary else colors.borderSubtle
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                                                    verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                                ) {
                                                    Text(
                                                        text = title,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = colors.textSecondary
                                                    )
                                                }
                                            }
                                        }
                                        if (onboardingTrackingType == TrackingType.DURATION) {
                                            Stepper(
                                                label = t("Daily minute goal"),
                                                value = onboardingDurationTarget,
                                                min = 1,
                                                max = 600,
                                                onValueChange = { onboardingDurationTarget = it }
                                            )
                                        }
                                    }
                                }

                                FormSection(title = t("Frequency")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                        ) {
                                            SelectChip(
                                                title = t("Every day"),
                                                selected = frequency == TaskFrequency.DAILY,
                                                onClick = { frequency = TaskFrequency.DAILY }
                                            )
                                            SelectChip(
                                                title = t("Selected weekdays"),
                                                selected = frequency == TaskFrequency.SELECTED_DAYS,
                                                onClick = { frequency = TaskFrequency.SELECTED_DAYS }
                                            )
                                        }
                                        if (frequency == TaskFrequency.SELECTED_DAYS) {
                                            WeekdaySelector(
                                                selectedDays = customDays.toSet(),
                                                onToggle = { day ->
                                                    val next = customDays.toMutableSet()
                                                    if (!next.add(day)) {
                                                        next.remove(day)
                                                    }
                                                    customDays = next.toList().sorted()
                                                }
                                            )
                                            if (customDays.isEmpty()) {
                                                Text(
                                                    text = t("Select at least one weekday."),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colors.danger
                                                )
                                            }
                                        }
                                    }
                                }

                                FormSection(title = t("Reminders")) {
                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                        SettingsSwitchRow(
                                            title = t("Reminders"),
                                            subtitle = t("Enable habit reminder notifications"),
                                            checked = reminderEnabled,
                                            onCheckedChange = { reminderEnabled = it }
                                        )
                                        AnimatedVisibility(visible = reminderEnabled) {
                                            OutlinedButton(
                                                onClick = {
                                                    showThemedTimePicker(
                                                        context = context,
                                                        themeResId = pickerTheme,
                                                        initialHour = reminderHour,
                                                        initialMinute = reminderMinute,
                                                        is24HourView = is24HourView,
                                                        actionColorArgb = pickerActionColor,
                                                        onTimeSet = { hour, minute ->
                                                            reminderHour = hour.coerceIn(0, 23)
                                                            reminderMinute = minute.coerceIn(0, 59)
                                                        }
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(radius.md),
                                                border = BorderStroke(stroke.thin, colors.primary),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = Color.Transparent,
                                                    contentColor = colors.primary
                                                )
                                            ) {
                                                Text(
                                                    tf(
                                                        "Reminder: %s",
                                                        formatTimeForDevice(context, reminderHour, reminderMinute)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                ) {
                                    OutlinedButton(
                                        onClick = { step = OnboardingStep.TEMPLATE },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Back"))
                                    }
                                    Button(
                                        onClick = {
                                            if (pendingCreate) return@Button
                                            if (!setupValid) return@Button
                                            pendingCreate = true
                                            onCreateHabit(
                                                OnboardingHabitDraft(
                                                    name = habitName.trim(),
                                                    category = selectedCategory,
                                                    template = selectedTemplate,
                                                    trackingType = onboardingTrackingType,
                                                    dailyTarget = if (onboardingTrackingType == TrackingType.DURATION) {
                                                        onboardingDurationTarget
                                                    } else {
                                                        1
                                                    },
                                                    unitLabel = "",
                                                    frequency = frequency,
                                                    customDays = customDays.toSet(),
                                                    reminderEnabled = reminderEnabled,
                                                    reminderHour = reminderHour,
                                                    reminderMinute = reminderMinute
                                                )
                                            )
                                        },
                                        enabled = setupValid,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.md)
                                    ) {
                                        Text(t("Create habit"))
                                    }
                                }
                            }
                        }
                    }

                    OnboardingStep.READY -> {
                        GlassCard(
                            contentPadding = PaddingValues(spacing.x2)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1_5),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.success,
                                    modifier = Modifier.size(spacing.x6)
                                )
                                Text(
                                    text = t("Your habit is ready"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = t("We highlighted the completion button so you can log your first win."),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                                AnimatedVisibility(
                                    visible = completionOverlayVisible,
                                    enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.9f, animationSpec = tween(180)),
                                    exit = fadeOut(tween(140))
                                ) {
                                    StreakRewardOverlay(
                                        model = StreakOverlayModel(streak = 1, milestone = false)
                                    )
                                }
                                Button(
                                    onClick = onFinish,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(radius.md)
                                ) {
                                    Text(t("Go to tracker"))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            OnboardingProgressDots(
                step = step,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 25.dp)
            )
        }
    }
}

@Composable
private fun OnboardingHeader(onSkip: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onSkip != null) {
            TextButton(onClick = onSkip) {
                Text(t("Skip"))
            }
        }
    }
}

@Composable
private fun OnboardingProgressDots(step: OnboardingStep, modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val current = when (step) {
        OnboardingStep.WELCOME -> 1
        OnboardingStep.CATEGORY -> 2
        OnboardingStep.TEMPLATE -> 3
        OnboardingStep.SETUP -> 4
        OnboardingStep.READY -> 4
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(if (index < current) spacing.x1 else spacing.x0_5 + spacing.x1)
                    .clip(RoundedCornerShape(AppTheme.radius.full))
                    .background(if (index < current) colors.primary else colors.borderSubtle.copy(alpha = 0.55f))
            )
        }
    }
}

@Composable
private fun OnboardingMiniCalendar(filledDays: Int) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val labels = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
            labels.forEachIndexed { index, label ->
                val filled = index < filledDays
                val scale by animateFloatAsState(
                    targetValue = if (filled) 1f else 0.95f,
                    animationSpec = tween(durationMillis = 180),
                    label = "onboardingCalendarScale$index"
                )
                Box(
                    modifier = Modifier
                        .size(spacing.x3)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(AppTheme.radius.md))
                        .background(
                            if (filled) colors.success.copy(alpha = 0.9f)
                            else colors.backgroundSurfaceMuted
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (filled) Color.White else colors.textSecondary
                    )
                }
            }
        }
        Text(
            text = tf("%d day streak", filledDays.coerceAtLeast(1)),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun OnboardingCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.97f,
        animationSpec = tween(durationMillis = 150),
        label = "onboardingCategoryScale"
    )
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        color = if (selected) colors.primaryMuted else colors.backgroundSurfaceMuted
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.x1_5, vertical = AppTheme.spacing.x1_5),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun OnboardingTemplateCard(
    title: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.985f,
        animationSpec = tween(durationMillis = 140),
        label = "onboardingTemplateScale"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        color = if (selected) colors.primaryMuted else colors.backgroundSurfaceMuted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.x1_5, vertical = spacing.x1_5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = t("Selected"),
                    tint = colors.primary
                )
            }
        }
    }
}

@Composable
private fun TaskControlsRow(
    canAddTask: Boolean,
    onCreate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    canEditDelete: Boolean
) {
    val spacing = AppTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = if (canAddTask) t("New") else "PRO",
            icon = if (canAddTask) Icons.Rounded.AddCircle else Icons.Rounded.WorkspacePremium,
            onClick = onCreate
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Edit"),
            icon = Icons.Rounded.Edit,
            onClick = onEdit,
            enabled = canEditDelete
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Delete"),
            icon = Icons.Rounded.Delete,
            onClick = onDelete,
            enabled = canEditDelete,
            danger = true
        )
    }
}

@Composable
private fun TaskControlButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    danger: Boolean = false
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val container = when {
        !enabled -> semantic.backgroundSurfaceMuted
        danger -> MaterialTheme.colorScheme.errorContainer
        else -> semantic.backgroundSurface
    }
    val content = when {
        !enabled -> semantic.textTertiary
        danger -> MaterialTheme.colorScheme.onErrorContainer
        else -> semantic.textPrimary
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(spacing.x6),
        shape = RoundedCornerShape(radius.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = semantic.backgroundSurfaceMuted,
            disabledContentColor = semantic.textTertiary
        ),
        contentPadding = PaddingValues(horizontal = spacing.x1, vertical = spacing.x0)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(spacing.x2))
            Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TaskSelector(
    tasks: List<HabitTask>,
    selectedTaskId: String?,
    onSelect: (String) -> Unit,
    onAddHabit: (() -> Unit)? = null
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val density = LocalDensity.current
    val selectedTask = tasks.firstOrNull { it.id == selectedTaskId } ?: tasks.firstOrNull()
    var expanded by rememberSaveable(tasks.map { it.id }, selectedTaskId) { mutableStateOf(false) }
    var triggerSize by remember { mutableStateOf(IntSize.Zero) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "taskSelectorArrowRotation"
    )
    val dropdownTransitionState = remember { MutableTransitionState(false) }
    val dropdownVisible = dropdownTransitionState.currentState || dropdownTransitionState.targetState

    LaunchedEffect(expanded) {
        dropdownTransitionState.targetState = expanded
    }

    GlassCard(contentPadding = PaddingValues(spacing.x1)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(radius.md))
                    .onGloballyPositioned { coordinates ->
                        triggerSize = coordinates.size
                    }
                    .clickable(enabled = tasks.isNotEmpty()) { expanded = !expanded },
                color = semantic.backgroundSurfaceMuted
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                    ) {
                        Text(
                            text = selectedTask?.emoji?.ifBlank { "✨" } ?: "✨",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = selectedTask?.title ?: t("No active habit"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
                            color = semantic.textPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = semantic.textSecondary,
                        modifier = Modifier.graphicsLayer { rotationZ = arrowRotation }
                    )
                }
            }

            if (dropdownVisible && tasks.isNotEmpty() && triggerSize.width > 0) {
                val popupOffset = IntOffset(
                    x = 0,
                    y = triggerSize.height + with(density) { spacing.x0_5.roundToPx() }
                )
                Popup(
                    alignment = Alignment.TopStart,
                    offset = popupOffset,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        clippingEnabled = false
                    )
                ) {
                    AnimatedVisibility(
                        visibleState = dropdownTransitionState,
                        enter = fadeIn(animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)) +
                            slideInVertically(
                                initialOffsetY = { -it / 6 },
                                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                            ),
                        exit = fadeOut(animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)) +
                            slideOutVertically(
                                targetOffsetY = { -it / 8 },
                                animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                            )
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(with(density) { triggerSize.width.toDp() })
                                .heightIn(max = 320.dp),
                            shape = RoundedCornerShape(radius.md),
                            color = semantic.backgroundSurface,
                            tonalElevation = 6.dp,
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = spacing.x0_5)
                            ) {
                                tasks.forEach { task ->
                                    val selected = task.id == selectedTaskId
                                    val rowColor = if (selected) {
                                        semantic.primary.copy(alpha = 0.12f)
                                    } else {
                                        Color.Transparent
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = spacing.x0_5, vertical = 1.dp)
                                            .clip(RoundedCornerShape(radius.sm))
                                            .background(rowColor)
                                            .clickable {
                                                expanded = false
                                                onSelect(task.id)
                                            }
                                            .padding(horizontal = spacing.x1, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                    ) {
                                        Text(
                                            text = task.emoji.ifBlank { "✨" },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = task.title,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = semantic.textPrimary
                                        )
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = t("Selected"),
                                                tint = semantic.primary
                                            )
                                        }
                                    }
                                }
                                onAddHabit?.let { onAdd ->
                                    Spacer(modifier = Modifier.height(spacing.x0_5))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = spacing.x0_5, vertical = 1.dp)
                                            .clip(RoundedCornerShape(radius.sm))
                                            .clickable {
                                                expanded = false
                                                onAdd()
                                            }
                                            .padding(horizontal = spacing.x1, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddCircle,
                                            contentDescription = null,
                                            tint = semantic.primary,
                                            modifier = Modifier.size(spacing.x2)
                                        )
                                        Text(
                                            text = t("Create habit"),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = semantic.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackerHabitContextHeader(
    tasks: List<HabitTask>,
    selectedTaskId: String?,
    onSelect: (String) -> Unit,
    onAddHabit: () -> Unit
) {
    TaskSelector(
        tasks = tasks,
        selectedTaskId = selectedTaskId,
        onSelect = onSelect,
        onAddHabit = onAddHabit
    )
}

@Composable
private fun HeroCard(
    task: HabitTask?,
    selectedDate: LocalDate,
    done: Boolean,
    partial: Boolean,
    scheduled: Boolean,
    selectedValue: Int,
    selectedTarget: Int,
    selectedUnit: String,
    selectedCompletionPercent: Int,
    minimumCompletionPercent: Int,
    plan: SubscriptionPlan,
    durationTimerRunning: Boolean,
    durationTimerElapsedSeconds: Int,
    onDone: () -> Unit,
    onIncrementValue: (Int) -> Unit,
    onStartDurationTimer: () -> Boolean,
    onStopDurationTimerAndApply: () -> Int,
    onOpenPaywall: () -> Unit,
    highlightMarkButton: Boolean,
    onHighlightConsumed: () -> Unit,
    appThemeMode: AppThemeMode,
    swipeEnabled: Boolean = false,
    onSwipeNext: () -> Unit = {},
    onSwipePrevious: () -> Unit = {}
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    val context = LocalContext.current
    val locale = appLocale()
    val trackingType = task?.trackingType ?: TrackingType.YES_NO
    val isValueTracking = trackingType != TrackingType.YES_NO
    val isDurationTracking = trackingType == TrackingType.DURATION
    val isCountTracking = trackingType == TrackingType.COUNT
    val isPro = plan == SubscriptionPlan.PRO
    val canMarkForSelectedDate = scheduled
    val highlightActive = highlightMarkButton && !done && canMarkForSelectedDate
    val pressScaleTarget = 0.985f
    val pressScaleDuration = 80
    val density = LocalDensity.current
    val systemDark = isSystemInDarkTheme()
    val useDarkCompletedLottie = when (appThemeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }
    val swipeThresholdPx = with(density) { 56.dp.toPx() }
    var horizontalDragDistance by remember(task?.id, selectedDate) { mutableStateOf(0f) }
    val completedButtonLottieResId = remember(context, useDarkCompletedLottie) {
        val packageName = context.packageName
        if (useDarkCompletedLottie) {
            val darkUnderscore = context.resources.getIdentifier("completed_button_lottie_dark", "raw", packageName)
            val darkDash = context.resources.getIdentifier("completed_button_lottie-dark", "raw", packageName)
            when {
                darkUnderscore != 0 -> darkUnderscore
                darkDash != 0 -> darkDash
                else -> context.resources.getIdentifier("completed_button_lottie", "raw", packageName)
            }
        } else {
            context.resources.getIdentifier("completed_button_lottie", "raw", packageName)
        }
    }
    val completedButtonComposition by if (completedButtonLottieResId != 0) {
        rememberLottieComposition(LottieCompositionSpec.RawRes(completedButtonLottieResId))
    } else {
        remember { mutableStateOf(null) }
    }
    val completedTargetFrame = 22f
    val completedTargetProgress = remember(completedButtonComposition) {
        val durationFrames = completedButtonComposition?.durationFrames ?: 1f
        (completedTargetFrame / durationFrames.coerceAtLeast(1f)).coerceIn(0f, 1f)
    }
    val completionPulseScale = remember(task?.id, selectedDate) { Animatable(1f) }
    var completedAnimationPlaying by remember(task?.id, selectedDate) { mutableStateOf(false) }
    var completedLottieReady by remember(task?.id, selectedDate) { mutableStateOf(done) }
    var showSuccessMessage by remember(task?.id, selectedDate) { mutableStateOf(false) }
    var previousDoneState by remember(task?.id, selectedDate) { mutableStateOf(done) }
    val completedAnimationProgress by animateLottieCompositionAsState(
        composition = completedButtonComposition,
        iterations = 1,
        isPlaying = completedAnimationPlaying,
        speed = 1f
    )
    val completedButtonLottieProgress = when {
        done && completedAnimationPlaying -> completedAnimationProgress
        done && completedLottieReady -> completedTargetProgress
        else -> 0f
    }
    LaunchedEffect(done, task?.id, selectedDate) {
        if (done && !previousDoneState) {
            showSuccessMessage = true
            completedAnimationPlaying = false
            completedLottieReady = false
            completionPulseScale.snapTo(1f)
            completionPulseScale.animateTo(
                targetValue = 1.018f,
                animationSpec = tween(durationMillis = 95, easing = FastOutSlowInEasing)
            )
            delay(40)
            completedAnimationPlaying = true
            completionPulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
            delay(1400)
            showSuccessMessage = false
        }
        if (!done) {
            completedAnimationPlaying = false
            completionPulseScale.snapTo(1f)
            showSuccessMessage = false
            completedLottieReady = false
        }
        previousDoneState = done
    }
    LaunchedEffect(completedAnimationPlaying, completedAnimationProgress, completedTargetProgress) {
        if (completedAnimationPlaying && completedAnimationProgress >= completedTargetProgress) {
            completedAnimationPlaying = false
            completedLottieReady = true
        }
    }
    val highlightPulseTransition = rememberInfiniteTransition(label = "heroHighlightPulse")
    val highlightPulse by highlightPulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroHighlightPulseScale"
    )
    var durationManualInput by remember(task?.id, selectedDate) { mutableStateOf("") }
    val unitLabel = when {
        trackingType == TrackingType.DURATION -> t("min")
        trackingType == TrackingType.COUNT && selectedUnit.isNotBlank() -> selectedUnit
        trackingType == TrackingType.COUNT -> t("times")
        else -> ""
    }
    val timerLabel = remember(durationTimerElapsedSeconds) {
        val totalSeconds = durationTimerElapsedSeconds.coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }
    val addedMinutesTemplate = t("Added %d min")
    val progressLabel = when (trackingType) {
        TrackingType.YES_NO -> ""
        TrackingType.COUNT -> "$selectedValue / $selectedTarget $unitLabel"
        TrackingType.DURATION -> "$selectedValue / $selectedTarget ${t("min")}"
    }
    val completionStatusText = when {
        done -> t("Completed")
        partial -> tf("%d%% of %d%% threshold", selectedCompletionPercent, minimumCompletionPercent)
        else -> t("In progress")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!swipeEnabled) {
                    Modifier
                } else {
                    Modifier.pointerInput(task?.id, selectedDate, swipeEnabled) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                horizontalDragDistance += dragAmount
                            },
                            onDragEnd = {
                                when {
                                    horizontalDragDistance <= -swipeThresholdPx -> onSwipeNext()
                                    horizontalDragDistance >= swipeThresholdPx -> onSwipePrevious()
                                }
                                horizontalDragDistance = 0f
                            },
                            onDragCancel = { horizontalDragDistance = 0f }
                        )
                    }
                }
            ),
        verticalArrangement = Arrangement.spacedBy(spacing.x1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale)),
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
            if (!canMarkForSelectedDate) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .height(56.dp),
                        shape = RoundedCornerShape(radius.full),
                        border = BorderStroke(stroke.thin * 1.5f, semantic.borderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            disabledContentColor = semantic.textSecondary
                        )
                    ) {
                        Text(
                            text = t("Not scheduled for this date"),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else if (!isValueTracking && done) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (pressed) pressScaleTarget else 1f,
                    animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                    label = "completeButtonPressScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val showCompletedLottie = completedButtonComposition != null &&
                        (completedAnimationPlaying || completedLottieReady)
                    val completedLottieSize = (spacing.x2 + spacing.x1) * 1.5f
                    val completedLottieSlotWidth by animateDpAsState(
                        targetValue = if (showCompletedLottie) completedLottieSize else 0.dp,
                        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                        label = "completedLottieSlotWidth"
                    )
                    Button(
                        onClick = onDone,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .height(56.dp)
                            .graphicsLayer {
                                scaleX = pressScale * completionPulseScale.value
                                scaleY = pressScale * completionPulseScale.value
                            },
                        shape = RoundedCornerShape(radius.full),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = semantic.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.5.dp)
                        ) {
                            Text(
                                text = t("Completed"),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W600)
                            )
                            Box(
                                modifier = Modifier
                                    .width(completedLottieSlotWidth)
                                    .height(completedLottieSize)
                                    .clip(RoundedCornerShape(AppTheme.radius.sm)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (completedButtonComposition != null && completedLottieSlotWidth > 0.dp) {
                                    LottieAnimation(
                                        composition = completedButtonComposition,
                                        progress = { completedButtonLottieProgress },
                                        modifier = Modifier.size(completedLottieSize)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!isValueTracking) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (pressed) pressScaleTarget else 1f,
                    animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                    label = "markButtonPressScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            if (highlightActive) onHighlightConsumed()
                            onDone()
                        },
                        enabled = canMarkForSelectedDate,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .height(56.dp)
                            .graphicsLayer {
                                val pulse = if (highlightActive && !pressed) highlightPulse else 1f
                                scaleX = pressScale * pulse * completionPulseScale.value
                                scaleY = pressScale * pulse * completionPulseScale.value
                            },
                        shape = RoundedCornerShape(radius.full),
                        border = BorderStroke(
                            stroke.thin * if (highlightActive) 2.4f else 1.5f,
                            when {
                                !canMarkForSelectedDate -> semantic.borderSubtle
                                highlightActive -> semantic.success
                                else -> semantic.primary
                            }
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = if (highlightActive) semantic.success else semantic.primary,
                            disabledContentColor = semantic.textSecondary
                        )
                    ) {
                        Text(
                            text = when {
                                !canMarkForSelectedDate -> t("Not scheduled for this date")
                                else -> t("Mark as done")
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.x1),
                    contentPadding = PaddingValues(spacing.x1_5)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.x1),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = progressLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = semantic.textPrimary
                        )
                        Text(
                            text = completionStatusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (done) semantic.success else semantic.textSecondary
                        )

                        if (isCountTracking) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onIncrementValue(-1) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(radius.full)
                                ) {
                                    Text("−")
                                }
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(radius.md),
                                    color = semantic.backgroundSurfaceMuted
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = selectedValue.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = semantic.textPrimary
                                        )
                                    }
                                }
                                OutlinedButton(
                                    onClick = { onIncrementValue(1) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(radius.full)
                                ) {
                                    Text("+")
                                }
                            }
                        }

                        if (isDurationTracking) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                            ) {
                                listOf(5, 10, 15).forEach { delta ->
                                    OutlinedButton(
                                        onClick = { onIncrementValue(delta) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.full)
                                    ) {
                                        Text("+$delta")
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = durationManualInput,
                                    onValueChange = { value ->
                                        durationManualInput = value.filter { it.isDigit() }.take(4)
                                    },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    label = { Text(t("Manual minutes")) },
                                    placeholder = { Text("20") }
                                )
                                Button(
                                    onClick = {
                                        val minutes = durationManualInput.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                        if (minutes > 0) {
                                            onIncrementValue(minutes)
                                            durationManualInput = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(radius.md)
                                ) {
                                    Text(t("Add"))
                                }
                            }

                            if (isPro) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (durationTimerRunning) {
                                                val addedMinutes = onStopDurationTimerAndApply()
                                                if (addedMinutes > 0) {
                                                    Toast.makeText(
                                                        context,
                                                        String.format(locale, addedMinutesTemplate, addedMinutes),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                onStartDurationTimer()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(radius.full)
                                    ) {
                                        Text(if (durationTimerRunning) t("Stop timer") else t("Start timer"))
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(radius.md),
                                        color = semantic.backgroundSurfaceMuted
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = timerLabel,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onOpenPaywall,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(radius.full)
                                ) {
                                    Text(t("Timer (Premium)"))
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSuccessMessage,
                    enter = fadeIn(animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)
                        ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)) +
                        slideOutVertically(
                            targetOffsetY = { -it / 4 },
                            animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)
                        )
                ) {
                    Text(
                        text = t("Great job, your streak is safe."),
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.success
                    )
                }
            }

    }
}

@Composable
private fun StreakRewardOverlay(model: StreakOverlayModel) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors
    val container by animateColorAsState(
        targetValue = colors.backgroundSurface,
        animationSpec = tween(durationMillis = 220),
        label = "streakOverlayContainer"
    )
    val subtitle = if (model.streak <= 1) t("Great job") else t("Keep it going")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.x2),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = "🔥",
                style = MaterialTheme.typography.titleLarge
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
            ) {
                Text(
                    text = tf("%d day streak", model.streak),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun MilestoneCelebrationDialog(
    model: StreakOverlayModel,
    currentStreak: Int,
    bestStreak: Int,
    completion30Day: Int,
    points: List<Int>,
    onDismiss: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(t("Continue"))
            }
        },
        title = {
            Text(
                text = t("Streak milestone"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                MilestoneFlameHero(streak = model.streak)

                Text(
                    text = t("Amazing consistency"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = t("You're building momentum"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    MilestoneMetric(
                        modifier = Modifier.weight(1f),
                        label = t("Current streak"),
                        value = "${currentStreak}d"
                    )
                    MilestoneMetric(
                        modifier = Modifier.weight(1f),
                        label = t("Best streak"),
                        value = "${bestStreak}d"
                    )
                    MilestoneMetric(
                        modifier = Modifier.weight(1f),
                        label = t("30 day completion"),
                        value = "${completion30Day}%"
                    )
                }

                MilestonePreview(points = points)
            }
        },
        shape = RoundedCornerShape(radius.lg),
        containerColor = colors.backgroundSurface
    )
}

@Composable
private fun MilestoneFlameHero(streak: Int) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors
    val flameSize = spacing.x6 * 2f
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.streak_milestone_lottie)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = lottieComposition != null,
        speed = 1f
    )
    val infinite = rememberInfiniteTransition(label = "milestoneFlameMotion")
    val flamePulse by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 920, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flamePulse"
    )
    val flameFlicker by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 360, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )
    val flameDriftY by infinite.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameDriftY"
    )
    val glowPulse by infinite.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameGlowPulse"
    )
    val sweepProgress = remember(streak) { Animatable(-1.2f) }
    val sweepAlpha = remember(streak) { Animatable(0f) }
    LaunchedEffect(streak) {
        sweepProgress.snapTo(-1.2f)
        sweepAlpha.snapTo(0f)
        sweepAlpha.animateTo(
            targetValue = 0.78f,
            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
        )
        sweepProgress.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing)
        )
        sweepAlpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 180))
        delay(560)
        sweepProgress.snapTo(-1.2f)
        sweepAlpha.snapTo(0f)
        sweepAlpha.animateTo(
            targetValue = 0.52f,
            animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
        )
        sweepProgress.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
        sweepAlpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 160))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.x6 * 2.4f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(flameSize * 1.64f)
                    .graphicsLayer {
                        scaleX = glowPulse
                        scaleY = glowPulse
                        alpha = 0.12f
                    }
                    .clip(RoundedCornerShape(radius.full))
                    .background(colors.success.copy(alpha = 0.24f))
            )
            Box(
                modifier = Modifier
                    .size(flameSize * 1.28f)
                    .graphicsLayer {
                        scaleX = glowPulse
                        scaleY = glowPulse
                        alpha = 0.2f
                    }
                    .clip(RoundedCornerShape(radius.full))
                    .background(colors.primary.copy(alpha = 0.34f))
            )
            Box(
                modifier = Modifier
                    .size(flameSize)
                    .graphicsLayer {
                        translationY = flameDriftY
                        scaleX = flamePulse
                        scaleY = flamePulse
                        alpha = flameFlicker
                    }
                    .clip(RoundedCornerShape(radius.full))
                    .background(colors.backgroundSurface)
                    .border(
                        border = BorderStroke(2.dp, colors.primary.copy(alpha = 0.28f)),
                        shape = RoundedCornerShape(radius.full)
                    )
                    .drawWithContent {
                        drawContent()
                        if (sweepAlpha.value > 0.01f) {
                            val band = size.width * 0.34f
                            val centerX = sweepProgress.value * size.width
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = sweepAlpha.value),
                                        Color.Transparent
                                    ),
                                    start = Offset(centerX - band, 0f),
                                    end = Offset(centerX + band, size.height)
                                ),
                                alpha = 0.95f
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (lottieComposition != null) {
                    LottieAnimation(
                        composition = lottieComposition,
                        progress = { lottieProgress },
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                            .padding(spacing.x0_5)
                    )
                } else {
                    Text(
                        text = "🔥",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        }

        Text(
            text = tf("%d day streak", streak),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun MilestoneMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(radius.md))
            .background(colors.backgroundSurfaceMuted)
            .padding(horizontal = spacing.x1, vertical = spacing.x1),
        verticalArrangement = Arrangement.spacedBy(spacing.x0_5),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MilestonePreview(points: List<Int>) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors
    val safe = if (points.size == 7) points else List(7) { 0 }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius.md))
                .background(colors.backgroundSurfaceMuted)
                .padding(horizontal = spacing.x1, vertical = spacing.x1),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = t("7 day chart"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                safe.forEach { point ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.x2)
                            .clip(RoundedCornerShape(radius.full))
                            .background(
                                if (point > 0) colors.success.copy(alpha = 0.9f)
                                else colors.borderSubtle.copy(alpha = 0.6f)
                            )
                    )
                }
            }
        }
    }
}

private fun isStreakMilestone(streak: Int): Boolean {
    if (streak <= 0) return false
    if (streak in setOf(1, 7, 15, 30, 60, 90, 120)) return true
    return streak > 120 && streak % 30 == 0
}

@Composable
private fun TrackerStreakRow(
    streak: Int,
    bestStreak: Int
) {
    val spacing = AppTheme.spacing
    val streakScale = remember { Animatable(1f) }
    var previousStreak by remember { mutableStateOf(streak) }

    LaunchedEffect(streak) {
        if (streak > previousStreak) {
            streakScale.snapTo(1f)
            streakScale.animateTo(
                targetValue = 1.12f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
            streakScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
        }
        previousStreak = streak
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        StatTile(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            label = t("Current streak"),
            value = if (streak >= 7) "🔥 ${streak}d" else "${streak}d",
            valueScale = streakScale.value
        )
        StatTile(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            label = t("Best streak"),
            value = "${bestStreak}d"
        )
    }
}

@Composable
private fun StatsRow(streak: Int, bestStreak: Int, progress: Int, total: Int) {
    val spacing = AppTheme.spacing
    val streakScale = remember { Animatable(1f) }
    var previousStreak by remember { mutableStateOf(streak) }
    var previousTotal by remember { mutableStateOf(total) }

    LaunchedEffect(streak, total) {
        val shouldAnimate = total == previousTotal + 1 && streak >= previousStreak
        if (shouldAnimate) {
            streakScale.snapTo(1f)
            streakScale.animateTo(
                targetValue = 1.12f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
            streakScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
        }
        previousStreak = streak
        previousTotal = total
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            StatTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                label = t("Current streak"),
                value = if (streak >= 7) "🔥 ${streak}d" else "${streak}d",
                valueScale = streakScale.value
            )
            StatTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                label = t("Best streak"),
                value = "${bestStreak}d"
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            StatTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                label = t("30 day completion"),
                value = "${progress}%"
            )
            StatTile(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                label = t("Total completions"),
                value = "$total"
            )
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueScale: Float = 1f
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors

    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = semantic.backgroundSurface),
        shape = RoundedCornerShape(radius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = spacing.x2, vertical = spacing.x2),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedContent(targetState = value, label = "statValue") { animatedValue ->
                Text(
                    text = animatedValue,
                    modifier = Modifier.graphicsLayer {
                        scaleX = valueScale
                        scaleY = valueScale
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary,
                minLines = 2,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SevenDayChart(
    points: List<Int>,
    scheduled: List<Boolean>,
    anchorDate: LocalDate
) {
    val spacing = AppTheme.spacing
    val locale = appLocale()
    val safe = if (points.size == 7) points else List(7) { 0 }
    val safeScheduled = if (scheduled.size == 7) scheduled else List(7) { false }
    val today = LocalDate.now()

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(t("7 day chart"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                safe.forEachIndexed { index, value ->
                    val date = anchorDate.minusDays((6 - index).toLong())
                    val isToday = date == today
                    val isFuture = date.isAfter(today)
                    val progressPercent = value.coerceIn(0, 100)
                    val isDone = progressPercent >= 100
                    val isPartial = progressPercent in 1..99
                    val isScheduled = safeScheduled[index]
                    val state = when {
                        isFuture -> CalendarDayState.FUTURE
                        isDone -> CalendarDayState.COMPLETED
                        isPartial && isScheduled -> CalendarDayState.PARTIAL
                        isScheduled && date.isBefore(today) -> CalendarDayState.MISSED
                        else -> CalendarDayState.NOT_SCHEDULED
                    }
                    val leftDone = if (index > 0) {
                        val leftDate = anchorDate.minusDays((6 - (index - 1)).toLong())
                        safe[index - 1].coerceIn(0, 100) >= 100 && !leftDate.isAfter(today)
                    } else {
                        false
                    }
                    val rightDone = if (index < safe.lastIndex) {
                        val rightDate = anchorDate.minusDays((6 - (index + 1)).toLong())
                        safe[index + 1].coerceIn(0, 100) >= 100 && !rightDate.isAfter(today)
                    } else {
                        false
                    }
                    DayBar(
                        modifier = Modifier.weight(1f),
                        state = state,
                        progressPercent = progressPercent,
                        isScheduled = isScheduled,
                        isToday = isToday,
                        connectLeft = isDone && leftDone,
                        connectRight = isDone && rightDone,
                        label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBar(
    modifier: Modifier = Modifier,
    state: CalendarDayState,
    progressPercent: Int,
    isScheduled: Boolean,
    isToday: Boolean,
    connectLeft: Boolean,
    connectRight: Boolean,
    label: String
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    val safeProgress = progressPercent.coerceIn(0, 100)
    val completionProgress by animateFloatAsState(
        targetValue = safeProgress / 100f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dayBarCompletionProgress"
    )
    val connectorProgress by animateFloatAsState(
        targetValue = if (
            state == CalendarDayState.COMPLETED &&
            (connectLeft || connectRight)
        ) {
            if (connectLeft && connectRight) 1f else 0.7f
        } else {
            0f
        },
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dayBarConnectorProgress"
    )
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            CalendarDayState.COMPLETED -> semantic.success.copy(alpha = 0.90f)
            CalendarDayState.PARTIAL -> semantic.successMuted.copy(alpha = 0.72f)
            CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.08f)
            CalendarDayState.NOT_SCHEDULED -> semantic.neutralMuted.copy(alpha = 0.42f)
            CalendarDayState.FUTURE -> semantic.backgroundSurfaceMuted.copy(alpha = 0.28f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "dayBarBackgroundColor"
    )
    val borderColor = when (state) {
        CalendarDayState.COMPLETED -> Color.Transparent
        CalendarDayState.PARTIAL -> semantic.success.copy(alpha = 0.35f)
        CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.45f)
        CalendarDayState.NOT_SCHEDULED -> semantic.borderSubtle
        CalendarDayState.FUTURE -> semantic.borderSubtle.copy(alpha = 0.4f)
    }
    val dayColor = when (state) {
        CalendarDayState.COMPLETED -> MaterialTheme.colorScheme.onPrimary
        CalendarDayState.PARTIAL -> semantic.success
        CalendarDayState.MISSED -> semantic.danger
        CalendarDayState.NOT_SCHEDULED -> semantic.textSecondary
        CalendarDayState.FUTURE -> semantic.textTertiary
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.x5 + spacing.x1)
                .clip(RoundedCornerShape(radius.sm)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(connectorProgress)
                    .height(spacing.x1)
                    .background(semantic.successMuted.copy(alpha = completionProgress), RoundedCornerShape(radius.full))
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.x5 + spacing.x1)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(
                            topStart = if (state == CalendarDayState.COMPLETED && connectLeft) 4.dp else radius.sm,
                            topEnd = if (state == CalendarDayState.COMPLETED && connectRight) 4.dp else radius.sm,
                            bottomStart = if (state == CalendarDayState.COMPLETED && connectLeft) 4.dp else radius.sm,
                            bottomEnd = if (state == CalendarDayState.COMPLETED && connectRight) 4.dp else radius.sm
                        )
                    )
                    .border(stroke.thin, borderColor, RoundedCornerShape(radius.sm)),
                contentAlignment = Alignment.Center
            ) {
                if (isToday) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.x5 + spacing.x1)
                            .padding(1.dp)
                            .border(stroke.thin, semantic.calendarTodayRing, RoundedCornerShape(radius.sm))
                    )
                }
                Text(
                    text = when (state) {
                        CalendarDayState.COMPLETED -> "✓"
                        CalendarDayState.PARTIAL -> "${safeProgress}%"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = dayColor,
                    fontWeight = FontWeight.Bold
                )
                if (isScheduled && state != CalendarDayState.FUTURE) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(completionProgress)
                            .height(3.dp)
                            .background(semantic.success, RoundedCornerShape(radius.full))
                    )
                }
            }
        }
        Text(
            text = label.replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday) semantic.primary else semantic.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    selectedTask: HabitTask?,
    doneDates: Set<LocalDate>,
    partialDates: Set<LocalDate>,
    scheduledDates: Set<LocalDate>,
    onMoveMonth: (Long) -> Unit,
    onToday: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val language = LocalAppLanguage.current
    val locale = appLocale()
    val today = LocalDate.now()

    GlassCard(
        modifier = Modifier.height(416.dp),
        contentPadding = PaddingValues(spacing.x2)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            CalendarHeaderRow(
                monthLabel = localizedMonthYear(month, language, locale),
                isTodaySelected = selectedDate == today && month == YearMonth.now(),
                onPrev = { onMoveMonth(-1) },
                onToday = onToday,
                onNext = { onMoveMonth(1) }
            )

            val days = weekdayLabels(LocalAppLanguage.current)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                }
            }

            monthGrid(month).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    week.forEachIndexed { _, day ->
                        val state = dayStateFor(
                            date = day,
                            doneDates = doneDates,
                            partialDates = partialDates,
                            scheduledDates = scheduledDates,
                            today = today
                        )
                        val dayDate = day
                        val done = dayDate != null && dayDate in doneDates
                        CalendarDay(
                            modifier = Modifier.weight(1f),
                            date = dayDate,
                            state = state,
                            selected = dayDate == selectedDate,
                            today = dayDate == today,
                            connectLeft = done && dayDate != null && dayDate.minusDays(1) in doneDates,
                            connectRight = done && dayDate != null && dayDate.plusDays(1) in doneDates,
                            enabled = selectedTask != null,
                            onClick = { dayDate?.let(onDateSelect) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeaderRow(
    monthLabel: String,
    isTodaySelected: Boolean,
    onPrev: () -> Unit,
    onToday: () -> Unit,
    onNext: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthLabel,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onPrev,
                contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0),
                modifier = Modifier.height(30.dp)
            ) {
                Text("<", color = colors.textSecondary)
            }
            OutlinedButton(
                onClick = onToday,
                enabled = !isTodaySelected,
                modifier = Modifier
                    .height(30.dp)
                    .graphicsLayer(alpha = if (isTodaySelected) 0.55f else 1f),
                shape = RoundedCornerShape(radius.full),
                contentPadding = PaddingValues(horizontal = spacing.x1, vertical = spacing.x0),
                border = BorderStroke(stroke.thin, colors.borderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.textSecondary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = colors.textSecondary
                )
            ) {
                Text(t("Today"), style = MaterialTheme.typography.labelMedium)
            }
            TextButton(
                onClick = onNext,
                contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0),
                modifier = Modifier.height(30.dp)
            ) {
                Text(">", color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun PlanCard(title: String, subtitle: String, selected: Boolean, actionLabel: String, onAction: () -> Unit) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, color = semantic.textSecondary)
            Button(onClick = onAction, enabled = !selected) { Text(actionLabel) }
        }
    }
}

@Composable
private fun SelectChip(title: String, selected: Boolean, onClick: () -> Unit) {
    val semantic = AppTheme.colors
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) semantic.primary else semantic.backgroundSurfaceMuted,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else semantic.textPrimary
        )
    ) {
        Text(title)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskEditorDialog(
    state: HabitUiState,
    onDismiss: () -> Unit,
    vm: MainViewModel,
    onSaveRequest: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke
    val context = LocalContext.current
    val locale = appLocale()
    val is24HourView = android.text.format.DateFormat.is24HourFormat(context)
    val selectedColor = parseColorHex(state.editorColorHex)
    val pickerTheme = R.style.ThemeOverlay_MicroHabit_Picker
    val pickerActionColor = colors.primary.toArgb()

    val trackingCards = listOf(
        Triple(
            TrackingType.YES_NO,
            t("Do once"),
            t("Just mark whether you did it today")
        ),
        Triple(
            TrackingType.COUNT,
            t("Do N times"),
            t("Set a daily quantity target")
        ),
        Triple(
            TrackingType.DURATION,
            t("Do N minutes"),
            t("Set a daily time target")
        )
    )
    val frequencyOptions = listOf(
        ChoiceOption(TaskFrequency.DAILY, t("Every day")),
        ChoiceOption(TaskFrequency.SELECTED_DAYS, t("Selected weekdays")),
        ChoiceOption(TaskFrequency.TIMES_PER_WEEK, t("X / week"))
    )
    val palette = listOf("#1F6F64", "#3B7EA1", "#7B6BC9", "#3E8E5F", "#B36A3C", "#C65C74", "#5D6D7E")

    BackHandler(onBack = onDismiss)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (state.editingTaskId == null) t("Create Habit") else t("Edit Habit"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = t("Close"))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    color = colors.backgroundSurface,
                    tonalElevation = AppTheme.elevation.sm
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                    ) {
                        if (!vm.canSaveEditor()) {
                            Text(
                                text = t("Fill required fields to continue."),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        Button(
                            onClick = onSaveRequest,
                            enabled = vm.canSaveEditor(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppTheme.radius.md)
                        ) {
                            Text(if (state.editingTaskId == null) t("Save habit") else t("Save changes"))
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                FormSection(title = t("Basic setup")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        OutlinedTextField(
                            value = state.editorTitle,
                            onValueChange = vm::setEditorTitle,
                            label = { Text(t("Habit name")) },
                            placeholder = { Text(t("Morning meditation")) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = state.editorEmoji,
                                onValueChange = vm::setEditorEmoji,
                                label = { Text(t("Icon / emoji")) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .size(spacing.x5)
                                    .clip(RoundedCornerShape(AppTheme.radius.md))
                                    .background(selectedColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.editorEmoji.ifBlank { "✨" },
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }

                FormSection(title = t("Color")) {
                    ColorSwatchPicker(
                        colorsHex = palette,
                        selectedHex = state.editorColorHex,
                        onSelect = vm::setEditorColorHex
                    )
                }

                FormSection(title = t("Tracking type")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        trackingCards.forEach { (type, title, description) ->
                            val selected = state.editorTrackingType == type
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AppTheme.radius.md))
                                    .clickable { vm.setEditorTrackingType(type) },
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                color = if (selected) {
                                    colors.primary.copy(alpha = 0.14f)
                                } else {
                                    colors.backgroundSurfaceMuted
                                },
                                border = BorderStroke(
                                    width = if (selected) stroke.medium else stroke.thin,
                                    color = if (selected) colors.primary else colors.borderSubtle
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
                                    verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                when (state.editorTrackingType) {
                    TrackingType.YES_NO -> Unit
                    TrackingType.COUNT -> {
                        FormSection(title = t("Count target")) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                Stepper(
                                    label = t("Daily target"),
                                    value = state.editorDailyTarget,
                                    min = 1,
                                    max = 999,
                                    onValueChange = vm::setEditorDailyTarget
                                )
                                OutlinedTextField(
                                    value = state.editorUnitLabel,
                                    onValueChange = vm::setEditorUnitLabel,
                                    label = { Text(t("Unit label")) },
                                    placeholder = { Text(t("e.g. glasses, pages, reps")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                    TrackingType.DURATION -> {
                        FormSection(title = t("Duration target")) {
                            Stepper(
                                label = t("Daily minute goal"),
                                value = state.editorDailyTarget,
                                min = 1,
                                max = 600,
                                onValueChange = vm::setEditorDailyTarget
                            )
                        }
                    }
                }

                FormSection(title = t("Frequency")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        SingleSelectChips(
                            options = frequencyOptions,
                            selected = state.editorFrequency,
                            onSelect = vm::setEditorFrequency
                        )

                        if (state.editorFrequency == TaskFrequency.SELECTED_DAYS) {
                            WeekdaySelector(
                                selectedDays = state.editorCustomDays,
                                onToggle = vm::toggleEditorCustomDay
                            )
                            if (state.editorCustomDays.isEmpty()) {
                                Text(
                                    text = t("Select at least one weekday."),
                                    color = colors.danger,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (state.editorFrequency == TaskFrequency.TIMES_PER_WEEK) {
                            Stepper(
                                label = t("Times per week"),
                                value = state.editorTimesPerWeek,
                                min = 1,
                                max = 7,
                                onValueChange = vm::setEditorTimesPerWeek
                            )
                        }
                    }
                }

                FormSection(title = t("Reminders")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        SettingsSwitchRow(
                            title = t("Reminders"),
                            subtitle = t("Enable habit reminder notifications"),
                            checked = state.editorReminderEnabled,
                            onCheckedChange = vm::setEditorReminderEnabled
                        )
                        AnimatedVisibility(visible = state.editorReminderEnabled) {
                            OutlinedButton(
                                onClick = {
                                    showThemedTimePicker(
                                        context = context,
                                        themeResId = pickerTheme,
                                        initialHour = state.editorReminderHour,
                                        initialMinute = state.editorReminderMinute,
                                        is24HourView = is24HourView,
                                        actionColorArgb = pickerActionColor,
                                        onTimeSet = vm::setEditorReminder
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                border = BorderStroke(stroke.thin, colors.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = colors.primary
                                )
                            ) {
                                Text(
                                    tf(
                                        "Reminder: %s",
                                        formatTimeForDevice(
                                            context,
                                            state.editorReminderHour,
                                            state.editorReminderMinute
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

                TextButton(onClick = { vm.setEditorShowAdvanced(!state.editorShowAdvanced) }) {
                    Text(if (state.editorShowAdvanced) t("Hide advanced settings") else t("Show advanced settings"))
                }

                AnimatedVisibility(visible = state.editorShowAdvanced) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        FormSection(title = t("Advanced settings")) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                                OutlinedButton(
                                    onClick = {
                                        showThemedDatePicker(
                                            context = context,
                                            themeResId = pickerTheme,
                                            initialDate = state.editorStartDate,
                                            actionColorArgb = pickerActionColor,
                                            onDateSet = { year, month, day ->
                                                vm.setEditorStartDate(LocalDate.of(year, month + 1, day))
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(AppTheme.radius.md),
                                    border = BorderStroke(stroke.thin, colors.primary),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = colors.primary
                                    )
                                ) {
                                    Text(
                                        tf(
                                            "Start date: %s",
                                            state.editorStartDate.format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale))
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.x2))
            }
        }
    }
}

private fun showThemedTimePicker(
    context: Context,
    themeResId: Int,
    initialHour: Int,
    initialMinute: Int,
    is24HourView: Boolean,
    actionColorArgb: Int,
    onTimeSet: (hour: Int, minute: Int) -> Unit
) {
    val dialog = TimePickerDialog(
        context,
        themeResId,
        { _, hour, minute -> onTimeSet(hour, minute) },
        initialHour,
        initialMinute,
        is24HourView
    )
    dialog.setOnShowListener {
        dialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(actionColorArgb)
        dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(actionColorArgb)
    }
    dialog.show()
}

private fun showThemedDatePicker(
    context: Context,
    themeResId: Int,
    initialDate: LocalDate,
    actionColorArgb: Int,
    onDateSet: (year: Int, month: Int, day: Int) -> Unit
) {
    val dialog = DatePickerDialog(
        context,
        themeResId,
        { _, year, month, day -> onDateSet(year, month, day) },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    )
    dialog.setOnShowListener {
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(actionColorArgb)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(actionColorArgb)
    }
    dialog.show()
}

private fun openNotificationOrAppSettings(context: Context): Boolean {
    val packageName = context.packageName
    val activity = context.findActivity()

    val notificationSettingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        putExtra("android.provider.extra.APP_PACKAGE", packageName)
        if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(notificationSettingsIntent) }.onSuccess { return true }

    val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(appDetailsIntent) }.onSuccess { return true }

    return false
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun formatTimeForDevice(context: Context, hour: Int, minute: Int): String {
    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
        set(java.util.Calendar.MINUTE, minute.coerceIn(0, 59))
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return android.text.format.DateFormat.getTimeFormat(context).format(calendar.time)
}

private fun localizedMonthYear(month: YearMonth, language: AppLanguage, locale: Locale): String {
    val raw = month.format(DateTimeFormatter.ofPattern(translate(language, "LLLL yyyy"), locale))
    val shouldCapitalize = language == AppLanguage.RU || language == AppLanguage.UK || language == AppLanguage.CS
    if (!shouldCapitalize || raw.isEmpty()) return raw
    return raw.replaceFirstChar { first ->
        if (first.isLowerCase()) first.titlecase(locale) else first.toString()
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val elevation = AppTheme.elevation
    val resolvedPadding = contentPadding ?: PaddingValues(spacing.x2)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = semantic.backgroundSurface),
        shape = RoundedCornerShape(radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.md)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(resolvedPadding)) {
            content()
        }
    }
}

private fun monthGrid(month: YearMonth): List<List<LocalDate?>> {
    val firstDay = month.atDay(1)
    val leadingEmpty = firstDay.dayOfWeek.value - 1
    val days = mutableListOf<LocalDate?>()
    repeat(leadingEmpty) { days += null }
    repeat(month.lengthOfMonth()) { index -> days += month.atDay(index + 1) }
    while (days.size % 7 != 0) days += null
    return days.chunked(7)
}

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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class AppPage {
    TRACKER,
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

private data class StreakOverlayModel(
    val streak: Int,
    val milestone: Boolean
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

    CompositionLocalProvider(LocalAppLanguage provides state.language) {
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
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                pageTitle(page),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Rounded.Menu, contentDescription = t("Menu"))
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
                        onUpgrade = {
                            previousPage = AppPage.TRACKER
                            page = AppPage.PAYWALL
                        }
                    )
                    AppPage.HABITS -> HabitsPage(
                        state = state,
                        vm = vm,
                        onOpenHabit = {
                            page = AppPage.TRACKER
                        },
                        onUpgrade = {
                            previousPage = AppPage.HABITS
                            page = AppPage.PAYWALL
                        }
                    )
                    AppPage.ANALYTICS -> AnalyticsPage(state = state)
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
    onUpgrade: () -> Unit
) {
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    val canAdd = state.plan == SubscriptionPlan.PRO || state.tasks.size < 1
    val spacing = AppTheme.spacing
    var pendingDeleteTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        streakOverlay = null
        overlayVisible = false
    }
    LaunchedEffect(state.totalCompletions, state.selectedDateDone, state.streak) {
        if (state.selectedDateDone && state.totalCompletions > previousTotalCompletions) {
            streakOverlay = StreakOverlayModel(
                streak = state.streak,
                milestone = isStreakMilestone(state.streak)
            )
            overlayVisible = true
        }
        previousTotalCompletions = state.totalCompletions
    }
    LaunchedEffect(streakOverlay, overlayVisible) {
        if (overlayVisible && streakOverlay != null) {
            delay(if (streakOverlay?.milestone == true) 2300 else 1700)
            overlayVisible = false
        }
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
                    TaskControlsRow(
                        canAddTask = canAdd,
                        onCreate = { if (canAdd) vm.openCreateTask() else onUpgrade() },
                        onEdit = { selectedTask?.id?.let(vm::openEditTask) },
                        onDelete = { selectedTask?.id?.let { pendingDeleteTaskId = it } },
                        canEditDelete = selectedTask != null
                    )
                }
                item { TaskSelector(state.tasks, state.selectedTaskId, vm::selectTask) }
                item {
                    HeroCard(
                        task = selectedTask,
                        selectedDate = state.selectedDate,
                        done = state.selectedDateDone,
                        scheduled = state.selectedDateScheduled,
                        onDone = vm::toggleSelectedDateDone
                    )
                }
                item {
                    StatsRow(
                        streak = state.streak,
                        bestStreak = state.bestStreak,
                        progress = state.progressPercent,
                        total = state.totalCompletions
                    )
                }
                item {
                    SevenDayChart(
                        points = state.last7Days,
                        scheduled = state.last7DaysScheduled,
                        anchorDate = state.selectedDate
                    )
                }
                item {
                    CalendarCard(
                        month = state.currentMonth,
                        selectedDate = state.selectedDate,
                        selectedTask = selectedTask,
                        doneDates = state.doneDatesInCurrentMonth,
                        scheduledDates = state.scheduledDatesInCurrentMonth,
                        onMoveMonth = vm::moveMonth,
                        onToday = vm::jumpToToday,
                        onDateSelect = vm::selectDate
                    )
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
private fun AnalyticsPage(state: HabitUiState) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }

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
    val weeklyValues = state.last7Days.map { if (it > 0) 100 else 0 }
    val weeklyLabels = (6 downTo 0).map { offset ->
        state.selectedDate.minusDays(offset.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
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
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                    Text(
                        text = t("Analytics"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = selectedTask.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
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
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x0_5)) {
                    Text(
                        text = t("Calendar"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = selectedTask.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }

        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { vm.moveMonth(-1) }) { Text("<") }
                        Text(
                            text = localizedMonthYear(state.currentMonth, state.language, locale),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        TextButton(onClick = { vm.moveMonth(1) }) { Text(">") }
                    }

                    Button(
                        onClick = vm::jumpToToday,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(t("Today"))
                    }

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
    scheduledDates: Set<LocalDate>,
    today: LocalDate
): CalendarDayState {
    if (date == null) return CalendarDayState.FUTURE
    if (date.isAfter(today)) return CalendarDayState.FUTURE
    if (date in doneDates) return CalendarDayState.COMPLETED
    if (date in scheduledDates && date.isBefore(today)) return CalendarDayState.MISSED
    return CalendarDayState.NOT_SCHEDULED
}

private fun statusLabel(state: CalendarDayState, language: AppLanguage): String {
    return when (state) {
        CalendarDayState.COMPLETED -> translate(language, "Completed")
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
private fun TaskSelector(tasks: List<HabitTask>, selectedTaskId: String?, onSelect: (String) -> Unit) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            tasks.forEach { task ->
                val selected = task.id == selectedTaskId
                Button(
                    onClick = { onSelect(task.id) },
                    shape = RoundedCornerShape(radius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) semantic.primary else semantic.backgroundSurfaceMuted,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else semantic.textPrimary
                    )
                ) {
                    Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    task: HabitTask?,
    selectedDate: LocalDate,
    done: Boolean,
    scheduled: Boolean,
    onDone: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    val locale = appLocale()
    val canMarkForSelectedDate = scheduled

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = task?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale)),
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
            Text(
                text = t("Did you complete it on this date?"),
                style = MaterialTheme.typography.headlineSmall
            )
            if (done && canMarkForSelectedDate) {
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.x5 + spacing.x1),
                    shape = RoundedCornerShape(radius.lg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = semantic.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = t("Completed ✓"),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onDone,
                    enabled = canMarkForSelectedDate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.x5 + spacing.x1),
                    shape = RoundedCornerShape(radius.lg),
                    border = BorderStroke(
                        stroke.thin * 1.5f,
                        if (canMarkForSelectedDate) semantic.primary else semantic.borderSubtle
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = semantic.primary,
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

            AnimatedVisibility(
                visible = done && canMarkForSelectedDate,
                enter = fadeIn(animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing)) +
                    expandVertically(
                        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                        expandFrom = Alignment.Top
                    ),
                exit = fadeOut(animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)) +
                    shrinkVertically(
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.Top
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = semantic.success,
                        modifier = Modifier.size(spacing.x2 + spacing.x0_5)
                    )
                    Text(
                        t("Great job, your streak is safe."),
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakRewardOverlay(model: StreakOverlayModel) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    val container by animateColorAsState(
        targetValue = if (model.milestone) colors.primary else colors.backgroundSurface,
        animationSpec = tween(durationMillis = 220),
        label = "streakOverlayContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (model.milestone) MaterialTheme.colorScheme.onPrimary else colors.textPrimary,
        animationSpec = tween(durationMillis = 220),
        label = "streakOverlayContent"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.x2),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.lg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                .border(
                    width = stroke.thin,
                    color = if (model.milestone) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f) else colors.borderSubtle,
                    shape = RoundedCornerShape(radius.md)
                )
                .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
            verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
        ) {
            Text(
                text = "🔥 ${tf("%d day streak", model.streak)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = if (model.milestone) t("Milestone reached!") else t("Streak updated"),
                style = MaterialTheme.typography.bodySmall,
                color = if (model.milestone) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else colors.textSecondary
            )
            if (model.milestone) {
                Text(
                    text = "✦  ✦  ✦",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
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
private fun StatsRow(streak: Int, bestStreak: Int, progress: Int, total: Int) {
    val spacing = AppTheme.spacing
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
                value = if (streak >= 7) "🔥 ${streak}d" else "${streak}d"
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
private fun StatTile(modifier: Modifier = Modifier, label: String, value: String) {
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
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                    val isDone = value == 1
                    val isScheduled = safeScheduled[index]
                    val state = when {
                        isFuture -> CalendarDayState.FUTURE
                        isDone -> CalendarDayState.COMPLETED
                        isScheduled && date.isBefore(today) -> CalendarDayState.MISSED
                        else -> CalendarDayState.NOT_SCHEDULED
                    }
                    val leftDone = if (index > 0) {
                        val leftDate = anchorDate.minusDays((6 - (index - 1)).toLong())
                        safe[index - 1] == 1 && !leftDate.isAfter(today)
                    } else {
                        false
                    }
                    val rightDone = if (index < safe.lastIndex) {
                        val rightDate = anchorDate.minusDays((6 - (index + 1)).toLong())
                        safe[index + 1] == 1 && !rightDate.isAfter(today)
                    } else {
                        false
                    }
                    DayBar(
                        modifier = Modifier.weight(1f),
                        state = state,
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
    isToday: Boolean,
    connectLeft: Boolean,
    connectRight: Boolean,
    label: String
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    val completionProgress by animateFloatAsState(
        targetValue = if (state == CalendarDayState.COMPLETED) 1f else 0f,
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
            CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.08f)
            CalendarDayState.NOT_SCHEDULED -> semantic.neutralMuted.copy(alpha = 0.42f)
            CalendarDayState.FUTURE -> semantic.backgroundSurfaceMuted.copy(alpha = 0.28f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "dayBarBackgroundColor"
    )
    val borderColor = when (state) {
        CalendarDayState.COMPLETED -> Color.Transparent
        CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.45f)
        CalendarDayState.NOT_SCHEDULED -> semantic.borderSubtle
        CalendarDayState.FUTURE -> semantic.borderSubtle.copy(alpha = 0.4f)
    }
    val dayColor = when (state) {
        CalendarDayState.COMPLETED -> MaterialTheme.colorScheme.onPrimary
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
                    text = if (state == CalendarDayState.COMPLETED) "✓" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = dayColor,
                    fontWeight = FontWeight.Bold
                )
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
    scheduledDates: Set<LocalDate>,
    onMoveMonth: (Long) -> Unit,
    onToday: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val language = LocalAppLanguage.current
    val locale = appLocale()

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    localizedMonthYear(month, language, locale),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = semantic.textPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onMoveMonth(-1) }) { Text("<") }
                    TextButton(onClick = { onMoveMonth(1) }) { Text(">") }
                }
            }

            Button(
                onClick = onToday,
                modifier = Modifier.fillMaxWidth().height(spacing.x5),
                shape = RoundedCornerShape(radius.md)
            ) {
                Text(t("Today"))
            }

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
                            scheduledDates = scheduledDates,
                            today = LocalDate.now()
                        )
                        val dayDate = day
                        val done = dayDate != null && dayDate in doneDates
                        CalendarDay(
                            modifier = Modifier.weight(1f),
                            date = dayDate,
                            state = state,
                            selected = dayDate == selectedDate,
                            today = dayDate == LocalDate.now(),
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

    val trackingOptions = listOf(
        ChoiceOption(TrackingType.YES_NO, t("Yes / No")),
        ChoiceOption(TrackingType.COUNT, t("Count")),
        ChoiceOption(TrackingType.DURATION, t("Duration"))
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
                    SingleSelectChips(
                        options = trackingOptions,
                        selected = state.editorTrackingType,
                        onSelect = vm::setEditorTrackingType
                    )
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
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val elevation = AppTheme.elevation
    val resolvedPadding = contentPadding ?: PaddingValues(spacing.x2)

    Card(
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

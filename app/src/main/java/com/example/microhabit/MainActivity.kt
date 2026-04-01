package com.example.microhabit

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.ApplicationInfo
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.Easing
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
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.LottieProperty
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.data.AppThemeMode
import com.example.microhabit.data.HabitCategory
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.data.HabitTask
import com.example.microhabit.data.HabitTemplate
import com.example.microhabit.data.HabitTemplateCatalog
import com.example.microhabit.data.MAX_HABIT_TITLE_LENGTH
import com.example.microhabit.data.PremiumPlan
import com.example.microhabit.data.ProAccessSource
import com.example.microhabit.data.SubscriptionState
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.TaskFrequency
import com.example.microhabit.data.TrackingType
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.appLocale
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.i18n.languageNativeLabel
import com.example.microhabit.i18n.streakDaysUnit
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.i18n.translate
import com.example.microhabit.i18n.weekdayLabels
import com.example.microhabit.notifications.HabitReminderScheduler
import com.example.microhabit.ui.components.ChoiceOption
import com.example.microhabit.ui.components.CalendarDay
import com.example.microhabit.ui.components.CalendarDayState
import com.example.microhabit.ui.components.FeatureBulletRow
import com.example.microhabit.ui.components.FormSection
import com.example.microhabit.ui.components.HorizontalPercentBars
import com.example.microhabit.ui.components.HabitEditModeCard
import com.example.microhabit.ui.components.HabitListCard
import com.example.microhabit.ui.components.HabitCardModel
import com.example.microhabit.ui.components.AnalyticsMetricTile
import com.example.microhabit.ui.components.PlanComparisonRow
import com.example.microhabit.ui.components.PricingCardModel
import com.example.microhabit.ui.components.PricingPlanCard
import com.example.microhabit.ui.components.Stepper
import com.example.microhabit.ui.components.VerticalPercentBars
import com.example.microhabit.ui.components.WeekdaySelector
import com.example.microhabit.ui.components.SettingsDivider
import com.example.microhabit.ui.components.SettingsGroup
import com.example.microhabit.ui.components.SettingsRow
import com.example.microhabit.ui.components.SettingsSwitchRow
import com.example.microhabit.ui.components.parseColorHex
import com.example.microhabit.ui.analytics.AnalyticsScreen
import com.example.microhabit.ui.create.CreateHabitTemplate
import com.example.microhabit.ui.create.CreateHabitTemplateCatalog
import com.example.microhabit.ui.create.TemplateCategory
import com.example.microhabit.ui.create.TemplateConfirmDraft
import com.example.microhabit.ui.calendar.BreakdownCard
import com.example.microhabit.ui.tracker.HabitPageDots
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.theme.MicroHabitTheme
import com.example.microhabit.widget.HabitWidgetUpdateScheduler
import com.example.microhabit.widget.WidgetDebugLog
import com.example.microhabit.widget.WidgetUpdateTrigger
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

// ─── Tracker habit switch animation constants ────────────────────────────────
// Shared by HeroCard transition, calendar crossfade and page dots spring.
internal const val HABIT_TRANSITION_MS = 220
internal val HABIT_EASING: Easing = EaseInOutQuart
internal const val DOTS_SPRING_DAMPING = 0.7f
internal const val DOTS_SPRING_STIFFNESS = 500f
private const val DEFAULT_REMINDER_HOUR = 8
private const val DEFAULT_REMINDER_MINUTE = 0
// ──────────────────────────────────────────────────────────────────────────────

private enum class AppPage {
    TRACKER,
    HABIT_DETAIL,
    HABITS,
    ANALYTICS,
    CALENDAR,
    PAYWALL,
    ACCOUNT,
    MANAGE_SUBSCRIPTION,
    SETTINGS
}

private enum class PaywallTrigger {
    DEFAULT,
    HABIT_LIMIT,
    ANALYTICS,
    WIDGETS
}

private enum class BillingCycle {
    MONTHLY,
    YEARLY,
    LIFETIME
}

private const val PRODUCT_ID_MONTHLY = "micro_habit_pro_monthly"
private const val PRODUCT_ID_YEARLY = "micro_habit_pro_yearly"
private const val PRODUCT_ID_LIFETIME = "micro_habit_pro_lifetime"
const val EXTRA_OPEN_PAYWALL_TRIGGER = "extra_open_paywall_trigger"
const val EXTRA_PAYWALL_TRIGGER_WIDGETS = "WIDGETS"

private fun billingProductIdFor(cycle: BillingCycle): String {
    return when (cycle) {
        BillingCycle.MONTHLY -> PRODUCT_ID_MONTHLY
        BillingCycle.YEARLY -> PRODUCT_ID_YEARLY
        BillingCycle.LIFETIME -> PRODUCT_ID_LIFETIME
    }
}

private fun proAccessSourceFor(cycle: BillingCycle): ProAccessSource {
    return when (cycle) {
        BillingCycle.MONTHLY -> ProAccessSource.MONTHLY
        BillingCycle.YEARLY -> ProAccessSource.YEARLY
        BillingCycle.LIFETIME -> ProAccessSource.LIFETIME
    }
}

private enum class NotificationPermissionAction {
    SAVE_EDITOR_REMINDER,
    PICK_TEMPLATE_REMINDER_TIME
}

private enum class DurationSheetMode {
    MANUAL,
    TIMER
}

private enum class TimerUiState {
    IDLE,
    RUNNING,
    PAUSED
}

private enum class OnboardingStep {
    WELCOME,
    CATEGORY,
    TEMPLATE,
    SETUP,
    READY
}

private enum class SurfaceTone {
    PRIMARY,
    SECONDARY,
    TERTIARY
}

private enum class ActionEmphasis {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    DANGER
}

private data class PrimaryNavItem(
    val page: AppPage,
    val icon: ImageVector,
    val contentDescription: String
)

private data class StreakOverlayModel(
    val streak: Int,
    val milestone: Boolean
)

private data class StreakMilestoneTier(
    val days: Int,
    val badgeLabel: String,
    val headline: String,
    val message: String,
    val ctaLabel: String,
    val accentColor: Color,
    val backgroundColor: Color,
    val nextMilestoneDays: Int?,
    val nextMilestoneIcon: String
)

private data class StreakMilestoneTierDefinition(
    val days: Int,
    val accentColor: Color,
    val backgroundColor: Color,
    val nextMilestoneDays: Int?,
    val nextMilestoneIcon: String
)

private val streakMilestoneTierDefinitions = listOf(
    StreakMilestoneTierDefinition(1, Color(0xFF2DCF96), Color(0xFF0A2318), 3, "🎯"),
    StreakMilestoneTierDefinition(3, Color(0xFF2DCF96), Color(0xFF0A2318), 7, "🔥"),
    StreakMilestoneTierDefinition(7, Color(0xFFF59E42), Color(0xFF1F1008), 14, "⚡"),
    StreakMilestoneTierDefinition(14, Color(0xFFF59E42), Color(0xFF1F1008), 21, "⭐"),
    StreakMilestoneTierDefinition(21, Color(0xFFEAB308), Color(0xFF1A1200), 30, "🏅"),
    StreakMilestoneTierDefinition(30, Color(0xFFEAB308), Color(0xFF1A1200), 50, "💪"),
    StreakMilestoneTierDefinition(50, Color(0xFFFBBF24), Color(0xFF160E00), 66, "⚡"),
    StreakMilestoneTierDefinition(66, Color(0xFFFBBF24), Color(0xFF160E00), 100, "💎"),
    StreakMilestoneTierDefinition(100, Color(0xFFFBBF24), Color(0xFF160E00), 180, "🌟"),
    StreakMilestoneTierDefinition(180, Color(0xFFA78BFA), Color(0xFF0F0820), 365, "👑"),
    StreakMilestoneTierDefinition(365, Color(0xFFA78BFA), Color(0xFF0F0820), 500, "🔮"),
    StreakMilestoneTierDefinition(500, Color(0xFFC084FC), Color(0xFF0C0618), 1000, "∞"),
    StreakMilestoneTierDefinition(1000, Color(0xFFE879F9), Color(0xFF080818), null, "")
)

@Composable
private fun streakMilestoneTier(days: Int): StreakMilestoneTier? {
    val definition = streakMilestoneTierDefinitions.firstOrNull { it.days == days } ?: return null
    return StreakMilestoneTier(
        days = definition.days,
        badgeLabel = t("milestone_badge_${definition.days}"),
        headline = t("milestone_headline_${definition.days}"),
        message = t("milestone_message_${definition.days}"),
        ctaLabel = t("milestone_cta_${definition.days}"),
        accentColor = definition.accentColor,
        backgroundColor = definition.backgroundColor,
        nextMilestoneDays = definition.nextMilestoneDays,
        nextMilestoneIcon = definition.nextMilestoneIcon
    )
}

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
        HabitWidgetUpdateScheduler.scheduleWidgetUpdates(applicationContext)

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

    override fun onResume() {
        super.onResume()
        WidgetDebugLog.d("MainActivity.onResume trigger widget refresh")
        WidgetUpdateTrigger.triggerUpdate(this)
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
        AppPage.MANAGE_SUBSCRIPTION -> t("Manage subscription")
        AppPage.SETTINGS -> t("Settings")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitApp(state: HabitUiState, vm: MainViewModel) {
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

@Composable
private fun PrimaryBottomNavigationBar(
    currentPage: AppPage,
    onSelect: (destination: AppPage, reselected: Boolean) -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    val items = remember {
        listOf(
            PrimaryNavItem(AppPage.TRACKER, Icons.Rounded.Home, "Tracker"),
            PrimaryNavItem(AppPage.HABITS, Icons.Rounded.Checklist, "Habits"),
            PrimaryNavItem(AppPage.ANALYTICS, Icons.Rounded.BarChart, "Analytics"),
            PrimaryNavItem(AppPage.CALENDAR, Icons.Rounded.CalendarMonth, "Calendar"),
            PrimaryNavItem(AppPage.ACCOUNT, Icons.Rounded.AccountCircle, "Account")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = spacing.x1_5, vertical = spacing.x1),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(radius.xl),
            color = colors.backgroundSurface.copy(alpha = 0.94f),
            border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.55f)),
            tonalElevation = AppTheme.elevation.sm,
            shadowElevation = AppTheme.elevation.md
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing.x0_5,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentPage == item.page
                    BottomNavigationIconItem(
                        icon = item.icon,
                        description = t(item.contentDescription),
                        selected = selected,
                        onClick = { onSelect(item.page, selected) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationIconItem(
    icon: ImageVector,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val activeColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.textTertiary,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "bottomNavIconColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "bottomNavIconScale"
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "bottomNavIndicatorAlpha"
    )
    Column(
        modifier = Modifier
            .width(50.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(AppTheme.radius.full))
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.x0_5, vertical = spacing.x0_5),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = activeColor,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }.size(27.dp)
        )
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 6.dp)
                .clip(RoundedCornerShape(AppTheme.radius.full))
                .background(colors.primary.copy(alpha = 0.85f * indicatorAlpha))
        )
    }
}

@Composable
private fun TrackerPage(
    state: HabitUiState,
    vm: MainViewModel,
    onOpenCreateHabit: () -> Unit,
    onOpenDetails: () -> Unit,
    highlightCompletionButton: Boolean,
    onHighlightConsumed: () -> Unit,
    scrollToTopSignal: Int
) {
    val spacing = AppTheme.spacing
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val selectorListState = rememberLazyListState()
    var previousTotalCompletions by remember(state.selectedTaskId) { mutableStateOf(state.totalCompletions) }
    var streakOverlay by remember { mutableStateOf<StreakOverlayModel?>(null) }
    var overlayVisible by remember { mutableStateOf(false) }
    var habitSwitchDirection by remember { mutableStateOf(0) }

    LaunchedEffect(state.selectedTaskId) {
        previousTotalCompletions = state.totalCompletions
        streakOverlay = null
        overlayVisible = false
        if (habitSwitchDirection != 0) {
            delay(16L)
            habitSwitchDirection = 0
        }
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
                state.selectedTaskId?.let { taskId ->
                    StreakMilestoneQueue.enqueueIfEligible(context, taskId, state.streak)
                }
            }
        }
        previousTotalCompletions = state.totalCompletions
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
    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) {
            listState.animateScrollToItem(0)
        }
    }
    val selectedHabitIndex = state.tasks
        .indexOfFirst { it.id == state.selectedTaskId }
        .let { if (it < 0) 0 else it }
    LaunchedEffect(selectedHabitIndex, state.tasks.size) {
        if (state.tasks.isEmpty()) return@LaunchedEffect
        val targetIndex = selectedHabitIndex + 1 // + tile is first item in tracker selector.
        val layoutInfo = selectorListState.layoutInfo
        val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { item -> item.index == targetIndex }
        val isFullyVisible = visibleItem != null &&
            visibleItem.offset >= layoutInfo.viewportStartOffset &&
            visibleItem.offset + visibleItem.size <= layoutInfo.viewportEndOffset
        if (!isFullyVisible) {
            selectorListState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = -40
            )
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
            state = listState,
            contentPadding = PaddingValues(start = spacing.x2, top = spacing.x1, end = spacing.x2, bottom = spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
            if (state.tasks.isEmpty()) {
                item {
                    HabitSelectorRow(
                        habits = emptyList(),
                        selectedId = null,
                        onHabitSelected = {},
                        onCreateHabit = onOpenCreateHabit,
                        listState = selectorListState
                    )
                }
                item { OnboardingCard(vm) }
            } else {
                item {
                    HabitSelectorRow(
                        habits = state.tasks,
                        selectedId = state.selectedTaskId,
                        onHabitSelected = { taskId -> switchToTask(taskId, 0) },
                        onCreateHabit = onOpenCreateHabit,
                        listState = selectorListState
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
                    val currentHabitIndex = state.tasks
                        .indexOfFirst { it.id == state.selectedTaskId }
                        .let { if (it < 0) 0 else it }
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        AnimatedContent(
                            targetState = state.selectedTaskId,
                            transitionSpec = {
                                if (habitSwitchDirection != 0) {
                                    // +1 = next habit (left swipe), -1 = previous habit (right swipe).
                                    val sign = if (habitSwitchDirection > 0) 1 else -1
                                    (
                                        slideInHorizontally(
                                            animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                        ) { fullWidth -> sign * fullWidth / 3 } +
                                            fadeIn(
                                                animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                            )
                                        ) togetherWith (
                                        slideOutHorizontally(
                                            animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                        ) { fullWidth -> -sign * fullWidth / 3 } +
                                            fadeOut(
                                                animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                            )
                                        )
                                } else {
                                    fadeIn(
                                        animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                    ) togetherWith fadeOut(
                                        animationSpec = tween(HABIT_TRANSITION_MS, easing = HABIT_EASING)
                                    )
                                }
                            },
                            label = "trackerHeroSwitch"
                        ) { taskId ->
                            val animatedTask = state.tasks.firstOrNull { it.id == taskId }
                            HeroCard(
                                task = animatedTask,
                                selectedDate = state.selectedDate,
                                done = state.selectedDateDone,
                                scheduled = state.selectedDateScheduled,
                                nextScheduledDate = state.selectedDateNextScheduled,
                                selectedValue = state.selectedDateValue,
                                selectedTarget = state.selectedDateTarget,
                                selectedUnit = state.selectedDateUnit,
                                streak = state.streak,
                                bestStreak = state.bestStreak,
                                weeklyRingProgress = state.weeklyRingProgress,
                                weeklyRingCompleted = state.weeklyRingCompleted,
                                weeklyRingScheduled = state.weeklyRingScheduled,
                                last7Days = state.last7Days,
                                last7DaysScheduled = state.last7DaysScheduled,
                                last7DaysManualOverride = state.last7DaysManualOverride,
                                onDone = vm::toggleSelectedDateDone,
                                onMarkAnyway = vm::markSelectedDateAnyway,
                                onSetValue = vm::setSelectedDateValue,
                                onIncrementValue = vm::incrementSelectedDateValue,
                                onNavigateToDetail = onOpenDetails,
                                highlightMarkButton = highlightCompletionButton,
                                onHighlightConsumed = onHighlightConsumed,
                                appThemeMode = state.themeMode,
                                swipeEnabled = state.tasks.size > 1,
                                onSwipeNext = { switchHabitBy(1) },
                                onSwipePrevious = { switchHabitBy(-1) }
                            )
                        }
                        HabitPageDots(
                            total = state.tasks.size,
                            current = currentHabitIndex
                        )
                        Crossfade(
                            targetState = state.selectedTaskId,
                            animationSpec = tween(
                                durationMillis = HABIT_TRANSITION_MS,
                                easing = HABIT_EASING
                            ),
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
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitsPage(
    state: HabitUiState,
    vm: MainViewModel,
    onCreateHabit: () -> Unit,
    onUpgrade: () -> Unit,
    isEditMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    scrollToTopSignal: Int
) {
    val context = LocalContext.current
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val listState = rememberLazyListState()
    val canAdd = vm.canCreateTask()
    var pendingDeleteTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    val activeHabits = remember(state.habits) {
        state.habits.filter { !it.isArchived && !it.isCompleted }
    }
    val completedHabits = remember(state.habits) {
        state.habits.filter { !it.isArchived && it.isCompleted }
    }
    val archivedHabits = remember(state.habits) {
        state.habits.filter { it.isArchived }
    }
    var editModeHabits by remember(state.habits) { mutableStateOf(activeHabits) }
    val reorderListState = rememberLazyListState()
    val editHeaderItemsCount = 1
    val reorderableListState = rememberReorderableLazyListState(reorderListState) { from, to ->
        if (editModeHabits.isEmpty()) return@rememberReorderableLazyListState

        val fromIndex = from.index - editHeaderItemsCount
        val toIndex = to.index - editHeaderItemsCount
        if (fromIndex !in editModeHabits.indices) return@rememberReorderableLazyListState

        val clampedTo = toIndex.coerceIn(0, editModeHabits.lastIndex)
        if (fromIndex == clampedTo) return@rememberReorderableLazyListState

        val updated = editModeHabits.toMutableList()
        val moved = updated.removeAt(fromIndex)
        updated.add(clampedTo, moved)
        editModeHabits = updated
    }
    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) {
            listState.animateScrollToItem(0)
        }
    }
    LaunchedEffect(isEditMode, activeHabits) {
        if (isEditMode) {
            editModeHabits = activeHabits
            if (activeHabits.isEmpty()) onEditModeChange(false)
        }
    }

    if (state.habits.isEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
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
                            onClick = { if (canAdd) onCreateHabit() else onUpgrade() },
                            shape = RoundedCornerShape(AppTheme.radius.md)
                        ) {
                            Text(if (canAdd) t("Create habit") else t("Get Premium"))
                        }
                    }
                }
            }
        }
    } else if (isEditMode) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = reorderListState,
            contentPadding = PaddingValues(horizontal = spacing.x2, vertical = spacing.x2),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            item {
                Text(
                    text = t("Active habits"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
            }
            items(items = editModeHabits, key = { it.id }) { habit ->
                ReorderableItem(reorderableListState, key = habit.id) { isDragging ->
                    HabitEditModeCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            secondaryLine = buildHabitsMetaString(context, habit),
                            streak = habit.streak,
                            completionPercent = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        isDragging = isDragging,
                        onDelete = { pendingDeleteTaskId = habit.id },
                        onDragStopped = {
                            vm.reorderActiveHabits(editModeHabits.map { it.id })
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
        ) {
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
                    HabitListCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            secondaryLine = buildHabitsMetaString(context, habit),
                            streak = habit.streak,
                            completionPercent = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        onEdit = { vm.openEditTask(habit.id) },
                        onArchive = { vm.archiveTask(habit.id) },
                        onUnarchive = { },
                        onDelete = { pendingDeleteTaskId = habit.id }
                    )
                }
            }

            if (completedHabits.isNotEmpty()) {
                item {
                    Text(
                        text = t("Completed habits"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
                items(items = completedHabits, key = { it.id }) { habit ->
                    HabitListCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            secondaryLine = buildHabitsMetaString(context, habit),
                            streak = habit.streak,
                            completionPercent = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        onEdit = { vm.openEditTask(habit.id) },
                        onArchive = { vm.archiveTask(habit.id) },
                        onUnarchive = { },
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
                    HabitListCard(
                        habit = HabitCardModel(
                            emoji = habit.emoji,
                            name = habit.name,
                            colorHex = habit.colorHex,
                            secondaryLine = buildHabitsMetaString(context, habit),
                            streak = habit.streak,
                            completionPercent = habit.completionRate,
                            isArchived = habit.isArchived
                        ),
                        onEdit = { },
                        onArchive = { },
                        onUnarchive = {
                            if (!vm.unarchiveTask(habit.id)) {
                                onUpgrade()
                            }
                        },
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
private fun buildHabitsMetaString(context: Context, habit: HabitListItem): String {
    val parts = mutableListOf(habit.frequency)
    if (habit.reminderEnabled) {
        parts += tf("Reminder: %s", formatTimeForDevice(context, habit.reminderHour, habit.reminderMinute))
    }
    return parts.joinToString(" · ")
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
                        weeklyCompletion = state.completionRate7Day
                    )
                }
                item {
                    HabitLevelProgressCard(
                        streak = state.streak,
                        bestStreak = state.bestStreak
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
                    val notePlaceholder = when {
                        state.todayDone -> t("notes_placeholder_done")
                        state.todayScheduled && !state.todayDone -> t("notes_placeholder_missed")
                        else -> t("notes_placeholder_default")
                    }
                    HabitNotesCard(
                        note = noteDraft,
                        placeholder = notePlaceholder,
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
    weeklyCompletion: Int
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
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
                    modifier = Modifier.size(132.dp),
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
                        strokeWidth = 9.dp
                    )
                    AnimatedContent(targetState = streak, label = "habitsDepthStreakValue") { value ->
                        Text(
                            text = "🔥 $value",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = semantic.textPrimary
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
    val previous = remember(history) {
        history.filter { it > 0 }.take(3)
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
    placeholder: String,
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
                label = { Text(placeholder) },
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
            GlassCard(tone = SurfaceTone.SECONDARY) {
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
            GlassCard(tone = SurfaceTone.SECONDARY) {
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
            GlassCard(tone = SurfaceTone.SECONDARY) {
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
    val today = LocalDate.now()
    val maxCompletedInMonth = state.calendarCompletedCountByDate.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val taskById = remember(state.allTasks) { state.allTasks.associateBy { it.id } }
    val calendarFilterHabits = remember(state.calendarFilterOptions, taskById) {
        state.calendarFilterOptions.mapNotNull { option -> taskById[option.taskId] }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            if (calendarFilterHabits.isEmpty()) {
                GlassCard(tone = SurfaceTone.SECONDARY) {
                    Text(
                        text = t("No active or completed habits yet."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            } else {
                HabitSelectorRow(
                    habits = calendarFilterHabits,
                    selectedId = state.calendarFilterTaskId,
                    onHabitSelected = vm::setCalendarFilterTask,
                    onCreateHabit = null,
                    showAllHabitsOption = true,
                    onSelectAll = { vm.setCalendarFilterTask(null) },
                    showCountLabel = false
                )
            }
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
                            week.forEach { dayDate ->
                                GlobalCalendarHeatCell(
                                    modifier = Modifier.weight(1f),
                                    date = dayDate,
                                    selected = dayDate == state.selectedDate,
                                    today = dayDate == today,
                                    completedCount = if (dayDate != null) {
                                        state.calendarCompletedCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    scheduledCount = if (dayDate != null) {
                                        state.calendarScheduledCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    manualOverrideCount = if (dayDate != null) {
                                        state.calendarManualOverrideCountByDate[dayDate] ?: 0
                                    } else {
                                        0
                                    },
                                    maxCompletedInMonth = maxCompletedInMonth,
                                    onClick = { dayDate?.let(vm::selectDate) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            BreakdownCard(
                selectedDate = state.selectedDate,
                completedCount = state.calendarBreakdownCompletedCount,
                scheduledCount = state.calendarBreakdownScheduledCount,
                items = state.calendarBreakdownItems
            )
        }
    }
}

@Composable
private fun GlobalCalendarHeatCell(
    date: LocalDate?,
    selected: Boolean,
    today: Boolean,
    completedCount: Int,
    scheduledCount: Int,
    manualOverrideCount: Int,
    maxCompletedInMonth: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors

    if (date == null) {
        Box(
            modifier = modifier
                .height(spacing.x5 + spacing.x0_5)
        )
        return
    }

    val todayDate = LocalDate.now()
    val isToday = date.isEqual(todayDate)
    val isFuture = date.isAfter(todayDate)
    val hasManualOverride = manualOverrideCount > 0 && scheduledCount <= 0 && !isFuture && !isToday
    val intensityLevel = if (completedCount <= 0 || maxCompletedInMonth <= 0) {
        0
    } else {
        kotlin.math.ceil((completedCount.toFloat() / maxCompletedInMonth.toFloat()) * 4f).toInt().coerceIn(1, 4)
    }
    val fillColor = when {
        isToday -> Color.Transparent
        isFuture -> Color.Transparent
        hasManualOverride -> colors.success.copy(alpha = 0.18f)
        scheduledCount <= 0 -> Color.Transparent
        completedCount <= 0 -> colors.danger.copy(alpha = 0.10f)
        intensityLevel == 1 -> colors.success.copy(alpha = 0.31f)
        intensityLevel == 2 -> colors.success.copy(alpha = 0.46f)
        intensityLevel == 3 -> colors.success.copy(alpha = 0.64f)
        else -> colors.success.copy(alpha = 0.81f)
    }
    val baseBorderColor = when {
        isToday -> colors.primary
        isFuture -> Color.Transparent
        hasManualOverride -> colors.success.copy(alpha = 0.45f)
        scheduledCount <= 0 -> Color.Transparent
        completedCount <= 0 -> colors.danger.copy(alpha = 0.25f)
        else -> Color.Transparent
    }
    val baseBorderWidth = when {
        isToday -> 1.5.dp
        hasManualOverride -> 1.dp
        completedCount <= 0 && scheduledCount > 0 && !isFuture -> 1.dp
        else -> 0.dp
    }
    val borderColor = if (selected && !isToday) {
        colors.primary.copy(alpha = 0.72f)
    } else {
        baseBorderColor
    }
    val borderWidth = if (selected && !isToday) {
        stroke.medium
    } else {
        baseBorderWidth
    }
    val textColor = when {
        isToday -> colors.primary
        isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
        hasManualOverride -> colors.success
        completedCount > 0 -> MaterialTheme.colorScheme.onPrimary
        scheduledCount > 0 -> colors.danger
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
    }

    Box(
        modifier = modifier
            .height(spacing.x5 + spacing.x0_5)
            .clip(RoundedCornerShape(radius.sm))
            .background(fillColor, RoundedCornerShape(radius.sm))
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(radius.sm)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (selected || today || isToday) FontWeight.SemiBold else FontWeight.Medium
        )
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
    onOpenPaywall: (PaywallTrigger) -> Unit,
    onOpenManageSubscription: () -> Unit,
    onOpenSettings: () -> Unit,
    onExportData: () -> Result<String>,
    onResetProgress: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val spacing = AppTheme.spacing
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val locale = appLocale()
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    }
    val freeHabitLimit = 3
    val activeHabitsCount = state.tasks.size
    val usageProgress = (activeHabitsCount.toFloat() / freeHabitLimit.toFloat()).coerceIn(0f, 1f)
    val freeSlots = (freeHabitLimit - activeHabitsCount).coerceAtLeast(0)
    @Suppress("DEPRECATION")
    val appVersionName = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        }.getOrDefault("")
    }
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val colorScheme = MaterialTheme.colorScheme
    val planCardSurface = if (isDark) Color(0xFF0A1F13) else colorScheme.surface
    val innerWidgetSurface = if (isDark) Color(0xFF0F2318) else colorScheme.background
    val innerSunkenSurface = if (isDark) Color(0xFF152E1F) else colorScheme.surfaceVariant
    val primaryOnDarkText = if (isDark) Color(0xFFE8F5EF) else colorScheme.onBackground
    val secondaryOnDarkText = if (isDark) Color(0xFF9ECFB4) else colorScheme.onSurfaceVariant
    val mutedOnDarkText = if (isDark) Color(0xFF6AAA85) else colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val disabledOnDarkText = if (isDark) Color(0xFF4A7A5E) else colorScheme.outline
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val accentSecondary = if (isDark) Color(0xFF1D9E75) else colorScheme.primary
    val sectionDividerColor = if (isDark) Color(0xFF1A3528) else colorScheme.outline.copy(alpha = 0.45f)
    val freeBadgeBackground = if (isDark) Color(0xFF1A2A1A) else colorScheme.surfaceVariant
    val activeBadgeBackground = if (isDark) Color(0xFF1A3A20) else accentPrimary.copy(alpha = 0.14f)
    val cancelledBadgeBackground = if (isDark) Color(0xFF2A1F0A) else colorScheme.error.copy(alpha = 0.12f)
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.x2, vertical = spacing.x2),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            val featureItems = listOf(
                t("Unlimited habits"),
                t("Home screen widgets"),
                t("Advanced analytics")
            )

            val cardBorderColor = when (state.subscriptionState) {
                is SubscriptionState.PremiumActive -> accentPrimary
                is SubscriptionState.PremiumCancelled -> if (isDark) Color(0xFF3A1A1A) else colorScheme.error.copy(alpha = 0.45f)
                SubscriptionState.Free -> Color.Transparent
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = planCardSurface,
                shape = RoundedCornerShape(16.dp),
                border = if (state.subscriptionState == SubscriptionState.Free) null else BorderStroke(1.5.dp, cardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (val subscriptionState = state.subscriptionState) {
                        SubscriptionState.Free -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = t("Current plan").uppercase(locale),
                                        fontSize = 11.sp,
                                        color = mutedOnDarkText,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = t("plan_free_title"),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryOnDarkText
                                    )
                                    Text(
                                        text = "$activeHabitsCount / $freeHabitLimit ${t("plan_habits_usage").lowercase(locale)}",
                                        fontSize = 12.sp,
                                        color = mutedOnDarkText
                                    )
                                }
                                Surface(
                                    color = freeBadgeBackground,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = t("plan_free_badge"),
                                        color = mutedOnDarkText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = innerWidgetSurface,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = t("plan_habits_usage"),
                                            fontSize = 12.sp,
                                            color = secondaryOnDarkText
                                        )
                                        Text(
                                            text = "$activeHabitsCount / $freeHabitLimit",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryOnDarkText
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { usageProgress },
                                        color = if (activeHabitsCount >= freeHabitLimit) Color(0xFFF59E42) else accentSecondary,
                                        trackColor = innerSunkenSurface,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                    )
                                    Text(
                                        text = if (activeHabitsCount >= freeHabitLimit) {
                                            t("plan_limit_reached")
                                        } else {
                                            t("plan_slots_free").replace("{n}", freeSlots.toString())
                                        },
                                        fontSize = 10.sp,
                                        color = if (activeHabitsCount >= freeHabitLimit) Color(0xFFF59E42) else mutedOnDarkText
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                            featureItems.forEach { feature ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(15.dp)
                                            .border(1.5.dp, if (isDark) Color(0xFF2A4A38) else colorScheme.outline, RoundedCornerShape(999.dp))
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        fontSize = 13.sp,
                                        color = disabledOnDarkText
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onOpenPaywall(PaywallTrigger.DEFAULT) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentPrimary,
                                    contentColor = Color(0xFF04342C)
                                ),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Text(
                                    text = "${t("Get Premium")} ✦",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        is SubscriptionState.PremiumActive -> {
                            val isLifetime = subscriptionState.plan == PremiumPlan.LIFETIME
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = t("Current plan").uppercase(locale),
                                        fontSize = 11.sp,
                                        color = mutedOnDarkText,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${t("Premium")} ✦",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryOnDarkText
                                    )
                                }
                                Surface(
                                    color = activeBadgeBackground,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = t("Active"),
                                        color = accentPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            if (!isLifetime && subscriptionState.nextBillingDate != null) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = t("Next billing"),
                                        fontSize = 13.sp,
                                        color = secondaryOnDarkText
                                    )
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = subscriptionState.nextBillingAmount.orEmpty(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryOnDarkText
                                        )
                                        Text(
                                            text = subscriptionState.nextBillingDate.format(dateFormatter),
                                            fontSize = 11.sp,
                                            color = mutedOnDarkText
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                            featureItems.forEach { feature ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = accentSecondary,
                                        shape = RoundedCornerShape(999.dp),
                                        modifier = Modifier.size(15.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        fontSize = 13.sp,
                                        color = secondaryOnDarkText
                                    )
                                }
                            }                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onOpenManageSubscription,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentPrimary,
                                    contentColor = Color(0xFF04342C)
                                ),
                                contentPadding = PaddingValues(vertical = 13.dp)
                            ) {
                                Text(
                                    text = t("manage_subscription"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        is SubscriptionState.PremiumCancelled -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = t("Current plan").uppercase(locale),
                                        fontSize = 11.sp,
                                        color = mutedOnDarkText,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${t("Premium")} ✦",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryOnDarkText
                                    )
                                }
                                Surface(
                                    color = cancelledBadgeBackground,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = t("Until expiry"),
                                        color = Color(0xFFF59E42),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t("Premium active until"),
                                    fontSize = 13.sp,
                                    color = secondaryOnDarkText
                                )
                                Text(
                                    text = subscriptionState.expiresOn.format(dateFormatter),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E42)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = t("No charge ✓"),
                                fontSize = 12.sp,
                                color = accentPrimary
                            )

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = innerSunkenSurface, thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                            featureItems.forEach { feature ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = accentSecondary,
                                        shape = RoundedCornerShape(999.dp),
                                        modifier = Modifier.size(15.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        fontSize = 13.sp,
                                        color = secondaryOnDarkText
                                    )
                                }
                            }                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onOpenManageSubscription,
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.5.dp, accentPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentPrimary),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 13.dp)
                            ) {
                                Text(
                                    text = t("manage_subscription"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AccountSectionLabel(title = t("Support"))
            AccountActionCard {
                AccountActionRow(
                    title = t("Help center"),
                    onClick = {
                        Toast.makeText(
                            context,
                            translate(language, "Help center is not available in debug build."),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                HorizontalDivider(color = sectionDividerColor, thickness = 1.dp)
                AccountActionRow(
                    title = t("Contact support"),
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

        item {
            AccountSectionLabel(title = t("Data"))
            AccountActionCard {
                AccountActionRow(
                    title = t("Export data"),
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
                HorizontalDivider(color = sectionDividerColor, thickness = 1.dp)
                AccountActionRow(
                    title = t("Reset progress"),
                    onClick = { showResetConfirm = true }
                )
            }
            Spacer(Modifier.height(4.dp))
            AccountActionCard {
                AccountActionRow(
                    title = t("Delete account"),
                    destructive = true,
                    onClick = { showDeleteConfirm = true }
                )
            }
        }

        item {
            Text(
                text = "Micro Habit · $appVersionName",
                fontSize = 11.sp,
                color = mutedOnDarkText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
        }
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
private fun AccountSectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = AppTheme.colors.textTertiary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun AccountActionCard(content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.backgroundSurfaceMuted,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colors.borderSubtle.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun AccountActionRow(
    title: String,
    destructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = if (destructive) AppTheme.colors.danger else AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 16.sp,
            color = if (destructive) AppTheme.colors.danger else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SettingsPage(
    state: HabitUiState,
    onSetTheme: (AppThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetMinimumCompletionPercent: (Int) -> Unit,
    onOpenPaywall: () -> Unit,
    onExportData: () -> Result<String>,
    onResetProgress: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showResetConfirm by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showCompletionThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var completionPercentInput by rememberSaveable(state.minimumCompletionPercent) {
        mutableStateOf(state.minimumCompletionPercent.coerceIn(1, 100).toString())
    }
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
            SettingsGroup(title = t("Appearance")) {
                SettingsRow(
                    title = t("Theme"),
                    value = themeLabel(state.themeMode, language),
                    onClick = { showThemeDialog = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Language")) {
                SettingsRow(
                    title = t("Language"),
                    value = languageNativeLabel(state.language),
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Tracking")) {
                SettingsRow(
                    title = t("Completion threshold"),
                    value = "${state.minimumCompletionPercent}%",
                    onClick = {
                        completionPercentInput = state.minimumCompletionPercent.coerceIn(1, 100).toString()
                        showCompletionThresholdDialog = true
                    }
                )
            }
        }

        item {
            SettingsGroup(title = t("Subscription")) {
                SettingsRow(
                    title = t("Manage subscription"),
                    value = if (state.plan.hasPremiumAccess()) t("Premium") else t("Free"),
                    onClick = onOpenPaywall
                )
            }
        }

        item {
            SettingsGroup(title = t("Data")) {
                SettingsRow(
                    title = t("Export data"),
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
                    onClick = { showResetConfirm = true }
                )
            }
        }

        item {
            SettingsGroup(title = t("Danger zone")) {
                SettingsRow(
                    title = t("Delete account"),
                    destructive = true,
                    onClick = { showDeleteConfirm = true }
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
        val parsedThreshold = completionPercentInput.toIntOrNull()
        val isValidThreshold = parsedThreshold != null && parsedThreshold in 1..100
        val showError = completionPercentInput.isNotBlank() && !isValidThreshold
        AlertDialog(
            onDismissRequest = { showCompletionThresholdDialog = false },
            title = { Text(t("Minimum completion percent")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                    Text(
                        text = t("Applies to count and duration habits"),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = t("Completion threshold"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        OutlinedTextField(
                            value = completionPercentInput,
                            onValueChange = { value ->
                                completionPercentInput = value.filter { it.isDigit() }.take(3)
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = showError
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textSecondary
                        )
                    }
                    Text(
                        text = t("For example: 100%"),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                    if (showError) {
                        Text(
                            text = t("Value must be between 1 and 100"),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.danger
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isValidThreshold) return@Button
                        val threshold = parsedThreshold ?: return@Button
                        showCompletionThresholdDialog = false
                        onSetMinimumCompletionPercent(threshold.coerceIn(1, 100))
                    },
                    enabled = isValidThreshold
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
    trigger: PaywallTrigger,
    selectedBilling: BillingCycle,
    onSelectBilling: (BillingCycle) -> Unit,
    onSubscribe: () -> Unit,
    onRestorePurchase: () -> Unit,
    onClose: () -> Unit
) {
    val colors = AppTheme.colors
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val legalColor = if (isDark) Color(0xFF6AAA85) else colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val footerDividerColor = if (isDark) Color(0xFF1A3528) else colorScheme.outline.copy(alpha = 0.45f)
    val subtitle = when (trigger) {
        PaywallTrigger.HABIT_LIMIT -> t("You reached the limit of 3 habits.")
        PaywallTrigger.ANALYTICS -> t("Unlock advanced analytics.")
        PaywallTrigger.WIDGETS -> t("Add widgets on your home screen.")
        PaywallTrigger.DEFAULT -> t("Get access to all features.")
    }
    val ctaLabel = when (selectedBilling) {
        BillingCycle.YEARLY -> "${t("Get Premium")} — \$24.99 / ${t("year")}".replace('$', '$')
        BillingCycle.MONTHLY -> "${t("Get Premium")} — \$3.99 / ${t("month")}".replace('$', '$')
        BillingCycle.LIFETIME -> "${t("Get Premium")} — \$59.99".replace('$', '$')
    }
    val _ignore = onClose

    Column(Modifier.fillMaxSize().background(colors.backgroundCanvas)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("✦", fontSize = 26.sp, color = colors.primary)
                    Text(t("Unlock Premium"), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Text(subtitle, fontSize = 12.sp, color = colors.textSecondary)
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), color = colors.backgroundSurface, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp)) {
                        PremiumFeatureRow(t("Unlimited habits"), t("Track as many goals as you want"), showSubtitle = false)
                        PremiumFeatureRow(t("Home screen widgets"), t("Progress right on your home screen"), showSubtitle = false)
                        PremiumFeatureRow(t("Advanced analytics"), t("Patterns, consistency, best time insights"), showSubtitle = false)
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaywallPlanCard(
                        title = t("Yearly"),
                        price = "\$24.99",
                        meta = "/ ${t("year")} · \$2.08 / mo".replace('$', '$'),
                        selected = selectedBilling == BillingCycle.YEARLY,
                        isLifetime = false,
                        badge = t("paywall_savings_badge"),
                        onClick = { onSelectBilling(BillingCycle.YEARLY) }
                    )
                    PaywallPlanCard(
                        title = t("Monthly"),
                        price = "\$3.99",
                        meta = "/ ${t("month")}",
                        selected = selectedBilling == BillingCycle.MONTHLY,
                        isLifetime = false,
                        onClick = { onSelectBilling(BillingCycle.MONTHLY) }
                    )
                    PaywallPlanCard(
                        title = t("Lifetime"),
                        price = "\$59.99",
                        meta = "· ${t("plan_lifetime_subtitle")}",
                        selected = selectedBilling == BillingCycle.LIFETIME,
                        isLifetime = true,
                        badge = t("plan_lifetime_forever_badge"),
                        onClick = { onSelectBilling(BillingCycle.LIFETIME) }
                    )
                }
            }
        }

        HorizontalDivider(color = footerDividerColor, thickness = 1.dp)
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth(),
                enabled = !currentPlan.hasPremiumAccess(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentPrimary,
                    contentColor = if (isDark) Color(0xFF04342C) else colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(ctaLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 2.dp))
            }

            Text(
                text = t("Restore purchase"),
                fontSize = 12.sp,
                color = accentPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 6.dp).clickable(onClick = onRestorePurchase)
            )
            Spacer(Modifier.height(8.dp))
            Text(t("Auto-renewal. Cancel anytime."), fontSize = 11.sp, lineHeight = 16.5.sp, color = legalColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(t("Terms"), fontSize = 11.sp, color = legalColor, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { })
                Text(" · ", fontSize = 11.sp, color = legalColor)
                Text(t("Privacy"), fontSize = 11.sp, color = legalColor, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { })
            }
        }
    }
}
@Composable
private fun PaywallPlanCard(
    title: String,
    price: String,
    meta: String,
    selected: Boolean,
    isLifetime: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val colors = AppTheme.colors
    val isDark = isSystemInDarkTheme()
    val selectedBorder = if (isDark) Color(0xFF2DCF96) else colors.primary
    val selectedTitle = if (isDark) Color(0xFFE8F5EF) else colors.textPrimary
    val selectedPrice = if (isDark) Color(0xFF2DCF96) else colors.primary
    val selectedMeta = if (isDark) Color(0xFF6AAA85) else colors.textSecondary
    val selectedBadgeBg = if (isDark) Color(0xFF1A3A20) else colors.primaryMuted
    val selectedBadgeText = if (isDark) Color(0xFF2DCF96) else colors.primary
    val lifetimeSelectedBadgeBg = if (isDark) Color(0xFF1A2A1A) else colors.warning.copy(alpha = 0.16f)
    val lifetimeSelectedBadgeText = if (isDark) Color(0xFFF59E42) else colors.warning
    val unselectedTitle = if (isDark) Color(0xFF9ECFB4) else colors.textTertiary
    val unselectedPrice = if (isDark) Color(0xFF9ECFB4) else colors.textTertiary
    val unselectedMeta = if (isDark) Color(0xFF4A7A5E) else colors.textTertiary.copy(alpha = 0.88f)
    val unselectedBadgeBg = if (isDark) Color(0xFF152E1F) else colors.backgroundSurfaceMuted
    val unselectedBadgeText = if (isDark) Color(0xFF4A7A5E) else colors.textTertiary
    val lifetimeUnselectedBadgeBg = if (isDark) Color(0xFF151E15) else colors.backgroundSurfaceMuted
    val lifetimeUnselectedBadgeText = if (isDark) Color(0xFF7A6030) else colors.textTertiary

    val titleColor = if (selected) selectedTitle else unselectedTitle
    val priceColor = if (selected) selectedPrice else unselectedPrice
    val metaColor = if (selected) selectedMeta else unselectedMeta
    val badgeBgColor = when {
        badge == null -> Color.Transparent
        isLifetime && selected -> lifetimeSelectedBadgeBg
        isLifetime && !selected -> lifetimeUnselectedBadgeBg
        selected -> selectedBadgeBg
        else -> unselectedBadgeBg
    }
    val badgeTextColor = when {
        badge == null -> Color.Transparent
        isLifetime && selected -> lifetimeSelectedBadgeText
        isLifetime && !selected -> lifetimeUnselectedBadgeText
        selected -> selectedBadgeText
        else -> unselectedBadgeText
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = colors.backgroundSurface,
        border = if (selected) BorderStroke(1.5.dp, selectedBorder) else null
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 13.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
                if (!badge.isNullOrBlank()) {
                    Text(
                        badge,
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(badgeBgColor, RoundedCornerShape(5.dp)).padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = priceColor)
                Spacer(Modifier.width(4.dp))
                Text(meta, fontSize = 12.sp, color = metaColor, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
private fun ManageSubscriptionScreen(
    state: HabitUiState,
    onOpenPaywall: (PaywallTrigger) -> Unit,
    onRestorePurchase: () -> Unit,
    onCancelSubscription: () -> Unit,
    onRenewSubscription: () -> Unit,
    onSwitchPlan: (PremiumPlan) -> Unit,
    onDebugForceFree: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val context = LocalContext.current
    val locale = appLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale) }
    var showCancelSheet by rememberSaveable { mutableStateOf(false) }
    var showMilestonePreview by remember { mutableStateOf(false) }
    var previewDays by remember { mutableStateOf<Int?>(null) }
    val subscriptionState = state.subscriptionState
    val activeState = subscriptionState as? SubscriptionState.PremiumActive
    var selectedPlan by rememberSaveable(subscriptionState) { mutableStateOf(activeState?.plan ?: PremiumPlan.YEARLY) }
    val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val colorScheme = MaterialTheme.colorScheme
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val darkInnerSurface = if (isDark) Color(0xFF152E1F) else colorScheme.surfaceVariant
    val activeBadgeBg = if (isDark) Color(0xFF1A3A20) else accentPrimary.copy(alpha = 0.14f)
    val warningBadgeBg = if (isDark) Color(0xFF2A1F0A) else colorScheme.error.copy(alpha = 0.12f)
    val lifetimeBadgeBg = if (isDark) Color(0xFF1A2A1A) else colorScheme.surfaceVariant
    val activeCardBorder = accentPrimary
    val cancelledCardBorder = if (isDark) Color(0xFF3A1A1A) else colorScheme.error.copy(alpha = 0.45f)

    LaunchedEffect(activeState?.plan) { if (activeState != null) selectedPlan = activeState.plan }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(spacing.x2), verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
        item {
            when (subscriptionState) {
                SubscriptionState.Free -> {
                    ManageSubscriptionCard(colors.borderSubtle.copy(alpha = 0.45f)) {
                        Text(t("Free"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(t("Upgrade to Premium to unlock all features."), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { onOpenPaywall(PaywallTrigger.DEFAULT) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentPrimary, contentColor = Color(0xFF04342C)),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text(t("Get Premium"), fontWeight = FontWeight.SemiBold) }
                    }
                }

                is SubscriptionState.PremiumActive -> {
                    val isLifetime = subscriptionState.plan == PremiumPlan.LIFETIME
                    ManageSubscriptionCard(activeCardBorder, 1.5.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(t("Active plan"), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                            Surface(color = activeBadgeBg, shape = RoundedCornerShape(999.dp)) {
                                Text(t("Active"), color = accentPrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (isLifetime) "${managePlanName(subscriptionState.plan)} ✦" else "${managePlanName(subscriptionState.plan)} · ${managePlanPriceSummary(subscriptionState.plan)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = colors.borderSubtle.copy(alpha = 0.35f))
                        Spacer(Modifier.height(10.dp))
                        if (isLifetime) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(t("billing_no_recurring"), color = colors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                                Text(t("billing_never"), color = accentPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Text(t("Next billing"), color = colors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(subscriptionState.nextBillingAmount.orEmpty(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(subscriptionState.nextBillingDate?.format(dateFormatter).orEmpty(), style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                                }
                            }
                        }
                    }
                }

                is SubscriptionState.PremiumCancelled -> {
                    ManageSubscriptionCard(cancelledCardBorder, 1.5.dp) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(t("Subscription cancelled"), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                            Surface(color = warningBadgeBg, shape = RoundedCornerShape(999.dp)) {
                                Text(t("Until expiry"), style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E42), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(t("cancelled_plan_title").replace("{date}", subscriptionState.expiresOn.format(dateFormatter)), color = Color(0xFFF59E42), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = colors.borderSubtle.copy(alpha = 0.35f))
                        Spacer(Modifier.height(10.dp))
                        Text(t("cancelled_plan_subtitle").replace("{date}", subscriptionState.expiresOn.format(dateFormatter)), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    }
                }
            }
        }

        if (activeState != null) {
            if (activeState.plan == PremiumPlan.LIFETIME) {
                item {
                    ManageSubscriptionCard(colors.borderSubtle.copy(alpha = 0.45f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(t("plan_switcher_title"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("🔒 ${t("plan_switcher_unavailable")}", fontSize = 11.sp, color = colors.textSecondary)
                        }
                        Spacer(Modifier.height(10.dp))
                        ManagePlanSwitchRow(managePlanName(PremiumPlan.MONTHLY), "\$3.99 / ${t("month")}", selected = false, enabled = false, opacity = 0.45f, onClick = {})
                        Spacer(Modifier.height(8.dp))
                        ManagePlanSwitchRow(managePlanName(PremiumPlan.YEARLY), "\$24.99 / ${t("year")}", selected = false, enabled = false, opacity = 0.45f, badge = "-48%", onClick = {})
                        Spacer(Modifier.height(8.dp))
                        ManagePlanSwitchRow(
                            title = managePlanName(PremiumPlan.LIFETIME),
                            price = "\$59.99 · ${t("plan_lifetime_subtitle")}",
                            selected = true,
                            enabled = false,
                            opacity = 1f,
                            badge = t("plan_lifetime_yours"),
                            badgeBackground = lifetimeBadgeBg,
                            badgeColor = Color(0xFFF59E42),
                            onClick = {}
                        )
                        Spacer(Modifier.height(10.dp))
                        Surface(color = darkInnerSurface, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✦", color = Color(0xFFF59E42))
                                Text(t("plan_lifetime_locked"), fontSize = 12.sp, color = colors.textSecondary)
                            }
                        }
                    }
                }
            } else {
                item {
                    val ctaEnabled = selectedPlan != activeState.plan
                    ManageSubscriptionCard(colors.borderSubtle.copy(alpha = 0.45f)) {
                        Text(t("plan_switcher_title"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(2.dp))
                        Text(t("plan_switcher_credit"), fontSize = 12.sp, color = colors.textSecondary)
                        Spacer(Modifier.height(10.dp))
                        ManagePlanSwitchRow(managePlanName(PremiumPlan.MONTHLY), "\$3.99 / ${t("month")}", selected = selectedPlan == PremiumPlan.MONTHLY, enabled = true, onClick = { selectedPlan = PremiumPlan.MONTHLY })
                        Spacer(Modifier.height(8.dp))
                        ManagePlanSwitchRow(managePlanName(PremiumPlan.YEARLY), "\$24.99 / ${t("year")}", selected = selectedPlan == PremiumPlan.YEARLY, enabled = true, badge = "-48%", onClick = { selectedPlan = PremiumPlan.YEARLY })
                        Spacer(Modifier.height(8.dp))
                        ManagePlanSwitchRow(
                            title = managePlanName(PremiumPlan.LIFETIME),
                            price = "\$59.99 · ${t("plan_lifetime_subtitle")}",
                            selected = selectedPlan == PremiumPlan.LIFETIME,
                            enabled = true,
                            badge = t("plan_lifetime_forever_badge"),
                            badgeBackground = lifetimeBadgeBg,
                            badgeColor = Color(0xFFF59E42),
                            onClick = { selectedPlan = PremiumPlan.LIFETIME }
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onSwitchPlan(selectedPlan) },
                            enabled = ctaEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentPrimary,
                                contentColor = Color(0xFF04342C),
                                disabledContainerColor = colors.backgroundSurfaceMuted,
                                disabledContentColor = colors.textSecondary
                            )
                        ) { Text(managePlanCtaLabel(selectedPlan), fontWeight = FontWeight.SemiBold) }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = managePlanHintText(activeState.plan, selectedPlan, activeState.nextBillingDate, dateFormatter),
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (subscriptionState is SubscriptionState.PremiumCancelled) {
            item {
                ManageSubscriptionCard(colors.borderSubtle.copy(alpha = 0.45f)) {
                    Text(t("Changed your mind?"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(t("cancelled_renew_subtitle").replace("{date}", subscriptionState.expiresOn.format(dateFormatter)), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = onRenewSubscription,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accentPrimary, contentColor = Color(0xFF04342C)),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text(t("Renew subscription"), fontWeight = FontWeight.SemiBold) }
                }
            }
        }

        if (subscriptionState != SubscriptionState.Free) {
            item {
                ManageSubscriptionCard(colors.borderSubtle.copy(alpha = 0.45f)) {
                    Text(t("Included in Premium"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    FeatureBulletRow(t("Unlimited habits"))
                    Spacer(Modifier.height(6.dp))
                    FeatureBulletRow(t("Home screen widgets"))
                    Spacer(Modifier.height(6.dp))
                    FeatureBulletRow(t("Advanced analytics"))
                }
            }
        }

        if (activeState != null && activeState.plan != PremiumPlan.LIFETIME) {
            item {
                Text(
                    text = t("Cancel subscription"),
                    fontSize = 13.sp,
                    color = colors.danger,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { showCancelSheet = true }
                )
            }
        }

        if (subscriptionState != SubscriptionState.Free) {
            item {
                Text(
                    text = t("Restore purchase"),
                    fontSize = 12.sp,
                    color = AppTheme.colors.textTertiary,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = onRestorePurchase)
                )
            }
        }

        if (isDebugBuild) {
            item {
                ManageSubscriptionCard(Color(0xFF3A3A10)) {
                    Text("Debug", style = MaterialTheme.typography.labelMedium, color = Color(0xFF8A8A30))
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(onClick = onDebugForceFree, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color(0xFF3A3A10))) {
                        Text(t("Switch to Free plan"))
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { showMilestonePreview = true }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color(0xFF3A3A10))) {
                        Text("Preview milestones →")
                    }
                }
            }
        }
    }

    if (showCancelSheet && activeState != null && activeState.plan != PremiumPlan.LIFETIME) {
        CancelSubscriptionSheet(
            state = activeState,
            onDismiss = { showCancelSheet = false },
            onConfirmCancel = {
                onCancelSubscription()
                showCancelSheet = false
            }
        )
    }

    if (isDebugBuild && showMilestonePreview) {
        MilestonePreviewSheet(
            onDismiss = { showMilestonePreview = false },
            onSelect = { days ->
                showMilestonePreview = false
                previewDays = days
            }
        )
    }

    if (isDebugBuild) {
        previewDays?.let { days ->
            StreakMilestoneScreen(
                habitId = "",
                days = days,
                forceShow = true,
                onDismiss = { previewDays = null }
            )
        }
    }
}

@Composable
private fun ManageSubscriptionCard(
    borderColor: Color,
    borderWidth: androidx.compose.ui.unit.Dp = 1.dp,
    content: @Composable () -> Unit
) {
    val colors = AppTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.backgroundSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) { content() }
    }
}

@Composable
private fun ManagePlanSwitchRow(
    title: String,
    price: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    badgeBackground: Color = Color.Unspecified,
    badgeColor: Color = Color.Unspecified,
    opacity: Float = 1f
) {
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val colorScheme = MaterialTheme.colorScheme
    val cardBackground = if (isDark) Color(0xFF0A1F13) else colorScheme.surface

    val selectedBorder = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val checkFill = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val unselectedRing = if (isDark) Color(0xFF2A5A3A) else colorScheme.outline
    val baseBadgeBackground = if (badgeBackground == Color.Unspecified) Color(0xFF1A3A20) else badgeBackground
    val baseBadgeColor = if (badgeColor == Color.Unspecified) { if (isDark) Color(0xFF2DCF96) else colorScheme.primaryContainer } else badgeColor
    val badgeBg = if (isDark) baseBadgeBackground else colorScheme.surfaceVariant
    val badgeFg = if (isDark) baseBadgeColor else colorScheme.primaryContainer
    val selectedTitleColor = if (isDark) Color(0xFFE8F5EF) else AppTheme.colors.textPrimary
    val unselectedTitleColor = if (isDark) Color(0xFF9ECFB4) else AppTheme.colors.textSecondary
    val selectedPriceColor = if (isDark) Color(0xFF2DCF96) else AppTheme.colors.primary
    val unselectedPriceColor = if (isDark) Color(0xFF4A7A5E) else AppTheme.colors.textTertiary

    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier).graphicsLayer { alpha = opacity },
        shape = RoundedCornerShape(14.dp),
        color = cardBackground,
        border = if (selected) BorderStroke(1.5.dp, selectedBorder) else null
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Surface(Modifier.size(20.dp), shape = RoundedCornerShape(999.dp), color = checkFill) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(11.dp)) }
                }
            } else {
                Box(Modifier.size(20.dp).border(2.dp, unselectedRing, RoundedCornerShape(999.dp)))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (selected) selectedTitleColor else unselectedTitleColor)
                    if (!badge.isNullOrBlank()) {
                        Text(badge, color = badgeFg, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(badgeBg, RoundedCornerShape(5.dp)).padding(horizontal = 7.dp, vertical = 2.dp))
                    }
                }
                Text(price, fontSize = 13.sp, color = if (selected) selectedPriceColor else unselectedPriceColor)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MilestonePreviewSheet(
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val darkPrimaryText = if (isDark) Color(0xFFE8F5EF) else colorScheme.onBackground
    val darkMutedText = if (isDark) Color(0xFF6AAA85) else colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val dividerColor = if (isDark) Color(0xFF1A3528) else colorScheme.outline.copy(alpha = 0.45f)
    val rows = remember {
        streakMilestoneTierDefinitions.map { definition ->
            definition.days to definition.accentColor
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = AppTheme.colors.backgroundCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isDark) Color(0xFF2A4A38) else colorScheme.outline.copy(alpha = 0.45f))
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Preview milestone screens",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = darkPrimaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                rows.forEachIndexed { index, row ->
                    val days = row.first
                    val accent = row.second
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(days) }
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(accent)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "$days ${streakDaysUnit(days)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkPrimaryText
                            )
                            Text(
                                text = t("milestone_badge_${days}"),
                                fontSize = 11.sp,
                                color = AppTheme.colors.textTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "›",
                            fontSize = 16.sp,
                            color = darkMutedText
                        )
                    }
                    if (index != rows.lastIndex) {
                        HorizontalDivider(color = dividerColor, thickness = 1.dp)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancelSubscriptionSheet(
    state: SubscriptionState.PremiumActive,
    onDismiss: () -> Unit,
    onConfirmCancel: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val colorScheme = MaterialTheme.colorScheme
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val statusCardSurface = if (isDark) Color(0xFF0A1F13) else colorScheme.surface
    val keepButtonBorder = if (isDark) Color(0xFF1A3A27) else colorScheme.outline
    val keepButtonText = if (isDark) Color(0xFF9ECFB4) else colorScheme.onSurfaceVariant
    val locale = appLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale) }
    val activeUntil = state.nextBillingDate?.format(dateFormatter).orEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.backgroundCanvas
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(t("cancel_sheet_title"), fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Text("${managePlanName(state.plan)} · ${managePlanPriceSummary(state.plan)}", fontSize = 13.sp, color = colors.textSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

            Surface(Modifier.fillMaxWidth(), color = statusCardSurface, shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(t("Premium active until"), color = colors.textSecondary, fontSize = 12.sp)
                        Text(activeUntil, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = colors.borderSubtle.copy(alpha = 0.28f))
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(t("Charge"), color = colors.textSecondary, fontSize = 12.sp)
                        Text(t("No charge ✓"), color = accentPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Surface(Modifier.fillMaxWidth(), color = colors.backgroundSurface, shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t("cancel_loses_title").replace("{date}", activeUntil), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.danger)
                    CancelSheetLossRow(t("Home screen widgets"))
                    CancelSheetLossRow(t("Advanced analytics"))
                    CancelSheetLossRow(t("More than 3 active habits"))
                }
            }

            Spacer(Modifier.height(6.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, keepButtonBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = keepButtonText),
                shape = RoundedCornerShape(14.dp)
            ) { Text(t("Keep Premium"), fontWeight = FontWeight.SemiBold) }

            Text(t("cancel_confirm_btn"), fontSize = 13.sp, color = colors.danger, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable(onClick = onConfirmCancel))
            Spacer(Modifier.height(spacing.x1))
        }
    }
}
@Composable
private fun CancelSheetLossRow(label: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("✕", color = AppTheme.colors.danger, fontWeight = FontWeight.Bold)
        Text(label, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun PremiumPlan.displayName(): String = when (this) {
    PremiumPlan.MONTHLY -> t("Monthly")
    PremiumPlan.YEARLY -> t("Yearly")
    PremiumPlan.LIFETIME -> t("Lifetime")
}

@Composable
private fun planPriceLabel(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> "\$3.99 / ${t("month")}".replace('$', '$')
    PremiumPlan.YEARLY -> "\$24.99 / ${t("year")}".replace('$', '$')
    PremiumPlan.LIFETIME -> "\$59.99".replace('$', '$')
}

@Composable
private fun managePlanName(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> t("plan_monthly")
    PremiumPlan.YEARLY -> t("plan_yearly")
    PremiumPlan.LIFETIME -> t("plan_lifetime")
}

@Composable
private fun managePlanPriceSummary(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> "\$3.99 / ${t("month")}".replace('$', '$')
    PremiumPlan.YEARLY -> "\$24.99 / ${t("year")}".replace('$', '$')
    PremiumPlan.LIFETIME -> "\$59.99".replace('$', '$')
}

@Composable
private fun managePlanCtaLabel(plan: PremiumPlan): String = t("switch_to_plan").replace("{plan}", managePlanName(plan))

@Composable
private fun managePlanHintText(currentPlan: PremiumPlan, targetPlan: PremiumPlan, nextBillingDate: LocalDate?, formatter: DateTimeFormatter): String {
    if (currentPlan == targetPlan) return t("plan_switcher_select_hint")
    val baseDate = nextBillingDate ?: LocalDate.now()
    return when {
        currentPlan == PremiumPlan.MONTHLY && targetPlan == PremiumPlan.YEARLY -> t("hint_yearly_upgrade").replace("{date}", baseDate.plusYears(1).format(formatter))
        targetPlan == PremiumPlan.LIFETIME -> t("hint_lifetime_upgrade")
        currentPlan == PremiumPlan.YEARLY && targetPlan == PremiumPlan.MONTHLY -> t("hint_monthly_downgrade").replace("{date}", baseDate.format(formatter))
        else -> t("plan_switcher_select_hint")
    }
}

@Composable
private fun PremiumFeatureRow(title: String, subtitle: String, showSubtitle: Boolean = true) {
    val colors = AppTheme.colors
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = if (showSubtitle) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(Modifier.size(18.dp), shape = RoundedCornerShape(999.dp), color = accentPrimary.copy(alpha = if (isDark) 0.2f else 0.14f)) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Check, contentDescription = null, tint = accentPrimary, modifier = Modifier.size(12.dp)) }
        }
        if (showSubtitle) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = colors.textSecondary)
            }
        } else {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
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
private fun OnboardingCard(vm: MainViewModel) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val canAdd = vm.canCreateTask()
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
                Text(if (canAdd) t("Create habit") else t("Get Premium"))
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
    var reminderHour by rememberSaveable {
        mutableStateOf(DEFAULT_REMINDER_HOUR)
    }
    var reminderMinute by rememberSaveable {
        mutableStateOf(DEFAULT_REMINDER_MINUTE)
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
            label = if (canAddTask) t("New") else t("Premium"),
            icon = if (canAddTask) Icons.Rounded.AddCircle else Icons.Rounded.WorkspacePremium,
            onClick = onCreate,
            emphasis = ActionEmphasis.PRIMARY
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Edit"),
            icon = Icons.Rounded.Edit,
            onClick = onEdit,
            enabled = canEditDelete,
            emphasis = ActionEmphasis.SECONDARY
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = t("Delete"),
            icon = Icons.Rounded.Delete,
            onClick = onDelete,
            enabled = canEditDelete,
            emphasis = ActionEmphasis.DANGER
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
    emphasis: ActionEmphasis = ActionEmphasis.SECONDARY
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val container = when {
        !enabled -> semantic.backgroundSurfaceMuted
        emphasis == ActionEmphasis.PRIMARY -> semantic.primary
        emphasis == ActionEmphasis.SECONDARY -> semantic.primaryMuted.copy(alpha = 0.9f)
        emphasis == ActionEmphasis.DANGER -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.78f)
        else -> semantic.backgroundSurfaceMuted
    }
    val content = when {
        !enabled -> semantic.textTertiary
        emphasis == ActionEmphasis.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        emphasis == ActionEmphasis.SECONDARY -> semantic.primary
        emphasis == ActionEmphasis.DANGER -> MaterialTheme.colorScheme.onErrorContainer
        else -> semantic.textSecondary
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
    val stroke = AppTheme.stroke
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
                            border = BorderStroke(stroke.thin, semantic.borderSubtle.copy(alpha = 0.55f)),
                            tonalElevation = 3.dp,
                            shadowElevation = 4.dp
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

private const val SELECTOR_HINT_PREF_KEY = "pref_selector_hint_shown"

@Composable
private fun formatHeroDate(date: LocalDate, locale: Locale): String {
    val today = LocalDate.now()
    val formattedDate = date.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    )
    return when (date) {
        today -> "${t("Today")}, $formattedDate"
        today.minusDays(1) -> "${t("Yesterday")}, $formattedDate"
        else -> {
            val dayLabel = date.dayOfWeek
                .getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            "$dayLabel, $formattedDate"
        }
    }
}

private fun activeHabitsCountLabel(count: Int, language: AppLanguage): String {
    if (count <= 0) return translate(language, "No habits")
    return when (language) {
        AppLanguage.RU, AppLanguage.UK -> {
            val mod10 = count % 10
            val mod100 = count % 100
            val key = when {
                mod10 == 1 && mod100 != 11 -> "%d habit"
                mod10 in 2..4 && mod100 !in 12..14 -> "%d habits few"
                else -> "%d habits"
            }
            formatTranslate(language, key, count)
        }
        AppLanguage.CS -> {
            val key = when (count) {
                1 -> "%d habit"
                2, 3, 4 -> "%d habits few"
                else -> "%d habits"
            }
            formatTranslate(language, key, count)
        }
        else -> {
            val key = if (count == 1) "%d habit" else "%d habits"
            formatTranslate(language, key, count)
        }
    }
}

@Composable
fun HabitSelectorRow(
    habits: List<HabitTask>,
    selectedId: String?,
    onHabitSelected: (String) -> Unit,
    onCreateHabit: (() -> Unit)? = null,
    showAllHabitsOption: Boolean = false,
    onSelectAll: (() -> Unit)? = null,
    showCountLabel: Boolean = true,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
    }
    var showHint by rememberSaveable {
        mutableStateOf(!prefs.getBoolean(SELECTOR_HINT_PREF_KEY, false))
    }
    val canScrollRight by remember {
        derivedStateOf { listState.canScrollForward }
    }
    val selectorItemCount = habits.size +
        (if (onCreateHabit != null) 1 else 0) +
        (if (showAllHabitsOption) 1 else 0)
    val shouldShowHint = showHint && canScrollRight && selectorItemCount > 1
    val fadeAlpha by animateFloatAsState(
        targetValue = if (canScrollRight) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "habitSelectorFadeAlpha"
    )

    LaunchedEffect(
        listState.firstVisibleItemIndex,
        listState.firstVisibleItemScrollOffset,
        showHint
    ) {
        val didScroll = listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        if (showHint && didScroll) {
            showHint = false
            prefs.edit().putBoolean(SELECTOR_HINT_PREF_KEY, true).apply()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
    ) {
        if (showCountLabel) {
            Text(
                text = activeHabitsCountLabel(habits.size, language),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = spacing.x2, end = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                onCreateHabit?.let { onAdd ->
                    item {
                        AddHabitTile(onClick = onAdd)
                    }
                }
                if (showAllHabitsOption) {
                    item {
                        AllHabitsPill(
                            isSelected = selectedId == null,
                            onClick = { onSelectAll?.invoke() }
                        )
                    }
                }
                items(habits, key = { it.id }) { habit ->
                    HabitPill(
                        habit = habit,
                        isSelected = habit.id == selectedId,
                        onClick = { onHabitSelected(habit.id) }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(36.dp)
                    .graphicsLayer { alpha = fadeAlpha }
            ) {
                if (fadeAlpha > 0f) {
                    FadeOverlay()
                }
            }
        }
        AnimatedVisibility(
            visible = shouldShowHint,
            exit = fadeOut(animationSpec = tween(400))
        ) {
            Text(
                text = t("← → swipe to switch habits"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(start = spacing.x2, top = 4.dp, bottom = 2.dp)
            )
        }
    }
}

@Composable
private fun HabitPill(
    habit: HabitTask,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val selectedColor = parseColorHex(habit.colorHex)
    val unselectedBackgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val backgroundColor = if (isSelected) selectedColor else unselectedBackgroundColor
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = if (isDarkTheme) 0.36f else 0.55f)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = habit.emoji.ifBlank { "✨" },
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = habit.title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddHabitTile(onClick: () -> Unit) {
    val semantic = AppTheme.colors
    val radius = RoundedCornerShape(12.dp)
    val isDarkPalette = semantic.backgroundCanvas.red < 0.2f
    val tintBackground = if (isDarkPalette) {
        semantic.primary.copy(alpha = 0.18f)
    } else {
        semantic.primary.copy(alpha = 0.10f)
    }
    val outline = if (isDarkPalette) {
        semantic.primary.copy(alpha = 0.95f)
    } else {
        semantic.primary.copy(alpha = 0.8f)
    }
    val strokeWidthPx = with(LocalDensity.current) { 1.5.dp.toPx() }
    val dashPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(radius)
            .background(tintBackground)
            .clickable(onClick = onClick)
            .drawBehind {
                val halfStroke = strokeWidthPx / 2f
                drawRoundRect(
                    color = outline,
                    topLeft = Offset(halfStroke, halfStroke),
                    size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                    cornerRadius = CornerRadius(
                        12.dp.toPx() - halfStroke,
                        12.dp.toPx() - halfStroke
                    ),
                    style = Stroke(width = strokeWidthPx, pathEffect = dashPathEffect)
                )
            }
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = t("Create habit"),
            tint = semantic.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun FadeOverlay() {
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .width(20.dp)
            .fillMaxHeight()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        backgroundColor.copy(alpha = if (isDark) 1f else 0.6f)
                    )
                )
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroCard(
    task: HabitTask?,
    selectedDate: LocalDate,
    done: Boolean,
    scheduled: Boolean,
    nextScheduledDate: LocalDate?,
    selectedValue: Int,
    selectedTarget: Int,
    selectedUnit: String,
    streak: Int,
    bestStreak: Int,
    weeklyRingProgress: Float,
    weeklyRingCompleted: Int,
    weeklyRingScheduled: Int,
    last7Days: List<Int>,
    last7DaysScheduled: List<Boolean>,
    last7DaysManualOverride: List<Boolean>,
    onDone: () -> Unit,
    onMarkAnyway: () -> Unit,
    onSetValue: (Int) -> Unit,
    onIncrementValue: (Int) -> Unit,
    onNavigateToDetail: () -> Unit,
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
    val locale = appLocale()
    val trackingType = task?.trackingType ?: TrackingType.YES_NO
    val isValueTracking = trackingType != TrackingType.YES_NO
    val isDurationTracking = trackingType == TrackingType.DURATION
    val isCountTracking = trackingType == TrackingType.COUNT
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
    val useDarkPalette = useDarkCompletedLottie
    val swipeThresholdPx = with(density) { 56.dp.toPx() }
    var horizontalDragDistance by remember(task?.id, selectedDate) { mutableStateOf(0f) }
    val completedButtonLottieResId = remember { R.raw.completed_button_lottie }
    val completedButtonComposition by if (completedButtonLottieResId != 0) {
        rememberLottieComposition(LottieCompositionSpec.RawRes(completedButtonLottieResId))
    } else {
        remember { mutableStateOf(null) }
    }
    val completedCheckmarkFilter = remember(useDarkCompletedLottie, semantic.lottieCheckmarkTint) {
        if (!useDarkCompletedLottie) {
            null
        } else {
            PorterDuffColorFilter(semantic.lottieCheckmarkTint.toArgb(), PorterDuff.Mode.SRC_ATOP)
        }
    }
    val completedButtonDynamicProperties = if (completedCheckmarkFilter != null) {
        rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = completedCheckmarkFilter,
                keyPath = arrayOf("line1", "Group 4", "Stroke 1")
            ),
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = completedCheckmarkFilter,
                keyPath = arrayOf("line2", "Group 3", "Stroke 1")
            )
        )
    } else {
        null
    }
    val completedTargetFrame = 22f
    val completedTargetProgress = remember(completedButtonComposition) {
        val durationFrames = completedButtonComposition?.durationFrames ?: 1f
        (completedTargetFrame / durationFrames.coerceAtLeast(1f)).coerceIn(0f, 1f)
    }
    val completionPulseScale = remember(task?.id, selectedDate) { Animatable(1f) }
    var completedAnimationPlaying by remember(task?.id, selectedDate) { mutableStateOf(false) }
    var completedLottieReady by remember(task?.id, selectedDate) { mutableStateOf(done) }
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
        }
        if (!done) {
            completedAnimationPlaying = false
            completionPulseScale.snapTo(1f)
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
    val durationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var durationSheetMode by remember(task?.id, selectedDate) { mutableStateOf<DurationSheetMode?>(null) }
    var manualMinutesInput by remember(task?.id, selectedDate) { mutableStateOf(selectedValue.coerceAtLeast(0).toString()) }
    var timerUiState by remember(task?.id, selectedDate) { mutableStateOf(TimerUiState.IDLE) }
    var timerElapsedSeconds by remember(task?.id, selectedDate) { mutableStateOf(0) }
    var pendingTimerAddMinutes by remember(task?.id, selectedDate) { mutableStateOf<Int?>(null) }
    var showValueNumpad by rememberSaveable(task?.id, selectedDate) { mutableStateOf(false) }
    var valueNumpadInput by rememberSaveable(task?.id, selectedDate) { mutableStateOf("") }
    val unitLabel = when {
        trackingType == TrackingType.DURATION -> t("min")
        trackingType == TrackingType.COUNT && selectedUnit.isNotBlank() -> selectedUnit
        trackingType == TrackingType.COUNT -> t("times")
        else -> ""
    }
    val timerLabel = remember(timerElapsedSeconds) {
        val totalSeconds = timerElapsedSeconds.coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }
    val addMinutesQuestionTemplate = t("Add %d minutes?")
    val progressLabel = when (trackingType) {
        TrackingType.YES_NO -> ""
        TrackingType.COUNT -> "$selectedValue / $selectedTarget $unitLabel"
        TrackingType.DURATION -> "$selectedValue / $selectedTarget ${t("min")}"
    }
    val rawProgress = (selectedValue.toFloat() / selectedTarget.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "heroLinearProgress"
    )
    val displayPercent = (rawProgress * 100f).roundToInt()
    val remaining = selectedTarget - selectedValue
    val statusUnit = when (trackingType) {
        TrackingType.DURATION -> t("min")
        TrackingType.COUNT -> selectedUnit.ifBlank { t("times") }
        TrackingType.YES_NO -> ""
    }
    val goalStatusText = when {
        done && remaining >= 0 -> t("Goal reached! 🎉")
        remaining < 0 -> tf("+%d %s beyond goal", -remaining, statusUnit)
        else -> tf("%d %s to go", remaining, statusUnit)
    }
    val goalStatusColor = when {
        done -> MaterialTheme.colorScheme.primary
        remaining < 0 -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    }
    val habitColor = parseColorHex(task?.colorHex.orEmpty())
    val ringProgress = weeklyRingProgress.coerceIn(0f, 1f)
    val ringPercent = (ringProgress * 100f).roundToInt()
    val ringArcColor = when {
        weeklyRingScheduled > 0 && weeklyRingCompleted >= weeklyRingScheduled -> semantic.success
        trackingType == TrackingType.YES_NO -> semantic.primary
        else -> habitColor
    }
    val ringTrackColor = MaterialTheme.colorScheme.surfaceVariant
    val ringCenterLabel = "${ringPercent}%"
    val streakMetaText = when {
        !canMarkForSelectedDate -> {
            val nextLabel = nextScheduledDate?.format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            )
            if (nextLabel != null) {
                "😴 ${t("Rest day")} · ${tf("Next scheduled date: %s", nextLabel)}"
            } else {
                "😴 ${t("Rest day")}"
            }
        }
        streak <= 0 -> t("Start today 🌱")
        else -> tf("🔥 %dd streak · ⭐ %dd best · %d%% week", streak, bestStreak, ringPercent)
    }
    LaunchedEffect(durationSheetMode, timerUiState, task?.id, selectedDate) {
        while (durationSheetMode == DurationSheetMode.TIMER && timerUiState == TimerUiState.RUNNING) {
            delay(1000)
            timerElapsedSeconds += 1
        }
    }
    LaunchedEffect(showValueNumpad) {
        if (showValueNumpad) valueNumpadInput = ""
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = semantic.backgroundSurface,
        border = BorderStroke(stroke.thin, semantic.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
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
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = formatHeroDate(selectedDate, locale),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = semantic.textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.x0_5)
                ) {
                    Text(
                        text = "${task?.emoji?.ifBlank { "✨" } ?: "✨"}  ${task?.title ?: t("No active habit")}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = semantic.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = streakMetaText,
                        style = MaterialTheme.typography.labelSmall,
                        color = semantic.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                ProgressRing(
                    percent = ringProgress,
                    centerLabel = ringCenterLabel,
                    centerLabelColor = ringArcColor,
                    color = ringArcColor,
                    trackColor = ringTrackColor,
                    modifier = Modifier.offset(x = (-4).dp)
                )
            }
            Spacer(Modifier.height(spacing.x0_5))

            HeroMiniWeekRow(
                points = last7Days,
                scheduled = last7DaysScheduled,
                manualOverride = last7DaysManualOverride,
                trackingType = trackingType,
                anchorDate = LocalDate.now(),
                todayShortLabel = t("Today short")
            )

            if (!canMarkForSelectedDate) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    Text(
                        text = t("This habit is not scheduled for this date."),
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                    if (!isValueTracking && done) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressed by interactionSource.collectIsPressedAsState()
                        val pressScale by animateFloatAsState(
                            targetValue = if (pressed) pressScaleTarget else 1f,
                            animationSpec = tween(durationMillis = pressScaleDuration, easing = FastOutSlowInEasing),
                            label = "restDayCompleteButtonPressScale"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val showCompletedLottie = completedButtonComposition != null &&
                                (completedAnimationPlaying || completedLottieReady)
                            val completedLottieSize = (spacing.x2 + spacing.x1) * 1.5f
                            val completedLottieSlotWidth by animateDpAsState(
                                targetValue = if (showCompletedLottie) completedLottieSize else 0.dp,
                                animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                                label = "restDayCompletedLottieSlotWidth"
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
                                border = if (useDarkPalette) {
                                    BorderStroke(stroke.thin, semantic.borderSubtle)
                                } else {
                                    null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (useDarkPalette) semantic.successMuted else semantic.success,
                                    contentColor = if (useDarkPalette) semantic.success else Color.White
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
                                                dynamicProperties = completedButtonDynamicProperties,
                                                modifier = Modifier.size(completedLottieSize)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onMarkAnyway,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(radius.full)
                        ) {
                            Text(t("Mark anyway"))
                        }
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
                        .padding(top = 10.dp, bottom = 0.dp),
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
                        border = if (useDarkPalette) {
                            BorderStroke(stroke.thin, semantic.borderSubtle)
                        } else {
                            null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (useDarkPalette) semantic.successMuted else semantic.success,
                            contentColor = if (useDarkPalette) semantic.success else Color.White
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
                                        dynamicProperties = completedButtonDynamicProperties,
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
                        .padding(top = 10.dp, bottom = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
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
                                highlightActive -> semantic.success
                                useDarkPalette -> semantic.borderSubtle
                                else -> Color.Transparent
                            }
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (useDarkPalette) semantic.primaryMuted else semantic.primary,
                            contentColor = if (useDarkPalette) semantic.primary else Color.White,
                            disabledContainerColor = semantic.backgroundSurfaceMuted,
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
                        .padding(top = spacing.x1, bottom = 2.dp),
                    contentPadding = PaddingValues(spacing.x1_5)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.x1),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (isDurationTracking) {
                                    AnimatedContent(
                                        targetState = selectedValue,
                                        transitionSpec = {
                                            (fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)) +
                                                slideInVertically(
                                                    initialOffsetY = { it / 3 },
                                                    animationSpec = tween(170, easing = FastOutSlowInEasing)
                                                )) togetherWith
                                                (fadeOut(animationSpec = tween(120, easing = FastOutSlowInEasing)) +
                                                    slideOutVertically(
                                                        targetOffsetY = { -it / 4 },
                                                        animationSpec = tween(120, easing = FastOutSlowInEasing)
                                                    ))
                                        },
                                        label = "durationProgressValue"
                                    ) { animatedValue ->
                                        Text(
                                            text = "$animatedValue / $selectedTarget ${t("min")}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = semantic.textPrimary
                                        )
                                    }
                                } else {
                                    Text(
                                        text = progressLabel,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = semantic.textPrimary
                                    )
                                }
                            }
                            if (selectedValue > 0) {
                                EditValueButton(onClick = { showValueNumpad = true })
                            }
                        }

                        AnimatedVisibility(
                            visible = !showValueNumpad,
                            enter = expandVertically(animationSpec = tween(190, easing = FastOutSlowInEasing)) +
                                fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)),
                            exit = shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                                fadeOut(animationSpec = tween(130, easing = FastOutSlowInEasing))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.x1)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = habitColor,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = "$displayPercent%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.widthIn(min = 32.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                                Text(
                                    text = goalStatusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = goalStatusColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
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
                                        listOf(5, 10, 20).forEach { delta ->
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
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                showValueNumpad = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.md)
                                        ) {
                                            Text(t("Enter manually"))
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                timerElapsedSeconds = 0
                                                timerUiState = TimerUiState.IDLE
                                                durationSheetMode = DurationSheetMode.TIMER
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(radius.md)
                                        ) {
                                            Text(t("Timer"))
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = showValueNumpad,
                            enter = expandVertically(animationSpec = tween(190, easing = FastOutSlowInEasing)) +
                                fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)),
                            exit = shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                                fadeOut(animationSpec = tween(130, easing = FastOutSlowInEasing))
                        ) {
                            ValueNumpad(
                                input = valueNumpadInput,
                                unitLabel = unitLabel,
                                onInputChange = { updated -> valueNumpadInput = updated },
                                onBackspace = {
                                    if (valueNumpadInput.isNotEmpty()) {
                                        valueNumpadInput = valueNumpadInput.dropLast(1)
                                    }
                                },
                                onSave = {
                                    val newValue = valueNumpadInput.toIntOrNull() ?: return@ValueNumpad
                                    onSetValue(newValue.coerceAtLeast(0))
                                    showValueNumpad = false
                                },
                                onDismiss = { showValueNumpad = false }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                HeroDetailsButton(onClick = onNavigateToDetail)
            }
        }
    }

    if (durationSheetMode == DurationSheetMode.MANUAL && isDurationTracking) {
        val manualParsed = manualMinutesInput.toIntOrNull()
        val manualValid = manualParsed != null && manualParsed >= 0
        ModalBottomSheet(
            onDismissRequest = { durationSheetMode = null },
            sheetState = durationSheetState,
            containerColor = semantic.backgroundSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("Enter minutes manually"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = semantic.textPrimary
                )
                OutlinedTextField(
                    value = manualMinutesInput,
                    onValueChange = { value ->
                        manualMinutesInput = value.filter { it.isDigit() }.take(4)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(t("Manual minutes")) },
                    placeholder = { Text("20") }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    OutlinedButton(
                        onClick = { durationSheetMode = null },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md)
                    ) {
                        Text(t("Cancel"))
                    }
                    Button(
                        onClick = {
                            val value = manualParsed ?: return@Button
                            onSetValue(value.coerceAtLeast(0))
                            durationSheetMode = null
                        },
                        enabled = manualValid,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(radius.md)
                    ) {
                        Text(t("Save changes"))
                    }
                }
            }
        }
    }

    if (durationSheetMode == DurationSheetMode.TIMER && isDurationTracking) {
        ModalBottomSheet(
            onDismissRequest = { durationSheetMode = null },
            sheetState = durationSheetState,
            containerColor = semantic.backgroundSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("Timer"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = semantic.textPrimary
                )
                Text(
                    text = timerLabel,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = semantic.textPrimary
                )

                when (timerUiState) {
                    TimerUiState.IDLE -> {
                        Button(
                            onClick = { timerUiState = TimerUiState.RUNNING },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(radius.md)
                        ) {
                            Text(t("Start"))
                        }
                    }
                    TimerUiState.RUNNING -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            OutlinedButton(
                                onClick = { timerUiState = TimerUiState.PAUSED },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Pause"))
                            }
                            OutlinedButton(
                                onClick = {
                                    timerUiState = TimerUiState.PAUSED
                                    if (timerElapsedSeconds > 0) {
                                        val minutesToAdd = ceil(timerElapsedSeconds / 60.0).toInt().coerceAtLeast(1)
                                        pendingTimerAddMinutes = minutesToAdd
                                    } else {
                                        durationSheetMode = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Stop"))
                            }
                        }
                    }
                    TimerUiState.PAUSED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                        ) {
                            OutlinedButton(
                                onClick = { timerUiState = TimerUiState.RUNNING },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Resume"))
                            }
                            OutlinedButton(
                                onClick = {
                                    if (timerElapsedSeconds > 0) {
                                        val minutesToAdd = ceil(timerElapsedSeconds / 60.0).toInt().coerceAtLeast(1)
                                        pendingTimerAddMinutes = minutesToAdd
                                    } else {
                                        durationSheetMode = null
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(radius.md)
                            ) {
                                Text(t("Stop"))
                            }
                        }
                    }
                }
            }
        }
    }

    pendingTimerAddMinutes?.let { minutes ->
        AlertDialog(
            onDismissRequest = { pendingTimerAddMinutes = null },
            title = { Text(t("Timer")) },
            text = { Text(tf(addMinutesQuestionTemplate, minutes)) },
            confirmButton = {
                Button(
                    onClick = {
                        onIncrementValue(minutes)
                        pendingTimerAddMinutes = null
                        timerUiState = TimerUiState.IDLE
                        timerElapsedSeconds = 0
                        durationSheetMode = null
                    }
                ) {
                    Text(t("Add"))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingTimerAddMinutes = null }) {
                    Text(t("Cancel"))
                }
            }
        )
    }
}

@Composable
private fun ProgressRing(
    percent: Float,
    centerLabel: String,
    centerLabelColor: Color,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 80.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 7.dp
) {
    val easeOutCubic = remember { androidx.compose.animation.core.CubicBezierEasing(0.33f, 1f, 0.68f, 1f) }
    val animatedPercent by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = easeOutCubic),
        label = "heroProgressRing"
    )
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = trackColor,
            strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = { animatedPercent },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = strokeWidth
        )
        Text(
            text = centerLabel,
            fontSize = if (centerLabel.contains("%")) 13.sp else 18.sp,
            fontWeight = if (centerLabel.contains("%")) FontWeight.Bold else FontWeight.Normal,
            color = centerLabelColor
        )
    }
}

@Composable
private fun AllHabitsPill(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) AppTheme.colors.primary else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = t("All habits"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeroMiniWeekRow(
    points: List<Int>,
    scheduled: List<Boolean>,
    manualOverride: List<Boolean>,
    trackingType: TrackingType,
    anchorDate: LocalDate,
    todayShortLabel: String
) {
    val semantic = AppTheme.colors
    val locale = appLocale()
    val isDark = isSystemInDarkTheme()
    val lightMissedFill = Color(0xFFC0392B).copy(alpha = 0.08f)
    val lightMissedBorder = Color(0xFFC0392B).copy(alpha = 0.35f)
    val normalizedPoints = points.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { 0 } + it }
    val normalizedScheduled = scheduled.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { false } + it }
    val normalizedManualOverride = manualOverride.takeLast(7).let {
        if (it.size == 7) it else List(7 - it.size) { false } + it
    }
    val dates = (6L downTo 0L).map { offset -> anchorDate.minusDays(offset) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        for (index in dates.indices) {
            val date = dates[index]
            val value = normalizedPoints.getOrElse(index) { 0 }.coerceIn(0, 100)
            val isScheduled = normalizedScheduled.getOrElse(index) { false }
            val isManualOverride = normalizedManualOverride.getOrElse(index) { false }
            val isToday = date == LocalDate.now()
            val isFuture = date.isAfter(LocalDate.now())
            val isCompleted = when (trackingType) {
                TrackingType.YES_NO -> value >= 100
                TrackingType.COUNT, TrackingType.DURATION -> value >= 100
            }
            val isPartial = !isCompleted && value > 0 && isScheduled && !isFuture
            val isMissed = isScheduled && !isFuture && !isToday && !isCompleted && !isPartial
            val dayColor = when {
                isFuture -> MaterialTheme.colorScheme.surfaceVariant
                !isScheduled && isManualOverride -> semantic.success.copy(alpha = 0.55f)
                !isScheduled -> MaterialTheme.colorScheme.surfaceVariant
                isCompleted -> semantic.success
                isPartial -> semantic.success.copy(alpha = 0.45f)
                isToday -> Color.Transparent
                isMissed && !isDark -> lightMissedFill
                else -> semantic.chartMissed
            }
            val missedBorderColor = if (isMissed && !isDark) lightMissedBorder else null
            val showTodayBorder = isToday && isScheduled && !isFuture && !isCompleted && !isPartial
            val dayLabel = if (isToday) {
                todayShortLabel
            } else {
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            }
            DayDot(
                modifier = Modifier.weight(1f),
                label = dayLabel,
                fillColor = dayColor,
                isToday = isToday,
                showTodayBorder = showTodayBorder,
                todayBorderColor = semantic.primary,
                customBorderColor = missedBorderColor
            )
        }
    }
}

@Composable
private fun DayDot(
    modifier: Modifier = Modifier,
    label: String,
    fillColor: Color,
    isToday: Boolean,
    showTodayBorder: Boolean,
    todayBorderColor: Color,
    customBorderColor: Color? = null
) {
    val semantic = AppTheme.colors
    val borderModifier = when {
        showTodayBorder -> Modifier.border(1.dp, todayBorderColor, RoundedCornerShape(6.dp))
        customBorderColor != null -> Modifier.border(1.5.dp, customBorderColor, RoundedCornerShape(6.dp))
        else -> Modifier
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(fillColor)
                .then(borderModifier)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday) todayBorderColor else semantic.textTertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}
@Composable
private fun HeroDetailsButton(onClick: () -> Unit) {
    val semantic = AppTheme.colors
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Text(
            text = t("More details →"),
            style = MaterialTheme.typography.labelMedium,
            color = semantic.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EditValueButton(onClick: () -> Unit) {
    val colors = AppTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = colors.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .background(colors.primaryMuted.copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = t("edit"),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = colors.primary
        )
    }
}

@Composable
private fun ValueNumpad(
    input: String,
    unitLabel: String,
    onInputChange: (String) -> Unit,
    onBackspace: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = AppTheme.spacing
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
    val keySize = 52.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(
                text = if (input.isEmpty()) "—" else input,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (input.isEmpty()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = unitLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        keys.chunked(3).forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowKeys.forEach { key ->
                    when (key) {
                        "" -> Spacer(Modifier.size(keySize))
                        "⌫" -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(keySize)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable(onClick = onBackspace)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                                    contentDescription = t("Backspace"),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        else -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(keySize)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        if (input.length < 4) {
                                            onInputChange(input + key)
                                        }
                                    }
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Text(t("Cancel"))
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(2f),
                enabled = input.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White,
                    disabledContainerColor = AppTheme.colors.primaryMuted,
                    disabledContentColor = AppTheme.colors.primary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = t("Save"),
                    fontWeight = FontWeight.Medium
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
private fun StreakMilestoneScreen(
    habitId: String,
    days: Int,
    onDismiss: () -> Unit,
    forceShow: Boolean = false
) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE) }
    val shownKey = remember(habitId, days) { "milestone_shown_${habitId}_${days}" }
    val alreadyShown = remember(habitId, days, forceShow) {
        if (forceShow || habitId.isBlank()) false else prefs.getBoolean(shownKey, false)
    }
    if (alreadyShown) {
        LaunchedEffect(shownKey) { onDismiss() }
        return
    }
    val dismissAndTrack: () -> Unit = {
        if (!forceShow && habitId.isNotBlank()) {
            prefs.edit().putBoolean(shownKey, true).apply()
        }
        onDismiss()
    }

    val tier = streakMilestoneTier(days) ?: return
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.streak_milestone_lottie)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = 1,
        isPlaying = true,
        speed = 1f
    )
    val flameFilter = remember(tier.accentColor) {
        PorterDuffColorFilter(tier.accentColor.toArgb(), PorterDuff.Mode.SRC_ATOP)
    }
    val lottieDynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = flameFilter,
            keyPath = arrayOf("**")
        )
    )

    BackHandler(onBack = dismissAndTrack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tier.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (lottieComposition != null) {
                    LottieAnimation(
                        composition = lottieComposition,
                        progress = { lottieProgress.coerceIn(0f, 1f) },
                        dynamicProperties = lottieDynamicProperties,
                        modifier = Modifier.size(140.dp)
                    )
                } else {
                    Text(
                        text = "🔥",
                        fontSize = 44.sp,
                        color = tier.accentColor
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tier.badgeLabel,
                    color = tier.accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.Black.copy(alpha = 0.22f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Text(
                    text = tier.days.toString(),
                    fontSize = when {
                        tier.days >= 1000 -> 48.sp
                        tier.days >= 100 -> 52.sp
                        else -> 64.sp
                    },
                    fontWeight = FontWeight.ExtraBold,
                    color = tier.accentColor
                )
                Text(
                    text = streakDaysUnit(tier.days),
                    fontSize = 15.sp,
                    color = tier.accentColor.copy(alpha = 0.65f)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tier.headline,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = tier.message,
                    fontSize = 13.sp,
                    color = tier.accentColor.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (tier.nextMilestoneDays != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t("milestone_next_goal"),
                            fontSize = 11.sp,
                            color = tier.accentColor.copy(alpha = 0.65f)
                        )
                        Text(
                            text = "${tier.nextMilestoneIcon} ${tier.nextMilestoneDays} ${streakDaysUnit(tier.nextMilestoneDays)}",
                            fontSize = 11.sp,
                            color = tier.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (tier.days.toFloat() / tier.nextMilestoneDays.toFloat()).coerceIn(0f, 1f) },
                        color = tier.accentColor,
                        trackColor = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            } else {
                Text(
                    text = t("milestone_top_percent"),
                    fontSize = 12.sp,
                    color = tier.accentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = dismissAndTrack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tier.accentColor,
                    contentColor = Color(0xFF051E16)
                )
            ) {
                Text(
                    text = tier.ctaLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun isStreakMilestone(streak: Int): Boolean {
    return streak in StreakMilestoneQueue.milestoneSet()
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
        colors = CardDefaults.cardColors(containerColor = semantic.backgroundSurfaceMuted.copy(alpha = 0.82f)),
        shape = RoundedCornerShape(radius.md),
        border = BorderStroke(AppTheme.stroke.thin, semantic.borderSubtle.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.none)
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
        CalendarDayState.PARTIAL -> semantic.success.copy(alpha = 0.28f)
        CalendarDayState.MISSED -> semantic.danger.copy(alpha = 0.3f)
        CalendarDayState.NOT_SCHEDULED -> semantic.borderSubtle.copy(alpha = 0.7f)
        CalendarDayState.FUTURE -> semantic.borderSubtle.copy(alpha = 0.35f)
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
                border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.65f)),
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
private fun SelectChip(title: String, selected: Boolean, onClick: () -> Unit) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(radius.full))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(radius.full),
        color = if (selected) semantic.primary.copy(alpha = 0.12f) else semantic.backgroundSurfaceMuted.copy(alpha = 0.82f),
        border = BorderStroke(
            stroke.thin,
            if (selected) semantic.primary.copy(alpha = 0.65f) else semantic.borderSubtle.copy(alpha = 0.65f)
        )
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
            color = if (selected) semantic.primary else semantic.textSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitCategoryScreen(
    onCategorySelected: (TemplateCategory) -> Unit,
    onCreateCustom: () -> Unit,
    onSkip: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val language = LocalAppLanguage.current
    val categories = remember {
        listOf(
            Triple(TemplateCategory.HEALTH, "💊", "cat_health"),
            Triple(TemplateCategory.SPORT, "🏃", "cat_sport"),
            Triple(TemplateCategory.MENTAL, "🧘", "cat_mental"),
            Triple(TemplateCategory.PRODUCTIVITY, "📚", "cat_productivity")
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = t("screen_new_habit"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = t("Close"))
                        }
                    },
                    actions = {
                        TextButton(onClick = onSkip) {
                            Text(t("label_skip"))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("label_what_to_improve"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = t("label_choose_category"),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                categories.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        row.forEach { (category, emoji, nameKey) ->
                            val templateCount = CreateHabitTemplateCatalog.templatesFor(category).size
                            CategoryTile(
                                emoji = emoji,
                                title = t(nameKey),
                                countLabel = templateCountLabel(templateCount, language),
                                modifier = Modifier.weight(1f),
                                onClick = { onCategorySelected(category) }
                            )
                        }
                        repeat((2 - row.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onCreateCustom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = t("btn_create_custom"),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                }
            }
        }
    }

}

@Composable
private fun CategoryTile(
    emoji: String,
    title: String,
    countLabel: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = colors.backgroundSurface,
        border = BorderStroke(1.dp, colors.borderSubtle),
        modifier = modifier.aspectRatio(1.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = countLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun templateCountLabel(count: Int, language: AppLanguage): String {
    if (count <= 0) return formatTranslate(language, "template_count_many", 0)
    return when (language) {
        AppLanguage.RU, AppLanguage.UK -> {
            val mod10 = count % 10
            val mod100 = count % 100
            val key = when {
                mod10 == 1 && mod100 != 11 -> "template_count_one"
                mod10 in 2..4 && mod100 !in 12..14 -> "template_count_few"
                else -> "template_count_many"
            }
            formatTranslate(language, key, count)
        }
        AppLanguage.CS -> {
            val key = when (count) {
                1 -> "template_count_one"
                2, 3, 4 -> "template_count_few"
                else -> "template_count_many"
            }
            formatTranslate(language, key, count)
        }
        else -> {
            val key = if (count == 1) "template_count_one" else "template_count_many"
            formatTranslate(language, key, count)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitTemplateScreen(
    category: TemplateCategory,
    onTemplateSelected: (CreateHabitTemplate) -> Unit,
    onCreateCustomHabit: () -> Unit,
    onBack: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val templates = remember(category) { CreateHabitTemplateCatalog.templatesFor(category) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = t(CreateHabitTemplateCatalog.categoryLabelKey(category)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = t("Back")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                items(templates, key = { it.id }) { template ->
                    Surface(
                        onClick = { onTemplateSelected(template) },
                        shape = RoundedCornerShape(AppTheme.radius.md),
                        color = colors.backgroundSurface,
                        border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = template.emoji.ifBlank { "✨" },
                                    fontSize = 20.sp
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = t(template.nameKey),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = templateMetaLabel(template),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textSecondary.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = onCreateCustomHabit,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppTheme.radius.md),
                        border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text(t("btn_create_custom"))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitTemplateConfirmScreen(
    initial: TemplateConfirmDraft,
    onBack: () -> Unit,
    onStateChange: (TemplateConfirmDraft) -> Unit,
    onCreateHabit: (TemplateConfirmDraft) -> Unit,
    onConfigureMore: (TemplateConfirmDraft) -> Unit,
    onPickStartDate: (LocalDate, (LocalDate) -> Unit) -> Unit,
    onRequestReminderTime: (Int, Int, (Int, Int) -> Unit) -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val locale = appLocale()
    val language = LocalAppLanguage.current
    val template = initial.template
    val habitName = remember(template.id, initial.habitName, language) {
        initial.habitName.ifBlank { translate(language, template.nameKey) }
    }
    val dailyTarget = remember(template.id, initial.dailyTarget) { initial.dailyTarget.coerceAtLeast(1) }
    var frequency by rememberSaveable(template.id) { mutableStateOf(initial.frequency) }
    var customDays by rememberSaveable(template.id) { mutableStateOf(initial.customDays) }
    var timesPerWeek by rememberSaveable(template.id) { mutableStateOf(initial.timesPerWeek.coerceIn(1, 7)) }
    var startDate by rememberSaveable(template.id) { mutableStateOf(initial.startDate) }
    var reminderEnabled by rememberSaveable(template.id) { mutableStateOf(initial.reminderEnabled) }
    var reminderHour by rememberSaveable(template.id) { mutableStateOf(initial.reminderHour.coerceIn(0, 23)) }
    var reminderMinute by rememberSaveable(template.id) { mutableStateOf(initial.reminderMinute.coerceIn(0, 59)) }
    var expandedParam by rememberSaveable(template.id) { mutableStateOf<ExpandedConfirmParam?>(null) }
    val currentDraft = remember(
        template,
        habitName,
        dailyTarget,
        frequency,
        customDays,
        timesPerWeek,
        startDate,
        reminderEnabled,
        reminderHour,
        reminderMinute
    ) {
        TemplateConfirmDraft(
            template = template,
            habitName = habitName.trim().ifBlank { translate(language, template.nameKey) },
            dailyTarget = dailyTarget.coerceAtLeast(1),
            frequency = frequency,
            customDays = if (frequency == TaskFrequency.SELECTED_DAYS) {
                customDays.ifEmpty { template.defaultDays.ifEmpty { setOf(1) } }
            } else {
                customDays
            },
            timesPerWeek = timesPerWeek.coerceIn(1, 7),
            startDate = startDate,
            reminderEnabled = reminderEnabled,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute
        )
    }

    LaunchedEffect(currentDraft) {
        onStateChange(currentDraft)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = t("Back")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
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
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.backgroundSurface,
                    border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(colors.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = template.emoji, fontSize = 22.sp)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = habitName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${t(CreateHabitTemplateCatalog.categoryLabelKey(template.category))} · ${templateTrackingTypeLabel(template.trackingType)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.backgroundSurfaceMuted.copy(alpha = 0.6f),
                            border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f))
                        ) {
                            Column {
                                val isFrequencyExpanded = expandedParam == ExpandedConfirmParam.FREQUENCY
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = t("label_frequency"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (!isFrequencyExpanded) {
                                            Text(
                                                text = templateFrequencyLabel(frequency),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = colors.primary
                                            )
                                        }
                                        Text(
                                            text = if (isFrequencyExpanded) t("action_done") else t("action_edit"),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colors.primary.copy(alpha = if (isFrequencyExpanded) 1f else 0.75f),
                                            fontWeight = if (isFrequencyExpanded) FontWeight.Medium else FontWeight.Normal,
                                            modifier = Modifier.clickable {
                                                expandedParam = if (isFrequencyExpanded) null else ExpandedConfirmParam.FREQUENCY
                                            }
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isFrequencyExpanded,
                                    enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
                                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TemplateFrequencyOption(
                                            label = t("freq_every_day"),
                                            selected = frequency == TaskFrequency.DAILY,
                                            onClick = { frequency = TaskFrequency.DAILY }
                                        )
                                        TemplateFrequencyOption(
                                            label = t("freq_selected_days"),
                                            selected = frequency == TaskFrequency.SELECTED_DAYS,
                                            description = t("freq_selected_days_desc"),
                                            onClick = {
                                                frequency = TaskFrequency.SELECTED_DAYS
                                                if (customDays.isEmpty()) {
                                                    customDays = template.defaultDays.ifEmpty { setOf(1, 2, 3, 4, 5) }
                                                }
                                            }
                                        )
                                        AnimatedVisibility(visible = frequency == TaskFrequency.SELECTED_DAYS) {
                                            WeekdaySelector(
                                                selectedDays = customDays,
                                                onToggle = { day ->
                                                    val next = customDays.toMutableSet()
                                                    if (!next.add(day)) next.remove(day)
                                                    customDays = next
                                                }
                                            )
                                        }
                                        TemplateFrequencyOption(
                                            label = t("freq_times_per_week"),
                                            selected = frequency == TaskFrequency.TIMES_PER_WEEK,
                                            description = t("freq_times_per_week_desc"),
                                            onClick = { frequency = TaskFrequency.TIMES_PER_WEEK }
                                        )
                                        AnimatedVisibility(visible = frequency == TaskFrequency.TIMES_PER_WEEK) {
                                            TimesPerWeekStepper(
                                                value = timesPerWeek,
                                                onValueChange = { timesPerWeek = it.coerceIn(1, 7) }
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                )
                                TemplateParamRow(
                                    label = t("label_start_date"),
                                    value = startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                                    action = t("action_edit"),
                                    onAction = {
                                        onPickStartDate(startDate) { picked ->
                                            startDate = picked
                                        }
                                    }
                                )
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                )
                                TemplateParamRow(
                                    label = t("label_reminder"),
                                    value = if (reminderEnabled) {
                                        formatTimeForDevice(LocalContext.current, reminderHour, reminderMinute)
                                    } else {
                                        t("label_reminder_off")
                                    },
                                    action = if (reminderEnabled) t("action_edit") else t("action_enable"),
                                    onAction = {
                                        onRequestReminderTime(reminderHour, reminderMinute) { hour, minute ->
                                            reminderEnabled = true
                                            reminderHour = hour
                                            reminderMinute = minute
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { onConfigureMore(currentDraft) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppTheme.radius.md),
                    border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Text(t("btn_configure_more"))
                }
                Button(
                    onClick = { onCreateHabit(currentDraft) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(AppTheme.radius.md)
                ) {
                    Text(
                        text = t("btn_create_habit"),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private enum class ExpandedConfirmParam {
    FREQUENCY
}

@Composable
private fun TimesPerWeekStepper(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Stepper(
        label = t("Times per week"),
        value = value.coerceIn(1, 7),
        min = 1,
        max = 7,
        onValueChange = { onValueChange(it.coerceIn(1, 7)) }
    )
}

@Composable
private fun TemplateFrequencyOption(
    label: String,
    selected: Boolean,
    description: String? = null,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppTheme.radius.md),
        color = if (selected) colors.primary.copy(alpha = 0.12f) else colors.backgroundSurfaceMuted,
        border = BorderStroke(
            AppTheme.stroke.thin,
            if (selected) colors.primary.copy(alpha = 0.5f) else colors.borderSubtle.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = colors.textPrimary
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun TemplateParamRow(
    label: String,
    value: String,
    action: String,
    onAction: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.width(spacing.x0_5))
        TextButton(
            onClick = onAction,
            contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = 0.dp)
        ) {
            Text(
                text = action,
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )
        }
    }
}

@Composable
private fun templateTrackingTypeLabel(type: TrackingType): String = when (type) {
    TrackingType.YES_NO -> t("tracking_type_yesno")
    TrackingType.COUNT -> t("tracking_type_count")
    TrackingType.DURATION -> t("tracking_type_duration")
}

@Composable
private fun templateMetaLabel(template: CreateHabitTemplate): String {
    val trackingLabel = when (template.trackingType) {
        TrackingType.YES_NO -> templateTrackingTypeLabel(template.trackingType)
        TrackingType.COUNT, TrackingType.DURATION -> {
            val unit = if (template.unitLabelKey.isBlank()) "" else t(template.unitLabelKey)
            "${template.dailyTarget} $unit".trim()
        }
    }
    val frequencyLabel = templateFrequencyMetaLabel(template)
    return "$trackingLabel · $frequencyLabel"
}

@Composable
private fun templateFrequencyLabel(frequency: TaskFrequency): String = when (frequency) {
    TaskFrequency.DAILY -> t("freq_every_day")
    TaskFrequency.SELECTED_DAYS -> t("freq_selected_days")
    TaskFrequency.TIMES_PER_WEEK -> t("freq_times_per_week")
}

@Composable
private fun templateFrequencyMetaLabel(template: CreateHabitTemplate): String = when (template.frequency) {
    TaskFrequency.DAILY -> t("freq_every_day")
    TaskFrequency.SELECTED_DAYS -> selectedDaysShortLabel(template.defaultDays)
    TaskFrequency.TIMES_PER_WEEK -> "${template.defaultTimesPerWeek} ${t("freq_times_short")}"
}

@Composable
private fun selectedDaysShortLabel(days: Set<Int>): String {
    val labels = weekdayLabels(LocalAppLanguage.current)
    val normalized = days.filter { it in 1..7 }.sorted()
    if (normalized.isEmpty()) return t("freq_selected_days")
    if (normalized.size >= 2) {
        val expectedRange = (normalized.first()..normalized.last()).toList()
        if (normalized == expectedRange) {
            val first = labels[normalized.first() - 1]
            val last = labels[normalized.last() - 1]
            return "$first–$last"
        }
    }
    return normalized.joinToString(" ") { day -> labels[day - 1] }
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
    val datePickerTheme = R.style.ThemeOverlay_MicroHabit_DatePicker
    val pickerActionColor = colors.primary.toArgb()
    var showEmojiPicker by rememberSaveable { mutableStateOf(false) }
    val trackingCards = listOf(
        Triple(TrackingType.YES_NO, t("tracking_type_yesno"), t("tracking_type_yesno_desc")),
        Triple(TrackingType.COUNT, t("tracking_type_count"), t("tracking_type_count_desc")),
        Triple(TrackingType.DURATION, t("tracking_type_duration"), t("tracking_type_duration_desc"))
    )
    val frequencyOptions = listOf(
        ChoiceOption(TaskFrequency.DAILY, t("freq_every_day")),
        ChoiceOption(TaskFrequency.SELECTED_DAYS, t("freq_selected_days")),
        ChoiceOption(TaskFrequency.TIMES_PER_WEEK, t("freq_times_per_week"))
    )
    val palette = listOf("#1F6F64", "#3B7EA1", "#7B6BC9", "#3E8E5F", "#B36A3C", "#C65C74", "#5D6D7E")
    val emojiSuggestions = listOf(
        "✨", "💧", "💊", "🥗", "🌿", "😴", "🏃", "🏋️", "🚴", "🤸",
        "👟", "🧘", "📵", "🙏", "📚", "✍️", "🎓", "💼", "🎯", "🔥",
        "☀️", "🌙", "💡", "📈", "🍎", "🧠", "🫶", "🎵", "🧹", "🧴"
    )

    if (showEmojiPicker) {
        ModalBottomSheet(
            onDismissRequest = { showEmojiPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                Text(
                    text = t("Choose emoji"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                emojiSuggestions.chunked(6).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        row.forEach { emoji ->
                            Surface(
                                onClick = {
                                    vm.setEditorEmoji(emoji)
                                    showEmojiPicker = false
                                },
                                shape = RoundedCornerShape(AppTheme.radius.md),
                                color = colors.backgroundSurfaceMuted,
                                border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.x1),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                        repeat((6 - row.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing.x1))
            }
        }
    }

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
                            if (state.editingTaskId == null) t("screen_create_habit") else t("Edit Habit"),
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(stroke.thin, colors.borderSubtle.copy(alpha = 0.7f), RoundedCornerShape(AppTheme.radius.md))
                                .padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { showEmojiPicker = true },
                                shape = RoundedCornerShape(8.dp),
                                color = colors.backgroundSurfaceMuted,
                                border = BorderStroke(stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .size(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.editorEmoji.ifBlank { "✨" },
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            BasicTextField(
                                value = state.editorTitle,
                                onValueChange = vm::setEditorTitle,
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                singleLine = true,
                                decorationBox = { inner ->
                                    if (state.editorTitle.isBlank()) {
                                        Text(
                                            text = t("Habit name"),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colors.textSecondary.copy(alpha = 0.5f)
                                        )
                                    }
                                    inner()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(AppTheme.radius.full))
                                    .background(selectedColor.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Spacer(modifier = Modifier.size(1.dp))
                            }
                        }
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            palette.forEach { hex ->
                                val color = parseColorHex(hex)
                                val selected = hex.equals(state.editorColorHex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(AppTheme.radius.full))
                                        .background(color)
                                        .border(
                                            width = if (selected) 2.dp else stroke.thin,
                                            color = if (selected) color else colors.borderSubtle.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(AppTheme.radius.full)
                                        )
                                        .clickable { vm.setEditorColorHex(hex) }
                                )
                            }
                        }
                    }
                }

                FormSection(title = t("Tracking type")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        trackingCards.forEach { (type, title, description) ->
                            val selected = state.editorTrackingType == type
                            val icon = when (type) {
                                TrackingType.YES_NO -> "✓"
                                TrackingType.COUNT -> "#"
                                TrackingType.DURATION -> "⏱"
                            }
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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = icon, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary
                                        )
                                    }
                                    if (!selected) {
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
                                    placeholder = {
                                        Text(
                                            if (state.editorDailyTarget <= 10) {
                                                t("unit_label_hint_small")
                                            } else {
                                                t("unit_label_hint_large")
                                            }
                                        )
                                    },
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

                FormSection(title = t("label_frequency")) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5)
                        ) {
                            items(frequencyOptions, key = { it.value }) { option ->
                                SelectChip(
                                    title = option.label,
                                    selected = option.value == state.editorFrequency,
                                    onClick = { vm.setEditorFrequency(option.value) }
                                )
                            }
                        }

                        when (state.editorFrequency) {
                            TaskFrequency.SELECTED_DAYS -> {
                                Text(
                                    text = t("freq_selected_days_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            TaskFrequency.TIMES_PER_WEEK -> {
                                Text(
                                    text = t("freq_times_per_week_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            TaskFrequency.DAILY -> Unit
                        }

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = t("label_start_date"),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = state.editorStartDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    TextButton(
                        onClick = {
                            showThemedDatePicker(
                                context = context,
                                themeResId = datePickerTheme,
                                initialDate = state.editorStartDate,
                                minDate = LocalDate.now(),
                                actionColorArgb = pickerActionColor,
                                onDateSet = { year, month, day ->
                                    vm.setEditorStartDate(LocalDate.of(year, month + 1, day))
                                }
                            )
                        },
                        contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0)
                    ) {
                        Text(t("Edit"))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.setEditorShowAdvanced(!state.editorShowAdvanced) }
                        .padding(vertical = spacing.x0_5),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.editorShowAdvanced) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.x0_5))
                    Text(
                        text = if (state.editorShowAdvanced) t("hide_advanced") else t("show_advanced"),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary
                    )
                }

                AnimatedVisibility(
                    visible = state.editorShowAdvanced,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.x1_5)) {
                        SettingsSwitchRow(
                            title = t("End date"),
                            subtitle = t("Optional challenge finish date"),
                            checked = state.editorEndDate != null,
                            onCheckedChange = { enabled ->
                                vm.setEditorEndDateEnabled(enabled)
                                if (enabled) {
                                    val minDate = maxOf(LocalDate.now(), state.editorStartDate)
                                    val initialDate = state.editorEndDate ?: maxOf(LocalDate.now().plusDays(30), state.editorStartDate)
                                    showThemedDatePicker(
                                        context = context,
                                        themeResId = datePickerTheme,
                                        initialDate = initialDate,
                                        minDate = minDate,
                                        actionColorArgb = pickerActionColor,
                                        onDateSet = { year, month, day ->
                                            vm.setEditorEndDate(LocalDate.of(year, month + 1, day))
                                        }
                                    )
                                }
                            }
                        )

                        AnimatedVisibility(visible = state.editorEndDate != null) {
                            OutlinedButton(
                                onClick = {
                                    val minDate = maxOf(LocalDate.now(), state.editorStartDate)
                                    showThemedDatePicker(
                                        context = context,
                                        themeResId = datePickerTheme,
                                        initialDate = state.editorEndDate ?: minDate,
                                        minDate = minDate,
                                        actionColorArgb = pickerActionColor,
                                        onDateSet = { year, month, day ->
                                            vm.setEditorEndDate(LocalDate.of(year, month + 1, day))
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
                                        "End date: %s",
                                        (state.editorEndDate ?: state.editorStartDate)
                                            .format(DateTimeFormatter.ofPattern(t("dd MMM yyyy"), locale))
                                    )
                                )
                            }
                        }

                        SettingsSwitchRow(
                            title = t("label_reminder"),
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
    minDate: LocalDate? = null,
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
        dialog.window?.let { window ->
            window.setLayout(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(android.view.Gravity.CENTER)
        }
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(actionColorArgb)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(actionColorArgb)
    }
    minDate?.let {
        val minMillis = it.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        dialog.datePicker.minDate = minMillis
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
    tone: SurfaceTone = SurfaceTone.PRIMARY,
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit
) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    val radius = AppTheme.radius
    val elevation = AppTheme.elevation
    val stroke = AppTheme.stroke
    val resolvedPadding = contentPadding ?: PaddingValues(spacing.x2)
    val containerColor = when (tone) {
        SurfaceTone.PRIMARY -> semantic.backgroundSurface
        SurfaceTone.SECONDARY -> semantic.backgroundSurfaceMuted.copy(alpha = 0.82f)
        SurfaceTone.TERTIARY -> semantic.backgroundCanvas
    }
    val borderColor = when (tone) {
        SurfaceTone.PRIMARY -> semantic.borderSubtle.copy(alpha = 0.5f)
        SurfaceTone.SECONDARY -> semantic.borderSubtle.copy(alpha = 0.4f)
        SurfaceTone.TERTIARY -> semantic.borderSubtle.copy(alpha = 0.3f)
    }
    val cardElevation = when (tone) {
        SurfaceTone.PRIMARY -> elevation.md
        SurfaceTone.SECONDARY -> elevation.sm
        SurfaceTone.TERTIARY -> elevation.none
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(radius.lg),
        border = BorderStroke(stroke.thin, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
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

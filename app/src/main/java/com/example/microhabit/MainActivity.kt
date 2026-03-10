package com.example.microhabit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.theme.MicroHabitTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.launch

private enum class AppPage(val title: String) {
    TRACKER("Трекер"),
    ACCOUNT("Аккаунт"),
    SETTINGS("Настройки")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel(
                factory = MainViewModel.Factory(HabitRepository(applicationContext))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitApp(state: HabitUiState, vm: MainViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var page by rememberSaveable { mutableStateOf(AppPage.TRACKER) }
    val semantic = AppTheme.colors

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                current = page,
                plan = state.plan,
                onNavigate = {
                    page = it
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(page.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
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
                        onUpgrade = { page = AppPage.ACCOUNT },
                        onOpenSettings = { page = AppPage.SETTINGS }
                    )
                    AppPage.ACCOUNT -> AccountPage(state = state, onSetPlan = vm::setPlan)
                    AppPage.SETTINGS -> SettingsPage(state = state, onSetTheme = vm::setThemeMode, onSetLanguage = vm::setLanguage)
                }
            }
        }

        if (state.showEditor && state.tasks.isNotEmpty()) {
            TaskEditorDialog(state = state, onDismiss = vm::closeEditor, vm = vm)
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
            Text("Micro-habit", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(
                if (plan == SubscriptionPlan.PRO) "План: PRO" else "План: Free",
                color = semantic.textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            NavigationDrawerItem(
                label = { Text("Трекер") },
                selected = current == AppPage.TRACKER,
                onClick = { onNavigate(AppPage.TRACKER) },
                icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text("Аккаунт") },
                selected = current == AppPage.ACCOUNT,
                onClick = { onNavigate(AppPage.ACCOUNT) },
                icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = colors.primary.copy(alpha = 0.15f))
            )
            NavigationDrawerItem(
                label = { Text("Настройки") },
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
    onUpgrade: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val selectedTask = state.tasks.firstOrNull { it.id == state.selectedTaskId }
    val canAdd = state.plan == SubscriptionPlan.PRO || state.tasks.size < 1
    val spacing = AppTheme.spacing

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x2)
    ) {
        if (state.tasks.isEmpty()) {
            item { OnboardingCard(vm, state) }
        } else {
            item {
                TrackerHeader(
                    habitTitle = selectedTask?.title.orEmpty(),
                    plan = state.plan,
                    onOpenSettings = onOpenSettings
                )
            }
            item {
                TaskControlsRow(
                    canAddTask = canAdd,
                    onCreate = { if (canAdd) vm.openCreateTask() else onUpgrade() },
                    onEdit = { selectedTask?.id?.let(vm::openEditTask) },
                    onDelete = { selectedTask?.id?.let(vm::deleteTask) },
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
                    progress = state.progressPercent,
                    total = state.doneDatesInCurrentMonth.size
                )
            }
            item { SevenDayChart(points = state.last7Days, anchorDate = state.selectedDate) }
            item {
                CalendarCard(
                    month = state.currentMonth,
                    selectedDate = state.selectedDate,
                    selectedTask = selectedTask,
                    doneDates = state.doneDatesInCurrentMonth,
                    onMoveMonth = vm::moveMonth,
                    onToday = vm::jumpToToday,
                    onDateSelect = vm::selectDate
                )
            }
        }
    }
}

@Composable
private fun AccountPage(state: HabitUiState, onSetPlan: (SubscriptionPlan) -> Unit) {
    val spacing = AppTheme.spacing
    val semantic = AppTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            GlassCard {
                Text("Планы", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(spacing.x1))
                Text("Выбирай план: Free или PRO (debug-переключение)", color = semantic.textSecondary)
            }
        }
        item {
            PlanCard(
                title = "Бесплатный",
                subtitle = "1 задача",
                selected = state.plan == SubscriptionPlan.FREE,
                actionLabel = if (state.plan == SubscriptionPlan.FREE) "Текущий" else "Выбрать",
                onAction = { onSetPlan(SubscriptionPlan.FREE) }
            )
        }
        item {
            PlanCard(
                title = "PRO",
                subtitle = "Безлимит задач",
                selected = state.plan == SubscriptionPlan.PRO,
                actionLabel = if (state.plan == SubscriptionPlan.PRO) "Текущий" else "Выбрать PRO",
                onAction = { onSetPlan(SubscriptionPlan.PRO) }
            )
        }
    }
}

@Composable
private fun SettingsPage(
    state: HabitUiState,
    onSetTheme: (AppThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit
) {
    val spacing = AppTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.x2),
        verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
    ) {
        item {
            GlassCard {
                Text("Тема", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(spacing.x1))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    SelectChip("Система", state.themeMode == AppThemeMode.SYSTEM) { onSetTheme(AppThemeMode.SYSTEM) }
                    SelectChip("Светлая", state.themeMode == AppThemeMode.LIGHT) { onSetTheme(AppThemeMode.LIGHT) }
                    SelectChip("Темная", state.themeMode == AppThemeMode.DARK) { onSetTheme(AppThemeMode.DARK) }
                }
            }
        }

        item {
            GlassCard {
                Text("Язык", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(spacing.x1))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    AppLanguage.entries.forEach { lang ->
                        SelectChip(lang.label, state.language == lang) { onSetLanguage(lang) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard(vm: MainViewModel, state: HabitUiState) {
    val spacing = AppTheme.spacing
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text("Создай первую задачу", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = state.editorTitle,
                onValueChange = vm::setEditorTitle,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название") }
            )
            FrequencySelector(
                selected = state.editorFrequency,
                selectedCustomDays = state.editorCustomDays,
                onFrequencySelect = vm::setEditorFrequency,
                onToggleCustomDay = vm::toggleEditorCustomDay
            )
            Button(onClick = vm::saveEditor, enabled = state.editorTitle.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                Text("Создать")
            }
        }
    }
}

@Composable
private fun TrackerHeader(
    habitTitle: String,
    plan: SubscriptionPlan,
    onOpenSettings: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    val title = if (habitTitle.isBlank()) "Без активной задачи" else habitTitle

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.x1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TODAY",
                    style = MaterialTheme.typography.labelLarge,
                    color = semantic.textSecondary
                )
                Surface(
                    color = semantic.primaryMuted,
                    shape = RoundedCornerShape(radius.full)
                ) {
                    Text(
                        text = if (plan == SubscriptionPlan.PRO) "PRO" else "FREE",
                        modifier = Modifier.padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                        style = MaterialTheme.typography.labelLarge,
                        color = semantic.primary
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Surface(
            shape = CircleShape,
            color = semantic.backgroundSurface,
            tonalElevation = AppTheme.elevation.md,
            shadowElevation = AppTheme.elevation.md
        ) {
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(spacing.x5)) {
                Icon(Icons.Rounded.Settings, contentDescription = "Настройки")
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
            label = if (canAddTask) "Новая" else "PRO",
            icon = if (canAddTask) Icons.Rounded.AddCircle else Icons.Rounded.WorkspacePremium,
            onClick = onCreate
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = "Редактировать",
            icon = Icons.Rounded.Edit,
            onClick = onEdit,
            enabled = canEditDelete
        )
        TaskControlButton(
            modifier = Modifier.weight(1f),
            label = "Удалить",
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
    val semantic = AppTheme.colors

    val ctaColor by animateColorAsState(
        targetValue = if (done) semantic.success else semantic.primary,
        animationSpec = tween(durationMillis = 220),
        label = "heroCtaColor"
    )

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text(
                text = task?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("ru"))),
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
            Text(
                text = "Сегодня сделал?",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = onDone,
                enabled = scheduled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.x5 + spacing.x1),
                shape = RoundedCornerShape(radius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ctaColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = semantic.backgroundSurfaceMuted,
                    disabledContentColor = semantic.textSecondary
                )
            ) {
                Text(
                    text = if (!scheduled) "Не по расписанию" else if (done) "Выполнено сегодня" else "Отметить выполнение",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            AnimatedVisibility(visible = done && scheduled) {
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
                        "Отлично, ты сохранил серию.",
                        style = MaterialTheme.typography.bodySmall,
                        color = semantic.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(streak: Int, progress: Int, total: Int) {
    val spacing = AppTheme.spacing
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier.fillMaxWidth()
    ) {
        StatTile(modifier = Modifier.weight(1f), label = "Current streak", value = "${streak}d")
        StatTile(modifier = Modifier.weight(1f), label = "30 day completion", value = "${progress}%")
        StatTile(modifier = Modifier.weight(1f), label = "Total completions", value = "$total")
    }
}

@Composable
private fun StatTile(modifier: Modifier = Modifier, label: String, value: String) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = semantic.backgroundSurface),
        shape = RoundedCornerShape(radius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.x2, vertical = spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = semantic.textSecondary
            )
        }
    }
}

@Composable
private fun SevenDayChart(points: List<Int>, anchorDate: LocalDate) {
    val spacing = AppTheme.spacing
    val safe = if (points.size == 7) points else List(7) { 0 }
    val today = LocalDate.now()

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Text("График 7 дней", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                safe.forEachIndexed { index, value ->
                    val date = anchorDate.minusDays((6 - index).toLong())
                    val isToday = date == today
                    DayBar(
                        modifier = Modifier.weight(1f),
                        done = value == 1,
                        isToday = isToday,
                        label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBar(modifier: Modifier = Modifier, done: Boolean, isToday: Boolean, label: String) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.x5 + spacing.x1)
                .clip(RoundedCornerShape(radius.sm))
                .background(if (isToday) semantic.primaryMuted else semantic.backgroundSurfaceMuted),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (done) spacing.x5 else spacing.x2)
                    .clip(RoundedCornerShape(radius.sm))
                    .background(if (done) semantic.chartDone else semantic.chartMissed)
            )
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
    onMoveMonth: (Long) -> Unit,
    onToday: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val semantic = AppTheme.colors

    GlassCard(contentPadding = PaddingValues(spacing.x2)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.x1)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))),
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
                Text("Сегодня")
            }

            val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
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
                    horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                ) {
                    week.forEach { day ->
                        CalendarCell(
                            modifier = Modifier.weight(1f),
                            date = day,
                            selected = day == selectedDate,
                            done = day != null && day in doneDates,
                            enabled = selectedTask != null,
                            onClick = { day?.let(onDateSelect) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    modifier: Modifier = Modifier,
    date: LocalDate?,
    selected: Boolean,
    done: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val semantic = AppTheme.colors

    if (date == null) {
        Box(modifier = modifier.height(spacing.x5 + spacing.x0_5))
        return
    }

    val isToday = date == LocalDate.now()
    val borderColor = when {
        selected -> semantic.primary
        isToday -> semantic.calendarTodayRing
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .height(spacing.x5 + spacing.x0_5)
            .clip(RoundedCornerShape(radius.sm))
            .border(stroke.thin, borderColor, RoundedCornerShape(radius.sm))
            .background(if (selected) semantic.primary else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = when {
                selected -> MaterialTheme.colorScheme.onPrimary
                isToday -> semantic.primary
                else -> semantic.textPrimary
            }
        )
        Box(
            modifier = Modifier
                .padding(top = spacing.x0_5)
                .size(spacing.x0_5)
                .background(
                    if (!done) Color.Transparent else if (selected) MaterialTheme.colorScheme.onPrimary else semantic.calendarDoneDot,
                    CircleShape
                )
        )
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
private fun TaskEditorDialog(state: HabitUiState, onDismiss: () -> Unit, vm: MainViewModel) {
    val spacing = AppTheme.spacing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (state.editingTaskId == null) "Новая задача" else "Редактировать задачу") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                OutlinedTextField(
                    value = state.editorTitle,
                    onValueChange = vm::setEditorTitle,
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                FrequencySelector(
                    selected = state.editorFrequency,
                    selectedCustomDays = state.editorCustomDays,
                    onFrequencySelect = vm::setEditorFrequency,
                    onToggleCustomDay = vm::toggleEditorCustomDay
                )
            }
        },
        confirmButton = {
            Button(onClick = vm::saveEditor, enabled = state.editorTitle.isNotBlank()) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
private fun FrequencySelector(
    selected: TaskFrequency,
    selectedCustomDays: Set<Int>,
    onFrequencySelect: (TaskFrequency) -> Unit,
    onToggleCustomDay: (Int) -> Unit
) {
    val spacing = AppTheme.spacing
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.x1),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        FrequencyChip("Каждый день", selected == TaskFrequency.DAILY) { onFrequencySelect(TaskFrequency.DAILY) }
        FrequencyChip("Будни", selected == TaskFrequency.WEEKDAYS) { onFrequencySelect(TaskFrequency.WEEKDAYS) }
        FrequencyChip("Кастом", selected == TaskFrequency.CUSTOM) { onFrequencySelect(TaskFrequency.CUSTOM) }

        if (selected == TaskFrequency.CUSTOM) {
            (1..7).forEach { day ->
                val label = DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, Locale("ru")).replaceFirstChar { it.uppercaseChar() }
                FrequencyChip(label, day in selectedCustomDays) { onToggleCustomDay(day) }
            }
        }
    }
}

@Composable
private fun FrequencyChip(title: String, active: Boolean, onClick: () -> Unit) {
    val semantic = AppTheme.colors
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) semantic.primary else semantic.backgroundSurfaceMuted,
            contentColor = if (active) MaterialTheme.colorScheme.onPrimary else semantic.textPrimary
        )
    ) {
        Text(title)
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

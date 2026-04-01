package com.example.microhabit.ui.habits

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.microhabit.*
import com.example.microhabit.i18n.t
import com.example.microhabit.i18n.tf
import com.example.microhabit.ui.components.HabitCardModel
import com.example.microhabit.ui.components.HabitEditModeCard
import com.example.microhabit.ui.components.HabitListCard
import com.example.microhabit.ui.shared.GlassCard
import com.example.microhabit.ui.shared.formatTimeForDevice
import com.example.microhabit.ui.theme.AppTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HabitsPage(
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




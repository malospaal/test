package com.example.microhabit.ui.shared

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.microhabit.*
import com.example.microhabit.ui.tracker.activeHabitsCountLabel
import com.example.microhabit.data.HabitTask
import com.example.microhabit.i18n.*
import com.example.microhabit.ui.theme.AppTheme
@Composable
internal fun TaskSelector(
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
internal fun HabitSelectorRow(
    habits: List<HabitTask>,
    selectedId: String?,
    onHabitSelected: (String) -> Unit,
    onCreateHabit: (() -> Unit)? = null,
    addHabitTileSize: Dp = 36.dp,
    addHabitGapAfter: Dp = 0.dp,
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
                contentPadding = PaddingValues(start = 0.dp, end = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                onCreateHabit?.let { onAdd ->
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            AddHabitTile(
                                onClick = onAdd,
                                tileSize = addHabitTileSize
                            )
                            if (addHabitGapAfter > 0.dp) {
                                Spacer(modifier = Modifier.width(addHabitGapAfter))
                            }
                        }
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
                modifier = Modifier.padding(start = 0.dp, top = 4.dp, bottom = 2.dp)
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
    val selectedColor = AppTheme.colors.primary
    val unselectedBackgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val backgroundColor = if (isSelected) selectedColor else unselectedBackgroundColor
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val emojiBubbleColor = if (isSelected) {
        MaterialTheme.colorScheme.surface.copy(alpha = if (isDarkTheme) 0.26f else 0.86f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val emojiBubbleBorder = MaterialTheme.colorScheme.outline.copy(alpha = if (isDarkTheme) 0.42f else 0.28f)
    val borderColor = if (isSelected) {
        AppTheme.colors.primary.copy(alpha = 0.95f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = if (isDarkTheme) 0.36f else 0.55f)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = emojiBubbleColor,
                border = BorderStroke(1.dp, emojiBubbleBorder),
                modifier = Modifier.size(20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = habit.emoji.ifBlank { "✨" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp
                    )
                }
            }
            Text(
                text = habit.title,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
private fun AddHabitTile(
    onClick: () -> Unit,
    tileSize: Dp = 36.dp
) {
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
            .size(tileSize)
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

@Composable
private fun AllHabitsPill(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) AppTheme.colors.primary else MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) AppTheme.colors.primary.copy(alpha = 0.95f) else MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.36f else 0.55f)
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = t("All habits"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 15.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun SelectChip(title: String, selected: Boolean, onClick: () -> Unit) {
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

@Composable
internal fun GlassCard(
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

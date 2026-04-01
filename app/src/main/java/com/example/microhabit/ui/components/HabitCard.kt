package com.example.microhabit.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Unarchive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
import sh.calvin.reorderable.ReorderableCollectionItemScope

data class HabitCardModel(
    val emoji: String,
    val name: String,
    val colorHex: String,
    val secondaryLine: String,
    val streak: Int,
    val completionPercent: Int,
    val isArchived: Boolean
)

@Composable
fun HabitListCard(
    habit: HabitCardModel,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.backgroundSurface,
        border = BorderStroke(stroke.thin, colors.borderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(parseColorHex(habit.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = habit.emoji, fontSize = 18.sp)
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = habit.secondaryLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "🔥 ${habit.streak}d",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                    Text(
                        text = "${habit.completionPercent.coerceIn(0, 100)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(6.dp))

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = t("More"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    HabitContextMenu(
                        expanded = menuExpanded,
                        isArchived = habit.isArchived,
                        onDismiss = { menuExpanded = false },
                        onEdit = { menuExpanded = false; onEdit() },
                        onArchive = { menuExpanded = false; onArchive() },
                        onUnarchive = { menuExpanded = false; onUnarchive() },
                        onDelete = { menuExpanded = false; onDelete() }
                    )
                }
            }

            val progress = habit.completionPercent.coerceIn(0, 100) / 100f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(3.dp)
                        .background(AppTheme.colors.success)
                )
            }
        }
    }
}

@Composable
fun HabitContextMenu(
    expanded: Boolean,
    isArchived: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(180.dp)
    ) {
        if (!isArchived) {
            DropdownMenuItem(
                text = { Text(t("Edit")) },
                leadingIcon = {
                    Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                onClick = onEdit
            )
        }

        if (!isArchived) {
            DropdownMenuItem(
                text = { Text(t("Archive")) },
                leadingIcon = {
                    Icon(Icons.Rounded.Archive, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                onClick = onArchive
            )
        } else {
            DropdownMenuItem(
                text = { Text(t("Unarchive")) },
                leadingIcon = {
                    Icon(Icons.Rounded.Unarchive, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                onClick = onUnarchive
            )
        }

        HorizontalDivider()

        DropdownMenuItem(
            text = {
                Text(
                    text = t("Delete"),
                    color = AppTheme.colors.danger
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = AppTheme.colors.danger,
                    modifier = Modifier.size(16.dp)
                )
            },
            onClick = onDelete
        )
    }
}

@Composable
fun ReorderableCollectionItemScope.HabitEditModeCard(
    habit: HabitCardModel,
    isDragging: Boolean,
    onDelete: () -> Unit,
    onDragStarted: () -> Unit = {},
    onDragStopped: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.02f else 1f,
        animationSpec = tween(durationMillis = 140),
        label = "dragScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 140),
        label = "dragElevation"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundSurface),
        border = BorderStroke(
            width = if (isDragging) 1.5.dp else 1.dp,
            color = if (isDragging) AppTheme.colors.primary else AppTheme.colors.borderSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .draggableHandle(
                        onDragStarted = { onDragStarted() },
                        onDragStopped = { onDragStopped() }
                    )
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(parseColorHex(habit.colorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.emoji, fontSize = 18.sp)
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.danger.copy(alpha = 0.12f))
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = t("Delete"),
                    tint = colors.danger,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


package com.example.microhabit.ui.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.microhabit.AppPage
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Home
private data class PrimaryNavItem(
    val page: AppPage,
    val icon: ImageVector,
    val contentDescription: String
)

@Composable
internal fun PrimaryBottomNavigationBar(
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





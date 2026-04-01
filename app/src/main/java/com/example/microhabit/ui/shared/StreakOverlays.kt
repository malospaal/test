package com.example.microhabit.ui.shared

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.microhabit.R
import com.example.microhabit.*
import com.example.microhabit.i18n.*
import com.example.microhabit.ui.theme.AppTheme


@Composable
internal fun streakMilestoneTier(days: Int): StreakMilestoneTier? {
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


@Composable
internal fun StreakRewardOverlay(model: StreakOverlayModel) {
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
internal fun StreakMilestoneScreen(
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


internal fun isStreakMilestone(streak: Int): Boolean {
    return streak in StreakMilestoneQueue.milestoneSet()
}




package com.example.microhabit.ui.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.*
import com.example.microhabit.data.*
import com.example.microhabit.i18n.*
import com.example.microhabit.ui.theme.AppTheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
internal fun AccountPage(
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
    val currentPlanLabelColor = if (isDark) Color(0xFF9ECFB4) else Color(0xFF2D4A30)
    val disabledOnDarkText = if (isDark) Color(0xFF4A7A5E) else colorScheme.outline
    val accentPrimary = if (isDark) Color(0xFF2DCF96) else colorScheme.primary
    val accentSecondary = if (isDark) Color(0xFF1D9E75) else colorScheme.primary
    val sectionDividerColor = if (isDark) Color(0xFF1A3528) else Color(0xFFE4EDE5)
    val freeBadgeBackground = if (isDark) Color(0xFF1A2A1A) else Color(0xFFE0EAE1)
    val freeBadgeTextColor = if (isDark) mutedOnDarkText else Color(0xFF3A5C3E)
    val activeBadgeBackground = if (isDark) Color(0xFF1A3A20) else Color(0xFFD1F0E4)
    val activeBadgeTextColor = if (isDark) accentPrimary else Color(0xFF0F6E56)
    val cancelledBadgeBackground = if (isDark) Color(0xFF2A1F0A) else colorScheme.error.copy(alpha = 0.12f)
    val primaryButtonTextColor = Color(0xFF04342C)
    val freeUsageLabelColor = if (isDark) secondaryOnDarkText else Color(0xFF3A5C3E)
    val freeLimitReachedColor = if (isDark) Color(0xFFF59E42) else Color(0xFFB45309)
    val freeProgressFillReachedColor = if (isDark) Color(0xFFF59E42) else Color(0xFFF59E0B)
    val freeProgressTrackColor = if (isDark) innerSunkenSurface else Color(0xFFD8E6D9)
    val lockedFeatureBorderColor = if (isDark) Color(0xFF2A4A38) else Color(0xFFA8BEA9)
    val lockedFeatureTextColor = if (isDark) disabledOnDarkText else Color(0xFF0D1F12)
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

            val planCardBorder = when (state.subscriptionState) {
                is SubscriptionState.PremiumActive -> BorderStroke(
                    1.5.dp,
                    if (isDark) accentPrimary else Color(0xFF1D9E75)
                )
                is SubscriptionState.PremiumCancelled -> BorderStroke(
                    1.5.dp,
                    if (isDark) Color(0xFF3A1A1A) else colorScheme.error.copy(alpha = 0.45f)
                )
                SubscriptionState.Free -> BorderStroke(
                    1.5.dp,
                    if (isDark) accentPrimary else Color(0xFF1D9E75)
                )
            }

            val planCardElevation = when (state.subscriptionState) {
                SubscriptionState.Free -> if (isDark) 0.dp else 1.dp
                is SubscriptionState.PremiumActive, is SubscriptionState.PremiumCancelled -> if (isDark) 0.dp else 2.dp
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = planCardElevation,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = if (isDark) Color.Transparent else Color(0xFF0D1F12).copy(alpha = 0.08f),
                        spotColor = if (isDark) Color.Transparent else Color(0xFF0D1F12).copy(alpha = 0.08f)
                    ),
                color = planCardSurface,
                shape = RoundedCornerShape(16.dp),
                border = planCardBorder
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
                                        color = currentPlanLabelColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = t("plan_free_title"),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryOnDarkText
                                    )

                                }
                                Surface(
                                    color = freeBadgeBackground,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = t("plan_free_badge"),
                                        color = freeBadgeTextColor,
                                        fontSize = 12.sp,
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
                                shape = RoundedCornerShape(10.dp),
                                border = if (isDark) null else BorderStroke(1.dp, Color(0xFFD8E6D9))
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
                                            color = freeUsageLabelColor
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
                                        color = if (activeHabitsCount >= freeHabitLimit) freeProgressFillReachedColor else accentSecondary,
                                        trackColor = freeProgressTrackColor,
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
                                        fontSize = if (activeHabitsCount >= freeHabitLimit) 11.sp else 10.sp,
                                        color = if (activeHabitsCount >= freeHabitLimit) freeLimitReachedColor else mutedOnDarkText
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
                                            .border(1.5.dp, lockedFeatureBorderColor, RoundedCornerShape(999.dp))
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        fontSize = 13.sp,
                                        color = lockedFeatureTextColor
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
                                    contentColor = primaryButtonTextColor
                                ),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Text(
                                    text = "${t("Get Premium")} ✦",
                                    fontSize = 15.sp,
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
                                        color = currentPlanLabelColor,
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
                                        color = activeBadgeTextColor,
                                        fontSize = 12.sp,
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
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryOnDarkText
                                        )
                                        Text(
                                            text = subscriptionState.nextBillingDate.format(dateFormatter),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
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
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onOpenManageSubscription,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentPrimary,
                                    contentColor = primaryButtonTextColor
                                ),
                                contentPadding = PaddingValues(vertical = 13.dp)
                            ) {
                                Text(
                                    text = t("manage_subscription"),
                                    fontSize = 14.sp,
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
                                        color = currentPlanLabelColor,
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
                            }
                            Spacer(Modifier.height(12.dp))
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
                                    fontSize = 14.sp,
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
            AccountActionCard(
                borderColorOverride = if (isDark) null else Color(0xFFE8D4D4)
            ) {
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
                color = if (isDark) mutedOnDarkText else Color(0xFFA8C4AA),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
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
internal fun AccountSectionLabel(title: String) {
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val locale = appLocale()
    Text(
        text = title.uppercase(locale),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = if (isDark) AppTheme.colors.textTertiary else Color(0xFF5A7A5E),
        letterSpacing = 0.4.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 14.dp, bottom = 6.dp)
    )
}

@Composable
internal fun AccountActionCard(
    borderColorOverride: Color? = null,
    content: @Composable () -> Unit
) {
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val colors = AppTheme.colors
    val borderColor = borderColorOverride ?: if (isDark) {
        colors.borderSubtle.copy(alpha = 0.55f)
    } else {
        Color(0xFFC8D9CA)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 0.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isDark) Color.Transparent else Color(0xFF0D1F12).copy(alpha = 0.06f),
                spotColor = if (isDark) Color.Transparent else Color(0xFF0D1F12).copy(alpha = 0.06f)
            ),
        color = if (isDark) colors.backgroundSurfaceMuted else Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
internal fun AccountActionRow(
    title: String,
    destructive: Boolean = false,
    onClick: () -> Unit
) {
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
    val normalTextColor = if (isDark) AppTheme.colors.textPrimary else Color(0xFF0D1F12)
    val normalChevronColor = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFF5A7A5E)
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
            color = if (destructive) AppTheme.colors.danger else normalTextColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 16.sp,
            color = if (destructive) AppTheme.colors.danger else normalChevronColor
        )
    }
}







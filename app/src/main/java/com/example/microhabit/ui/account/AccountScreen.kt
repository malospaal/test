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
                            }
                            Spacer(Modifier.height(12.dp))
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
internal fun AccountSectionLabel(title: String) {
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
internal fun AccountActionCard(content: @Composable () -> Unit) {
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
internal fun AccountActionRow(
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



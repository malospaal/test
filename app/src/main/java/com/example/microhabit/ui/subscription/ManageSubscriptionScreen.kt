package com.example.microhabit.ui.subscription

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.*
import com.example.microhabit.data.*
import com.example.microhabit.i18n.*
import com.example.microhabit.ui.components.FeatureBulletRow
import com.example.microhabit.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.lazy.LazyColumn
import com.example.microhabit.ui.shared.StreakMilestoneScreen


@Composable
internal fun ManageSubscriptionScreen(
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
    val activeBadgeBg = if (isDark) Color(0xFF1A3A20) else Color(0xFFD1F0E4)
    val activeBadgeText = if (isDark) accentPrimary else Color(0xFF0F6E56)
    val activePlanLabelColor = if (isDark) colors.textSecondary else Color(0xFF2D4A30)
    val warningBadgeBg = if (isDark) Color(0xFF2A1F0A) else colorScheme.error.copy(alpha = 0.12f)
    val lifetimeBadgeBg = if (isDark) Color(0xFF1A2A1A) else Color(0xFFFEF3E0)
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
                            Text(t("Active plan"), style = MaterialTheme.typography.labelLarge, color = activePlanLabelColor)
                            Surface(color = activeBadgeBg, shape = RoundedCornerShape(999.dp)) {
                                Text(
                                    t("Active"),
                                    color = activeBadgeText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
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
                                    Text(
                                        subscriptionState.nextBillingAmount.orEmpty(),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 30.sp
                                    )
                                    Text(
                                        subscriptionState.nextBillingDate?.format(dateFormatter).orEmpty(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.textSecondary
                                    )
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
                            badgeColor = Color(0xFFB45309),
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
                            badgeColor = Color(0xFFB45309),
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
internal fun ManageSubscriptionCard(
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
internal fun ManagePlanSwitchRow(
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
    val cardBackground = if (isDark) Color(0xFF0A1F13) else Color(0xFFFFFFFF)

    val selectedBorder = if (isDark) Color(0xFF2DCF96) else Color(0xFF1D9E75)
    val unselectedBorder = if (isDark) null else BorderStroke(1.dp, Color(0xFFC8D9CA))
    val checkFill = if (isDark) Color(0xFF2DCF96) else Color(0xFF1D9E75)
    val unselectedRing = if (isDark) Color(0xFF2A5A3A) else Color(0xFFA8BEA9)

    val defaultBadgeBg = if (isDark) Color(0xFF1A3A20) else Color(0xFFE0F4EC)
    val defaultBadgeFg = if (isDark) Color(0xFF2DCF96) else Color(0xFF0F6E56)
    val badgeBg = if (badgeBackground == Color.Unspecified) defaultBadgeBg else badgeBackground
    val badgeFg = if (badgeColor == Color.Unspecified) defaultBadgeFg else badgeColor

    val selectedTitleColor = if (isDark) Color(0xFFE8F5EF) else Color(0xFF0D1F12)
    val unselectedTitleColor = if (isDark) Color(0xFF9ECFB4) else Color(0xFF2D4A30)
    val selectedPriceColor = if (isDark) Color(0xFF2DCF96) else Color(0xFF1D9E75)
    val unselectedPriceColor = if (isDark) Color(0xFF4A7A5E) else Color(0xFF3A5C3E)
    val selectedMetaColor = if (isDark) Color(0xFF6AAA85) else Color(0xFF3A5C3E)
    val unselectedMetaColor = if (isDark) Color(0xFF4A7A5E) else Color(0xFF5A7A5E)

    val priceSplitIndex = when {
        price.contains(" /") -> price.indexOf(" /")
        price.contains(" ·") -> price.indexOf(" ·")
        else -> -1
    }
    val priceMain = if (priceSplitIndex >= 0) price.substring(0, priceSplitIndex) else price
    val priceMeta = if (priceSplitIndex >= 0) price.substring(priceSplitIndex) else ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .graphicsLayer { alpha = opacity },
        shape = RoundedCornerShape(14.dp),
        color = cardBackground,
        border = if (selected) BorderStroke(1.5.dp, selectedBorder) else unselectedBorder
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Surface(
                    modifier = Modifier.size(18.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = checkFill
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            } else {
                Box(Modifier.size(18.dp).border(2.dp, unselectedRing, RoundedCornerShape(999.dp)))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) selectedTitleColor else unselectedTitleColor
                    )
                    if (!badge.isNullOrBlank()) {
                        Text(
                            badge,
                            color = badgeFg,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(5.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        priceMain,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) selectedPriceColor else unselectedPriceColor
                    )
                    if (priceMeta.isNotBlank()) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            priceMeta,
                            fontSize = 14.sp,
                            color = if (selected) selectedMetaColor else unselectedMetaColor,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MilestonePreviewSheet(
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = AppTheme.colors.backgroundCanvas.red < 0.2f
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
internal fun CancelSubscriptionSheet(
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
internal fun CancelSheetLossRow(label: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("✕", color = AppTheme.colors.danger, fontWeight = FontWeight.Bold)
        Text(label, color = AppTheme.colors.textPrimary)
    }
}

@Composable
internal fun PremiumPlan.displayName(): String = when (this) {
    PremiumPlan.MONTHLY -> t("Monthly")
    PremiumPlan.YEARLY -> t("Yearly")
    PremiumPlan.LIFETIME -> t("Lifetime")
}

@Composable
internal fun managePlanName(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> t("plan_monthly")
    PremiumPlan.YEARLY -> t("plan_yearly")
    PremiumPlan.LIFETIME -> t("plan_lifetime")
}

@Composable
internal fun managePlanPriceSummary(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> "\$3.99 / ${t("month")}".replace('$', '$')
    PremiumPlan.YEARLY -> "\$24.99 / ${t("year")}".replace('$', '$')
    PremiumPlan.LIFETIME -> "\$59.99".replace('$', '$')
}

@Composable
internal fun managePlanCtaLabel(plan: PremiumPlan): String = t("switch_to_plan").replace("{plan}", managePlanName(plan))

@Composable
internal fun managePlanHintText(currentPlan: PremiumPlan, targetPlan: PremiumPlan, nextBillingDate: LocalDate?, formatter: DateTimeFormatter): String {
    if (currentPlan == targetPlan) return t("plan_switcher_select_hint")
    val baseDate = nextBillingDate ?: LocalDate.now()
    return when {
        currentPlan == PremiumPlan.MONTHLY && targetPlan == PremiumPlan.YEARLY -> t("hint_yearly_upgrade").replace("{date}", baseDate.plusYears(1).format(formatter))
        targetPlan == PremiumPlan.LIFETIME -> t("hint_lifetime_upgrade")
        currentPlan == PremiumPlan.YEARLY && targetPlan == PremiumPlan.MONTHLY -> t("hint_monthly_downgrade").replace("{date}", baseDate.format(formatter))
        else -> t("plan_switcher_select_hint")
    }
}





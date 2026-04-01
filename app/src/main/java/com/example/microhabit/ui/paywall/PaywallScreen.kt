package com.example.microhabit.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.BillingCycle
import com.example.microhabit.PaywallTrigger
import com.example.microhabit.data.PremiumPlan
import com.example.microhabit.data.SubscriptionPlan
import com.example.microhabit.data.hasPremiumAccess
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip


@Composable
internal fun PaywallPage(
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
internal fun PaywallPlanCard(
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
internal fun planPriceLabel(plan: PremiumPlan): String = when (plan) {
    PremiumPlan.MONTHLY -> "\$3.99 / ${t("month")}".replace('$', '$')
    PremiumPlan.YEARLY -> "\$24.99 / ${t("year")}".replace('$', '$')
    PremiumPlan.LIFETIME -> "\$59.99".replace('$', '$')
}


@Composable
internal fun PremiumFeatureRow(title: String, subtitle: String, showSubtitle: Boolean = true) {
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


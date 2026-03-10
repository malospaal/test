package com.example.microhabit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.microhabit.ui.theme.AppTheme

data class PricingCardModel(
    val title: String,
    val priceLabel: String,
    val subtitle: String,
    val badge: String? = null
)

@Composable
fun PricingPlanCard(
    model: PricingCardModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val colors = AppTheme.colors
    val stroke = AppTheme.stroke

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (selected) stroke.medium else stroke.thin,
                color = if (selected) colors.primary else colors.borderSubtle,
                shape = RoundedCornerShape(radius.lg)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colors.primaryMuted else colors.backgroundSurface
        ),
        shape = RoundedCornerShape(radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.x2),
            verticalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                if (!model.badge.isNullOrBlank()) {
                    Text(
                        text = model.badge,
                        modifier = Modifier
                            .background(colors.successMuted, RoundedCornerShape(radius.full))
                            .padding(horizontal = spacing.x1, vertical = spacing.x0_5),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.success
                    )
                }
            }
            Text(
                text = model.priceLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = model.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
fun PlanComparisonRow(
    feature: String,
    freeIncluded: Boolean,
    premiumIncluded: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.x0_5),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Text(
            text = feature,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
        PlanStatusIcon(included = freeIncluded)
        PlanStatusIcon(included = premiumIncluded)
    }
}

@Composable
fun FeatureBulletRow(
    label: String,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = colors.success
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun PlanStatusIcon(included: Boolean) {
    val colors = AppTheme.colors
    Icon(
        imageVector = if (included) Icons.Rounded.Check else Icons.Rounded.Close,
        contentDescription = null,
        tint = if (included) colors.success else colors.textTertiary
    )
}

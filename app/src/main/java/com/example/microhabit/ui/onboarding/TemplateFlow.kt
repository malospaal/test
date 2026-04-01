package com.example.microhabit.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.data.AppLanguage
import com.example.microhabit.i18n.LocalAppLanguage
import com.example.microhabit.i18n.formatTranslate
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.create.CreateHabitTemplate
import com.example.microhabit.ui.create.CreateHabitTemplateCatalog
import com.example.microhabit.ui.create.TemplateCategory
import com.example.microhabit.ui.theme.AppTheme
@Composable
internal fun OnboardingCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.97f,
        animationSpec = tween(durationMillis = 150),
        label = "onboardingCategoryScale"
    )
    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        color = if (selected) colors.primaryMuted else colors.backgroundSurfaceMuted
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.x1_5, vertical = AppTheme.spacing.x1_5),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}

@Composable
internal fun OnboardingTemplateCard(
    title: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.985f,
        animationSpec = tween(durationMillis = 140),
        label = "onboardingTemplateScale"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .clickable(onClick = onClick),
        color = if (selected) colors.primaryMuted else colors.backgroundSurfaceMuted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.x1_5, vertical = spacing.x1_5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.x1)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = t("Selected"),
                    tint = colors.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitCategoryScreen(
    onCategorySelected: (TemplateCategory) -> Unit,
    onCreateCustom: () -> Unit,
    onSkip: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val language = LocalAppLanguage.current
    val categories = remember {
        listOf(
            Triple(TemplateCategory.HEALTH, "💊", "cat_health"),
            Triple(TemplateCategory.SPORT, "🏃", "cat_sport"),
            Triple(TemplateCategory.MENTAL, "🧘", "cat_mental"),
            Triple(TemplateCategory.PRODUCTIVITY, "📚", "cat_productivity")
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = t("screen_new_habit"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = t("Close"))
                        }
                    },
                    actions = {
                        TextButton(onClick = onSkip) {
                            Text(t("label_skip"))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1_5)
            ) {
                Text(
                    text = t("label_what_to_improve"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = t("label_choose_category"),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                categories.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.x1)
                    ) {
                        row.forEach { (category, emoji, nameKey) ->
                            val templateCount = CreateHabitTemplateCatalog.templatesFor(category).size
                            CategoryTile(
                                emoji = emoji,
                                title = t(nameKey),
                                countLabel = templateCountLabel(templateCount, language),
                                modifier = Modifier.weight(1f),
                                onClick = { onCategorySelected(category) }
                            )
                        }
                        repeat((2 - row.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onCreateCustom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = t("btn_create_custom"),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                }
            }
        }
    }

}

@Composable
internal fun CategoryTile(
    emoji: String,
    title: String,
    countLabel: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = colors.backgroundSurface,
        border = BorderStroke(1.dp, colors.borderSubtle),
        modifier = modifier.aspectRatio(1.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = countLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun templateCountLabel(count: Int, language: AppLanguage): String {
    if (count <= 0) return formatTranslate(language, "template_count_many", 0)
    return when (language) {
        AppLanguage.RU, AppLanguage.UK -> {
            val mod10 = count % 10
            val mod100 = count % 100
            val key = when {
                mod10 == 1 && mod100 != 11 -> "template_count_one"
                mod10 in 2..4 && mod100 !in 12..14 -> "template_count_few"
                else -> "template_count_many"
            }
            formatTranslate(language, key, count)
        }
        AppLanguage.CS -> {
            val key = when (count) {
                1 -> "template_count_one"
                2, 3, 4 -> "template_count_few"
                else -> "template_count_many"
            }
            formatTranslate(language, key, count)
        }
        else -> {
            val key = if (count == 1) "template_count_one" else "template_count_many"
            formatTranslate(language, key, count)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitTemplateScreen(
    category: TemplateCategory,
    onTemplateSelected: (CreateHabitTemplate) -> Unit,
    onCreateCustomHabit: () -> Unit,
    onBack: () -> Unit
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val templates = remember(category) { CreateHabitTemplateCatalog.templatesFor(category) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.backgroundCanvas
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = t(CreateHabitTemplateCatalog.categoryLabelKey(category)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = t("Back")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.backgroundSurface
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = spacing.x2, vertical = spacing.x1_5),
                verticalArrangement = Arrangement.spacedBy(spacing.x1)
            ) {
                items(templates, key = { it.id }) { template ->
                    Surface(
                        onClick = { onTemplateSelected(template) },
                        shape = RoundedCornerShape(AppTheme.radius.md),
                        color = colors.backgroundSurface,
                        border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = template.emoji.ifBlank { "✨" },
                                    fontSize = 20.sp
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = t(template.nameKey),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = templateMetaLabel(template),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textSecondary.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = onCreateCustomHabit,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppTheme.radius.md),
                        border = BorderStroke(AppTheme.stroke.thin, colors.borderSubtle.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text(t("btn_create_custom"))
                    }
                }
            }
        }
    }
}




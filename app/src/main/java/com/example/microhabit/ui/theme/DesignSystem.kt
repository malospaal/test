package com.example.microhabit.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val x0: Dp = 0.dp,
    val x0_5: Dp = 4.dp,
    val x1: Dp = 8.dp,
    val x1_5: Dp = 12.dp,
    val x2: Dp = 16.dp,
    val x3: Dp = 24.dp,
    val x4: Dp = 32.dp,
    val x5: Dp = 40.dp,
    val x6: Dp = 48.dp
)

@Immutable
data class AppRadius(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val full: Dp = 999.dp
)

@Immutable
data class AppElevation(
    val none: Dp = 0.dp,
    val sm: Dp = 1.dp,
    val md: Dp = 2.dp,
    val lg: Dp = 4.dp
)

@Immutable
data class AppStroke(
    val thin: Dp = 1.dp,
    val medium: Dp = 2.dp
)

@Immutable
data class AppSemanticColors(
    val primary: Color,
    val primaryMuted: Color,
    val success: Color,
    val successMuted: Color,
    val neutral: Color,
    val neutralMuted: Color,
    val backgroundCanvas: Color,
    val backgroundSurface: Color,
    val backgroundSurfaceMuted: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    val warning: Color,
    val danger: Color,
    val chartDone: Color,
    val chartMissed: Color,
    val calendarTodayRing: Color,
    val calendarDoneDot: Color
)

internal val AppSpacingTokens = AppSpacing()
internal val AppRadiusTokens = AppRadius()
internal val AppElevationTokens = AppElevation()
internal val AppStrokeTokens = AppStroke()

internal val LocalAppSpacing = staticCompositionLocalOf { AppSpacingTokens }
internal val LocalAppRadius = staticCompositionLocalOf { AppRadiusTokens }
internal val LocalAppElevation = staticCompositionLocalOf { AppElevationTokens }
internal val LocalAppStroke = staticCompositionLocalOf { AppStrokeTokens }
internal val LocalAppSemanticColors = compositionLocalOf<AppSemanticColors> {
    error("AppSemanticColors not provided")
}

object AppTheme {
    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val radius: AppRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalAppRadius.current

    val elevation: AppElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalAppElevation.current

    val colors: AppSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSemanticColors.current

    val stroke: AppStroke
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStroke.current
}

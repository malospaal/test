package com.example.microhabit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.microhabit.data.AppThemeMode

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1D9E75),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1F0E4),
    onPrimaryContainer = Color(0xFF0F6E56),
    secondary = Color(0xFF3A5C3E),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFEEF3EE),
    onBackground = Color(0xFF0D1F12),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0D1F12),
    surfaceVariant = Color(0xFFE4EDE5),
    onSurfaceVariant = Color(0xFF5A7A5E),
    outline = Color(0xFFC8D9CA),
    error = Color(0xFFC0392B),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6EC8B7),
    onPrimary = Color(0xFF082B25),
    primaryContainer = Color(0xFF1D4941),
    onPrimaryContainer = Color(0xFFCDEDE6),
    secondary = Color(0xFF95A8A1),
    onSecondary = Color(0xFF13201C),
    background = Color(0xFF0E1513),
    onBackground = Color(0xFFE7EFEB),
    surface = Color(0xFF141D1A),
    onSurface = Color(0xFFE7EFEB),
    surfaceVariant = Color(0xFF1F2926),
    onSurfaceVariant = Color(0xFFA7B5AF),
    outline = Color(0xFF323E39),
    error = Color(0xFFF08D8D),
    onError = Color(0xFF3C1010)
)

private val LightSemanticColors = AppSemanticColors(
    primary = Color(0xFF1D9E75),
    primaryMuted = Color(0xFFECFDF5),
    success = Color(0xFF1D9E75),
    successMuted = Color(0xFFECFDF5),
    lottieCheckmarkTint = Color(0xFFFFFFFF),
    neutral = Color(0xFF3A5C3E),
    neutralMuted = Color(0xFFE8EFE8),
    backgroundCanvas = Color(0xFFEEF3EE),
    backgroundSurface = Color(0xFFFFFFFF),
    backgroundSurfaceMuted = Color(0xFFE4EDE5),
    textPrimary = Color(0xFF0D1F12),
    textSecondary = Color(0xFF3A5C3E),
    textTertiary = Color(0xFF5A7A5E),
    borderSubtle = Color(0xFFD8E6D9),
    borderStrong = Color(0xFFA8BEA9),
    warning = Color(0xFFF59E0B),
    danger = Color(0xFFC0392B),
    chartDone = Color(0xFF1D9E75),
    chartMissed = Color(0xFFC0392B),
    calendarTodayRing = Color(0xFF1D9E75),
    calendarDoneDot = Color(0xFF0F6E56)
)

private val DarkSemanticColors = AppSemanticColors(
    primary = Color(0xFF6EC8B7),
    primaryMuted = Color(0xFF1D3D37),
    success = Color(0xFF62B88E),
    successMuted = Color(0xFF1B352B),
    lottieCheckmarkTint = Color(0xFF62B88E),
    neutral = Color(0xFFA7B5AF),
    neutralMuted = Color(0xFF1C2623),
    backgroundCanvas = Color(0xFF0E1513),
    backgroundSurface = Color(0xFF141D1A),
    backgroundSurfaceMuted = Color(0xFF1A2421),
    textPrimary = Color(0xFFE7EFEB),
    textSecondary = Color(0xFFB8C5BF),
    textTertiary = Color(0xFF8D9B95),
    borderSubtle = Color(0xFF28322F),
    borderStrong = Color(0xFF3C4844),
    warning = Color(0xFFD0A469),
    danger = Color(0xFFF08D8D),
    chartDone = Color(0xFF6EC8B7),
    chartMissed = Color(0xFF495751),
    calendarTodayRing = Color(0xFF6EC8B7),
    calendarDoneDot = Color(0xFF62B88E)
)

@Composable
fun MicroHabitTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (dark) DarkColorScheme else LightColorScheme
    val semanticColors = if (dark) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacingTokens,
        LocalAppRadius provides AppRadiusTokens,
        LocalAppElevation provides AppElevationTokens,
        LocalAppStroke provides AppStrokeTokens,
        LocalAppSemanticColors provides semanticColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}



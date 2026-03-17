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
    primary = Color(0xFF1F6F64),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDEFEA),
    onPrimaryContainer = Color(0xFF123A33),
    secondary = Color(0xFF55645F),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F7F6),
    onBackground = Color(0xFF111A17),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111A17),
    surfaceVariant = Color(0xFFEDF1EF),
    onSurfaceVariant = Color(0xFF5B6862),
    outline = Color(0xFFE3E9E6),
    error = Color(0xFFB54747),
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
    primary = Color(0xFF1F6F64),
    primaryMuted = Color(0xFFEAF5F1),
    success = Color(0xFF2E8C63),
    successMuted = Color(0xFFE6F4ED),
    lottieCheckmarkTint = Color(0xFFFFFFFF),
    neutral = Color(0xFF5B6862),
    neutralMuted = Color(0xFFF2F5F3),
    backgroundCanvas = Color(0xFFF5F7F6),
    backgroundSurface = Color(0xFFFFFFFF),
    backgroundSurfaceMuted = Color(0xFFF2F5F3),
    textPrimary = Color(0xFF111A17),
    textSecondary = Color(0xFF4F5C56),
    textTertiary = Color(0xFF7A8781),
    borderSubtle = Color(0xFFE7EDE9),
    borderStrong = Color(0xFFD2DBD7),
    warning = Color(0xFFA3722C),
    danger = Color(0xFFB54747),
    chartDone = Color(0xFF1F6F64),
    chartMissed = Color(0xFFC8D2CD),
    calendarTodayRing = Color(0xFF1F6F64),
    calendarDoneDot = Color(0xFF2E8C63)
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

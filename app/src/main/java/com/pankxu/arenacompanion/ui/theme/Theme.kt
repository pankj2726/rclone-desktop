package com.pankxu.arenacompanion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// R2: neutral palette, no arena brand colours, no Basel Grotesk / Martina Plantijn.

private val Ink = Color(0xFF14161A)
private val Paper = Color(0xFFFDFDFC)
private val Muted = Color(0xFF5F6570)
private val Line = Color(0xFFE3E5E9)
private val Accent = Color(0xFF2F5BEA)
private val AccentSoft = Color(0xFFE8EDFD)

private val LightColors = lightColorScheme(
    primary = Accent, onPrimary = Color.White,
    primaryContainer = AccentSoft, onPrimaryContainer = Ink,
    background = Paper, onBackground = Ink,
    surface = Color.White, onSurface = Ink,
    surfaceVariant = Color(0xFFF4F5F7), onSurfaceVariant = Muted,
    outline = Line, error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FA8FF), onPrimary = Color(0xFF0B1B4D),
    primaryContainer = Color(0xFF1B2A55), onPrimaryContainer = Color(0xFFDCE3FF),
    background = Color(0xFF101216), onBackground = Color(0xFFE7E9EE),
    surface = Color(0xFF171A1F), onSurface = Color(0xFFE7E9EE),
    surfaceVariant = Color(0xFF21252C), onSurfaceVariant = Color(0xFFA9AFBA),
    outline = Color(0xFF3A3F48), error = Color(0xFFF2B8B5),
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 32.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)

@Composable
fun ArenaCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}

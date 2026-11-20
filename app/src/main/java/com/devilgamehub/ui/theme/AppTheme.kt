package com.devilgamehub.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color = VoidBlack,
    val surface: Color = SurfaceDark,
    val surfaceVariant: Color = SurfaceLightDark,
    val border: Color = SurfaceBorder,
    val cardGlow: Color,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted
)

val NeonCyberColors = AppColors(
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = NeonPink,
    cardGlow = CardGlowBorder
)

val CyberPinkColors = AppColors(
    primary = NeonPink,
    secondary = NeonAmber,
    tertiary = NeonPurple,
    cardGlow = Color(0x66FF007F)
)

val MatrixGreenColors = AppColors(
    primary = NeonGreen,
    secondary = NeonCyan,
    tertiary = NeonAmber,
    cardGlow = Color(0x6600FF66)
)

fun getAppColors(themeName: String): AppColors {
    return when {
        themeName.contains("Pink", ignoreCase = true) -> CyberPinkColors
        themeName.contains("Matrix", ignoreCase = true) -> MatrixGreenColors
        else -> NeonCyberColors
    }
}

val LocalAppColors = staticCompositionLocalOf { NeonCyberColors }

@Composable
fun DevilGameHubTheme(
    accentTheme: String = "Neon Cyber",
    content: @Composable () -> Unit
) {
    val appColors = getAppColors(accentTheme)

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        content = content
    )
}

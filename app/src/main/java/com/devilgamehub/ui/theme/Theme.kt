package com.devilgamehub.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DevilGamingColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceLightDark,
    onPrimaryContainer = NeonPurpleGlow,
    secondary = NeonCyan,
    onSecondary = VoidBlack,
    secondaryContainer = SurfaceLightDark,
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    onTertiary = TextPrimary,
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLightDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = CardGlowBorder
)

@Composable
fun DevilGameHubTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = VoidBlack.toArgb()
            window.navigationBarColor = VoidBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DevilGamingColorScheme,
        typography = Typography,
        content = content
    )
}

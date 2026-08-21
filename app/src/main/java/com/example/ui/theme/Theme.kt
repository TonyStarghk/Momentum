package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MomentumColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0E3A45),
    onPrimaryContainer = NeonCyan,
    secondary = FlameAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D2405),
    onSecondaryContainer = FlameAmber,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = CharcoalDark,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = Color(0x1AFFFFFF)
)

@Composable
fun MomentumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce the deep charcoal dark aesthetic as requested
    MaterialTheme(
        colorScheme = MomentumColorScheme,
        typography = Typography,
        content = content
    )
}

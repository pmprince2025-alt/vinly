package com.vinyl.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VinylColorScheme = darkColorScheme(
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceElevated,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceMuted,
    outline = Divider,
    primary = AccentAmber,
    onPrimary = Background,
    primaryContainer = AccentAmber
)

@Composable
fun VinylTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VinylColorScheme,
        typography = VinylTypography,
        content = content
    )
}

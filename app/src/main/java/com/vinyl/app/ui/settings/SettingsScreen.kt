package com.vinyl.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTexture
import com.vinyl.app.domain.model.VinylTheme
import com.vinyl.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = VinylTypography.bodyLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", color = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme picker
            SectionLabel("Theme")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemeSwatch("Obsidian", Color(0xFF0A0A12), settings.theme == VinylTheme.OBSIDIAN) {
                    viewModel.onThemeChange(VinylTheme.OBSIDIAN)
                }
                ThemeSwatch("Amber", Color(0xFF100A04), settings.theme == VinylTheme.AMBER) {
                    viewModel.onThemeChange(VinylTheme.AMBER)
                }
                ThemeSwatch("Arctic", Color(0xFF04080C), settings.theme == VinylTheme.ARCTIC) {
                    viewModel.onThemeChange(VinylTheme.ARCTIC)
                }
            }

            // Vinyl style
            SectionLabel("Vinyl Style")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VinylStyle.values().forEach { style ->
                    FilterChip(
                        selected = settings.vinylStyle == style,
                        onClick = { viewModel.onVinylStyleChange(style) },
                        label = { Text(style.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Surface,
                            selectedContainerColor = AccentAmber
                        )
                    )
                }
            }

            // Vinyl texture
            SectionLabel("Vinyl Texture")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VinylTexture.values().forEach { texture ->
                    FilterChip(
                        selected = settings.vinylTexture == texture,
                        onClick = { viewModel.onVinylTextureChange(texture) },
                        label = { Text(texture.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Surface,
                            selectedContainerColor = AccentAmber
                        )
                    )
                }
            }

            // Behavior
            SectionLabel("Behavior")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Battery Saver", style = VinylTypography.bodyMedium, color = OnSurface)
                Switch(
                    checked = settings.batterySaver,
                    onCheckedChange = { viewModel.onBatterySaverChange(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = AccentAmber)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tilt Parallax", style = VinylTypography.bodyMedium, color = OnSurface)
                Switch(
                    checked = settings.tiltParallax,
                    onCheckedChange = { viewModel.onTiltParallaxChange(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = AccentAmber)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = VinylTypography.bodySmall,
        color = OnSurfaceMuted,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ThemeSwatch(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (isSelected) Modifier.background(
                        AccentAmber.copy(alpha = 0.3f)
                    ) else Modifier
                )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            style = VinylTypography.bodySmall,
            color = if (isSelected) AccentAmber else OnSurfaceMuted
        )
    }
}

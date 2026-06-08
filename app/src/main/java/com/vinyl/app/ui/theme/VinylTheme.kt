package com.vinyl.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class VinylThemeData(
    val background: Color,
    val tonearmBody: Color,
    val tonearmHighlight: Color,
    val vinylBody: Color,
    val vinylGrooveStart: Color,
    val vinylGrooveEnd: Color,
    val labelRingColor: Color,
    val labelRingOpacity: Float,
    val textPrimary: Color,
    val textSecondary: Color,
    val backgroundTint: Color?
)

val ObsidianThemeData = VinylThemeData(
    background = Obsidian.background,
    tonearmBody = Obsidian.tonearmBody,
    tonearmHighlight = Obsidian.tonearmHighlight,
    vinylBody = Obsidian.vinylBody,
    vinylGrooveStart = Obsidian.vinylGrooveStart,
    vinylGrooveEnd = Obsidian.vinylGrooveEnd,
    labelRingColor = Color.Transparent,
    labelRingOpacity = 0f,
    textPrimary = Obsidian.textPrimary,
    textSecondary = Obsidian.textSecondary,
    backgroundTint = null
)

val AmberThemeData = VinylThemeData(
    background = AmberTheme.background,
    tonearmBody = AmberTheme.tonearmBody,
    tonearmHighlight = AmberTheme.tonearmHighlight,
    vinylBody = AmberTheme.vinylBody,
    vinylGrooveStart = AmberTheme.vinylGrooveStart,
    vinylGrooveEnd = AmberTheme.vinylGrooveEnd,
    labelRingColor = AmberTheme.labelRingColor,
    labelRingOpacity = AmberTheme.labelRingOpacity,
    textPrimary = AmberTheme.textPrimary,
    textSecondary = AmberTheme.textSecondary,
    backgroundTint = AmberTheme.backgroundTint
)

val ArcticThemeData = VinylThemeData(
    background = ArcticTheme.background,
    tonearmBody = ArcticTheme.tonearmBody,
    tonearmHighlight = ArcticTheme.tonearmHighlight,
    vinylBody = ArcticTheme.vinylBody,
    vinylGrooveStart = ArcticTheme.vinylGrooveStart,
    vinylGrooveEnd = ArcticTheme.vinylGrooveEnd,
    labelRingColor = ArcticTheme.labelRingColor,
    labelRingOpacity = ArcticTheme.labelRingOpacity,
    textPrimary = ArcticTheme.textPrimary,
    textSecondary = ArcticTheme.textSecondary,
    backgroundTint = null
)

val LocalVinylTheme = staticCompositionLocalOf { ObsidianThemeData }

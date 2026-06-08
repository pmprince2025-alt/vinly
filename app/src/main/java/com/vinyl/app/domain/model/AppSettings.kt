package com.vinyl.app.domain.model

data class AppSettings(
    val theme: VinylTheme = VinylTheme.OBSIDIAN,
    val vinylStyle: VinylStyle = VinylStyle.CLASSIC,
    val vinylTexture: VinylTexture = VinylTexture.STANDARD,
    val batterySaver: Boolean = false,
    val tiltParallax: Boolean = false,
    val controlsTimeoutSeconds: Int = 3,
    val firstLaunch: Boolean = true
)

enum class VinylTheme {
    OBSIDIAN, AMBER, ARCTIC
}

enum class VinylStyle {
    CLASSIC, MINIMAL, PICTURE, COLORED
}

enum class VinylTexture {
    STANDARD, CARBON_FIBER, COLORED
}

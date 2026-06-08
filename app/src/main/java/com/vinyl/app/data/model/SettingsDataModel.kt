package com.vinyl.app.data.model

data class SettingsDataModel(
    val theme: String = "OBSIDIAN",
    val vinylStyle: String = "CLASSIC",
    val vinylTexture: String = "STANDARD",
    val batterySaver: Boolean = false,
    val tiltParallax: Boolean = false,
    val controlsTimeoutSeconds: Int = 3,
    val firstLaunch: Boolean = true
)

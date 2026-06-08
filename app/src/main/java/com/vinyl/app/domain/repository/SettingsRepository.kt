package com.vinyl.app.domain.repository

import com.vinyl.app.domain.model.AppSettings
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTexture
import com.vinyl.app.domain.model.VinylTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setTheme(theme: VinylTheme)
    suspend fun setVinylStyle(style: VinylStyle)
    suspend fun setVinylTexture(texture: VinylTexture)
    suspend fun setBatterySaver(enabled: Boolean)
    suspend fun setTiltParallax(enabled: Boolean)
    suspend fun setControlsTimeout(seconds: Int)
    suspend fun setFirstLaunchComplete()
}

package com.vinyl.app.data.settings

import com.vinyl.app.domain.model.AppSettings
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTexture
import com.vinyl.app.domain.model.VinylTheme
import com.vinyl.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataSource.settings.map { data ->
        AppSettings(
            theme = try { VinylTheme.valueOf(data.theme) } catch (_: Exception) { VinylTheme.OBSIDIAN },
            vinylStyle = try { VinylStyle.valueOf(data.vinylStyle) } catch (_: Exception) { VinylStyle.CLASSIC },
            vinylTexture = try { VinylTexture.valueOf(data.vinylTexture) } catch (_: Exception) { VinylTexture.STANDARD },
            batterySaver = data.batterySaver,
            tiltParallax = data.tiltParallax,
            controlsTimeoutSeconds = data.controlsTimeoutSeconds,
            firstLaunch = data.firstLaunch
        )
    }

    override suspend fun setTheme(theme: VinylTheme) = dataSource.setTheme(theme.name)
    override suspend fun setVinylStyle(style: VinylStyle) = dataSource.setVinylStyle(style.name)
    override suspend fun setVinylTexture(texture: VinylTexture) = dataSource.setVinylTexture(texture.name)
    override suspend fun setBatterySaver(enabled: Boolean) = dataSource.setBatterySaver(enabled)
    override suspend fun setTiltParallax(enabled: Boolean) = dataSource.setTiltParallax(enabled)
    override suspend fun setControlsTimeout(seconds: Int) = dataSource.setControlsTimeout(seconds)
    override suspend fun setFirstLaunchComplete() = dataSource.setFirstLaunchComplete()
}

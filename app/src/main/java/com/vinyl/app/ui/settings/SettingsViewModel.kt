package com.vinyl.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinyl.app.domain.model.AppSettings
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTexture
import com.vinyl.app.domain.model.VinylTheme
import com.vinyl.app.domain.repository.SettingsRepository
import com.vinyl.app.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun onThemeChange(theme: VinylTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun onVinylStyleChange(style: VinylStyle) {
        viewModelScope.launch { settingsRepository.setVinylStyle(style) }
    }

    fun onVinylTextureChange(texture: VinylTexture) {
        viewModelScope.launch { settingsRepository.setVinylTexture(texture) }
    }

    fun onBatterySaverChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBatterySaver(enabled) }
    }

    fun onTiltParallaxChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setTiltParallax(enabled) }
    }

    fun onControlsTimeoutChange(seconds: Int) {
        viewModelScope.launch { settingsRepository.setControlsTimeout(seconds) }
    }
}

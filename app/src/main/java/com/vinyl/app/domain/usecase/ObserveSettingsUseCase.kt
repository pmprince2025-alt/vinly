package com.vinyl.app.domain.usecase

import com.vinyl.app.domain.model.AppSettings
import com.vinyl.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}

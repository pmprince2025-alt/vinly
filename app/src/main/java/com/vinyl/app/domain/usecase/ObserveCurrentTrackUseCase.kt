package com.vinyl.app.domain.usecase

import com.vinyl.app.domain.model.Track
import com.vinyl.app.domain.repository.MediaSessionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveCurrentTrackUseCase @Inject constructor(
    private val repository: MediaSessionRepository
) {
    operator fun invoke(): StateFlow<Track?> = repository.currentTrack
}

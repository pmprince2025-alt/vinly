package com.vinyl.app.domain.usecase

import com.vinyl.app.domain.model.PlaybackState
import com.vinyl.app.domain.repository.MediaSessionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObservePlaybackStateUseCase @Inject constructor(
    private val repository: MediaSessionRepository
) {
    operator fun invoke(): StateFlow<PlaybackState> = repository.playbackState
}

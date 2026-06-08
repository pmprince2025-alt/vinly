package com.vinyl.app.domain.usecase

import com.vinyl.app.domain.repository.MediaSessionRepository
import javax.inject.Inject

class SendPlaybackCommandUseCase @Inject constructor(
    private val repository: MediaSessionRepository
) {
    fun play() = repository.play()
    fun pause() = repository.pause()
    fun next() = repository.next()
    fun previous() = repository.previous()
    fun seek(position: Long) = repository.seek(position)
}

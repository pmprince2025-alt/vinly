package com.vinyl.app.domain.repository

import com.vinyl.app.domain.model.PlaybackState
import com.vinyl.app.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface MediaSessionRepository {
    val currentTrack: StateFlow<Track?>
    val playbackState: StateFlow<PlaybackState>

    fun play()
    fun pause()
    fun next()
    fun previous()
    fun seek(position: Long)
    fun release()
}

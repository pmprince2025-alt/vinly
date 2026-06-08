package com.vinyl.app.data.mediasession

import com.vinyl.app.data.model.TrackDataModel
import kotlinx.coroutines.flow.StateFlow

data class PlaybackCommand(
    val action: PlaybackAction,
    val seekPosition: Long? = null
)

enum class PlaybackAction {
    PLAY, PAUSE, NEXT, PREVIOUS, SEEK
}

interface MediaSessionDataSource {
    val currentTrack: StateFlow<TrackDataModel?>
    val playbackState: StateFlow<PlaybackStateDataModel>
    val isConnected: StateFlow<Boolean>

    fun sendCommand(command: PlaybackCommand)
    fun release()
}

data class PlaybackStateDataModel(
    val status: PlaybackStatus = PlaybackStatus.NONE,
    val position: Long = 0,
    val playbackSpeed: Float = 1f,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PlaybackStatus { PLAYING, PAUSED, STOPPED, BUFFERING, NONE }

package com.vinyl.app.data.mediasession

import com.vinyl.app.data.model.TrackDataModel
import com.vinyl.app.domain.model.PlaybackState
import com.vinyl.app.domain.model.Track
import com.vinyl.app.domain.repository.MediaSessionRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSessionRepositoryImpl @Inject constructor(
    private val dataSource: MediaSessionDataSource
) : MediaSessionRepository {

    override val currentTrack: StateFlow<Track?> = dataSource.currentTrack
        .map { it?.toDomain() }
        .stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    override val playbackState: StateFlow<PlaybackState> = dataSource.playbackState
        .map { it.toDomain() }
        .stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaybackState(
                status = PlaybackState.Status.NONE,
                position = 0,
                playbackSpeed = 0f,
                timestamp = System.currentTimeMillis()
            )
        )

    override fun play() = dataSource.sendCommand(PlaybackCommand(PlaybackAction.PLAY))
    override fun pause() = dataSource.sendCommand(PlaybackCommand(PlaybackAction.PAUSE))
    override fun next() = dataSource.sendCommand(PlaybackCommand(PlaybackAction.NEXT))
    override fun previous() = dataSource.sendCommand(PlaybackCommand(PlaybackAction.PREVIOUS))
    override fun seek(position: Long) = dataSource.sendCommand(
        PlaybackCommand(PlaybackAction.SEEK, position)
    )

    override fun release() = dataSource.release()
}

private fun TrackDataModel.toDomain(): Track = Track(
    title = title,
    artist = artist,
    album = album,
    albumArtBitmap = albumArtBitmap,
    albumArtUri = albumArtUri,
    duration = duration,
    sourcePackage = sourcePackage
)

private fun PlaybackStateDataModel.toDomain(): PlaybackState = PlaybackState(
    status = when (status) {
        com.vinyl.app.data.mediasession.PlaybackStatus.PLAYING -> PlaybackState.Status.PLAYING
        com.vinyl.app.data.mediasession.PlaybackStatus.PAUSED -> PlaybackState.Status.PAUSED
        com.vinyl.app.data.mediasession.PlaybackStatus.STOPPED -> PlaybackState.Status.STOPPED
        com.vinyl.app.data.mediasession.PlaybackStatus.BUFFERING -> PlaybackState.Status.BUFFERING
        com.vinyl.app.data.mediasession.PlaybackStatus.NONE -> PlaybackState.Status.NONE
    },
    position = position,
    playbackSpeed = playbackSpeed,
    timestamp = timestamp
)

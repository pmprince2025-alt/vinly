package com.vinyl.app.data.mediasession

import android.content.Context
import android.media.session.MediaSession
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.vinyl.app.data.model.TrackDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSessionDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaSessionDataSource {

    override val currentTrack = MutableStateFlow<TrackDataModel?>(null)
    override val playbackState = MutableStateFlow(PlaybackStateDataModel())
    override val isConnected = MutableStateFlow(false)

    private val sessions = ConcurrentHashMap<MediaSession.Token, MediaControllerCompat>()
    private var activeToken: MediaSession.Token? = null
    private var activeController: MediaControllerCompat? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var pollingJob: Job? = null

    fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras ?: return
        val rawToken = extras.getParcelable(NotificationCompat.EXTRA_MEDIA_SESSION) as? MediaSession.Token ?: return
        val token = rawToken

        if (sessions.containsKey(token)) return

        val controller = MediaControllerCompat(context, token)
        sessions[token] = controller

        controller.registerCallback(object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                if (controller === activeController) {
                    metadata?.let { updateTrack(it) }
                }
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                if (controller === activeController) {
                    state?.let { updatePlaybackState(it) }
                }
            }

            override fun onSessionDestroyed() {
                cleanupSession(token)
            }
        })

        setActiveSession(token, controller)
    }

    fun onNotificationRemoved(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras ?: return
        val rawToken = extras.getParcelable(NotificationCompat.EXTRA_MEDIA_SESSION) as? MediaSession.Token ?: return
        val token = rawToken

        sessions.remove(token)?.unregisterCallback(null)
        if (activeToken == token) {
            activeToken = null
            activeController = null
            sessions.keys.lastOrNull()?.let { setActiveSession(it, sessions[it]!!) }
            if (sessions.isEmpty()) {
                currentTrack.value = null
                playbackState.value = PlaybackStateDataModel(status = PlaybackStatus.NONE)
                stopPolling()
            }
        }
    }

    fun onListenerConnected() {
        isConnected.value = true
    }

    fun onListenerDisconnected() {
        isConnected.value = false
    }

    private fun setActiveSession(token: MediaSession.Token, controller: MediaControllerCompat) {
        activeToken = token
        activeController = controller

        controller.metadata?.let { updateTrack(it) }
        controller.playbackState?.let { updatePlaybackState(it) }

        startPolling()
    }

    private fun updateTrack(metadata: MediaMetadataCompat) {
        currentTrack.value = TrackDataModel(
            title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "Unknown Track",
            artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "Unknown Artist",
            album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "Unknown Album",
            albumArtBitmap = null,
            albumArtUri = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)?.let {
                android.net.Uri.parse(it)
            },
            duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
            sourcePackage = activeController?.packageName ?: ""
        )
    }

    private fun updatePlaybackState(state: PlaybackStateCompat) {
        playbackState.value = PlaybackStateDataModel(
            status = when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> PlaybackStatus.PLAYING
                PlaybackStateCompat.STATE_PAUSED -> PlaybackStatus.PAUSED
                PlaybackStateCompat.STATE_STOPPED -> PlaybackStatus.STOPPED
                PlaybackStateCompat.STATE_BUFFERING -> PlaybackStatus.BUFFERING
                else -> PlaybackStatus.NONE
            },
            position = state.position,
            playbackSpeed = state.playbackSpeed,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                delay(500)
                val controller = activeController ?: continue
                val state = controller.playbackState ?: continue
                if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                    updatePlaybackState(state)
                }
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun cleanupSession(token: MediaSession.Token) {
        sessions.remove(token)
        if (activeToken == token) {
            activeToken = null
            activeController = null
            sessions.keys.lastOrNull()?.let {
                sessions[it]?.let { ctrl -> setActiveSession(it, ctrl) }
            } ?: run {
                currentTrack.value = null
                playbackState.value = PlaybackStateDataModel(status = PlaybackStatus.NONE)
                stopPolling()
            }
        }
    }

    override fun sendCommand(command: PlaybackCommand) {
        val controller = activeController ?: return
        val transport = controller.transportControls ?: return
        when (command.action) {
            PlaybackAction.PLAY -> transport.play()
            PlaybackAction.PAUSE -> transport.pause()
            PlaybackAction.NEXT -> transport.skipToNext()
            PlaybackAction.PREVIOUS -> transport.skipToPrevious()
            PlaybackAction.SEEK -> command.seekPosition?.let { transport.seekTo(it) }
        }
    }

    override fun release() {
        scope.cancel()
        pollingJob?.cancel()
        sessions.values.forEach { it.unregisterCallback(null) }
        sessions.clear()
        activeToken = null
        activeController = null
    }
}

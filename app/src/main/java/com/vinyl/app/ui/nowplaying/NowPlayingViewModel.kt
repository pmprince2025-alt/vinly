package com.vinyl.app.ui.nowplaying

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.vinyl.app.domain.model.PlaybackState
import com.vinyl.app.domain.model.Track
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTheme
import com.vinyl.app.domain.repository.SettingsRepository
import com.vinyl.app.domain.usecase.*
import com.vinyl.app.ui.theme.AccentAmber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val observeTrack: ObserveCurrentTrackUseCase,
    private val observePlaybackState: ObservePlaybackStateUseCase,
    private val sendCommand: SendPlaybackCommandUseCase,
    observeSettings: ObserveSettingsUseCase
) : ViewModel() {

    companion object {
        const val VINYL_DEGREES_PER_SECOND = 200f
    }

    private val _controlsVisible = MutableStateFlow(true)
    private val _dominantColor = MutableStateFlow(Color.Transparent)

    private var controlsTimerJob: Job? = null

    val uiState: StateFlow<NowPlayingUiState> = combine(
        observeTrack(),
        observePlaybackState(),
        observeSettings(),
        _controlsVisible,
        _dominantColor
    ) { track, playbackState, settings, controlsVisible, dominantColor ->
        if (settings.firstLaunch) {
            NowPlayingUiState.PermissionRequired
        } else if (track == null || playbackState.status == PlaybackState.Status.NONE) {
            NowPlayingUiState.Idle
        } else {
            val rotation = if (playbackState.status == PlaybackState.Status.PLAYING) {
                (playbackState.currentPosition() / 1000f) * VINYL_DEGREES_PER_SECOND % 360f
            } else {
                playbackState.currentPosition() / 1000f * VINYL_DEGREES_PER_SECOND % 360f
            }

            val progress = if (track.duration > 0) {
                (playbackState.currentPosition().toFloat() / track.duration).coerceIn(0f, 1f)
            } else 0f

            val tArmAngle = com.vinyl.app.ui.nowplaying.components.tonearmAngle(progress)

            NowPlayingUiState.Playing(
                track = track,
                playbackState = playbackState,
                dominantColor = if (dominantColor == Color.Transparent) AccentAmber else dominantColor,
                vinylRotation = rotation,
                tonearmAngle = tArmAngle,
                controlsVisible = controlsVisible,
                theme = settings.theme,
                vinylStyle = settings.vinylStyle
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NowPlayingUiState.Idle
    )

    init {
        // Extract dominant color when track changes
        viewModelScope.launch {
            observeTrack().collect { track ->
                track?.albumArtBitmap?.let { extractDominantColor(it) }
            }
        }

        // Auto-hide controls timer
        viewModelScope.launch {
            observePlaybackState().collect { state ->
                if (state.status == PlaybackState.Status.PLAYING && _controlsVisible.value) {
                    startControlsTimer()
                }
            }
        }
    }

    private suspend fun extractDominantColor(bitmap: Bitmap) {
        withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()
            val swatch = palette.vibrantSwatch
                ?: palette.lightVibrantSwatch
                ?: palette.dominantSwatch
            val color = swatch?.rgb?.let { Color(it) }
                ?: AccentAmber
            _dominantColor.value = color
        }
    }

    private fun startControlsTimer() {
        controlsTimerJob?.cancel()
        controlsTimerJob = viewModelScope.launch {
            delay(3000)
            _controlsVisible.value = false
        }
    }

    fun onPlayPause() {
        val state = (uiState.value as? NowPlayingUiState.Playing)?.playbackState
        if (state?.status == PlaybackState.Status.PLAYING) {
            sendCommand.pause()
        } else {
            sendCommand.play()
        }
    }

    fun onNext() = sendCommand.next()
    fun onPrevious() = sendCommand.previous()

    fun onSeek(normalizedPosition: Float) {
        val track = (uiState.value as? NowPlayingUiState.Playing)?.track ?: return
        val positionMs = (normalizedPosition * track.duration).toLong()
        sendCommand.seek(positionMs)
    }

    fun onTouched() {
        _controlsVisible.value = true
        startControlsTimer()
    }
}

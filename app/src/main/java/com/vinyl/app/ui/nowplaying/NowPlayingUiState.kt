package com.vinyl.app.ui.nowplaying

import androidx.compose.ui.graphics.Color
import com.vinyl.app.domain.model.PlaybackState
import com.vinyl.app.domain.model.Track
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.domain.model.VinylTheme

sealed interface NowPlayingUiState {
    object Idle : NowPlayingUiState
    object PermissionRequired : NowPlayingUiState

    data class Playing(
        val track: Track,
        val playbackState: PlaybackState,
        val dominantColor: Color,
        val vinylRotation: Float,
        val tonearmAngle: Float,
        val controlsVisible: Boolean,
        val theme: VinylTheme,
        val vinylStyle: VinylStyle
    ) : NowPlayingUiState
}

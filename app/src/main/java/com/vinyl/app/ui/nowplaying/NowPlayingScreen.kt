package com.vinyl.app.ui.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinyl.app.ui.nowplaying.components.*
import com.vinyl.app.ui.theme.Background
import com.vinyl.app.ui.theme.OnSurfaceSubtle
import com.vinyl.app.ui.theme.VinylTypography

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { viewModel.onTouched() }
    ) {
        when (val state = uiState) {
            is NowPlayingUiState.Idle -> IdleContent()
            is NowPlayingUiState.PermissionRequired -> PermissionRequiredContent()
            is NowPlayingUiState.Playing -> PlayingContent(
                state = state,
                onPlayPause = viewModel::onPlayPause,
                onNext = viewModel::onNext,
                onPrevious = viewModel::onPrevious,
                onSeek = viewModel::onSeek
            )
        }
    }
}

@Composable
private fun IdleContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Open any music app",
                style = VinylTypography.bodyMedium,
                color = OnSurfaceSubtle,
                textAlign = TextAlign.Center
            )
            Text(
                text = "and start playing",
                style = VinylTypography.bodyMedium,
                color = OnSurfaceSubtle,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionRequiredContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Permission required",
            style = VinylTypography.bodyLarge,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlayingContent(
    state: NowPlayingUiState.Playing,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val discSize = minOf(screenWidth * 0.82f, 360.dp)
    val discCenterY = screenHeight * 0.30f

    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
        // Layer 1: Dynamic background
        DynamicBackground(
            albumArt = state.track.albumArtBitmap,
            dominantColor = state.dominantColor
        )

        // Layer 2: Main content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(screenHeight * 0.08f))

            // Vinyl disc + tonearm
            Box(
                modifier = Modifier
                    .size(discSize)
                    .offset(y = discCenterY / 2)
            ) {
                VinylDisc(
                    albumArt = state.track.albumArtBitmap,
                    rotation = state.vinylRotation,
                    discSize = discSize,
                    style = state.vinylStyle
                )
                Tonearm(
                    angle = state.tonearmAngle,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(Modifier.height(24.dp))

            // Track info
            TrackInfo(
                title = state.track.title,
                artist = state.track.artist,
                album = state.track.album,
                isPlaying = state.playbackState.status == com.vinyl.app.domain.model.PlaybackState.Status.PLAYING
            )

            Spacer(Modifier.height(16.dp))

            // Controls (animated visibility)
            AnimatedVisibility(
                visible = state.controlsVisible,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Time row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatDuration(state.playbackState.currentPosition()),
                            style = VinylTypography.labelSmall,
                            color = com.vinyl.app.ui.theme.OnSurfaceMuted
                        )
                        Text(
                            text = formatDuration(state.track.duration),
                            style = VinylTypography.labelSmall,
                            color = com.vinyl.app.ui.theme.OnSurfaceMuted
                        )
                    }

                    SeekBar(
                        position = if (state.track.duration > 0)
                            (state.playbackState.currentPosition().toFloat() / state.track.duration)
                                .coerceIn(0f, 1f)
                        else 0f,
                        onSeek = onSeek
                    )

                    Spacer(Modifier.height(24.dp))

                    PlaybackControls(
                        isPlaying = state.playbackState.status == com.vinyl.app.domain.model.PlaybackState.Status.PLAYING,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious
                    )
                }
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val totalSec = millis / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}

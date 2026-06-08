package com.vinyl.app.ui.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import com.vinyl.app.domain.model.Track
import com.vinyl.app.domain.model.VinylStyle
import com.vinyl.app.ui.nowplaying.components.*
import com.vinyl.app.ui.theme.Background
import com.vinyl.app.ui.theme.OnSurfaceMuted
import com.vinyl.app.ui.theme.OnSurfaceSubtle
import com.vinyl.app.ui.theme.VinylTypography
import kotlinx.coroutines.delay

@Composable
fun DemoPlayingScreen(
    onBack: () -> Unit = {}
) {
    val track = remember {
        Track(
            title = "Bohemian Rhapsody",
            artist = "Queen",
            album = "A Night at the Opera",
            albumArtBitmap = null,
            albumArtUri = null,
            duration = 354000,
            sourcePackage = "demo"
        )
    }

    var progress by remember { mutableFloatStateOf(0.3f) }

    LaunchedEffect(Unit) {
        while (true) {
            progress = ((progress + 0.002f).coerceAtMost(1f))
            if (progress >= 1f) progress = 0f
            delay(100)
        }
    }

    val rotation by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(100, easing = LinearEasing),
        label = "rotation"
    )

    val tonearmAngle = tonearmAngle(progress)

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val discSize = minOf(screenWidth * 0.82f, 360.dp)

    var controlsVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000)
        controlsVisible = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        DynamicBackground(
            albumArt = null,
            dominantColor = Color(0xFFFFB300)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(screenHeight * 0.08f))

            Text(
                text = "DEMO",
                style = VinylTypography.labelSmall,
                color = OnSurfaceMuted.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onBack)
                    .padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(discSize)
                    .offset(y = screenHeight * 0.30f / 2)
            ) {
                VinylDisc(
                    albumArt = null,
                    rotation = rotation,
                    discSize = discSize,
                    style = VinylStyle.CLASSIC_BLACK
                )
                Tonearm(
                    angle = tonearmAngle,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(Modifier.height(24.dp))

            TrackInfo(
                title = track.title,
                artist = track.artist,
                album = track.album,
                isPlaying = true
            )

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatDuration((progress * track.duration).toLong()),
                            style = VinylTypography.labelSmall,
                            color = OnSurfaceMuted
                        )
                        Text(
                            text = formatDuration(track.duration),
                            style = VinylTypography.labelSmall,
                            color = OnSurfaceMuted
                        )
                    }

                    SeekBar(
                        position = progress,
                        onSeek = { progress = it }
                    )

                    Spacer(Modifier.height(24.dp))

                    PlaybackControls(
                        isPlaying = true,
                        onPlayPause = { },
                        onNext = { progress = 0f },
                        onPrevious = { progress = 0f }
                    )
                }
            }
        }
    }
}

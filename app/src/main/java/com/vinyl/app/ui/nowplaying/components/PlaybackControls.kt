package com.vinyl.app.ui.nowplaying.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.OnSurface

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = OnSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            icon = Icons.Filled.SkipPrevious,
            contentDescription = "Previous track",
            size = 28.dp,
            touchSize = 56.dp,
            onClick = onPrevious,
            color = iconColor
        )

        Spacer(Modifier.width(16.dp))

        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayPause,
            color = iconColor
        )

        Spacer(Modifier.width(16.dp))

        ControlButton(
            icon = Icons.Filled.SkipNext,
            contentDescription = "Next track",
            size = 28.dp,
            touchSize = 56.dp,
            onClick = onNext,
            color = iconColor
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    contentDescription: String,
    size: androidx.compose.ui.unit.Dp,
    touchSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    color: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(touchSize)
            .scale(if (isPressed) 0.88f else 1f),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = color
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    color: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(72.dp)
            .scale(if (isPressed) 0.88f else 1f),
        interactionSource = interactionSource
    ) {
        AnimatedContent(
            targetState = isPlaying,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "play_pause"
        ) { playing ->
            Icon(
                imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (playing) "Pause" else "Play",
                modifier = Modifier.size(40.dp),
                tint = color
            )
        }
    }
}

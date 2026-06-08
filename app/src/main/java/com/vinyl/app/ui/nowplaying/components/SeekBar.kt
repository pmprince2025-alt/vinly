package com.vinyl.app.ui.nowplaying.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.AccentAmber

@Composable
fun SeekBar(
    position: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0x20FFFFFF),
    fillColor: Color = AccentAmber.copy(alpha = 0.8f),
    thumbColor: Color = Color.White
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(position) }

    val displayPosition = if (isDragging) dragPosition else position

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragPosition = (it.x / size.width).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        isDragging = false
                        onSeek(dragPosition)
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        dragPosition = (change.position.x / size.width).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
        ) {
            val trackHeight = 3.dp.toPx()
            val trackTop = size.height / 2 - trackHeight / 2
            val trackWidth = size.width
            val fillWidth = trackWidth * displayPosition

            // Track background
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, trackTop),
                size = androidx.compose.ui.geometry.Size(trackWidth, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2)
            )

            // Track fill
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(0f, trackTop),
                size = androidx.compose.ui.geometry.Size(fillWidth, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2)
            )

            // Thumb (only visible when dragging)
            if (isDragging) {
                val thumbRadius = 6.dp.toPx()
                drawCircle(
                    color = thumbColor,
                    radius = thumbRadius,
                    center = Offset(fillWidth, size.height / 2)
                )
            }
        }
    }
}

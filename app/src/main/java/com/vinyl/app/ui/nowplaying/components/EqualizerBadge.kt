package com.vinyl.app.ui.nowplaying.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.AccentAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EqualizerBadge(
    modifier: Modifier = Modifier,
    barCount: Int = 4,
    color: Color = AccentAmber.copy(alpha = 0.9f)
) {
    val heights = remember {
        List(barCount) {
            Animatable(8f)
        }
    }

    LaunchedEffect(Unit) {
        heights.forEachIndexed { index, animatable ->
            launch {
                while (isActive) {
                    val target = Random.nextFloat() * 14 + 2
                    val duration = Random.nextLong(200, 500)
                    animatable.animateTo(
                        targetValue = target,
                        animationSpec = tween(duration.toInt())
                    )
                    delay(Random.nextLong(100, 300))
                }
            }
        }
    }

    Row(
        modifier = modifier.height(16.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        heights.forEach { animatable ->
            Canvas(modifier = Modifier.width(3.dp).fillMaxHeight()) {
                val barHeight = animatable.value * 1.dp.toPx()
                drawRoundRect(
                    color = color,
                    topLeft = Offset(
                        0f,
                        size.height - barHeight
                    ),
                    size = Size(size.width, barHeight),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }
    }
}

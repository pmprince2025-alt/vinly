package com.vinyl.app.ui.idle

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.nowplaying.components.VinylDisc
import com.vinyl.app.ui.theme.Background
import com.vinyl.app.ui.theme.OnSurfaceMuted
import com.vinyl.app.ui.theme.OnSurfaceSubtle
import com.vinyl.app.ui.theme.VinylTypography

@Composable
fun IdleScreen(
    onDemoClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "idle")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(72000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "idle_rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_glow"
    )

    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp
    val discSize = minOf(screenWidth * 0.82f, 360.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(discSize)
                    .graphicsLayer { alpha = 0.5f }
            ) {
                VinylDisc(
                    albumArt = null,
                    rotation = rotation,
                    discSize = discSize
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension * 0.15f
                    drawCircle(
                        color = Color(0xFFFFB300).copy(alpha = glowAlpha * 0.5f),
                        radius = radius
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = glowAlpha * 0.2f),
                        radius = radius * 0.6f
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

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

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Demo",
                style = VinylTypography.bodySmall,
                color = OnSurfaceMuted.copy(alpha = 0.4f),
                modifier = Modifier
                    .clickable(onClick = onDemoClick)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

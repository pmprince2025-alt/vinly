package com.vinyl.app.ui.nowplaying.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.LocalVinylTheme

const val IDLE_ANGLE = -15f
const val PLAYING_START_ANGLE = 25f
const val PLAYING_END_ANGLE = 55f

@Composable
fun Tonearm(
    angle: Float,
    modifier: Modifier = Modifier
) {
    val theme = LocalVinylTheme.current

    Canvas(modifier = modifier.fillMaxSize()) {
        val discCenter = Offset(size.width / 2, size.height / 2)
        val discRadius = size.minDimension / 2
        val pivotX = discCenter.x + discRadius * 0.95f
        val pivotY = discCenter.y - discRadius * 0.85f
        val armLength = discRadius * 1.1f
        val armWidth = 6.dp.toPx()
        val cartridgeWidth = 14.dp.toPx()
        val cartridgeHeight = 10.dp.toPx()
        val counterweightRadius = 10.dp.toPx()
        val needleRadius = 3.dp.toPx()

        val pivot = Offset(pivotX, pivotY)

        // Shadow
        drawContext.canvas.save()
        drawContext.canvas.translate(2.dp.toPx(), 2.dp.toPx())
        rotate(angle, pivot) {
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.2f),
                topLeft = Offset(pivotX - armWidth / 2, pivotY),
                size = androidx.compose.ui.geometry.Size(armWidth, armLength),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(armWidth / 2)
            )
        }
        drawContext.canvas.restore()

        // Main arm
        rotate(angle, pivot) {
            drawRoundRect(
                color = theme.tonearmBody,
                topLeft = Offset(pivotX - armWidth / 2, pivotY),
                size = androidx.compose.ui.geometry.Size(armWidth, armLength),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(armWidth / 2)
            )

            drawRoundRect(
                color = theme.tonearmHighlight.copy(alpha = 0.4f),
                topLeft = Offset(pivotX - armWidth / 4, pivotY),
                size = androidx.compose.ui.geometry.Size(armWidth / 2, armLength),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(armWidth / 4)
            )

            drawCircle(
                color = theme.tonearmBody,
                radius = counterweightRadius,
                center = Offset(pivotX, pivotY)
            )

            val cartridgeX = pivotX - cartridgeWidth / 2
            val cartridgeY = pivotY + armLength - cartridgeHeight / 2
            drawRoundRect(
                color = theme.tonearmBody,
                topLeft = Offset(cartridgeX, cartridgeY),
                size = androidx.compose.ui.geometry.Size(cartridgeWidth, cartridgeHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            drawCircle(
                color = theme.tonearmHighlight,
                radius = needleRadius,
                center = Offset(pivotX, pivotY + armLength)
            )
        }
    }
}

fun tonearmAngle(progress: Float): Float {
    return PLAYING_START_ANGLE + (PLAYING_END_ANGLE - PLAYING_START_ANGLE) * progress.coerceIn(0f, 1f)
}

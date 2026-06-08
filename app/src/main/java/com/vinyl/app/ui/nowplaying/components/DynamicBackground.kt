package com.vinyl.app.ui.nowplaying.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.vinyl.app.ui.theme.Background
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DynamicBackground(
    albumArt: Bitmap?,
    dominantColor: Color,
    modifier: Modifier = Modifier
) {
    var blurredBitmap by remember(albumArt) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(albumArt) {
        blurredBitmap = albumArt?.let { blurBitmap(it) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = blurredBitmap,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
            label = "bg_transition"
        ) { bitmap ->
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.6f }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Background))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background.copy(alpha = 0.62f))
        )

        if (dominantColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dominantColor.copy(alpha = 0.1f))
            )
        }
    }
}

private suspend fun blurBitmap(source: Bitmap): Bitmap {
    return withContext(Dispatchers.Default) {
        val width = 80
        val height = (source.height.toFloat() / source.width * width).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(source, width, height, true)
        stackBlur(scaled, 40)
    }
}

private fun stackBlur(source: Bitmap, radius: Int): Bitmap {
    val w = source.width
    val h = source.height
    val result = Bitmap.createBitmap(source)
    val pixels = IntArray(w * h)
    result.getPixels(pixels, 0, w, 0, 0, w, h)

    val r = radius.coerceIn(1, 200)
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = r + r + 1
    val r1 = r + 1

    val divSum = IntArray(div) { it + r1 }
    for (i in 1 until div) divSum[i] += divSum[i - 1]

    val rValues = IntArray(wh)
    val gValues = IntArray(wh)
    val bValues = IntArray(wh)
    val vMin = IntArray(maxOf(w, h))

    var index = 0
    for (y in 0 until h) {
        var sumR = 0
        var sumG = 0
        var sumB = 0
        for (i in -r..r) {
            val pixel = pixels[(y * w + (i.coerceIn(0, wm)))]
            val ri = (pixel shr 16) and 0xFF
            val gi = (pixel shr 8) and 0xFF
            val bi = pixel and 0xFF
            val ds = divSum[i + r]
            sumR += ri * ds
            sumG += gi * ds
            sumB += bi * ds
        }
        for (x in 0 until w) {
            rValues[index] = sumR / divSum[r]
            gValues[index] = sumG / divSum[r]
            bValues[index] = sumB / divSum[r]

            if (y == 0) {
                vMin[x] = minOf(x + r + 1, wm)
            }
            val p1 = pixels[(y * w + vMin[x])]
            val p2 = pixels[(y * w + maxOf(x - r, 0))]
            val ri1 = (p1 shr 16) and 0xFF
            val gi1 = (p1 shr 8) and 0xFF
            val bi1 = p1 and 0xFF
            val ri2 = (p2 shr 16) and 0xFF
            val gi2 = (p2 shr 8) and 0xFF
            val bi2 = p2 and 0xFF
            sumR += ri1 - ri2
            sumG += gi1 - gi2
            sumB += bi1 - bi2
            index++
        }
    }

    index = 0
    for (x in 0 until w) {
        var sumR = 0
        var sumG = 0
        var sumB = 0
        for (i in -r..r) {
            val yi = (i.coerceIn(0, hm))
            val si = yi * w + x
            sumR += rValues[si]
            sumG += gValues[si]
            sumB += bValues[si]
        }
        for (y in 0 until h) {
            result.setPixel(x, y,
                (0xFF shl 24) or
                        ((sumR / divSum[r]).coerceIn(0, 255) shl 16) or
                        ((sumG / divSum[r]).coerceIn(0, 255) shl 8) or
                        (sumB / divSum[r]).coerceIn(0, 255)
            )

            if (x == 0) {
                vMin[y] = minOf(y + r1, hm)
            }
            val p1 = y * w + vMin[y]
            val p2 = y * w + maxOf(y - r, 0)
            sumR += rValues[p1] - rValues[p2]
            sumG += gValues[p1] - gValues[p2]
            sumB += bValues[p1] - bValues[p2]
            index++
        }
    }

    return result
}

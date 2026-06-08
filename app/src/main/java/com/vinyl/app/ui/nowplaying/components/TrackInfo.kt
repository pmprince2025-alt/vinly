package com.vinyl.app.ui.nowplaying.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.LocalVinylTheme
import com.vinyl.app.ui.theme.VinylTypography

@Composable
fun TrackInfo(
    title: String,
    artist: String,
    album: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = LocalVinylTheme.current

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = title,
            transitionSpec = {
                (slideInVertically { it / 4 } + fadeIn(tween(250))) togetherWith
                        fadeOut(tween(150))
            },
            label = "title_transition"
        ) { currentTitle ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentTitle,
                    style = VinylTypography.headlineLarge,
                    color = theme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
                if (isPlaying) {
                    Spacer(Modifier.width(8.dp))
                    EqualizerBadge()
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        AnimatedContent(
            targetState = artist,
            transitionSpec = {
                (slideInVertically { it / 4 } + fadeIn(tween(250))) togetherWith
                        fadeOut(tween(150))
            },
            label = "artist_transition"
        ) { currentArtist ->
            Text(
                text = currentArtist,
                style = VinylTypography.titleMedium,
                color = theme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(2.dp))

        AnimatedContent(
            targetState = album,
            transitionSpec = {
                (slideInVertically { it / 4 } + fadeIn(tween(250))) togetherWith
                        fadeOut(tween(150))
            },
            label = "album_transition"
        ) { currentAlbum ->
            Text(
                text = currentAlbum,
                style = VinylTypography.titleSmall,
                color = theme.textSecondary.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

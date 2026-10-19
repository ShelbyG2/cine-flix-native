package tech.unrealistic.cineflix.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Media3-based player surface that provides native playback controls.
 *
 * This replaces the WebView-based player when a stream URL is available.
 */
@Composable
fun MediaController(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    PlayerView(
        player = player,
        modifier = modifier
    )
}

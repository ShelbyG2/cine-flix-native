package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import tech.unrealistic.cineflix.data.remote.VideoStreamResolver

@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    mediaId: Int,
    mediaType: String,
    context: Context = LocalContext.current
) {
    var playerState by remember { mutableStateOf<ExoPlayer?>(null) }
    var resolutionError by remember { mutableStateOf<String?>(null) }
    var isResolvingStreams by remember { mutableStateOf(true) }
    LaunchedEffect(mediaId) {
        val resolver = VideoStreamResolver(context)
        val targetedEmbedGateWay = "https://vidsrc.me/embed/$mediaType/$mediaId"

        resolver.resolvedEmbedUrl(
            embedUrl = targetedEmbedGateWay,
            listener = object : VideoStreamResolver.StreamResolutionListener {
                override fun onStreamResolved(streamUrl: String, referrer: String) {
                    playerState = createCustomStreamingPlayer(context, streamUrl, referrer)
                    isResolvingStreams = false
                }

                override fun onError(message: String) {
                    resolutionError = message
                    isResolvingStreams = false
                }
            })
    }
    //cleanup player component out of device memory when user closes the player screen
    DisposableEffect(Unit) {
        onDispose {
            playerState?.release()
        }
    }
    // Render your conditional layout frames cleanly
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        when {
            isResolvingStreams -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
                Text(
                    text = "Bypassing server protections...",
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 64.dp)
                )
            }

            resolutionError != null -> {
                Text(
                    text = "Failed to resolve sources: $resolutionError",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            playerState != null -> {
                // Paint your native Media3 AndroidView player container frame here
                // Customize it with custom overlays, volume gestures, or playback controllers
            }
        }
    }
}
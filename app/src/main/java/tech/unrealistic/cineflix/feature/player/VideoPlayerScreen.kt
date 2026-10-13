package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer
import tech.unrealistic.cineflix.data.remote.VideoStreamResolver
import tech.unrealistic.cineflix.ui.theme.AppTheme

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
    // Retry attempt counter — re-runs the same resolution effect on demand
    var resolveAttempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(mediaId, resolveAttempt) {
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
    // Player surface stays black in BOTH modes (video letterboxing)
    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.playerBackground)) {
        when {
            isResolvingStreams -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Bypassing server protections...",
                    style = MaterialTheme.typography.bodyMedium,
                    // on-image roles: legible on the black player surface in BOTH modes
                    color = AppTheme.colors.onImageMuted,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = AppTheme.spacing.xl)
                )
            }

            resolutionError != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(AppTheme.spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                ) {
                    Text(
                        text = "Failed to resolve sources: $resolutionError",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppTheme.colors.onImage
                    )
                    Button(
                        onClick = {
                            resolutionError = null
                            isResolvingStreams = true
                            resolveAttempt++
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier.defaultMinSize(
                            minHeight = AppTheme.sizes.touchTarget
                        )
                    ) {
                        Text(
                            text = "Try Again",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            playerState != null -> {
                // Paint your native Media3 AndroidView player container frame here
                // Customize it with custom overlays, volume gestures, or playback controllers
            }
        }
    }
}

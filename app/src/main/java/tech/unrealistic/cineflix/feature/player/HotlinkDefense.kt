package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient
import java.net.URI

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

/**
 * Builds an [ExoPlayer] that fetches [streamUrl] with the headers the hoster
 * checks: `Referer` is the embed page the stream was discovered on and `Origin`
 * is that page's site root (a full URL is not a valid Origin).
 */
@OptIn(UnstableApi::class)
fun createStreamingPlayer(
    context: Context,
    streamUrl: String,
    referrer: String,
): ExoPlayer {
    val headers = buildMap {
        put("Referer", referrer)
        referrer.siteRoot()?.let { put("Origin", it) }
    }
    val dataSourceFactory = OkHttpDataSource.Factory(OkHttpClient.Builder().build())
        .setUserAgent(DESKTOP_USER_AGENT)
        .setDefaultRequestProperties(headers)

    return ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
        .build()
        .apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            playWhenReady = true
            prepare()
        }
}

private fun String.siteRoot(): String? = runCatching {
    val uri = URI(this)
    if (uri.scheme == null || uri.host == null) null else "${uri.scheme}://${uri.host}"
}.getOrNull()

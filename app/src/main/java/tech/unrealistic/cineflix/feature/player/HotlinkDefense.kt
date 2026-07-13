package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient


@OptIn(UnstableApi::class)
fun createCustomStreamingPlayer(
    context: Context,
    extractedStreamUrl: String,
    originReferer: String
): ExoPlayer {
    //the network layer
    val okHttpClient = OkHttpClient.Builder().build()

    //a customised http dataSource factory that that spoofs hosters security check
    val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
        .setDefaultRequestProperties(
            mapOf(
                "Referer" to originReferer,
                "Origin" to originReferer.substringBefore("/eembed")
            )
        )
    //create an ExoPlayer instance linked natively to our safe proxy data injector
    val player = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
        .build()

    //mount the stream media cleanly
    val mediaItem = MediaItem.fromUri(extractedStreamUrl)
    player.setMediaItem(mediaItem)
    player.prepare()
    return player
}
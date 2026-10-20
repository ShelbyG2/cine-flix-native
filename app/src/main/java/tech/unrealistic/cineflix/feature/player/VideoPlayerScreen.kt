package tech.unrealistic.cineflix.feature.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlin.coroutines.resume
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import tech.unrealistic.cineflix.core.components.ErrorState
import tech.unrealistic.cineflix.data.remote.VideoStreamResolver
import tech.unrealistic.cineflix.data.remote.embedUrl
import tech.unrealistic.cineflix.data.remote.streamProviders
import tech.unrealistic.cineflix.feature.player.components.PlayerControlsLayer
import tech.unrealistic.cineflix.feature.player.components.PlayerLoadingLayer
import tech.unrealistic.cineflix.ui.theme.AppTheme

/** Controls auto-hide delay (§3). */
private const val ControlsAutoHideMs = 3_000L

/**
 * Full-screen playback for a TMDB title.
 *
 * The screen asks each provider embed page for a playable manifest and plays
 * the first one it finds with Media3 (native ExoPlayer, proper controls,
 * seeking). Only when no provider exposes a manifest does it fall back to
 * hosting the embed page itself in a WebView.
 *
 * `PlayerView` runs with `useController = false`: every piece of chrome —
 * loading state, controls, track selection — is a Compose layer drawn on top so
 * it can use `AppTheme` tokens and Compose animations (§0 of the player
 * customisation instructions).
 *
 * Playback always runs in landscape with the system bars hidden; both are
 * restored when the screen leaves composition.
 */
@Composable
fun VideoPlayerScreen(
    tmdbId: Int,
    mediaType: String,                  // "movie" or "tv"
    mediaTitle: String,                 // movie title or show name
    backdropPath: String?,              // TMDB backdrop for the loading screen
    season: Int? = null,
    episode: Int? = null,
    episodeTitle: String? = null,       // e.g. "Pilot"
    episodeIndex: Int? = null,          // 0-based position in the current season list
    totalEpisodesInSeason: Int? = null, // total count for next/prev logic
    onNext: (() -> Unit)? = null,
    onPrevious: (() -> Unit)? = null,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playerBackground = AppTheme.colors.playerBackground

    // Provider order lives in one place; the first playable manifest wins.
    val embedUrls = remember(tmdbId, mediaType, season, episode) {
        streamProviders.map { provider ->
            provider.embedUrl(
                tmdbId = tmdbId,
                mediaType = mediaType,
                season = season,
                episode = episode,
            )
        }
    }
    val fallbackEmbedUrl = embedUrls.first()

    val playerViewModel: PlayerViewModel = viewModel()
    val uiState by playerViewModel.uiState.collectAsState()

    var reloadKey by remember { mutableIntStateOf(0) }
    var progress by remember { mutableIntStateOf(0) }
    var loadFailed by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }
    var webViewFallback by remember { mutableStateOf(false) }

    // ExoPlayer state surfaced for the Compose layers (§5).
    var playerState by remember { mutableIntStateOf(Player.STATE_IDLE) }
    var bufferedPercent by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }

    // Controls show/hide: any tap reveals them, 3 seconds of no interaction
    // hides them again (§3).
    var controlsVisible by remember { mutableStateOf(false) }
    val controlsScope = rememberCoroutineScope()
    var hideJob by remember { mutableStateOf<Job?>(null) }
    fun showControls() {
        controlsVisible = true
        hideJob?.cancel()
        hideJob = controlsScope.launch {
            delay(ControlsAutoHideMs)
            controlsVisible = false
        }
    }

    // WebView callbacks outlive composition, so they must not capture a stale lambda.
    val currentOnExit by rememberUpdatedState(onExit)

    ForceLandscapePlayback()
    BackHandler { currentOnExit() }

    LaunchedEffect(embedUrls, reloadKey) {
        playerViewModel.preparePlayer(embedUrls) { url ->
            resolveStreamUrl(context, url)
        }
    }

    val resolved = (uiState as? PlayerUiState.Ready)?.stream
    val exoPlayer = remember(resolved) {
        resolved?.let {
            createStreamingPlayer(
                context = context.applicationContext,
                streamUrl = it.url,
                referrer = it.referrer,
            )
        }
    }
    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer?.release() }
    }

    // Player state wiring — buffering drives the loading layer (§5).
    DisposableEffect(exoPlayer) {
        val player = exoPlayer ?: return@DisposableEffect onDispose { }
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                playerState = state
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    // Position/duration polling runs only while actually playing (§3.1).
    LaunchedEffect(exoPlayer, playerState, isPlaying) {
        if (exoPlayer != null && playerState == Player.STATE_READY && isPlaying) {
            while (true) {
                currentPositionMs = exoPlayer.currentPosition
                exoPlayer.duration.takeIf { it > 0 }?.let { durationMs = it }
                delay(500)
            }
        }
    }

    // Seed the scrubber from the player as soon as the controls appear, so a
    // paused video still shows where it stopped.
    LaunchedEffect(controlsVisible, exoPlayer) {
        if (controlsVisible && exoPlayer != null) {
            currentPositionMs = exoPlayer.currentPosition
            exoPlayer.duration.takeIf { it > 0 }?.let { durationMs = it }
        }
    }

    // Real buffered percentage, polled every 500 ms (§2.4).
    LaunchedEffect(exoPlayer) {
        while (true) {
            bufferedPercent = exoPlayer?.bufferedPercentage ?: 0
            delay(500)
        }
    }

    val displayTitle = when {
        mediaType == "movie" -> mediaTitle
        episode != null && episodeTitle != null -> "S${season}E${episode} · $episodeTitle"
        episode != null -> "S${season}E${episode}"
        else -> mediaTitle
    }

    // Next / previous visibility (§3.2). Movies never show them; a season's
    // first episode has no previous and its last has no next. A hidden button
    // also means the caller gave us no callback, so the control never exists
    // without an action behind it.
    val isMovie = mediaType == "movie"
    val showPrev = !isMovie && episodeIndex != null && episodeIndex > 0 && onPrevious != null
    val showNext = !isMovie && episodeIndex != null && totalEpisodesInSeason != null &&
        episodeIndex < totalEpisodesInSeason - 1 && onNext != null

    // Loading: the stream has not been handed to ExoPlayer yet, or ExoPlayer is
    // buffering it. A WebView fallback owns its own loading state instead.
    val showLoadingLayer = when {
        webViewFallback -> false
        exoPlayer != null ->
            playerState == Player.STATE_BUFFERING || playerState == Player.STATE_IDLE
        uiState is PlayerUiState.NoStream -> false
        else -> true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(playerBackground)
    ) {
        when {
            exoPlayer != null -> AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "Video player" },
                factory = { viewContext ->
                    createPlayerView(viewContext, exoPlayer, playerBackground.toArgb())
                },
                update = { view -> view.player = exoPlayer },
                onRelease = { view -> view.player = null },
            )

            webViewFallback || uiState is PlayerUiState.NoStream -> {
                // Re-keying rebuilds the WebView from scratch, which is what a
                // retry needs after a failed page load.
                key(reloadKey) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { contentDescription = "Video player" },
                        factory = { viewContext ->
                            createPlayerWebView(
                                context = viewContext,
                                embedUrl = fallbackEmbedUrl,
                                backgroundColor = playerBackground.toArgb(),
                                onProgress = { newProgress ->
                                    progress = newProgress
                                    if (newProgress >= 100) {
                                        hasLoaded = true
                                        loadFailed = false
                                    }
                                },
                                onPageFailed = {
                                    loadFailed = true
                                    hasLoaded = true
                                }
                            )
                        },
                        onRelease = { view ->
                            view.stopLoading()
                            view.destroy()
                        }
                    )
                }
            }

            else -> Unit
        }

        // Custom loading state (backdrop + wavy bars + buffered %) replaces any
        // spinner — there is no CircularProgressIndicator on this screen.
        if (showLoadingLayer) {
            PlayerLoadingLayer(
                displayTitle = displayTitle,
                bufferedPercent = bufferedPercent,
                backdropPath = backdropPath,
            )
        }

        if ((webViewFallback || uiState is PlayerUiState.NoStream) &&
            !hasLoaded && !loadFailed
        ) {
            PlayerLoadingLayer(
                displayTitle = displayTitle,
                bufferedPercent = bufferedPercent,
                backdropPath = backdropPath,
            )
        }

        // Tap anywhere to reveal the controls; they fade out after 3s (§3).
        // Sits above the video and loading layers but below the error state, so
        // its Retry button stays tappable.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showControls() }
        ) {
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                PlayerControlsLayer(
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTogglePlay = { exoPlayer?.let { if (it.isPlaying) it.pause() else it.play() } },
                    onPrevious = { onPrevious?.invoke() },
                    onNext = { onNext?.invoke() },
                    onQuality = { /* §4 */ },
                    onSubtitles = { /* §4 */ },
                    onAudio = { /* §4 */ },
                    onSeek = { position ->
                        currentPositionMs = position
                        exoPlayer?.seekTo(position)
                    },
                    onExit = { currentOnExit() },
                    onInteract = { showControls() },
                    showPrev = showPrev,
                    showNext = showNext,
                )
            }
        }

        if ((webViewFallback || uiState is PlayerUiState.NoStream) && loadFailed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(playerBackground),
                contentAlignment = Alignment.Center
            ) {
                ErrorState(
                    title = "Couldn't start playback",
                    message = "The stream didn't load. Check your connection and try again.",
                    onRetry = {
                        loadFailed = false
                        hasLoaded = false
                        progress = 0
                        webViewFallback = false
                        reloadKey++
                    }
                )
            }
        }

        // Exit affordance while nothing else is on screen — the controls layer
        // carries its own close button once it is composed (§3).
        if ((showLoadingLayer || loadFailed) && !controlsVisible) {
            FilledIconButton(
                onClick = { currentOnExit() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(AppTheme.spacing.md)
                    .size(AppTheme.sizes.touchTarget),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AppTheme.colors.scrimStrong,
                    contentColor = AppTheme.colors.onImage
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close player",
                    modifier = Modifier.size(AppTheme.sizes.iconMd)
                )
            }
        }
    }
}

/**
 * `PlayerView` with its own controller disabled — every control is drawn by the
 * Compose layers above it (§0).
 */
private fun createPlayerView(
    context: Context,
    player: ExoPlayer,
    backgroundColor: Int,
): PlayerView {
    val view = PlayerView(context)
    view.useController = false
    view.setBackgroundColor(backgroundColor)
    view.player = player
    return view
}

/**
 * Resolves a playable manifest URL from the embed page via an off-screen
 * WebView. Cancelling the coroutine (timeout or leaving the screen) tears the
 * headless WebView down.
 */
@SuppressLint("SetJavaScriptEnabled")
private suspend fun resolveStreamUrl(context: Context, embedUrl: String): ResolvedStream? =
    suspendCancellableCoroutine { continuation ->
        val resolver = VideoStreamResolver(context)
        val headless = resolver.resolvedEmbedUrl(
            embedUrl,
            object : VideoStreamResolver.StreamResolutionListener {
                override fun onStreamResolved(streamUrl: String, referrer: String) {
                    if (continuation.isActive) continuation.resume(ResolvedStream(streamUrl, referrer))
                }

                override fun onError(message: String) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        )
        continuation.invokeOnCancellation {
            Log.d("VideoPlayerScreen", "stream lookup cancelled")
            headless.destroy()
        }
    }

/**
 * Locks the hosting activity to landscape (both sensor directions) and hides
 * the system bars while the player is composed. Both are restored on dispose,
 * which returns the activity to its portrait manifest orientation.
 */
@Composable
private fun ForceLandscapePlayback() {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    DisposableEffect(activity) {
        if (activity == null) return@DisposableEffect onDispose { }

        val previousOrientation = activity.requestedOrientation
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            // "Unspecified" hands the decision back to the sensor, and with
            // rotation lock on the system keeps the landscape we just forced.
            // The app itself is portrait, so restore that explicitly.
            activity.requestedOrientation =
                if (previousOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    previousOrientation
                }
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createPlayerWebView(
    context: Context,
    embedUrl: String,
    backgroundColor: Int,
    onProgress: (Int) -> Unit,
    onPageFailed: () -> Unit,
): WebView = WebView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    setBackgroundColor(backgroundColor)

    settings.apply {
        javaScriptEnabled = true       // the embed player is a JS app
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        loadWithOverviewMode = true
        useWideViewPort = true
        setSupportZoom(false)
        allowFileAccess = false
        allowContentAccess = false
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }

    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            onProgress(newProgress)
        }
    }

    webViewClient = object : WebViewClient() {
        // Providers move domains (vidsrc.me -> vidsrc.sh): follow one hop, then
        // pin the player to that origin. Ads and "open in browser" links are
        // swallowed instead of navigating away from the player.
        var allowedHost: String? = runCatching { java.net.URI(embedUrl).host }.getOrNull()
        var followedRedirect = false

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            if (!request.isForMainFrame) return false
            val url = request.url
            if (url.scheme != "https") return true

            val host = url.host ?: return true
            if (host == allowedHost) return false
            if (!followedRedirect) {
                followedRedirect = true
                allowedHost = host
                return false
            }
            return true
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            if (request?.isForMainFrame == true) onPageFailed()
        }
    }

    loadUrl(embedUrl)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

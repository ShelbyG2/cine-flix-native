package tech.unrealistic.cineflix.feature.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
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
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.unrealistic.cineflix.core.components.ErrorState
import tech.unrealistic.cineflix.data.remote.EmbedWave
import tech.unrealistic.cineflix.feature.player.MediaController
import tech.unrealistic.cineflix.feature.player.PlayerViewModel
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * Full-screen playback for a TMDB title through the EmbedWave embed player.
 *
 * The provider ships a ready-made iframe player, so the app hosts it in a
 * WebView instead of scraping its (obfuscated, ad-gated) stream URLs — that is
 * the integration the EmbedWave docs describe and the only one that survives
 * their player changes.
 *
 * The screen always plays in landscape and hides the system bars for the
 * duration of playback, restoring both when it leaves composition.
 */
@Composable
fun VideoPlayerScreen(
    tmdbId: Int,
    mediaType: String,
    modifier: Modifier = Modifier,
    season: Int? = null,
    episode: Int? = null,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val playerBackground = AppTheme.colors.playerBackground

    val embedUrl = remember(tmdbId, mediaType, season, episode) {
        EmbedWave.embedUrl(
            tmdbId = tmdbId,
            mediaType = mediaType,
            season = season,
            episode = episode,
        )
    }

    var progress by remember { mutableIntStateOf(0) }
    var loadFailed by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableIntStateOf(0) }


    // Listen for stream URL resolution from the ViewModel
    val playerViewModel: PlayerViewModel = viewModel()
    LaunchedEffect(Unit) {
        playerViewModel.uiState.collect { state ->
            when (state) {
                is PlayerUiState.Ready -> {
                    // Stream URL resolved - Media3 will handle playback
                    hasLoaded = true
                }
                is PlayerUiState.Error -> {
                    loadFailed = true
                    hasLoaded = true
                }
                is PlayerUiState.NoStream -> {
                    // Fall back to embedded WebView
                    hasLoaded = true
                }
                else -> { /* Loading */ }
            }
        }
    }

    // Keep the current onExit reference for the WebView callbacks (they are
    // created once and must not capture a stale lambda).
    val currentOnExit by rememberUpdatedState(onExit)

    // Player ViewModel that resolves a stream URL, falls back to the embedded
    // WebView when none is available, and drives Media3 playback when possible.

    LaunchedEffect(tmdbId, mediaType, season, episode) {
        playerViewModel.preparePlayer(
            tmdbId = tmdbId,
            mediaType = mediaType,
            season = season,
            episode = episode,
            onFail = {
                loadFailed = true
                hasLoaded = true
            }
        )
    }        // Subscribe to player state changes
    val player by playerViewModel.player.collectAsStateWithLifecycle()
        val isPlaying by playerViewModel.playbackState.collectAsStateWithLifecycle()

        ForceLandscapePlayback()

        DisposableEffect(Unit) {
            onDispose {
                playerViewModel.releasePlayer()
            }
        }


    DisposableEffect(Unit) {
        onDispose {
            playerViewModel.releasePlayer()
        }
    }

    BackHandler(onBack = { currentOnExit() })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(playerBackground)
    ) {
        // Re-keying rebuilds the WebView from scratch, which is what a retry
        // needs after a failed page load.
        key(reloadKey) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "Video player" },
                factory = { viewContext ->
                    createPlayerWebView(
                        context = viewContext,
                        embedUrl = embedUrl,
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

        if (!hasLoaded && !loadFailed) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(AppTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.sizes.spinner),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Starting playback…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onImageMuted
                )
            }
        }

        if (loadFailed) {
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
                        reloadKey++
                    }
                )
            }
        }

        // Visible close affordance — the player chrome lives inside the
        // WebView, so the app owns the way back out.
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

        // Keep the loading progress visible for slow networks until playback begins.
        if (!hasLoaded && !loadFailed && progress in 1..99) {
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.onImageMuted,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppTheme.spacing.md)
            )
        }

        if (isPlaying && player != null) {
            MediaController(
                player = player!!,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Locks the hosting activity to landscape (both sensor directions) and hides
 * the system bars while the player is composed. Everything is restored on
 * dispose so the rest of the app stays portrait-aware.
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
            activity.requestedOrientation = previousOrientation
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
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            // The player must stay on the embed origin. Anything else (ad
            // redirects, "open in browser" links) is swallowed rather than
            // navigated to.
            val url = request.url
            val isEmbedOrigin = url.host == "embedwave.cc" ||
                    url.host?.endsWith(".embedwave.cc") == true
            return request.isForMainFrame && (url.scheme != "https" || !isEmbedOrigin)
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

/**
 * Extracts a playable stream URL from the EmbedWave embed page.
 *
 * This is a placeholder that currently returns null, meaning the embedded
 * WebView will be used for playback. In the future, this could be implemented
 * by intercepting the media requests from the WebView and parsing the HLS/DASH
 * manifest URLs.
 */
private fun extractStreamUrl(
    embedUrl: String,
    tmdbId: Int,
    mediaType: String,
    season: Int?,
    episode: Int?
): String? {
    // TODO: Implement stream URL extraction from the embed page
    return null
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

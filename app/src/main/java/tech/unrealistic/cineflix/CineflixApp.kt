package tech.unrealistic.cineflix

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import tech.unrealistic.cineflix.core.components.CineflixBottomBar
import tech.unrealistic.cineflix.core.navigation.LocalBottomBarInset
import tech.unrealistic.cineflix.core.preferences.OnboardingPreferences
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv
import tech.unrealistic.cineflix.feature.discover.DiscoverScreen
import tech.unrealistic.cineflix.feature.favorites.FavouriteScreen
import tech.unrealistic.cineflix.feature.home.HomeScreen
import tech.unrealistic.cineflix.feature.home.ProfileScreen
import tech.unrealistic.cineflix.feature.onboarding.OnboardingFlow
import tech.unrealistic.cineflix.feature.player.VideoPlayerScreen
import tech.unrealistic.cineflix.feature.splash.SplashScreen
import tech.unrealistic.cineflix.ui.theme.AppTheme
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.rememberHazeState


/**
 * Top-level app stages:
 *  Splash (2.8s) → Onboarding (first launch only) → Main app
 */
enum class AppStage { SPLASH, ONBOARDING, MAIN }


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CineflixApp(

) {
    val context = LocalContext.current

    // Splash always plays; onboarding shows only on the very first launch.
    var appStage by remember { mutableStateOf(AppStage.SPLASH) }

    Crossfade(
        targetState = appStage,
        animationSpec = AppTheme.motion.spec(AppTheme.motion.mediumMs),
        label = "app_stage"
    ) { stage ->
        when (stage) {
            AppStage.SPLASH -> SplashScreen(
                onFinished = {
                    appStage = if (OnboardingPreferences.hasCompletedOnboarding(context)) {
                        AppStage.MAIN
                    } else {
                        AppStage.ONBOARDING
                    }
                }
            )

            AppStage.ONBOARDING -> OnboardingFlow(
                onFinished = {
                    OnboardingPreferences.markOnboardingComplete(context)
                    appStage = AppStage.MAIN
                }
            )

            AppStage.MAIN -> MainAppContent()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainAppContent() {
    //Home is our default tab
    val backStack = remember { mutableStateListOf<Any>(Home) }
    // if the current tab in the stack  is null revert to default tab
    val currentRoot = backStack.firstOrNull{it !is Player && it!is MediaDetailModal}?: Home
    //Check the tab we are currently on
    val currentTab = AppDestinations.entries.find {it.route == currentRoot}?: AppDestinations.HOME

    val  mediaRepository = RetrofitClient.tmdbService

    // Playback is an overlay rather than a nav destination: the library tab
    // and its scroll position stay alive underneath the landscape player.
    var playback by remember { mutableStateOf<Player?>(null) }
    val playItem: (MediaItem) -> Unit = { item ->
        val title = when (item) {
            is Movie -> item.title
            is Tv -> item.name
            else -> null
        }
        playback = if (item.mediaType == "tv") {
            Player(
                id = item.id,
                type = "tv",
                season = 1,
                episode = 1,
                title = title,
                backdropPath = item.backdropPath,
            )
        } else {
            Player(
                id = item.id,
                type = "movie",
                title = title,
                backdropPath = item.backdropPath,
            )
        }
    }
    val playEpisode: (EpisodePlayRequest) -> Unit = { request ->
        playback = Player(
            id = request.seriesId,
            type = "tv",
            season = request.season,
            episode = request.episode,
            title = request.seriesTitle,
            backdropPath = request.backdropPath,
            episodeTitle = request.episodeTitle,
            episodeIndex = request.episodeIndex,
            totalEpisodesInSeason = request.totalEpisodesInSeason,
        )
    }

    // One Haze source for the whole nav surface: both glass bars sample it.
    val hazeState = rememberHazeState()
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarHeight = AppTheme.sizes.navBarHeight + navBarInset

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        CompositionLocalProvider(LocalBottomBarInset provides bottomBarHeight) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                // Content draws behind the translucent bar (edge-to-edge).
                modifier = Modifier
                    .fillMaxSize()
                    .haze(hazeState),
                entryProvider = { key ->
                    when (key) {
                        is Home -> NavEntry(key) {
                            HomeScreen(
                                onNavigate = {
                                    backStack.clear()
                                    backStack.add(Discover)
                                },
                                onPlay = playItem,
                                onPlayEpisode = playEpisode
                            )
                        }

                        is Favourites -> NavEntry(key) {
                            FavouriteScreen(
                                onNavigate = {
                                    backStack.clear()
                                    backStack.add(Discover)
                                },
                                onPlay = playItem,
                                onPlayEpisode = playEpisode
                            )
                        }

                        is Discover -> NavEntry(key) {
                            DiscoverScreen(
                                onNavigate = {
                                    backStack.clear()
                                    backStack.add(Home)
                                },
                                onPlay = playItem,
                                onPlayEpisode = playEpisode
                            )
                        }

                        is Profile -> NavEntry(key) { ProfileScreen() }

                        else -> NavEntry(key) { Text("Not found") }
                    }
                }
            )

            CineflixBottomBar(
                selected = currentTab,
                onSelect = { destination ->
                    backStack.clear()
                    backStack.add(destination.route)
                },
                hazeState = hazeState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        playback?.let { request ->
            // Next / previous are derived here — the graph owns the request —
            // so the screen never needs to know about episode lists.
            val isTv = request.type == "tv"
            val currentEpisode = request.episode
            val nextTarget = if (isTv && currentEpisode != null) {
                { playback = request.copy(episode = currentEpisode + 1, episodeTitle = null, episodeIndex = request.episodeIndex?.plus(1)) }
            } else {
                null
            }
            val previousTarget = if (isTv && currentEpisode != null && currentEpisode > 1) {
                { playback = request.copy(episode = currentEpisode - 1, episodeTitle = null, episodeIndex = request.episodeIndex?.minus(1)) }
            } else {
                null
            }

            VideoPlayerScreen(
                tmdbId = request.id,
                mediaType = request.type,
                mediaTitle = request.title.orEmpty(),
                backdropPath = request.backdropPath,
                season = request.season,
                episode = request.episode,
                episodeTitle = request.episodeTitle,
                episodeIndex = request.episodeIndex,
                totalEpisodesInSeason = request.totalEpisodesInSeason,
                onNext = nextTarget,
                onPrevious = previousTarget,
                onExit = { playback = null }
            )
        }
    }
}

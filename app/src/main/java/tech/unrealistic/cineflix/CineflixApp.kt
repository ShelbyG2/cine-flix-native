package tech.unrealistic.cineflix

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import tech.unrealistic.cineflix.core.preferences.OnboardingPreferences
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.feature.discover.DiscoverScreen
import tech.unrealistic.cineflix.feature.home.FavouriteScreen
import tech.unrealistic.cineflix.feature.home.HomeScreen
import tech.unrealistic.cineflix.feature.home.ProfileScreen
import tech.unrealistic.cineflix.feature.onboarding.OnboardingFlow
import tech.unrealistic.cineflix.feature.splash.SplashScreen


/**
 * Top-level app stages:
 *  Splash (2.8s) → Onboarding (first launch only) → Main app
 */
enum class AppStage { SPLASH, ONBOARDING, MAIN }


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineflixApp(

) {
    val context = LocalContext.current

    // Splash always plays; onboarding shows only on the very first launch.
    var appStage by remember { mutableStateOf(AppStage.SPLASH) }

    Crossfade(
        targetState = appStage,
        animationSpec = tween(500),
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppContent() {
    //Home is our default tab
    val backStack = remember { mutableStateListOf<Any>(Home) }
    // if the current tab in the stack  is null revert to default tab
    val currentRoot = backStack.firstOrNull{it !is Player && it!is MediaDetailModal}?: Home
    //Check the tab we are currently on
    val currentTab = AppDestinations.entries.find {it.route == currentRoot}?: AppDestinations.HOME

    val  mediaRepository = RetrofitClient.tmdbService

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label,
                            tint = if (it == currentTab) Color.White else Color.Gray

                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentTab,
                    onClick = {
                        backStack.clear()
                        backStack.add(it.route)
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize() ) { innerPadding ->

            NavDisplay(
                backStack= backStack,
                onBack = {backStack.removeLastOrNull()},
                modifier= Modifier.padding(innerPadding),
                entryProvider = {key ->
                    when(key){
                        is Home -> NavEntry(key){ HomeScreen (
                            onNavigate = {
                                backStack.clear()
                                backStack.add(Discover)
                            }
                        ) }
                        is Favourites -> NavEntry(key){ FavouriteScreen() }
                        is Discover -> NavEntry(key) {
                            DiscoverScreen(
                                onNavigate = {
                                    backStack.clear()
                                    backStack.add(Home)
                                }
                            )
                        }
                        is Profile -> NavEntry(key){ProfileScreen () }

                        else -> NavEntry(key){Text("Not found")}
                    }

                }
            )
        }
    }
}

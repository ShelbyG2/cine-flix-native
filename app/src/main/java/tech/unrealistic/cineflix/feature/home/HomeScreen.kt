package tech.unrealistic.cineflix.feature.home

import android.annotation.SuppressLint
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.core.components.FloatingTopBar
import tech.unrealistic.cineflix.feature.details.MediaDetailsBottomSheet
import tech.unrealistic.cineflix.feature.home.components.HeroSection
import tech.unrealistic.cineflix.feature.home.components.MediaSection


@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.P)
@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (() -> Unit)? = null, viewModel: HomeViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val carouselHeight = screenHeight * 0.4f
    val isScrolledPastHero by remember {
        derivedStateOf { scrollState.value > carouselHeight.value.toInt() }
    }

    // collectAsStateWithLifecycle stops collecting when the screen is in the background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isMediaDetailsSheetOpen by remember { mutableStateOf(false) }
    var selectedMediaId by remember { mutableStateOf<Int?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>(null) }

    val mediaDetailsSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val openMediaDetails: (Int, String) -> Unit = { id, t ->
        selectedMediaId = id
        selectedMediaType = t
        isMediaDetailsSheetOpen = true
    }
    var topBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current





    when (val state = uiState) {

        is HomeUiState.Loading -> {
            HomeScreenSkeleton()
        }

        is HomeUiState.Error -> {
            Column() {
                Icon(
                    imageVector = Icons.TwoTone.WifiOff,
                    contentDescription = "Error",

                    )
                AnimatedImageDrawable(

                )
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is HomeUiState.Success -> {
            val carouselState = rememberCarouselState(
                itemCount = { state.trendingMixed.size })
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)

                ) {

                    HeroSection(
                        items = state.trendingMixed,
                        carouselState = carouselState,
                        modifier = Modifier,
                        carouselHeight = carouselHeight,
                        onMediaClick = openMediaDetails
                    )

                    MediaSection(
                        title = "Trending Movies",
                        items = state.trendingMovies,
                        onMediaClick = openMediaDetails
                    )
                    MediaSection(
                        title = "Trending TV Shows",
                        items = state.trendingTvShows,
                        onMediaClick = openMediaDetails
                    )
                    MediaSection(
                        title = "Rated Movies",
                        items = state.ratedMovies,
                        onMediaClick = openMediaDetails
                    )
                    MediaSection(
                        title = "Rated Tv Shows",
                        items = state.ratedTv,
                        onMediaClick = openMediaDetails
                    )
                    MediaDetailsBottomSheet(
                        showBottomSheet = isMediaDetailsSheetOpen,
                        onDismissRequest = { isMediaDetailsSheetOpen = false },
                        mediaId = selectedMediaId ?: 0,
                        mediaType = selectedMediaType ?: "na",
                        sheetState = mediaDetailsSheetState,
                    )
                }
                FloatingTopBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .onGloballyPositioned { coordinates ->
                            topBarHeight = with(density) { coordinates.size.height.toDp() }
                        },
                    { onNavigate?.invoke() },
                    isScrolled = isScrolledPastHero
                )
            }
        }
    }


}







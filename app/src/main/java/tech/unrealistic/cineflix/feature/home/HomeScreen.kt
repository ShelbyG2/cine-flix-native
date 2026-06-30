package tech.unrealistic.cineflix.feature.home

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.core.components.CustomTopBar
import tech.unrealistic.cineflix.core.components.MediaDetailsBottomSheet
import tech.unrealistic.cineflix.feature.home.components.HeroSection
import tech.unrealistic.cineflix.feature.home.components.MediaSection


@RequiresApi(Build.VERSION_CODES.P)
@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (() -> Unit)? = null, viewModel: HomeViewModel = viewModel()
) {
    // collectAsStateWithLifecycle stops collecting when the screen is in the background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isMediaDetailsSheetOpen by remember { mutableStateOf(false) }
    var selectedMediaId by remember { mutableStateOf<Int?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>(null) }

    val mediaDetailsSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val openMediaDetails: (Int, String) -> Unit = { id, t ->
        selectedMediaId = id
        selectedMediaType = t
        isMediaDetailsSheetOpen = true
    }
    if (isMediaDetailsSheetOpen){

    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "CineFlix", onSearchClick = { onNavigate?.invoke() })
        }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
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


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())

                    ) {

                        HeroSection(
                            items = state.trendingMixed,
                            carouselState = carouselState,
                            modifier = Modifier,
                            topPadding = 10.dp
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
                            sheetState = mediaDetailsSheetState,
                            onDismissRequest = { isMediaDetailsSheetOpen = false },
                            mediaId = selectedMediaId ?: 0,
                            mediaType = selectedMediaType ?: "na"
                        )
                    }
                }
            }
        }
    }
}





package tech.unrealistic.cineflix.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.feature.details.components.DetailSheetHeroSection
import tech.unrealistic.cineflix.feature.details.components.TvEpisodeItem
import tech.unrealistic.cineflix.feature.home.components.MediaSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    mediaId: Int,
    mediaType: String,
    sheetState: SheetState,
    onPlayClick: (Any) -> Unit = {},
    onFavourite: (Any) -> Unit = {},
    onMediaShare: (Any) -> Unit = {}
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.navigationBars },
            dragHandle = {}
        ) {
            val sheetViewModel: DetailsSheetViewModel = viewModel()
            val currentEpisodes by sheetViewModel.currentSeasonEpisode.collectAsStateWithLifecycle()
            val uiState by sheetViewModel.uiState.collectAsStateWithLifecycle()

            // Hoist the season selection state here so it survives episode recompositions
            var selectedSeason by rememberSaveable { mutableIntStateOf(1) }

            LaunchedEffect(mediaId, mediaType) {
                selectedSeason = 1 // Reset season when a new show is opened
                sheetViewModel.fetchSheetDetails(mediaType, mediaId)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .statusBarsPadding()
            ) {
                when (val state = uiState) {
                    is DetailsSheetState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is DetailsSheetState.Error -> {
                        item {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    is DetailsSheetState.MovieSuccess, is DetailsSheetState.TvSuccess -> {
                        val (mediaItem, similarShows) = when (state) {
                            is DetailsSheetState.MovieSuccess -> state.movie to state.similarShows
                            is DetailsSheetState.TvSuccess -> state.tv to state.similarShows
                            else -> throw IllegalStateException("Invalid state context")
                        }

                        // 1. HERO SECTION & METADATA
                        item {
                            DetailSheetHeroSection(
                                mediaItem = mediaItem,
                                isTvShow = state is DetailsSheetState.TvSuccess,
                                onDismissRequest = onDismissRequest,
                                onPlayClick = onPlayClick,
                                onFavourite = onFavourite,
                                onMediaShare = onMediaShare
                            )
                        }

                        // 2. TV SHOW LAZY EPISODES SECTION
                        if (state is DetailsSheetState.TvSuccess) {
                            val tvItem = state.tv

                            item {
                                TvSeasonSelector(
                                    seasons = tvItem.numberOfSeasons ?: 1,
                                    selectedSeasonNumber = selectedSeason,
                                    onSeasonSelected = { newSeason ->
                                        selectedSeason = newSeason
                                        sheetViewModel.fetchEpisodesOnly(newSeason, tvItem.id)
                                    }
                                )
                            }

                            itemsIndexed(
                                items = currentEpisodes,
                                key = { _, episode -> episode.id ?: episode.hashCode() }
                            ) { index, episode ->
                                TvEpisodeItem(
                                    episode = episode,
                                    index = index,
                                    isLast = index == currentEpisodes.lastIndex
                                )
                            }
                        }

                        // 3. RELATED MEDIA ROW
                        item {
                            MediaSection(
                                title = "More Like This",
                                items = similarShows,
                                onMediaClick = { id: Int, type: String ->
                                    sheetViewModel.fetchSheetDetails(type, id)
                                }
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}





@Composable
private fun TvSeasonSelector(
    seasons: Int,
    selectedSeasonNumber: Int,
    onSeasonSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Episodes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(seasons) { index ->
                val seasonNumber = index + 1
                FilterChip(
                    selected = seasonNumber == selectedSeasonNumber,
                    onClick = { onSeasonSelected(seasonNumber) },
                    label = { Text("Season $seasonNumber") }
                )
            }
        }
    }
}

package tech.unrealistic.cineflix.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideoLibrary
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
import tech.unrealistic.cineflix.core.components.EmptyState
import tech.unrealistic.cineflix.core.components.ErrorState
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.feature.details.components.DetailSheetHeroSection
import tech.unrealistic.cineflix.feature.details.components.DetailShimmerLayout
import tech.unrealistic.cineflix.feature.details.components.EpisodeShimmerList
import tech.unrealistic.cineflix.feature.details.components.TvEpisodeItem
import tech.unrealistic.cineflix.feature.home.components.MediaSection
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapePill


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    mediaId: Int,
    mediaType: String,
    sheetState: SheetState,
    onPlayClick: (MediaItem) -> Unit = {},
    onFavourite: (MediaItem) -> Unit = {},
    onMediaShare: (MediaItem) -> Unit = {},
    onSave: (MediaItem) -> Unit = {},
    likedEpisodeIds: Set<Int> = emptySet(),
    onEpisodeLike: ((seriesId: Int, seasonNumber: Int, episodeId: Int) -> Unit)? = null
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.navigationBars },
            dragHandle = {
                // Themed drag handle — dismiss affordance (was: empty = no handle)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = AppTheme.spacing.xs)
                            .width(AppTheme.sizes.dragHandleWidth)
                            .height(AppTheme.sizes.dragHandleHeight)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                ShapePill
                            )
                    )
                }
            }
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
                            DetailShimmerLayout()
                        }
                    }

                    is DetailsSheetState.Error -> {
                        item {
                            ErrorState(
                                title = "Couldn't load details",
                                message = state.message,
                                onRetry = {
                                    sheetViewModel.fetchSheetDetails(mediaType, mediaId)
                                },
                                modifier = Modifier.padding(
                                    vertical = AppTheme.spacing.lg
                                )
                            )
                        }
                    }

                    is DetailsSheetState.MovieSuccess, is DetailsSheetState.TvSuccess -> {
                        val (mediaItem, similarShows) = when (state) {
                            is DetailsSheetState.MovieSuccess -> state.movie to state.similarShows
                            is DetailsSheetState.TvSuccess -> state.tv to state.similarShows
                            else -> throw IllegalStateException("Invalid state context")
                        }
                        // Favourite state lives in the sheet state so it stays
                        // correct when "More Like This" swaps the displayed item.
                        val isFavourite = when (state) {
                            is DetailsSheetState.MovieSuccess -> state.isFavourite
                            is DetailsSheetState.TvSuccess -> state.isFavourite
                            else -> false
                        }
                        val isSaved = when (state) {
                            is DetailsSheetState.MovieSuccess -> state.isSaved
                            is DetailsSheetState.TvSuccess -> state.isSaved
                            else -> false
                        }

                        // 1. HERO SECTION & METADATA
                        item {
                            DetailSheetHeroSection(
                                mediaItem = mediaItem,
                                isTvShow = state is DetailsSheetState.TvSuccess,
                                onDismissRequest = onDismissRequest,
                                onPlayClick = onPlayClick,
                                onFavourite = onFavourite,
                                onMediaShare = onMediaShare,
                                isFavourite = isFavourite,
                                isSaved = isSaved,
                                onSave = onSave
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

                            when {
                                state.episodesLoading -> item {
                                    EpisodeShimmerList()
                                }

                                state.episodesError != null -> item {
                                    ErrorState(
                                        title = "Couldn't load episodes",
                                        message = state.episodesError,
                                        onRetry = {
                                            sheetViewModel.fetchEpisodesOnly(
                                                selectedSeason,
                                                tvItem.id
                                            )
                                        }
                                    )
                                }

                                currentEpisodes.isEmpty() -> item {
                                    EmptyState(
                                        icon = Icons.Outlined.VideoLibrary,
                                        title = "No episodes available",
                                        subtitle = "This season has no episode data yet."
                                    )
                                }

                                else -> itemsIndexed(
                                    items = currentEpisodes,
                                    key = { _, episode -> episode.id }
                                ) { index, episode ->
                                    TvEpisodeItem(
                                        episode = episode,
                                        isLast = index == currentEpisodes.lastIndex,
                                        isLiked = likedEpisodeIds.contains(episode.id),
                                        onLike = {
                                            onEpisodeLike?.invoke(
                                                tvItem.id,
                                                selectedSeason,
                                                episode.id
                                            )
                                        }
                                    )
                                }
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
                            Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
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
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.md,
                vertical = AppTheme.spacing.sm
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            modifier = Modifier.padding(bottom = AppTheme.spacing.gap12)
        ) {
            items(seasons) { index ->
                val seasonNumber = index + 1
                FilterChip(
                    selected = seasonNumber == selectedSeasonNumber,
                    onClick = { onSeasonSelected(seasonNumber) },
                    label = { Text("Season $seasonNumber") },
                    // 48dp accessible chip height
                    modifier = Modifier.height(AppTheme.sizes.chipTouchArea)
                )
            }
        }
    }
}

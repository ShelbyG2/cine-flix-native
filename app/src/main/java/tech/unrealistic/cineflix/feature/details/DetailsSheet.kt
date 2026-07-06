package tech.unrealistic.cineflix.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle
import tech.unrealistic.cineflix.feature.home.components.MediaSection
import tech.unrealistic.cineflix.helpers.formatTime
import tech.unrealistic.cineflix.helpers.genreNames


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
    onMediaShare: (MediaItem) -> Unit = {}


) {
    if (showBottomSheet) {


        ModalBottomSheet(
            onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.navigationBars },
            dragHandle = {}) {
            val sheetViewModel: DetailsSheetViewModel = viewModel()
            val currentEpisodes by sheetViewModel.currentSeasonEpisode.collectAsStateWithLifecycle()

            val uiState by sheetViewModel.uiState.collectAsStateWithLifecycle()
            val scrollState= rememberScrollState()

            LaunchedEffect(mediaId, mediaType) {

                sheetViewModel.fetchSheetDetails(mediaType, mediaId)
            }

            Column(

                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .statusBarsPadding().verticalScroll(scrollState)
            ) {
                when (val state = uiState) {
                    is DetailsSheetState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is DetailsSheetState.MovieSuccess, is DetailsSheetState.TvSuccess -> {
                        val (mediaItem, similarShows) = when (state) {
                            is DetailsSheetState.MovieSuccess -> state.movie to state.similarShows
                            is DetailsSheetState.TvSuccess -> state.tv to state.similarShows
                            else -> throw IllegalStateException(" Invalid state context")
                        }

                        val posterUrl = "https://image.tmdb.org/t/p/w500${mediaItem.backdropPath}"

                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        ) {

                            AsyncImage(
                                model = posterUrl,
                                contentDescription = mediaItem.displayTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop

                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    0.85f
                                                ),
                                                MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    0.95f
                                                )

                                            ),

                                            )

                                    )
                                    .align(Alignment.BottomCenter)
                            )
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 16.dp)
                                    .clip(CircleShape)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(0.5f))
                                    .border(
                                        width = 0.8.dp,
                                        MaterialTheme.colorScheme.primary.copy(0.25f),
                                        shape = CircleShape
                                    )


                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Details",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Row {
                                    mediaItem.genreNames.forEach { genre ->
                                        Surface(
                                            shape = RoundedCornerShape(50),    // pill
                                            color = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier.border(
                                                0.8.dp,
                                                Color.White.copy(alpha = 0.25f),
                                                RoundedCornerShape(50)
                                            )
                                        ) {
                                            Text(
                                                text = genre,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp, vertical = 4.dp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = mediaItem.displayTitle,
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )


                            }
                        }
                        Row(
                            modifier = Modifier.padding(10.dp),
                            Arrangement.spacedBy(10.dp)
                        ) {
                            if (state is DetailsSheetState.MovieSuccess) {
                                val item = state.movie
                                Text(
                                    "• ${formatTime(item.runtime!!)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "• ${item.originCountry?.firstOrNull()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "• ${item.releaseDate?.substringBefore("-")}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                            }
                            if (state is DetailsSheetState.TvSuccess) {
                                val item = state.tv
                                Text(
                                    "• ${item.originCountry?.firstOrNull()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "• ${item.releaseDate?.substringBefore("-")}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "• ${item.numberOfSeasons} seasons",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                        }

                        ActionButtons(
                            item = mediaItem,
                            onMediaShare = { onMediaShare(mediaItem) },
                            isFavourite = false,
                            onFavourite = { onFavourite(mediaItem) },
                            onPlay = { onPlayClick(mediaItem) },
                        )
                        mediaItem.overview?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        if (state is DetailsSheetState.TvSuccess) {
                            val item = state.tv
                            TvSeasonsAndEpisodesSection(
                                seasons = item.numberOfSeasons!!,
                                currentEpisodes,
                                onSeasonSelected = { selectedNumber ->
                                    sheetViewModel.fetchEpisodesOnly(
                                        selectedNumber,
                                        item.id
                                    )
                                }
                            )
                        }

                        MediaSection(
                            title = "More Like this ",
                            items = similarShows,
                            onMediaClick = { id: Int, type: String ->
                                sheetViewModel.fetchSheetDetails(type, id)

                            })

                    }

                    is DetailsSheetState.Error -> {
                        Text(
                            text = state.message, color = MaterialTheme.colorScheme.error
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun TvSeasonsAndEpisodesSection(
    seasons: Int,
    episodesState: List<Episode>, // Dynamically provided based on selected season
    onSeasonSelected: (Int) -> Unit // Callback to ViewModel to load new episode list
) {
    var selectedSeasonNumber by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Episodes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 1. Horizontal Season Selector Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(seasons) { index ->
                val seasonNumber= index +1
                val isSelected = seasonNumber == selectedSeasonNumber

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedSeasonNumber =seasonNumber
                            onSeasonSelected(seasonNumber)
                    },
                    label = { Text("Season $seasonNumber") }
                )
            }
        }

        // 2. Vertical Episodes Column (Using Column + forEach to prevent nested scroll crashes)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            episodesState.forEach { episode ->
                Row {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${episode.stillPath}",
                        contentDescription = "Season Image",

                    )
                }
            }
        }
    }
}
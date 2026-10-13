package tech.unrealistic.cineflix.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.twotone.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.core.components.shimmerEffect
import tech.unrealistic.cineflix.data.local.LibraryRepository
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.feature.details.MediaDetailsBottomSheet

/**
 * Favourites — the user's local library.
 *
 * Three sections, all backed by the Room database:
 *   1. Favourites (movies & shows)   — heart toggles remove inline
 *   2. Saved for later (watchlist)   — bookmark toggles remove inline
 *   3. Liked episodes                — 16:9 stills with heart to unlike
 *
 * Typography follows the shared Lexend scale (headlineMedium / titleLarge /
 * titleMedium / bodyLarge) matching Home, Discover and Details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    onNavigate: (() -> Unit)? = null,
    viewModel: FavouritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Details sheet selection — declared first so sheet state can key off them
    var isMediaDetailsSheetOpen by remember { mutableStateOf(false) }
    var selectedMediaId by remember { mutableIntStateOf(0) }
    var selectedMediaType by remember { mutableStateOf("movie") }
    val mediaDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Real library state for the details sheet buttons (no hardcoded flags)
    val favouriteKeys by LibraryRepository.favouriteKeys.collectAsStateWithLifecycle()
    val savedKeys by LibraryRepository.savedKeys.collectAsStateWithLifecycle()
    val likedEpisodeKeys by LibraryRepository.likedEpisodeKeys.collectAsStateWithLifecycle()
    val isSheetFavourite = remember(favouriteKeys, selectedMediaId, selectedMediaType) {
        favouriteKeys.contains("favourite:$selectedMediaType:$selectedMediaId")
    }
    val isSheetSaved = remember(savedKeys, selectedMediaId, selectedMediaType) {
        savedKeys.contains("saved:$selectedMediaType:$selectedMediaId")
    }
    val sheetLikedEpisodes = remember(likedEpisodeKeys) {
        likedEpisodeKeys.mapNotNull { it.split(":").getOrNull(2)?.toIntOrNull() }.toSet()
    }

    val openMediaDetails: (Int, String) -> Unit = { id, type ->
        selectedMediaId = id
        selectedMediaType = type
        isMediaDetailsSheetOpen = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Screen title ──────────────────────────────────────────────────
        Text(
            text = "My Library",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        )

        when (val state = uiState) {
            is LibraryUiState.Loading -> {
                LibrarySkeleton()
            }

            is LibraryUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.WifiOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = viewModel::onRetry,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Try Again", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            is LibraryUiState.Success -> {
                if (state.isEmpty) {
                    EmptyLibrary(
                        onExplore = { onNavigate?.invoke() }
                    )
                } else {
                    // ── 1. Favourites row ─────────────────────────────────
                    if (state.favourites.isNotEmpty()) {
                        LibraryRow(
                            title = "Favourites",
                            subtitle = "Movies & shows you loved",
                            onRemove = { item ->
                                LibraryRepository.toggleFavourite(item.id, item.mediaType)
                            },
                            items = state.favourites,
                            onItemClick = openMediaDetails
                        )
                    }

                    // ── 2. Saved row ──────────────────────────────────────
                    if (state.saved.isNotEmpty()) {
                        LibraryRow(
                            title = "Saved for Later",
                            subtitle = "Your watchlist",
                            savedStyle = true,
                            onRemove = { item ->
                                LibraryRepository.toggleSaved(item.id, item.mediaType)
                            },
                            items = state.saved,
                            onItemClick = openMediaDetails
                        )
                    }

                    // ── 3. Liked episodes row ─────────────────────────────
                    if (state.episodes.isNotEmpty()) {
                        Text(
                            text = "Liked Episodes",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Text(
                            text = "Episodes you want to revisit",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.episodes,
                                key = { it.episode.id }
                            ) { liked ->
                                LikedEpisodeCard(
                                    episode = liked.episode,
                                    onClick = { openMediaDetails(liked.seriesId, "tv") },
                                    onUnlike = {
                                        LibraryRepository.toggleEpisodeLike(
                                            liked.seriesId, liked.seasonNumber, liked.episode.id
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    MediaDetailsBottomSheet(
        showBottomSheet = isMediaDetailsSheetOpen,
        onDismissRequest = { isMediaDetailsSheetOpen = false },
        mediaId = selectedMediaId,
        mediaType = selectedMediaType,
        sheetState = mediaDetailsSheetState,
        isFavourite = isSheetFavourite,
        onFavourite = {
            LibraryRepository.toggleFavourite(selectedMediaId, selectedMediaType)
        },
        isSaved = isSheetSaved,
        onSave = {
            LibraryRepository.toggleSaved(selectedMediaId, selectedMediaType)
        },
        likedEpisodeIds = sheetLikedEpisodes,
        onEpisodeLike = { seriesId, seasonNumber, episodeId ->
            LibraryRepository.toggleEpisodeLike(seriesId, seasonNumber, episodeId)
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// SECTION ROW — reusable favourites/saved shelf
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun LibraryRow(
    title: String,
    subtitle: String,
    items: List<MediaItem>,
    onRemove: (MediaItem) -> Unit,
    onItemClick: (Int, String) -> Unit,
    savedStyle: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = items, key = { "${it.mediaType}_${it.id}" }) { item ->
                if (savedStyle) {
                    // Saved shelf: bookmark overlay removes from the watchlist
                    MediaCard(
                        media = item,
                        isSaved = true,
                        onSaveToggle = { onRemove(item) },
                        modifier = Modifier.clickable { onItemClick(item.id, item.mediaType) }
                    )
                } else {
                    // Favourites shelf: filled heart overlay removes from favourites
                    MediaCard(
                        media = item,
                        isFavourite = true,
                        onFavouriteToggle = { onRemove(item) },
                        modifier = Modifier.clickable { onItemClick(item.id, item.mediaType) }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// LIKED EPISODE CARD — 16:9 still, heart to unlike
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun LikedEpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
    onUnlike: () -> Unit
) {
    Column(modifier = Modifier.width(220.dp)) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${episode.stillPath}",
                contentDescription = episode.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onClick() }
            )
            IconButton(
                onClick = onUnlike,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Unlike episode",
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = episode.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = episode.airDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// EMPTY & SKELETON STATES
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyLibrary(onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the heart on any title to add it here, or the bookmark to save it for later.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onExplore,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Explore Titles", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Skeleton matching the production row metrics (shimmer). */
@Composable
private fun LibrarySkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(2) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                // title placeholder — mirrors titleLarge spatial height
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .width(160.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

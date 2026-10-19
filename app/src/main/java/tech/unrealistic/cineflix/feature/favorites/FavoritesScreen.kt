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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.core.components.ShimmerBox
import tech.unrealistic.cineflix.data.local.LibraryRepository
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.feature.details.MediaDetailsBottomSheet
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle

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
    val likedEpisodeKeys by LibraryRepository.likedEpisodeKeys.collectAsStateWithLifecycle()
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
            modifier = Modifier.padding(
                start = AppTheme.spacing.md,
                top = AppTheme.spacing.md,
                end = AppTheme.spacing.md,
                bottom = AppTheme.spacing.xs
            )
        )

        when (val state = uiState) {
            is LibraryUiState.Loading -> {
                LibrarySkeleton()
            }

            is LibraryUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.WifiOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(AppTheme.sizes.stateIconLg)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.gap20))
                    Button(
                        onClick = viewModel::onRetry,
                        shape = MaterialTheme.shapes.medium
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
                            modifier = Modifier.padding(
                                horizontal = AppTheme.spacing.md,
                                vertical = AppTheme.spacing.sm
                            )
                        )
                        Text(
                            text = "Episodes you want to revisit",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.md)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = AppTheme.spacing.md,
                                vertical = AppTheme.spacing.gap12
                            ),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12)
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

                    Spacer(modifier = Modifier.height(AppTheme.spacing.gap24))
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
        onFavourite = { item ->
            LibraryRepository.toggleFavourite(item.id, item.mediaType)
        },
        onSave = { item ->
            LibraryRepository.toggleSaved(item.id, item.mediaType)
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
    Column(modifier = Modifier.padding(bottom = AppTheme.spacing.gap24)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.md,
                vertical = AppTheme.spacing.sm
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.md)
        )
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = AppTheme.spacing.md,
                vertical = AppTheme.spacing.gap12
            ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12)
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
    Column(modifier = Modifier.width(AppTheme.sizes.libraryCardW)) {
        Box {
            AsyncImage(
                model = episode.stillPath
                    ?.let { path -> "https://image.tmdb.org/t/p/w300$path" },
                contentDescription = episode.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onClick() }
            )
            IconButton(
                onClick = onUnlike,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppTheme.sizes.overlayButtonPad)
                    .size(AppTheme.sizes.touchTarget)   // 48dp target
                    .clip(ShapeCircle)
                    .background(AppTheme.colors.imageChipSurface)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Unlike episode",
                    tint = AppTheme.colors.favouriteActive,
                    modifier = Modifier.size(AppTheme.sizes.iconSm)
                )
            }
        }
        Text(
            text = episode.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = AppTheme.spacing.gap6)
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
            .padding(
                horizontal = AppTheme.spacing.lg,
                vertical = AppTheme.spacing.gap48
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(AppTheme.sizes.stateIconLg)
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
        Text(
            text = "Tap the heart on any title to add it here, or the bookmark to save it for later.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.gap20))
        Button(
            onClick = onExplore,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Explore Titles", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Skeleton matching the production row metrics (shimmer). */
@Composable
private fun LibrarySkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading…" }
    ) {
        repeat(2) {
            Column(modifier = Modifier.padding(bottom = AppTheme.spacing.gap24)) {
                // title placeholder — mirrors titleLarge spatial height
                ShimmerBox(
                    modifier = Modifier
                        .padding(
                            horizontal = AppTheme.spacing.md,
                            vertical = AppTheme.spacing.sm
                        )
                        .width(AppTheme.sizes.skeletonTitleW)
                        .height(AppTheme.sizes.skeletonTitleH),
                    shape = MaterialTheme.shapes.small
                )
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.spacing.md,
                        vertical = AppTheme.spacing.gap12
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12),
                    userScrollEnabled = false
                ) {
                    items(5) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(AppTheme.sizes.mediaCardWidth)
                                .height(
                                    AppTheme.sizes.mediaCardWidth *
                                            AppTheme.sizes.mediaCardPosterRatio
                                ),
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            }
        }
    }
}

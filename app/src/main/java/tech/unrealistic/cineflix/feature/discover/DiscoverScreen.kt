package tech.unrealistic.cineflix.feature.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.core.components.EmptyState
import tech.unrealistic.cineflix.core.components.ErrorState
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.core.components.ShimmerBox
import tech.unrealistic.cineflix.data.local.LibraryItemEntity
import tech.unrealistic.cineflix.data.local.LibraryRepository
import tech.unrealistic.cineflix.data.remote.models.Genre
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.feature.details.MediaDetailsBottomSheet
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.withAlpha

/**
 * Discover — browse, filter and search the TMDB catalogue.
 *
 * Typography follows the shared Lexend scale from ui/theme/Type.kt:
 *   headlineMedium  → screen title
 *   titleMedium     → section labels (Sort, Genre)
 *   bodyLarge       → search input, body copy
 *   labelLarge      → chips (matches button text token)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigate: (() -> Unit)? = null,
    viewModel: DiscoverViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val selectedGenreId by viewModel.selectedGenreId.collectAsStateWithLifecycle()
    val availableGenres by viewModel.availableGenres.collectAsStateWithLifecycle()

    // ── Library (favourites / saved) state ─────────────────────────────────
    val savedKeys by LibraryRepository.savedKeys.collectAsStateWithLifecycle()
    val likedEpisodeKeys by LibraryRepository.likedEpisodeKeys.collectAsStateWithLifecycle()
    val savedIds = remember(savedKeys) {
        savedKeys.mapNotNull { it.split(":").getOrNull(2)?.toIntOrNull() }.toSet()
    }
    val likedEpisodeIds = remember(likedEpisodeKeys) {
        likedEpisodeKeys.mapNotNull { it.split(":").getOrNull(2)?.toIntOrNull() }.toSet()
    }

    // Details sheet state — identical contract to HomeScreen
    var isMediaDetailsSheetOpen by remember { mutableStateOf(false) }
    var selectedMediaId by remember { mutableStateOf(0) }
    var selectedMediaType by remember { mutableStateOf("movie") }
    val mediaDetailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val openMediaDetails: (Int, String) -> Unit = { id, type ->
        selectedMediaId = id
        selectedMediaType = type
        isMediaDetailsSheetOpen = true
    }

    val gridState = rememberLazyGridState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Infinite scroll — trigger the next page when the user nears the grid end
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= layoutInfo.totalItemsCount - 6 && layoutInfo.totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.onLoadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ── Screen title ──────────────────────────────────────────────────
        Text(
            text = "Discover",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                start = AppTheme.spacing.md,
                top = AppTheme.spacing.md,
                end = AppTheme.spacing.md,
                bottom = AppTheme.spacing.gap12
            )
        )

        // ── Search bar ────────────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = {
                Text(
                    text = "Search movies & TV shows…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = ShapePill,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.onSearchSubmitted()
                keyboardController?.hide()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.md)
                .focusRequester(focusRequester)
        )

        // ── Category tabs (Movies / TV Shows) ─────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            modifier = Modifier.padding(top = AppTheme.spacing.gap12)
        ) {
            items(DiscoverCategory.entries) { category ->
                DiscoverChip(
                    label = category.label,
                    selected = category == selectedCategory,
                    onClick = { viewModel.onCategorySelected(category) }
                )
            }
        }

        // ── Sort options ──────────────────────────────────────────────────
        if (searchQuery.isBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = AppTheme.spacing.md,
                    top = AppTheme.spacing.gap14,
                    end = AppTheme.spacing.md
                )
            ) {
                Text(
                    text = "Sort",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                modifier = Modifier.padding(top = AppTheme.spacing.gap6)
            ) {
                items(DiscoverSort.entries) { sort ->
                    DiscoverChip(
                        label = sort.label,
                        selected = sort == selectedSort,
                        onClick = { viewModel.onSortSelected(sort) }
                    )
                }
            }

            // ── Genre filter ──────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = AppTheme.spacing.md,
                    top = AppTheme.spacing.gap14,
                    end = AppTheme.spacing.md
                )
            ) {
                Text(
                    text = "Genre",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                modifier = Modifier.padding(
                    top = AppTheme.spacing.gap6,
                    bottom = AppTheme.spacing.xs
                )
            ) {
                items(availableGenres) { genre ->
                    DiscoverChip(
                        label = genre.name,
                        selected = genre.id == selectedGenreId,
                        onClick = { viewModel.onGenreSelected(genre.id) }
                    )
                }
            }
        }

        // ── Results ───────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is DiscoverUiState.Loading -> {
                    DiscoverGridSkeleton()
                }

                is DiscoverUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorState(
                            message = state.message,
                            onRetry = viewModel::onRetry
                        )
                    }
                }

                is DiscoverUiState.Success -> {
                    if (state.items.isEmpty()) {
                        // ── Empty state (no results for query/filter) ──────
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                icon = Icons.Default.Search,
                                title = if (searchQuery.isBlank()) "Nothing here yet"
                                else "No results found",
                                subtitle = if (searchQuery.isBlank())
                                    "Try a different category, sort order or genre."
                                else "Check the spelling or try another keyword."
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = AppTheme.sizes.gridCellMin),
                            contentPadding = PaddingValues(
                                start = AppTheme.spacing.md,
                                end = AppTheme.spacing.md,
                                top = AppTheme.spacing.gap12,
                                bottom = AppTheme.spacing.gap24
                            ),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.items,
                                key = { "${it.mediaType}_${it.id}" }
                            ) { item ->
                                MediaCard(
                                    media = item,
                                    fillWidth = true,
                                    isSaved = savedIds.contains(item.id),
                                    onSaveToggle = { LibraryRepository.toggleSaved(item.id, item.mediaType) },
                                    modifier = Modifier
                                        .clickable {
                                            openMediaDetails(item.id, item.mediaType)
                                        }
                                )
                            }

                            if (state.isLoadingMore) {
                                item(span = { GridItemSpan(maxLineSpan) }) {                                        Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = AppTheme.spacing.md),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(AppTheme.sizes.spinner),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
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
        onFavourite = { item -> LibraryRepository.toggleFavourite(item.id, item.mediaType) },
        isSaved = selectedMediaId in savedIds,
        onSave = { item -> LibraryRepository.toggleSaved(item.id, item.mediaType) },
        likedEpisodeIds = likedEpisodeIds,
        onEpisodeLike = { seriesId, seasonNumber, episodeId ->
            LibraryRepository.toggleEpisodeLike(seriesId, seasonNumber, episodeId)
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// SHARED PIECES
// ═══════════════════════════════════════════════════════════════════════════

/** Pill chip used for category, sort and genre filters. */
@Composable
private fun DiscoverChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        shape = ShapePill,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
                .withAlpha(AppTheme.colors.chipUnselectedAlpha),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary
                .withAlpha(AppTheme.colors.chipSelectedAlpha),
            selectedLabelColor = MaterialTheme.colorScheme.onBackground
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            enabled = true,
            selected = selected
        )
    )
}

/** Poster-grid skeleton mirroring the production grid metrics (shimmer). */
@Composable
private fun DiscoverGridSkeleton(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = AppTheme.sizes.gridCellMin),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.md,
            end = AppTheme.spacing.md,
            top = AppTheme.spacing.gap12,
            bottom = AppTheme.spacing.gap24
        ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        userScrollEnabled = false,
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading…" }
    ) {
        items(12) {
            Column {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.sm))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(AppTheme.spacing.gap12),
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }
}

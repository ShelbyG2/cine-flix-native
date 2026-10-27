package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.helpers.PaletteHelper
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.withAlpha
import kotlin.collections.getOrNull

/**
 * Home hero — a full-bleed, horizontally paged carousel of posters with the
 * existing Play / details / favourite / save actions underneath.
 *
 * The section background is **driven by the focused movie**: its poster's
 * dominant colours (vibrant / dark-muted) paint a vertical gradient that
 * crossfades as the carousel settles on a new title — the signature of the
 * original design. Chrome stays monochrome; only the backdrop takes the tint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSection(
    items: List<MediaItem>,
    carouselState: CarouselState,
    modifier: Modifier = Modifier,
    onPlayClick: (MediaItem) -> Unit = {},
    onFavourite: (MediaItem) -> Unit = {},
    favouriteIds: Set<Int> = emptySet(),
    onSaved: (MediaItem) -> Unit = {},
    savedIds: Set<Int> = emptySet(),
    carouselHeight: Dp,
    onMediaClick: ((Int, String) -> Unit)
) {
    val context = LocalContext.current
    val motion = AppTheme.motion
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    // The cards start below the floating top bar (status bar + bar content),
    // so the bar never sits on top of the artwork.
    val topBarBottom = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() +
        sizes.topBarHeight
    val effects = AppTheme.effects
    val defaultVibrant = MaterialTheme.colorScheme.primaryContainer
    val defaultMuted = MaterialTheme.colorScheme.surface

    // ── Palette state ─────────────────────────────────────────────────────────
    var targetVibrant by remember { mutableStateOf(defaultVibrant) }
    var targetMuted by remember { mutableStateOf(defaultMuted) }

    // Long crossfades between titles; instant under reduce motion because the
    // spec comes from the motion tokens.
    val animatedVibrant by animateColorAsState(
        targetValue = targetVibrant,
        animationSpec = motion.spec(motion.longFadeMs, motion.standard),
        label = "hero_vibrant"
    )
    val animatedMuted by animateColorAsState(
        targetValue = targetMuted,
        animationSpec = motion.spec(motion.longFadeMs, motion.standard),
        label = "hero_muted"
    )

    // ── Colour extraction — only fires on actual item change ──────────────────
    LaunchedEffect(carouselState) {
        snapshotFlow { carouselState.currentItem }
            .distinctUntilChanged()
            .map { index -> items.getOrNull(index)?.posterPath }
            .collectLatest { path ->
                if (path == null) {
                    targetVibrant = defaultVibrant
                    targetMuted = defaultMuted
                    return@collectLatest
                }
                val palette = PaletteHelper.extractPalette(
                    context = context, url = "https://image.tmdb.org/t/p/w92$path"
                )
                targetVibrant = palette?.vibrantSwatch?.let { Color(it.rgb) }
                    ?: palette?.lightVibrantSwatch?.let { Color(it.rgb) } ?: defaultVibrant
                targetMuted = palette?.darkMutedSwatch?.let { Color(it.rgb) }
                    ?: palette?.darkVibrantSwatch?.let { Color(it.rgb) } ?: defaultMuted
            }
    }

    // The focused item drives the action row below the carousel.
    val focusedItem: MediaItem? = items.getOrNull(carouselState.currentItem)

    Box(modifier = modifier.fillMaxWidth()) {

        // ── Poster-derived gradient backdrop ──────────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to animatedVibrant.withAlpha(effects.heroTopAlpha),
                            0.40f to animatedVibrant.withAlpha(effects.heroMidAlpha),
                            0.75f to animatedMuted.withAlpha(effects.heroMutedAlpha),
                            1.00f to MaterialTheme.colorScheme.background
                                .withAlpha(effects.posterFadeAlpha),
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxWidth()) {

            HorizontalCenteredHeroCarousel(
                state = carouselState,
                itemSpacing = spacing.md,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(carouselHeight),
                contentPadding = PaddingValues(
                    start = spacing.lg,
                    end = spacing.lg,
                    top = topBarBottom + spacing.heroTopInset
                ),
                content = { index ->
                    val item = items.getOrNull(index) ?: return@HorizontalCenteredHeroCarousel
                    val isFocused = carouselState.currentItem == index

                    // Depth cue while paging. Motion collapses to snap under
                    // reduce-motion because the spec comes from the motion tokens.
                    val scale by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.94f,
                        animationSpec = motion.spec(motion.mediumMs, motion.emphasized),
                        label = "card_scale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.6f,
                        animationSpec = motion.spec(motion.mediumMs, motion.standard),
                        label = "card_alpha"
                    )

                    MediaCard(
                        media = item,
                        hero = true,
                        isFocused = isFocused,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            },
                    )
                })

            ActionButtons(
                item = focusedItem,
                isFavourite = favouriteIds.contains(focusedItem?.id),
                onPlay = { focusedItem?.let { onPlayClick(it) } },
                onFavourite = { focusedItem?.let { onFavourite(it) } },
                isSaved = savedIds.contains(focusedItem?.id),
                onSave = { focusedItem?.let { onSaved(it) } },
                onMediaClick = onMediaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = spacing.lg)
            )
        }
    }
}

/**
 * Plain screen backdrop behind the hero. Kept as a named container so the
 * loading skeleton shares the loaded layout's bounds.
 */
@Composable
fun HeroBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        content()
    }
}

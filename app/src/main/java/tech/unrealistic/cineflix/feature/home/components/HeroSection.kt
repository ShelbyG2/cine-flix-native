package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.ui.theme.AppTheme
import kotlin.collections.getOrNull

/**
 * Home hero — a full-bleed, horizontally paged carousel of posters with the
 * existing Play / details / favourite / save actions underneath.
 *
 * Monochrome by design: artwork carries the colour, the backdrop is the plain
 * screen background. No poster-derived tint, no grain.
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
    val motion = AppTheme.motion
    val spacing = AppTheme.spacing

    // The focused item drives the action row below the carousel.
    val focusedItem: MediaItem? = items.getOrNull(carouselState.currentItem)

    Column(modifier = modifier.fillMaxWidth()) {

        HorizontalCenteredHeroCarousel(
            state = carouselState,
            itemSpacing = spacing.md,
            modifier = Modifier
                .fillMaxWidth()
                .height(carouselHeight),
            contentPadding = PaddingValues(
                start = spacing.lg,
                end = spacing.lg,
                top = spacing.heroTopInset
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

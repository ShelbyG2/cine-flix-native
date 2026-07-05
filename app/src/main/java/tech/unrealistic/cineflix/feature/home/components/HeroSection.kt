package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.helpers.PaletteHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSection(
    items: List<MediaItem>,
    carouselState: CarouselState,
    modifier: Modifier = Modifier,
    onPlayClick: (MediaItem) -> Unit = {},
    onFavourite: (MediaItem) -> Unit = {},
    favouriteIds: Set<Int> = emptySet(),
    carouselHeight: Dp,
    onMediaClick: ((Int, String) -> Unit)
) {
    val context = LocalContext.current
    val defaultVibrant = MaterialTheme.colorScheme.primaryContainer
    val defaultMuted = MaterialTheme.colorScheme.surface

    // ── Palette state ─────────────────────────────────────────────────────────
    var targetVibrant by remember { mutableStateOf(defaultVibrant) }
    var targetMuted by remember { mutableStateOf(defaultMuted) }

    val animatedVibrant by animateColorAsState(
        targetValue = targetVibrant,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "hero_vibrant"
    )
    val animatedMuted by animateColorAsState(
        targetValue = targetMuted,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "hero_muted"
    )

    // ── Color extraction — only fires on actual item change ───────────────────
    LaunchedEffect(carouselState) {
        snapshotFlow { carouselState.currentItem }.distinctUntilChanged()
            .map { index -> items.getOrNull(index)?.posterPath }.collectLatest { path ->
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

    // ── Focused item reference ─────────────────────────────────────────────────
    val focusedItem: MediaItem? = items.getOrNull(carouselState.currentItem)

    // ── Glass background ───────────────────────────────────────────────────────
    // This is where glassmorphism lives — the SECTION background, not the cards.
    GlassHeroBackground(
        vibrantColor = animatedVibrant,
        mutedColor = animatedMuted,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp),
    ) {
        Column(modifier = Modifier) {

            HorizontalCenteredHeroCarousel(
                state = carouselState,
                itemSpacing = 10.dp,
                modifier = Modifier
                    .height(carouselHeight),
                contentPadding = PaddingValues(
                    start= 10.dp,
                    end = 10.dp,
                    top = 56.dp
                ),
                content = { index ->
                    val item = items.getOrNull(index) ?: return@HorizontalCenteredHeroCarousel
                    //determine the currently focused card
                    val isFocused = carouselState.currentItem == index

                    //create a depth animation when swiping cards and shrink unselected cards
                    val scale by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.8f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "card_scale"
                    )
                    //create a fade animation
                    val alpha by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.5f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "card_alpha"
                    )
                    MediaCard(
                        media = item,
                        hero = true,
                        isFocused = isFocused,
                        accentColor = animatedVibrant,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            },


                        )
                })


            ActionButtons  (
                item = focusedItem,
                isFavourite = favouriteIds.contains(focusedItem?.id),
                onPlay = { focusedItem?.let { onPlayClick(it) } },
                onFavourite = { onFavourite(focusedItem!!) },
                onMediaClick =onMediaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 0.dp, end = 16.dp
                    )
            )

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassHeroBackground(
    modifier: Modifier = Modifier,
    vibrantColor: Color,
    mutedColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier.fillMaxSize()

    ) {

        Box(
            modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to vibrantColor.copy(0.85f),
                            0.40f to vibrantColor.copy(0.60f),
                            0.75f to mutedColor.copy(0.75f),
                            1.00f to MaterialTheme.colorScheme.background.copy(0.95f)
                        )
                    )
                )
        )


        content()
    }
}






package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.helpers.PaletteHelper
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.withAlpha
import kotlin.collections.getOrNull
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

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
    val defaultVibrant = MaterialTheme.colorScheme.primaryContainer
    val defaultMuted = MaterialTheme.colorScheme.surface

    // ── Palette state ─────────────────────────────────────────────────────────
    var targetVibrant by remember { mutableStateOf(defaultVibrant) }
    var targetMuted by remember { mutableStateOf(defaultMuted) }

    val animatedVibrant by animateColorAsState(
        targetValue = targetVibrant,
        animationSpec = tween(motion.longFadeMs, easing = FastOutSlowInEasing),
        label = "hero_vibrant"
    )
    val animatedMuted by animateColorAsState(
        targetValue = targetMuted,
        animationSpec = tween(motion.longFadeMs, easing = FastOutSlowInEasing),
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
                itemSpacing = spacing.gap10,
                modifier = Modifier
                    .height(carouselHeight),
                contentPadding = PaddingValues(
                    start = spacing.gap10,
                    end = spacing.gap10,
                    top = spacing.heroTopInset
                ),
                content = { index ->
                    val item = items.getOrNull(index) ?: return@HorizontalCenteredHeroCarousel
                    //determine the currently focused card
                    val isFocused = carouselState.currentItem == index

                    //create a depth animation when swiping cards and shrink unselected cards
                    //decorative motion — instant under reduce-motion
                    val scale by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.8f,
                        animationSpec = tween(
                            durationMillis = if (motion.reduceMotion) 0 else motion.mediumMs,
                            easing = FastOutSlowInEasing
                        ),
                        label = "card_scale"
                    )
                    //create a fade animation
                    val alpha by animateFloatAsState(
                        targetValue = if (isFocused) 1f else 0.5f,
                        animationSpec = tween(
                            durationMillis = if (motion.reduceMotion) 0 else motion.mediumMs,
                            easing = FastOutSlowInEasing
                        ),
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
                onFavourite = { focusedItem?.let { onFavourite(it) } },
                isSaved = savedIds.contains(focusedItem?.id),
                onSave = { focusedItem?.let { onSaved(it) } },
                onMediaClick =onMediaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 0.dp, end = spacing.md
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
    val colors = AppTheme.colors
    val sizes = AppTheme.sizes

    Box(
        modifier
            .fillMaxSize()
            // Rim light — a hairline edge catches light on the glass hero.
            .border(
                width = sizes.glassBorderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.glassBorder,
                        Color.Transparent,
                        colors.glassBorder.withAlpha(colors.glassBorder.alpha * 0.1f)
                    )
                ),
                shape = RectangleShape
            )

    ) {

        // ── Layer 1: brand gradient ───────────────────────────────────────
        Box(
            modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to vibrantColor.withAlpha(colors.heroTopAlpha),
                            0.40f to vibrantColor.withAlpha(colors.heroMidAlpha),
                            0.75f to mutedColor.withAlpha(colors.heroMutedAlpha),
                            1.00f to MaterialTheme.colorScheme.background
                                .withAlpha(colors.posterFadeAlpha)
                        )
                    )
                )
        )

        // ── Layer 2: specular streak + grain ──────────────────────────────
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawWithCache {
                    val streak = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            colors.glassGloss.withAlpha(colors.glassGloss.alpha * 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width * 0.65f, size.height * 0.45f),
                        tileMode = TileMode.Clamp
                    )
                    onDrawBehind {
                        drawRect(streak)
                        val random = Random(42)   // stable grain across recompositions
                        repeat(colors.glassNoiseDensity) {
                            drawCircle(
                                color = colors.glassNoise.withAlpha(
                                    colors.glassNoise.alpha * colors.glassNoiseAlpha *
                                            random.nextFloat()
                                ),
                                radius = random.nextFloat() * 1.2f,
                                center = Offset(
                                    random.nextFloat() * size.width,
                                    random.nextFloat() * size.height
                                )
                            )
                        }
                    }
                }
        )

        content()
    }
}






package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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


                    HeroActionButtons (
                        item = focusedItem,
                        isFavourite = favouriteIds.contains(focusedItem?.id),
                        onPlay = { focusedItem?.let { onPlayClick(it) } },
                        onFavourite = { onFavourite(focusedItem!!) },
                        accentColor = animatedVibrant,
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

@Composable
private fun HeroActionButtons(
    item: MediaItem?,
    isFavourite: Boolean,
    onPlay: () -> Unit,
    onFavourite: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onMediaClick: (Int, String) -> Unit
) {

    val contentColor = Color.White

    Column(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp,
                    vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Play button  primary solid action
            Button(
                onClick = onPlay,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Play", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            // Info button glass icon button
            val detailScale by animateFloatAsState(
                targetValue = 1.0f,
                animationSpec = tween(200),
                label = "fav_scale"
            )
            IconButton(
                onClick = { onMediaClick(item?.id ?: -1, item?.mediaType ?: "") },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor =   contentColor
                ), modifier = Modifier
                    .size(52.dp)
                    .border(
                        width = 1.dp, color = Color.White.copy(alpha = 0.30f), shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Add to favourites",
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = detailScale
                            scaleY = detailScale
                        })
            }

            //  Favourite button glass icon button
            val favouriteScale by animateFloatAsState(
                targetValue = if (isFavourite) 1.25f else 1.0f,
                animationSpec = tween(200),
                label = "fav_scale"
            )

            IconButton(
                onClick = onFavourite, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = if (isFavourite) Color(0xFFFF6B6B) else contentColor
                ), modifier = Modifier
                    .size(52.dp)
                    .border(
                        width = 1.dp, color = Color.White.copy(alpha = 0.30f), shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourite) "Remove from favourites"
                    else "Add to favourites",
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = favouriteScale
                            scaleY = favouriteScale
                        })
            }
        }
    }
}






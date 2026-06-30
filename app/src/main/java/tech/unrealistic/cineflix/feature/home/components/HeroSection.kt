package tech.unrealistic.cineflix.feature.home.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.genreNames
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSection(
    items:         List<MediaItem>,
    carouselState: CarouselState,
    topPadding:    Dp,
    modifier:      Modifier = Modifier,
    onPlayClick:   (MediaItem) -> Unit = {},
    onFavourite:   (MediaItem) -> Unit = {},
    favouriteIds:  Set<Int> = emptySet(),
) {
    val context        = LocalContext.current
    val defaultVibrant = MaterialTheme.colorScheme.primaryContainer
    val defaultMuted   = MaterialTheme.colorScheme.surface

    // ── Palette state ─────────────────────────────────────────────────────────
    var targetVibrant by remember { mutableStateOf(defaultVibrant) }
    var targetMuted   by remember { mutableStateOf(defaultMuted) }

    val animatedVibrant by animateColorAsState(
        targetValue   = targetVibrant,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "hero_vibrant"
    )
    val animatedMuted by animateColorAsState(
        targetValue   = targetMuted,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "hero_muted"
    )

    // ── Color extraction — only fires on actual item change ───────────────────
    LaunchedEffect(carouselState) {
        snapshotFlow { carouselState.currentItem }
            .distinctUntilChanged()
            .map { index -> items.getOrNull(index)?.posterPath }
            .collectLatest { path ->
                if (path == null) {
                    targetVibrant = defaultVibrant
                    targetMuted   = defaultMuted
                    return@collectLatest
                }
                val palette = extractPalette(
                    context = context,
                    url     = "https://image.tmdb.org/t/p/w92$path"
                )
                targetVibrant = palette?.vibrantSwatch?.let { Color(it.rgb) }
                    ?: palette?.lightVibrantSwatch?.let { Color(it.rgb) }
                            ?: defaultVibrant
                targetMuted = palette?.darkMutedSwatch?.let { Color(it.rgb) }
                    ?: palette?.darkVibrantSwatch?.let { Color(it.rgb) }
                            ?: defaultMuted
            }
    }

    // ── Focused item reference ─────────────────────────────────────────────────
    val focusedItem = items.getOrNull(carouselState.currentItem)

    // ── Glass background ───────────────────────────────────────────────────────
    // This is where glassmorphism lives — the SECTION background, not the cards.
    GlassHeroBackground(
        vibrantColor = animatedVibrant,
        mutedColor   = animatedMuted,
        modifier     = modifier
            .fillMaxWidth()
            .padding(top =0.dp),
    ) {
        Column(modifier = Modifier) {

            HorizontalCenteredHeroCarousel(
                state       = carouselState,
                itemSpacing = 10.dp,
                modifier = Modifier.padding(topPadding),
                contentPadding = PaddingValues(
                    horizontal = 16.dp, // Gives spacing on left/right edges of screen
                    vertical   = 12.dp
                ),
                content = { index ->
                    val item = items.getOrNull(index) ?: return@HorizontalCenteredHeroCarousel
                    MediaCard(
                        media    = item,
                        hero     = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )

            // ── Info panel — animates when focused item changes ────────────
            AnimatedContent(
                targetState = focusedItem,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(250))
                },
                label = "hero_info_panel",
                modifier= Modifier

            ) { item ->
                if (item != null) {
                    HeroInfoPanel(
                        item         = item,
                        isFavourite  = favouriteIds.contains(item.id),
                        onPlay       = { onPlayClick(item) },
                        onFavourite  = { onFavourite(item) },
                        accentColor  = animatedVibrant,
                        modifier     = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassHeroBackground(modifier: Modifier = Modifier,
                        vibrantColor: Color,
                        mutedColor: Color,
                        content: @Composable () -> Unit
                        ) {
    Box(
        modifier.fillMaxSize()

    ){

     Box(modifier.matchParentSize()
         .background(
             Brush.verticalGradient(
                 colorStops = arrayOf(
                     0.00f to vibrantColor.copy(0.85f),
                     0.40f to vibrantColor.copy(0.60f),
                     0.75f to mutedColor.copy( 0.75f),
                     1.00f to mutedColor.copy(0.95f)
                 )
             )
         ))


content()
    }
}

@Composable
private fun HeroInfoPanel(
    item:        MediaItem,
    isFavourite: Boolean,
    onPlay:      () -> Unit,
    onFavourite: () -> Unit,
    accentColor: Color,
    modifier:    Modifier = Modifier,
) {

    val contentColor = Color.White

    Column(modifier = modifier) {

        // ── Title ────────────────────────────────────────────────────────────
        Text(
            text     = item.displayTitle,
            style    = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color    = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Rating + Genres row ───────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Star rating chip
            RatingChip(
                score        = item.voteAverage ?: 0.0,
                accentColor  = accentColor,
                contentColor = contentColor
            )

            // Genre chips — max 3

            item.genreNames.take(3).forEach { genreName ->
                GenreChip(
                    label        = genreName,
                    contentColor = contentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Action buttons ────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            // ── Play button — primary solid action ────────────────────────
            Button(
                onClick  = onPlay,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor   = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector        = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text  = "Play",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // ── Favourite button — glass icon button ──────────────────────

            val favouriteScale by animateFloatAsState(
                targetValue   = if (isFavourite) 1.25f else 1.0f,
                animationSpec = tween(200),
                label         = "fav_scale"
            )
            IconButton(
                onClick = onFavourite,
                colors  = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor   = if (isFavourite) Color(0xFFFF6B6B) else contentColor
                ),
                modifier = Modifier
                    .size(52.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.30f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector        = if (isFavourite)
                        Icons.Filled.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourite) "Remove from favourites"
                    else "Add to favourites",
                    modifier           = Modifier
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = favouriteScale
                            scaleY = favouriteScale
                        }
                )
            }
        }
    }
}

// ── Rating chip ───────────────────────────────────────────────────────────────
@Composable
private fun RatingChip(
    score:        Double,
    accentColor:  Color,
    contentColor: Color,
) {
    // Scale 0–10 → display as X.X
    val display = String.format("%.1f", score.coerceIn(0.0, 10.0))

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = accentColor.copy(alpha = 0.35f),
        modifier = Modifier
            .border(
                1.dp,
                accentColor.copy(alpha = 0.50f),
                RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector        = Icons.Filled.Star,
                contentDescription = null,
                tint               = Color(0xFFFFC107),   // amber star
                modifier           = Modifier.size(13.dp)
            )
            Text(
                text  = display,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )
        }
    }
}

// ── Genre chip ────────────────────────────────────────────────────────────────
@Composable
private fun GenreChip(
    label:        String,
    contentColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),    // pill
        color = Color.White.copy(alpha = 0.12f),
        modifier = Modifier
            .border(
                0.8.dp,
                Color.White.copy(alpha = 0.25f),
                RoundedCornerShape(50)
            )
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1
        )
    }
}

// ── Canvas noise helper ───────────────────────────────────────────────────────
private fun DrawScope.drawNoise(color: Color, alpha: Float, density: Int) {
    val rng = java.util.Random( 42L)    // fixed seed — stable across frames
    repeat(density) {
        drawCircle(
            color  = color.copy(alpha = alpha * rng.nextFloat()),
            radius = rng.nextFloat() * 1.2f,
            center = Offset(
                x = rng.nextFloat() * size.width,
                y = rng.nextFloat() * size.height
            )
        )
    }
}

// ── Palette extraction ────────────────────────────────────────────────────────
private suspend fun extractPalette(
    context: android.content.Context,
    url:     String,
): Palette? = withContext(Dispatchers.IO) {
    try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .allowHardware(false)
            .size(64, 96)
            .build()
        val bitmap = context.imageLoader
            .execute(request)
            .image
            ?.toBitmap()
            ?: return@withContext null
        Palette.from(bitmap).maximumColorCount(16).generate()
    } catch (e: Exception) {
        null
    }
}
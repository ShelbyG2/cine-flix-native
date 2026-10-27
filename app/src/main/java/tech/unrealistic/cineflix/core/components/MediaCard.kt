package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv
import tech.unrealistic.cineflix.helpers.genreNames
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.ShapeSmall

/**
 * Poster card.
 *
 * - Grid / row card: 2:3 poster with an 8dp radius, no border, title below in
 *   `titleMedium`. Favourite and save keep their corners on a subtle dark disc.
 * - Hero card: poster fills the carousel cell, a bottom scrim carries the title,
 *   rating and genre chips.
 */
@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    media: MediaItem,
    hero: Boolean? = null,
    isFocused: Boolean? = null,
    fillWidth: Boolean = false,
    isSaved: Boolean = false,
    onSaveToggle: (() -> Unit)? = null,
    isFavourite: Boolean = false,
    onFavouriteToggle: (() -> Unit)? = null,
) {
    val isHero = hero ?: false
    val sizes = AppTheme.sizes
    val spacing = AppTheme.spacing

    val displayTitle = when (media) {
        is Movie -> media.title
        is Tv -> media.name
    }
    val posterUrl = media.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" }

    if (isHero) {
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Bottom scrim so the title and chips stay AA over any poster.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.55f to Color.Transparent,
                                1.00f to AppTheme.colors.image.scrimStrong,
                            )
                        )
                    )
            )

            if (isFocused == true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(spacing.lg)
                ) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppTheme.colors.image.onImage,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        RatingPill(score = media.voteAverage)
                        media.genreNames.take(3).forEach { genreName ->
                            OverlayChip(label = genreName)
                        }
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = if (fillWidth) modifier.fillMaxWidth() else modifier.width(sizes.mediaCardWidth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 2f / 3f)
                .clip(ShapeSmall)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Favourite (heart) — top-start, 48dp target on a subtle dark disc.
            if (onFavouriteToggle != null) {
                IconButton(
                    onClick = onFavouriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(sizes.overlayButtonPad)
                        .size(sizes.touchTarget)
                        .clip(ShapeCircle)
                        .background(AppTheme.colors.image.chipSurface)
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavourite) "Remove from favourites"
                        else "Add to favourites",
                        tint = if (isFavourite) AppTheme.colors.accent.favourite
                        else AppTheme.colors.image.onImage,
                        modifier = Modifier.size(sizes.iconSm)
                    )
                }
            }

            // Save (bookmark) — top-end, one-tap watchlist.
            if (onSaveToggle != null) {
                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(sizes.overlayButtonPad)
                        .size(sizes.touchTarget)
                        .clip(ShapeCircle)
                        .background(AppTheme.colors.image.chipSurface)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Remove from saved"
                        else "Save for later",
                        tint = if (isSaved) AppTheme.colors.accent.saved
                        else AppTheme.colors.image.onImage,
                        modifier = Modifier.size(sizes.iconSm)
                    )
                }
            }
        }

        Text(
            text = displayTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = spacing.sm),
        )
    }
}

/** Rating chip over imagery: white star, white label, translucent disc. */
@Composable
private fun RatingPill(score: Double) {
    val spacing = AppTheme.spacing
    val display = String.format("%.1f", score.coerceIn(0.0, 10.0))

    Surface(
        shape = ShapePill,
        color = AppTheme.colors.image.chipSurface,
        modifier = Modifier.border(
            width = AppTheme.sizes.hairline,
            color = AppTheme.colors.image.chipBorder,
            shape = ShapePill
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = spacing.sm,
                vertical = spacing.xs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.inlineGap)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AppTheme.colors.accent.rating,
                modifier = Modifier.size(AppTheme.sizes.iconXs)
            )
            Text(
                text = display,
                style = MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.image.onImage
            )
        }
    }
}

/** Genre chip over imagery. */
@Composable
private fun OverlayChip(label: String) {
    val spacing = AppTheme.spacing

    Surface(
        shape = ShapePill,
        color = AppTheme.colors.image.onChipSurface,
        modifier = Modifier.border(
            width = AppTheme.sizes.hairline,
            color = AppTheme.colors.image.chipBorder,
            shape = ShapePill
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.image.onImageMuted,
            maxLines = 1,
            modifier = Modifier.padding(
                horizontal = spacing.sm,
                vertical = spacing.xs
            )
        )
    }
}

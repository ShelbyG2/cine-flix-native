package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
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
import tech.unrealistic.cineflix.ui.theme.withAlpha


@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    media: MediaItem,
    hero: Boolean? = null,
    isFocused: Boolean? = null,
    accentColor: Color? = null,
    fillWidth: Boolean = false,   // true → poster stretches to the parent width (grid cells)
    isSaved: Boolean = false,
    onSaveToggle: (() -> Unit)? = null,
    isFavourite: Boolean = false,
    onFavouriteToggle: (() -> Unit)? = null,
) {
    val isHero = hero ?: false
    val mediaType = media.mediaType

    val displayTitle = when (media) {
        is Movie -> media.title
        is Tv -> media.name
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = if (isHero) modifier else if (fillWidth) Modifier.fillMaxWidth() else Modifier.width(AppTheme.sizes.mediaCardWidth)

    ) {
        Card(
            modifier = modifier.fillMaxSize(),
//            shape = if (isHero) RectangleShape else CardDefaults.shape
        ) {


            val posterUrl = media.posterPath
                ?.let { path -> "https://image.tmdb.org/t/p/w500$path" }
            Box() {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = displayTitle,

                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                0.5f to Color.Transparent,
                                1.0f to MaterialTheme.colorScheme.background
                                    .withAlpha(AppTheme.effects.posterFadeAlpha)
                            )
                        )
                )
                // ── Favourite (heart) overlay — top-start corner, quick-remove ──
                if (onFavouriteToggle != null && !isHero) {
                    IconButton(
                        onClick = onFavouriteToggle,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(AppTheme.sizes.overlayButtonPad)
                            .size(AppTheme.sizes.touchTarget)   // 48dp accessible target
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
                            modifier = Modifier.size(AppTheme.sizes.iconSm)
                        )
                    }
                }

                // ── Save (bookmark) overlay — top-end corner, one-tap watchlist ──
                if (onSaveToggle != null && !isHero) {
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(AppTheme.sizes.overlayButtonPad)
                            .size(AppTheme.sizes.touchTarget)   // 48dp accessible target
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
                            modifier = Modifier.size(AppTheme.sizes.iconSm)
                        )
                    }
                }

                if (isHero && isFocused == true) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(AppTheme.spacing.lg)
                    ) {
                        // Over imagery: always light, guaranteed by the card's gradient scrim
                        val contentColor = AppTheme.colors.image.onImage
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                        // Rating + Genres row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                        ) {
                            // Star rating chip
                            RatingChip(
                                score = media.voteAverage,
                                accentColor = accentColor,
                                contentColor = contentColor
                            )

                            // Genre chips — max 3

                            media.genreNames.take(3).forEach { genreName ->
                                GenreChip(
                                    label = genreName, contentColor = contentColor
                                )
                            }
                        }
                    }
                }

            }
        }

        if (!isHero)
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,


                )
    }


}

@Composable
private fun GenreChip(
    label: String,
    contentColor: Color,
) {
    Surface(
        shape = ShapePill,
        color = AppTheme.colors.image.onChipSurface,
        modifier = Modifier.border(
            AppTheme.sizes.glassBorderWidth,
            AppTheme.colors.image.chipBorder,
            ShapePill
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.chipPaddingH,
                vertical = AppTheme.spacing.chipPaddingV
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun RatingChip(
    score: Double,
    accentColor: Color?,
    contentColor: Color,
) {
    // Scale 0–10 → display as X.X
    val display = String.format("%.1f", score.coerceIn(0.0, 10.0))
    // Palette extraction can return null — fall back to the theme primary.
    val chipColor = accentColor ?: MaterialTheme.colorScheme.primary

    Surface(
        shape = MaterialTheme.shapes.small,
        color = chipColor.withAlpha(AppTheme.effects.ratingChipFillAlpha),
        modifier = Modifier.border(
            AppTheme.sizes.borderWidth,
            chipColor.withAlpha(AppTheme.effects.ratingChipBorderAlpha),
            MaterialTheme.shapes.small
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.sm,
                vertical = AppTheme.spacing.chipPaddingV
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.chipRowGap)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = AppTheme.colors.accent.rating,
                modifier = Modifier.size(AppTheme.sizes.iconXs)
            )
            Text(
                text = display,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}
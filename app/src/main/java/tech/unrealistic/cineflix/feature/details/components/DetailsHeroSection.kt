package tech.unrealistic.cineflix.feature.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.ExpandableText
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle
import tech.unrealistic.cineflix.helpers.genreNames
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.HeadlineStrongStyle
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill

@Composable
 fun DetailSheetHeroSection(
    mediaItem: MediaItem,
    isTvShow: Boolean,
    onDismissRequest: () -> Unit,
    onPlayClick: (Any) -> Unit,
    onFavourite: (Any) -> Unit,
    onMediaShare: (Any) -> Unit,
    isFavourite: Boolean = false,
    isSaved: Boolean = false,
    onSave: (Any) -> Unit = {}
) {

    val backdropPath = mediaItem.backdropPath
    val title = mediaItem.displayTitle
    val genres = mediaItem.genreNames
    val releaseYear = mediaItem.releaseDate?.substringBefore("-")
    val runtimeOrSeasons = if (isTvShow) "Seasons" else "Runtime"
    val country = mediaItem.originCountry?.firstOrNull()
    val overview = mediaItem.overview
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val colors = AppTheme.colors

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.detailsHeroHeight)
        ) {
            val posterUrl = "https://image.tmdb.org/t/p/w500$backdropPath"

            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizes.detailsHeroFadeH)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = spacing.sm, end = spacing.sm)
                    .size(sizes.touchTarget)   // 48dp dismiss target
            ) {
                Box(
                    modifier = Modifier
                        .size(sizes.likeButton)
                        .clip(ShapeCircle)
                        .background(colors.scrimStrong)
                        .border(
                            width = sizes.glassBorderWidth,
                            color = colors.pillBorder,
                            shape = ShapeCircle
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.onImage,   // light in BOTH modes over dark disc
                        modifier = Modifier.size(sizes.iconCompact)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md)
                .offset(y = -spacing.titleOverlap)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.padding(bottom = spacing.sm)
            ) {
                items(genres) { genre ->
                    Surface(
                        shape = ShapePill,
                        // Solid role — solid chip keeps text AA over the backdrop fade
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            sizes.hairline,
                            colors.stroke
                        )
                    ) {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = spacing.chipPaddingH,
                                vertical = spacing.chipPaddingV
                            )
                        )
                    }
                }
            }

            Text(
                text = title,
                style = HeadlineStrongStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = spacing.gap6, bottom = spacing.md),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = releaseYear!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = runtimeOrSeasons,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (country!!.isNotEmpty()) {
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ActionButtons(
                item = mediaItem,
                onMediaShare = { onMediaShare(mediaItem) },
                isFavourite = isFavourite,
                onFavourite = { onFavourite(mediaItem) },
                isSaved = isSaved,
                onSave = { onSave(mediaItem) },
                onPlay = { onPlayClick(mediaItem) }
            )

            if (overview!!.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.md))
                ExpandableText(
                    text = overview,
                    collapsedMaxLines = 3
                )
            }
        }
    }
}

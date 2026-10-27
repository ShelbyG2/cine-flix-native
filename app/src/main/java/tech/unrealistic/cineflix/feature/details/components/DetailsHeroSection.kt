package tech.unrealistic.cineflix.feature.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.core.components.ExpandableText
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle
import tech.unrealistic.cineflix.helpers.genreNames
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailSheetHeroSection(
    mediaItem: MediaItem,
    isTvShow: Boolean,
    onDismissRequest: () -> Unit,
    onPlayClick: (MediaItem) -> Unit,
    onFavourite: (MediaItem) -> Unit,
    onMediaShare: (MediaItem) -> Unit,
    isFavourite: Boolean = false,
    isSaved: Boolean = false,
    onSave: (MediaItem) -> Unit = {}
) {

    val backdropUrl = mediaItem.backdropPath
        ?.let { path -> "https://image.tmdb.org/t/p/w500$path" }
    val title = mediaItem.displayTitle
    val genres = mediaItem.genreNames
    val releaseYear = mediaItem.releaseDate?.substringBefore("-")
    val runtimeOrSeasons = if (isTvShow) "Seasons" else "Runtime"
    val country = mediaItem.originCountry?.firstOrNull()
    val overview = mediaItem.overview
    val metadata = listOfNotNull(releaseYear, runtimeOrSeasons, country)
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val colors = AppTheme.colors

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Backdrops are 16:9 — let the width decide the height instead
                // of fixing a pixel height that crops on tall/short screens.
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = backdropUrl,
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
                        .size(sizes.closeButtonVisual)   // visual size only
                        .clip(ShapeCircle)
                        .background(colors.image.scrimStrong)
                        .border(
                            width = sizes.glassBorderWidth,
                            color = colors.image.pillBorder,
                            shape = ShapeCircle
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close details",
                        tint = colors.image.onImage,   // light in BOTH modes over dark disc
                        modifier = Modifier.size(sizes.iconSm)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg)
                .offset(y = -spacing.titleOverlap)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.padding(bottom = spacing.sm)
            ) {
                genres.take(4).forEach { genre ->
                    Surface(
                        shape = ShapePill,
                        // Solid role — keeps text AA over the backdrop fade
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(
                            sizes.hairline,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
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
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = spacing.sm, bottom = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Only render fields TMDB actually returned — never "null".
                metadata.forEachIndexed { index, value ->
                    if (index > 0) {
                        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = value,
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

            overview?.takeIf { it.isNotBlank() }?.let { text ->
                Spacer(modifier = Modifier.height(spacing.lg))
                ExpandableText(
                    text = text,
                    collapsedMaxLines = 3
                )
            }
        }
    }
}

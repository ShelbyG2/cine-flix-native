package tech.unrealistic.cineflix.feature.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.helpers.formatTime
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.TitleStrongStyle

@Composable
 fun TvEpisodeItem(
    episode: Episode,
    isLast: Boolean,
    isLiked: Boolean = false,
    onPlay: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null
) {

    val stillPath = episode.stillPath
    val name = episode.name
    val overview = episode.overview.takeIf { it.isNotBlank() }
    val runtime = episode.runtime?.let { formatTime(it) }
    val airDate = episode.airDate

    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val colors = AppTheme.colors

    Column(modifier = Modifier.padding(horizontal = spacing.md)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onPlay != null) {
                        Modifier.clickable(
                            onClickLabel = "Play episode ${episode.episodeNumber}",
                            onClick = onPlay
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(vertical = spacing.sm),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(sizes.episodeStillW)
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.small)
                    .background(colors.skeletonBase)
            ) {
                AsyncImage(
                    model = stillPath?.let { path -> "https://image.tmdb.org/t/p/w300$path" },
                    contentDescription = "Episode ${episode.episodeNumber}: $name",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (onPlay != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizes.iconXl)
                            .clip(ShapeCircle)
                            .background(colors.scrimStrong),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            // The row's click label already names the action.
                            contentDescription = null,
                            tint = colors.onImage,
                            modifier = Modifier.size(sizes.iconSm)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = spacing.gap12)
                    .weight(1f)
            ) {
                Text(
                    text = "${episode.episodeNumber}. $name",
                    style = TitleStrongStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                overview?.let { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = spacing.xs)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.gap12),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dedicated colour role — AA in both modes (was a hardcoded grey)
                        runtime?.let { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = airDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (onLike != null) {
                        IconButton(
                            onClick = onLike,
                            modifier = Modifier.size(sizes.likeButton)  // 48dp target
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isLiked) "Unlike episode"
                                else "Like episode",
                                tint = if (isLiked) colors.favouriteActive
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(sizes.iconCompact)
                            )
                        }
                    }
                }
            }
        }

        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = spacing.xs),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = sizes.hairline
            )
        }
    }
}
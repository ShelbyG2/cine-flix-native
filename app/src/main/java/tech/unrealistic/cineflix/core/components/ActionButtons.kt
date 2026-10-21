package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill

/**
 * Action row used by the home hero and the details sheet:
 * one solid primary Play pill plus the existing circular icon actions.
 * Same actions, same order, same content descriptions.
 */
@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    item: MediaItem?,
    isFavourite: Boolean,
    onPlay: () -> Unit,
    onFavourite: (() -> Unit)? = null,
    onMediaClick: ((Int, String) -> Unit)? = null,
    onMediaShare: (() -> Unit)? = null,
    isSaved: Boolean = false,
    onSave: (() -> Unit)? = null
) {
    val colors = AppTheme.colors
    val motion = AppTheme.motion
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    // The row sits over (scrimmed) imagery in both themes.
    val contentColor = colors.image.onImage

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = spacing.lg,
                    vertical = spacing.lg
                ),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onPlay,
                shape = ShapePill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = spacing.buttonPaddingH,
                    vertical = spacing.buttonPaddingV
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = sizes.touchTarget)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(sizes.iconMd)
                )
                Spacer(modifier = Modifier.width(spacing.buttonGap))
                Text(text = "Play", style = MaterialTheme.typography.labelLarge)
            }

            if (onMediaClick != null) {
                IconDiscButton(
                    onClick = { onMediaClick(item?.id ?: -1, item?.mediaType ?: "") },
                    contentColor = contentColor
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Show details",
                        modifier = Modifier.size(sizes.iconLg)
                    )
                }
            }

            // Save (watchlist) — keeps its position, pops on activation.
            val savedScale by animateFloatAsState(
                targetValue = when {
                    motion.reduceMotion -> 1.0f
                    isSaved -> 1.15f
                    else -> 1.0f
                },
                animationSpec = motion.pressSpec,
                label = "save_scale"
            )

            if (onSave != null) {
                IconDiscButton(
                    onClick = onSave,
                    contentColor = if (isSaved) colors.accent.saved else contentColor
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Remove from saved"
                        else "Save for later",
                        modifier = Modifier
                            .size(sizes.iconLg)
                            .graphicsLayer {
                                scaleX = savedScale
                                scaleY = savedScale
                            }
                    )
                }
            }

            // Favourite heart — the only chromatic element in the row.
            val favouriteScale by animateFloatAsState(
                targetValue = when {
                    motion.reduceMotion -> 1.0f
                    isFavourite -> 1.15f
                    else -> 1.0f
                },
                animationSpec = motion.pressSpec,
                label = "fav_scale"
            )

            if (onFavourite != null) {
                IconDiscButton(
                    onClick = onFavourite,
                    contentColor = if (isFavourite) colors.accent.favourite else contentColor
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavourite) "Remove from favourites"
                        else "Add to favourites",
                        modifier = Modifier
                            .size(sizes.iconLg)
                            .graphicsLayer {
                                scaleX = favouriteScale
                                scaleY = favouriteScale
                            }
                    )
                }
            }

            if (onMediaShare != null) {
                IconDiscButton(
                    onClick = onMediaShare,
                    contentColor = contentColor
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(sizes.iconLg)
                    )
                }
            }
        }
    }
}

/** Circular secondary action on a subtle dark disc with a hairline rim. */
@Composable
private fun IconDiscButton(
    onClick: () -> Unit,
    contentColor: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit,
) {
    val sizes = AppTheme.sizes

    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = AppTheme.colors.image.scrimSoft,
            contentColor = contentColor
        ),
        modifier = Modifier
            .size(sizes.actionButton)
            .border(
                width = sizes.hairline,
                color = AppTheme.colors.image.pillBorder,
                shape = ShapeCircle
            )
    ) {
        content()
    }
}

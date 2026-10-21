package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Bookmark
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

    // Always light-on-dark: buttons sit over (scrimmed) imagery in BOTH modes
    val contentColor = colors.image.onImage

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = spacing.actionRowH,
                    vertical = spacing.actionRowV
                ),
            horizontalArrangement = Arrangement.spacedBy(spacing.gap12),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Play button  primary solid action
            Button(
                onClick = onPlay,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.image.onImage, contentColor = colors.image.onImageInverse
                ),
                contentPadding = PaddingValues(
                    horizontal = spacing.buttonPaddingH,
                    vertical = spacing.buttonPaddingV
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(sizes.iconMd)
                )
                Spacer(modifier = Modifier.width(spacing.buttonGap))
                Text(text = "Play", style = MaterialTheme.typography.labelLarge)
            }
            if (onMediaClick != null) {  // Info button glass icon button
                val detailScale by animateFloatAsState(
                    targetValue = 1.0f,
                    animationSpec = tween(motion.shortMs),
                    label = "fav_scale"
                )
                IconButton(
                    onClick = { onMediaClick(item?.id ?: -1, item?.mediaType ?: "") },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.image.scrimSoft,
                        contentColor = contentColor
                    ), modifier = Modifier
                        .size(sizes.actionButton)
                        .border(
                            width = sizes.borderWidth,
                            color = colors.image.pillBorder,
                            shape = ShapeCircle
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Show details",
                        modifier = Modifier
                            .size(sizes.iconLg)
                            .graphicsLayer {
                                scaleX = detailScale
                                scaleY = detailScale
                            })
                }
            }

            //  Favourite button glass icon button
            //  Pop animations are decorative — disabled under reduce-motion
            val favouriteScale by animateFloatAsState(
                targetValue = when {
                    motion.reduceMotion -> 1.0f
                    isFavourite -> 1.25f
                    else -> 1.0f
                },
                animationSpec = tween(motion.shortMs),
                label = "fav_scale"
            )

            //  Saved (watchlist) button glass icon button
            val savedScale by animateFloatAsState(
                targetValue = when {
                    motion.reduceMotion -> 1.0f
                    isSaved -> 1.25f
                    else -> 1.0f
                },
                animationSpec = tween(motion.shortMs),
                label = "save_scale"
            )

            if (onSave != null) {
                IconButton(
                    onClick = onSave, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.image.scrimSoft,
                        contentColor = if (isSaved) colors.accent.saved else contentColor
                    ), modifier = Modifier
                        .size(sizes.actionButton)
                        .border(
                            width = sizes.borderWidth,
                            color = colors.image.pillBorder,
                            shape = ShapeCircle
                        )
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
                            })
                }
            }

            if (onFavourite != null) {
                IconButton(
                    onClick = onFavourite, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.image.scrimSoft,
                        contentColor = if (isFavourite) colors.accent.favourite else contentColor
                    ), modifier = Modifier
                        .size(sizes.actionButton)
                        .border(
                            width = sizes.borderWidth,
                            color = colors.image.pillBorder,
                            shape = ShapeCircle
                        )
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
                            })
                }
            }

            //share button
            val onShare = null
            if (onMediaShare != null) {
                IconButton(
                    onClick = onMediaShare, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.image.scrimSoft,
                        contentColor = contentColor

                        ), modifier = Modifier
                        .size(sizes.actionButton)
                        .border(
                            width = sizes.borderWidth,
                            color = colors.image.pillBorder,
                            shape = ShapeCircle
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        modifier = Modifier
                            .size(sizes.iconLg)
                            .graphicsLayer {
                                scaleX = favouriteScale
                                scaleY = favouriteScale
                            })
                }
            }
        }
    }
}


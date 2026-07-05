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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.data.remote.models.MediaItem

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    item: MediaItem?,
    isFavourite: Boolean,
    onPlay: () -> Unit,
    onFavourite: (() -> Unit)? = null,
    onMediaClick: ((Int, String) -> Unit)? = null,
    onMediaShare: (()->Unit )? = null
) {

    val contentColor = Color.White

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
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
            if (onMediaClick != null) {  // Info button glass icon button
                val detailScale by animateFloatAsState(
                    targetValue = 1.0f,
                    animationSpec = tween(200),
                    label = "fav_scale"
                )
                IconButton(
                    onClick = { onMediaClick(item?.id ?: -1, item?.mediaType ?: "") },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = contentColor
                    ), modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.30f),
                            shape = CircleShape
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
            }

            //  Favourite button glass icon button
            val favouriteScale by animateFloatAsState(
                targetValue = if (isFavourite) 1.25f else 1.0f,
                animationSpec = tween(200),
                label = "fav_scale"
            )



            if (onFavourite != null) {
                IconButton(
                    onClick = onFavourite, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = if (isFavourite) Color(0xFFFF6B6B) else contentColor
                    ), modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.30f),
                            shape = CircleShape
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

            //share button
            val onShare = null
            if (onMediaShare != null) {
                IconButton(
                    onClick = onMediaShare, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),

                        ), modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.30f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
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
}


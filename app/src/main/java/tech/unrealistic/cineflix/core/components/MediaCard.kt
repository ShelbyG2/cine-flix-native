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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv
import tech.unrealistic.cineflix.helpers.genreNames


@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    media: MediaItem,
    hero: Boolean? = null,
    isFocused: Boolean? = null,
    accentColor: Color? = null,
) {
    val isHero = hero ?: false
    val mediaType = media.mediaType

    val displayTitle = when (media) {
        is Movie -> media.title
        is Tv -> media.name
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = if (isHero) modifier else Modifier.width(140.dp)

    ) {
        Card(
            modifier = modifier.fillMaxSize(),
//            shape = if (isHero) RectangleShape else CardDefaults.shape
        ) {


            val posterUrl = "https://image.tmdb.org/t/p/w500${media.posterPath}"
            Box() {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = displayTitle,

                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(

                                0.5f to Color.Transparent,
                                1.0f to MaterialTheme.colorScheme.background.copy(
                                    0.95f

                                )
                            )
                        )
                )
                if (isHero && isFocused == true) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        val contentColor = Color.White
                        Text(
                            text = displayTitle, style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ), color = contentColor, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating + Genres row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Star rating chip
                            RatingChip(
                                score = media.voteAverage ?: 0.0,
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
                text = displayTitle ?: "Title ",
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
        shape = RoundedCornerShape(50),    // pill
        color = Color.White.copy(alpha = 0.12f), modifier = Modifier.border(
            0.8.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50)
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = accentColor!!.copy(alpha = 0.35f),
        modifier = Modifier.border(
            1.dp, accentColor.copy(alpha = 0.50f), RoundedCornerShape(8.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),   // amber star
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = display, style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ), color = contentColor
            )
        }
    }
}
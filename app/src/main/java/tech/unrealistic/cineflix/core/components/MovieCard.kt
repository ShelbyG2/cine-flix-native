package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import tech.unrealistic.cineflix.data.remote.models.Movie


@Composable
fun MovieCard(modifier: Modifier = Modifier, movie: Movie
) {
    Card( modifier= Modifier.width(150.dp).height(250.dp)) {
        Box(modifier= Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            AsyncImage(
                model = posterUrl,
                contentDescription = movie.title,
                modifier= Modifier
                    .fillMaxSize()
                    .aspectRatio(3f/5f),
                contentScale = ContentScale.FillBounds
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // height of gradient area
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
//        Column(
//            modifier = Modifier
//                .padding(horizontal = 8.dp, vertical = 10.dp)
//        ) {
//            // Movie Title
//
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            // Release Year / Subtext
//            val releaseYear = movie.releaseDate?.split("-")?.firstOrNull() ?: "Unknown"
//            Text(
//                text = releaseYear,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
    }
}
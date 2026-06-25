package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv


@Composable
fun MediaCard(modifier: Modifier = Modifier,
              media: MediaItem,
              hero: Boolean? = null
) {
    val isHero = hero?: false

         val displayTitle = when (media){
             is Movie -> media.title
             is Tv -> media.name
         }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier= if (isHero){
            Modifier.fillMaxSize()
        }else{Modifier.width(140.dp) }
    ) {
        Card(
            modifier = Modifier.fillMaxSize()
        ) {


            val posterUrl = "https://image.tmdb.org/t/p/w500${media.posterPath}"
            AsyncImage(
                model = posterUrl,
                contentDescription = displayTitle,

                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )

        }
        Box(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors=listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(0.95f))
                         ))
        )
        if (!isHero)
        Text(
            text = displayTitle ?: "Unknown title ",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,


        )
    }



}
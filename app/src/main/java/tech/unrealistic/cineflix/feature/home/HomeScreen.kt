package tech.unrealistic.cineflix.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.core.components.CustomTopBar
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (onNavigate :(()-> Unit)? =null,
                viewModel: HomeViewModel = viewModel()

){
    //observe any changes in the view model
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            CustomTopBar(
                onSearchClick = {onNavigate?.invoke()}
            )
        },
        containerColor = MaterialTheme.colorScheme.background

    ) {


        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                is HomeUiState.Success -> {
                    val carouselState = rememberCarouselState(itemCount = { state.trendingMixed.size })


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    ) {

                        HorizontalCenteredHeroCarousel(
                            state = carouselState,
                            itemSpacing = 8.dp,

                            ) { itemIndex ->
                            val heroShow = state.trendingMixed.getOrNull(itemIndex)
                            if (heroShow != null)
                                MediaCard(
                                    media = heroShow,
                                    hero = true
                                )

                        }
                        val focusedItem = state.trendingMixed.getOrNull(carouselState.currentItem)



                        MediaSection(
                            title = "TrendingMovies",
                            movies = state.trendingMovies,
                            onNavigate = onNavigate,
                        )
                        MediaSection(
                            title = "Trending Tv Shows",
                            tvShows = state.trendingTvShows,
                            onNavigate = onNavigate
                        )
                    }

                }

                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

}


@Composable
fun MediaSection (
    title: String,
    movies: List<Movie> = emptyList(),
    tvShows: List<Tv> = emptyList(),
    onNavigate: (()-> Unit)?,
    modifier: Modifier = Modifier
){
Column(modifier = modifier.padding(bottom = 24.dp)) {


    Text(
        text = title,
         color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp,
        fontStyle = FontStyle.Normal,
        modifier = Modifier.padding(16.dp, 8.dp),
        textAlign = TextAlign.Center
    )
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .height(210.dp)

    ) {
        if(movies.isNotEmpty()){
            items (items = movies, key={it.id}){ movie->
                MediaCard(media = movie,

                )

            }
        }else {
            items(items = tvShows, key = {it.id}){ tv ->
                MediaCard( media = tv,

                )

            }
        }
    }
}
}
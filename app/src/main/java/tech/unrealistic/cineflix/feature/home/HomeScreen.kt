package tech.unrealistic.cineflix.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.unrealistic.cineflix.core.components.MovieCard
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.Movie


@Composable
fun HomeScreen (onNavigate :(()-> Unit)? =null, viewModel: HomeViewModel = viewModel() ){
    //observe any changes in the view model
    val uiState by viewModel.uiState.collectAsState()
    Box(Modifier
        .fillMaxSize()
        .padding(16.dp)){
        when( val state = uiState){
            is HomeUiState.Loading ->{
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is HomeUiState.Success ->{
                MovieGrid(movies = state.data.results)
                
            }
            is HomeUiState.Error->{
                Text(text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }


}
@Composable
fun MovieGrid(modifier: Modifier = Modifier, movies: List<Movie>) {
    LazyHorizontalGrid (
        rows = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items( items=movies ,key={it.id}){ movie ->
            MovieCard(movie = movie)
        }
    }
}
package tech.unrealistic.cineflix.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv

sealed interface HomeUiState{
    object Loading: HomeUiState
    data class Success(
        val trendingMovies: List<Movie>,
        val trendingTvShows: List<Tv>,
        val trendingMixed: List<MediaItem>
    ): HomeUiState
    data class Error ( val message: String ): HomeUiState
}

class HomeViewModel : ViewModel(){
    // _uiState is private so only the ViewModel can change it.
// uiState is public so the UI can read it, but can't accidentally overwrite it.
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
            init{
                fetchAllHomeData()
            }
    private fun fetchAllHomeData(){
        viewModelScope.launch {
            _uiState.value= HomeUiState.Loading
            try {
                val trendingMoviesDeferred  = async { RetrofitClient.tmdbService.getTrendingMovies() }
                val trendingTvDeferred = async { RetrofitClient.tmdbService.getTrendingShows() }
                val trendingDeferred = async { RetrofitClient.tmdbService.getTrendingMixed() }

                val movieResponse=  trendingMoviesDeferred.await()
                val tvResponse = trendingTvDeferred.await()
                val trendingMixedResponse = trendingDeferred.await()


                //If successful, pass data to UI
                _uiState.value= HomeUiState.Success(
                    movieResponse.results,
                    tvResponse.results,
                    trendingMixedResponse.results

                )
            }catch (e : Exception){
                _uiState.value= HomeUiState.Error(e.localizedMessage?: "An unknown error occurred")
            }
        }
    }
}
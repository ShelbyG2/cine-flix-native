package tech.unrealistic.cineflix.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.TmdbService
import tech.unrealistic.cineflix.data.remote.models.TmdbResponse

sealed interface HomeUiState{
    object Loading: HomeUiState
    data class Success(val data: TmdbResponse): HomeUiState
    data class Error ( val message: String ): HomeUiState
}

class HomeViewModel : ViewModel(){
    // _uiState is private so only the ViewModel can change it.
// uiState is public so the UI can read it, but can't accidentally overwrite it.
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
            init{
                fetchPopularMovies()
            }
    private fun fetchPopularMovies(){
        viewModelScope.launch {
            _uiState.value= HomeUiState.Loading
            try {
                val response = RetrofitClient.tmdbService.getPopularMovies()

                //If successful, pass data to UI
                _uiState.value= HomeUiState.Success(response)
            }catch (e : Exception){
                _uiState.value= HomeUiState.Error(e.localizedMessage?: "An unknown error occurred")
            }
        }
    }
}
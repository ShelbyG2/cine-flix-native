package tech.unrealistic.cineflix.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.MediaItem

sealed interface DetailsSheetState {
    object Loading : DetailsSheetState
    data class Success(
        val selectedShow: MediaItem,
        val similarShows: List<MediaItem>,
    ) : DetailsSheetState

    data class Error(
        val message: String
    ) : DetailsSheetState
}

class DetailsSheetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsSheetState>(DetailsSheetState.Loading)
    val uiState: StateFlow<DetailsSheetState> = _uiState.asStateFlow()


    fun fetchSheetDetails(mediaType: String, mediaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsSheetState.Loading
            try {
                supervisorScope {
                    val detailsDeferred = async {
                        if (mediaType == "movie") RetrofitClient.tmdbService.getMovieDetail(mediaId)
                        else RetrofitClient.tmdbService.getTvDetail(mediaId)
                    }

                    val similarDeferred = async {
                        if (mediaType == "movie") RetrofitClient.tmdbService.getSimilarMovie(mediaId)
                        else RetrofitClient.tmdbService.getSimilarTv(mediaId)
                    }

                    _uiState.value = DetailsSheetState.Success(
                        selectedShow = detailsDeferred.await(),
                        similarShows = similarDeferred.await().results
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DetailsSheetState.Error("An error occurred ${e.message}")
            }
        }

    }
}

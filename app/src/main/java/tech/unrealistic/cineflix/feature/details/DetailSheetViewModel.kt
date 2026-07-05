package tech.unrealistic.cineflix.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.RetrofitClient

class DetailsSheetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsSheetState>(DetailsSheetState.Loading)
    val uiState: StateFlow<DetailsSheetState> = _uiState.asStateFlow()


    fun fetchSheetDetails(mediaType: String, mediaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsSheetState.Loading
            try {
                val mediaDetails =if (mediaType=="movie") {
                    RetrofitClient.tmdbService.getMovieDetail(id = mediaId)
                }else RetrofitClient.tmdbService.getTvDetail(mediaId)
                _uiState.value = DetailsSheetState.Success(
                    mediaDetails
                )
            } catch (e: Exception) {
                _uiState.value = DetailsSheetState.Error("An error occurred ${e.message}")
            }
        }

    }
}

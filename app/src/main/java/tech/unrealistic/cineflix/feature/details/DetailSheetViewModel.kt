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
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv

sealed interface DetailsSheetState {
    object Loading : DetailsSheetState
    data class MovieSuccess(
        val movie: Movie,
        val similarShows: List<MediaItem>,
    ) : DetailsSheetState

    data class TvSuccess(
        val tv: Tv,
        val similarShows: List<MediaItem>,

        ) : DetailsSheetState

    data class Error(
        val message: String
    ) : DetailsSheetState
}

class DetailsSheetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsSheetState>(DetailsSheetState.Loading)
    val uiState: StateFlow<DetailsSheetState> = _uiState.asStateFlow()
    private val _currentSeasonEpisode = MutableStateFlow<List<Episode>>(emptyList())
    val currentSeasonEpisode: StateFlow<List<Episode>> = _currentSeasonEpisode.asStateFlow()

    fun fetchSheetDetails(mediaType: String, mediaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsSheetState.Loading
            try {
                supervisorScope {
                    if (mediaType == "movie") {
                        val MovieDetailsDeferred = async {
                            RetrofitClient.tmdbService.getMovieDetail(mediaId)
                        }
                        val similarDeferredMovie =
                            async { RetrofitClient.tmdbService.getSimilarMovie(mediaId) }
                        _uiState.value = DetailsSheetState.MovieSuccess(
                            MovieDetailsDeferred.await(), similarDeferredMovie.await().results
                        )
                    } else {
                        val TvDetailsDeferred = async {
                            RetrofitClient.tmdbService.getTvDetail(mediaId)
                        }
                        val similarDeferredTv =
                            async { RetrofitClient.tmdbService.getSimilarTv(mediaId) }
                        val initialSeasonDeferred = async {
                            RetrofitClient.tmdbService.getSeason(
                                seriesId = mediaId, seasonNumber = 1
                            )
                        }
                        _currentSeasonEpisode.value = initialSeasonDeferred.await().episodes
                        _uiState.value = DetailsSheetState.TvSuccess(
                            TvDetailsDeferred.await(),
                            similarDeferredTv.await().results,


                            )
                    }

                }
            } catch (e: Exception) {
                _uiState.value = DetailsSheetState.Error("An error occurred ${e.message}")
            }
        }

    }

    fun fetchEpisodesOnly(seasonNumber: Int, seriesId: Int) {
        viewModelScope.launch {
            try {
                val fetchedSeasonEpisodes = RetrofitClient.tmdbService.getSeason(
                    seriesId = seriesId, seasonNumber = seasonNumber
                )
                _currentSeasonEpisode.value = fetchedSeasonEpisodes.episodes

            } catch (e: Exception) {

            }
        }

    }
}

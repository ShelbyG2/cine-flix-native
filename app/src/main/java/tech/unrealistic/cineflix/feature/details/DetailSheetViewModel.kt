package tech.unrealistic.cineflix.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import tech.unrealistic.cineflix.data.local.LibraryRepository
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Tv
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface DetailsSheetState {
    object Loading : DetailsSheetState
    data class MovieSuccess(
        val movie: Movie,
        val similarShows: List<MediaItem>,
        /** Comes from the local favourites repository, never a literal. */
        val isFavourite: Boolean = false,
    ) : DetailsSheetState

    data class TvSuccess(
        val tv: Tv,
        val similarShows: List<MediaItem>,
        /** True while a newly selected season is being fetched. */
        val episodesLoading: Boolean = false,
        /** Human-readable season failure, null when the list is valid. */
        val episodesError: String? = null,
        /** Comes from the local favourites repository, never a literal. */
        val isFavourite: Boolean = false,
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

    private var episodesJob: Job? = null

    init {
        // Keep the sheet's favourite flag in sync with the local library so
        // toggles (including on "More Like This" items) reflect real state.
        viewModelScope.launch {
            LibraryRepository.favouriteKeys.collect { keys ->
                when (val current = _uiState.value) {
                    is DetailsSheetState.MovieSuccess -> {
                        val favourite = keys.contains(
                            LibraryRepository.favouriteKey(current.movie.id, current.movie.mediaType)
                        )
                        if (favourite != current.isFavourite) {
                            _uiState.value = current.copy(isFavourite = favourite)
                        }
                    }

                    is DetailsSheetState.TvSuccess -> {
                        val favourite = keys.contains(
                            LibraryRepository.favouriteKey(current.tv.id, current.tv.mediaType)
                        )
                        if (favourite != current.isFavourite) {
                            _uiState.value = current.copy(isFavourite = favourite)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    fun fetchSheetDetails(mediaType: String, mediaId: Int) {
        episodesJob?.cancel()
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
                            movie = MovieDetailsDeferred.await(),
                            similarShows = similarDeferredMovie.await().results,
                            isFavourite = favouriteSnapshot(mediaId, "movie")
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
                            tv = TvDetailsDeferred.await(),
                            similarShows = similarDeferredTv.await().results,
                            isFavourite = favouriteSnapshot(mediaId, "tv")
                        )
                    }

                }
            } catch (e: Exception) {
                _uiState.value = DetailsSheetState.Error(humanMessage(e))
            }
        }

    }

    fun fetchEpisodesOnly(seasonNumber: Int, seriesId: Int) {
        episodesJob?.cancel()
        episodesJob = viewModelScope.launch {
            val current = _uiState.value as? DetailsSheetState.TvSuccess ?: return@launch
            // Keep the show details on screen and show skeleton rows in place
            // of the episode list while the new season loads.
            _uiState.value = current.copy(episodesLoading = true, episodesError = null)
            try {
                val fetchedSeasonEpisodes = RetrofitClient.tmdbService.getSeason(
                    seriesId = seriesId, seasonNumber = seasonNumber
                )
                // Ignore a stale response if the sheet moved to another show.
                val latest = _uiState.value as? DetailsSheetState.TvSuccess ?: return@launch
                if (latest.tv.id != seriesId) return@launch

                _currentSeasonEpisode.value = fetchedSeasonEpisodes.episodes
                _uiState.value = latest.copy(episodesLoading = false)
            } catch (e: Exception) {
                val latest = _uiState.value as? DetailsSheetState.TvSuccess ?: return@launch
                if (latest.tv.id != seriesId) return@launch
                _uiState.value = latest.copy(
                    episodesLoading = false,
                    episodesError = humanMessage(e)
                )
            }
        }

    }

    private fun favouriteSnapshot(mediaId: Int, mediaType: String): Boolean =
        LibraryRepository.favouriteKeys.value.contains(
            LibraryRepository.favouriteKey(mediaId, mediaType)
        )

    /**
     * Never surface raw exception class names/messages to the user.
     */
    private fun humanMessage(e: Exception): String = when (e) {
        is UnknownHostException ->
            "No internet connection. Please turn on mobile data or Wi-Fi."
        is SocketTimeoutException ->
            "The server took too long to respond. Please try again."
        else -> "Something went wrong. Please try again."
    }
}

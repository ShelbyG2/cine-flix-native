package tech.unrealistic.cineflix.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import tech.unrealistic.cineflix.data.local.LibraryItemEntity
import tech.unrealistic.cineflix.data.local.LibraryRepository
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.Episode
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// ═══════════════════════════════════════════════════════════════════════════
// LIBRARY UI STATE
// ═══════════════════════════════════════════════════════════════════════════

/** A liked episode plus the show context needed to display and navigate it. */
data class LikedEpisode(
    val episode: Episode,
    val seriesId: Int,
    val seasonNumber: Int
)

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(
        val favourites: List<MediaItem>,
        val saved: List<MediaItem>,
        val episodes: List<LikedEpisode>
    ) : LibraryUiState {
        val isEmpty: Boolean
            get() = favourites.isEmpty() && saved.isEmpty() && episodes.isEmpty()
    }

    data class Error(val message: String) : LibraryUiState
}

// ═══════════════════════════════════════════════════════════════════════════
// VIEWMODEL — resolves locally stored ids into TMDB metadata
// ═══════════════════════════════════════════════════════════════════════════

class FavouritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    // Caches keyed by "mediaType:mediaId" so toggling one item doesn't refetch the rest
    private val mediaCache = mutableMapOf<String, MediaItem>()
    private val episodeCache = mutableMapOf<Int, Episode>()

    private var observerStarted = false

    init {
        startObserving()
    }

    private fun startObserving() {
        if (observerStarted) return
        observerStarted = true

        // Rebuild lists whenever the underlying library sets change.
        // Cached metadata means a toggle only fetches the one new item.
        viewModelScope.launch {
            combine(
                LibraryRepository.favouriteKeys,
                LibraryRepository.savedKeys,
                LibraryRepository.likedEpisodeKeys
            ) { fav, saved, episodes -> Triple(fav, saved, episodes) }
                .collect { (favKeys, savedKeys, episodeKeys) ->
                    refresh(favKeys, savedKeys, episodeKeys)
                }
        }
    }

    fun onRetry() {
        mediaCache.clear()
        episodeCache.clear()
        _uiState.value = LibraryUiState.Loading
        // re-emit from current repository state
        startObserving()
    }

    private suspend fun refresh(favKeys: Set<String>, savedKeys: Set<String>, episodeKeys: Set<String>) {
        try {
            val favMediaKeys = favKeys.filter { !it.endsWith(":${LibraryItemEntity.TYPE_EPISODE}") }
            val favMedia = supervisorScope {
                favMediaKeys.map { key ->
                    async { resolveMedia(key) }
                }.mapNotNull { it.await() }
            }

            val savedMedia = supervisorScope {
                savedKeys.map { key ->
                    async { resolveMedia(key) }
                }.mapNotNull { it.await() }
            }

            val likedEpisodes = supervisorScope {
                episodeKeys.map { key ->
                    async { resolveEpisode(key) }
                }.mapNotNull { it.await() }
            }

            _uiState.value = LibraryUiState.Success(
                favourites = favMedia,
                saved = savedMedia,
                episodes = likedEpisodes
            )
        } catch (e: Exception) {
            _uiState.value = LibraryUiState.Error(
                when (e) {
                    is UnknownHostException ->
                        "No internet connection. Please turn on mobile data or Wi-Fi."
                    is SocketTimeoutException ->
                        "The server took too long to respond. Please try again."
                    else -> "An ${e.localizedMessage} error occurred"
                }
            )
        }
    }

    /** key format "listType:mediaType:mediaId" → full MediaItem via TMDB. */
    private suspend fun resolveMedia(key: String): MediaItem? {
        val parts = key.split(":")
        val mediaType = parts.getOrNull(1) ?: return null
        val mediaId = parts.getOrNull(2)?.toIntOrNull() ?: return null

        val cacheKey = "$mediaType:$mediaId"
        mediaCache[cacheKey]?.let { return it }

        return try {
            val item = when (mediaType) {
                LibraryItemEntity.TYPE_TV -> RetrofitClient.tmdbService.getTvDetail(mediaId)
                else -> RetrofitClient.tmdbService.getMovieDetail(mediaId)
            }
            mediaCache[cacheKey] = item
            item
        } catch (_: Exception) {
            null // item removed from TMDB or transient failure — skip, don't sink the page
        }
    }

    /**
     * Episode metadata has no direct endpoint — fetch the parent season and
     * pick the episode out (entity stores seriesId + seasonNumber for this).
     */
    private suspend fun resolveEpisode(key: String): LikedEpisode? {
        val parts = key.split(":")
        val episodeId = parts.getOrNull(2)?.toIntOrNull() ?: return null

        episodeCache[episodeId]?.let { episode ->
            // cached episode needs its stored context
            val entry = LibraryRepository.likedEpisodeEntries()
                .firstOrNull { it.mediaId == episodeId } ?: return null
            return LikedEpisode(episode, entry.seriesId ?: return null, entry.seasonNumber ?: 0)
        }

        val entry = LibraryRepository.likedEpisodeEntries()
            .firstOrNull { it.mediaId == episodeId } ?: return null
        val seriesId = entry.seriesId ?: return null
        val seasonNumber = entry.seasonNumber ?: return null

        return try {
            val season = RetrofitClient.tmdbService.getSeason(seriesId, seasonNumber)
            val episode = season.episodes.firstOrNull { it.id == episodeId } ?: return null
            episodeCache[episodeId] = episode
            LikedEpisode(episode, seriesId, seasonNumber)
        } catch (_: Exception) {
            null
        }
    }
}

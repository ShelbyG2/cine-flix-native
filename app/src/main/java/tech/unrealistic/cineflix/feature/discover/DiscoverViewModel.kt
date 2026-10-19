package tech.unrealistic.cineflix.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.models.Genre
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// ═══════════════════════════════════════════════════════════════════════════
// DISCOVER DOMAIN MODEL
// ═══════════════════════════════════════════════════════════════════════════

enum class DiscoverCategory(val label: String) {
    MOVIES("Movies"),
    TV_SHOWS("TV Shows")
}

enum class DiscoverSort(val label: String) {
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    NEW_RELEASES("New Releases")
}

sealed interface DiscoverUiState {
    object Loading : DiscoverUiState
    data class Success(
        val items: List<MediaItem>,
        val hasMorePages: Boolean,
        val isLoadingMore: Boolean
    ) : DiscoverUiState

    data class Error(val message: String) : DiscoverUiState
}

// ═══════════════════════════════════════════════════════════════════════════
// VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════════

class DiscoverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DiscoverCategory.MOVIES)
    val selectedCategory: StateFlow<DiscoverCategory> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(DiscoverSort.POPULAR)
    val selectedSort: StateFlow<DiscoverSort> = _selectedSort.asStateFlow()

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    val selectedGenreId: StateFlow<Int?> = _selectedGenreId.asStateFlow()

    private val _availableGenres = MutableStateFlow<List<Genre>>(emptyList())
    val availableGenres: StateFlow<List<Genre>> = _availableGenres.asStateFlow()

    // Pagination bookkeeping (kept out of UiState to avoid cloning the whole list)
    private var currentPage = 1
    private var hasMorePages = true
    private var isLoadingMore = false
    private var currentItems: List<MediaItem> = emptyList()

    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    init {
        // Genre lists per category — static TMDB ids, same source GenreHelper uses
        refreshGenresForCategory(DiscoverCategory.MOVIES)

        // Debounced search: fire 450 ms after the user stops typing
        // (the initial emission is a blank query, which loads the first page)
        viewModelScope.launch {
            _searchQuery
                .collect { query ->
                    searchJob?.cancel()
                    searchJob = viewModelScope.launch {
                        if (query.isBlank()) {
                            fetchFirstPage()
                        } else {
                            delay(450)
                            fetchFirstPage()
                        }
                    }
                }
        }
    }

    // ── User intents ─────────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: DiscoverCategory) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        _selectedGenreId.value = null          // genres differ between movies/tv
        refreshGenresForCategory(category)
        fetchFirstPage()
    }

    fun onSortSelected(sort: DiscoverSort) {
        if (_selectedSort.value == sort) return
        _selectedSort.value = sort
        fetchFirstPage()
    }

    fun onGenreSelected(genreId: Int?) {
        // tapping the active chip clears the filter
        val newId = if (_selectedGenreId.value == genreId) null else genreId
        _selectedGenreId.value = newId
        fetchFirstPage()
    }

    fun onLoadMore() {
        if (isLoadingMore || !hasMorePages) return
        isLoadingMore = true
        emitSuccessSnapshot()
        loadNextPage()
    }

    fun onRetry() = fetchFirstPage()

    // ── Fetching ─────────────────────────────────────────────────────────

    private fun fetchFirstPage() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            currentPage = 1
            hasMorePages = true
            isLoadingMore = false
            currentItems = emptyList()
            _uiState.value = DiscoverUiState.Loading
            fetchPage(1)
        }
    }

    private fun loadNextPage() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            fetchPage(currentPage + 1)
        }
    }

    private suspend fun fetchPage(page: Int) {
        try {
            val query = _searchQuery.value.trim()
            val category = _selectedCategory.value
            val sort = _selectedSort.value
            val genreId = _selectedGenreId.value

            val response = if (query.isNotEmpty()) {
                when (category) {
                    DiscoverCategory.MOVIES ->
                        RetrofitClient.tmdbService.searchMovies(query = query, page = page)

                    DiscoverCategory.TV_SHOWS ->
                        RetrofitClient.tmdbService.searchTvShows(query = query, page = page)
                }
            } else {
                val sortBy = when (sort) {
                    DiscoverSort.POPULAR -> "popularity.desc"
                    DiscoverSort.TOP_RATED -> "vote_average.desc"
                    DiscoverSort.NEW_RELEASES ->
                        if (category == DiscoverCategory.MOVIES) "primary_release_date.desc"
                        else "first_air_date.desc"
                }
                // top-rated sorting needs a vote threshold or obscure 10/10 titles flood the grid
                val minVotes = if (sort == DiscoverSort.TOP_RATED) 200 else null

                when (category) {
                    DiscoverCategory.MOVIES ->
                        RetrofitClient.tmdbService.discoverMovies(
                            page = page, sortBy = sortBy, withGenres = genreId?.toString(),
                            voteCountGte = minVotes
                        )

                    DiscoverCategory.TV_SHOWS ->
                        RetrofitClient.tmdbService.discoverTvShows(
                            page = page, sortBy = sortBy, withGenres = genreId?.toString(),
                            voteCountGte = minVotes
                        )
                }
            }

            val pageItems = response.results.filter { it.posterPath != null }
            currentItems = if (page == 1) pageItems else currentItems + pageItems
            currentPage = page
            // TMDB pages hold 20 results; an empty/short page means we're at the end
            hasMorePages = response.results.size >= 20
            isLoadingMore = false
            emitSuccessSnapshot()
        } catch (e: Exception) {
            isLoadingMore = false
            if (page == 1) {
                _uiState.value = DiscoverUiState.Error(
                    when (e) {
                        is UnknownHostException ->
                            "No internet connection. Please turn on mobile data or Wi-Fi."
                        is SocketTimeoutException ->
                            "The server took too long to respond. Please try again."
                        else -> "Something went wrong. Please try again."
                    }
                )
            } else {
                // pagination failure keeps already-loaded content on screen
                hasMorePages = false
                emitSuccessSnapshot()
            }
        }
    }

    private fun emitSuccessSnapshot() {
        _uiState.value = DiscoverUiState.Success(
            items = currentItems,
            hasMorePages = hasMorePages,
            isLoadingMore = isLoadingMore
        )
    }

    // ── Genre chips ──────────────────────────────────────────────────────
    // Static id lists — movie and tv genres differ on TMDB
    // (same ids as helpers/GenreHelper.kt so chips and cards never disagree)

    private fun refreshGenresForCategory(category: DiscoverCategory) {
        _availableGenres.value = when (category) {
            DiscoverCategory.MOVIES -> movieGenres
            DiscoverCategory.TV_SHOWS -> tvGenres
        }
    }

    companion object {
        val movieGenres = listOf(
            Genre(28, "Action"), Genre(12, "Adventure"), Genre(16, "Animation"),
            Genre(35, "Comedy"), Genre(80, "Crime"), Genre(99, "Documentary"),
            Genre(18, "Drama"), Genre(10751, "Family"), Genre(14, "Fantasy"),
            Genre(36, "History"), Genre(27, "Horror"), Genre(10402, "Music"),
            Genre(9648, "Mystery"), Genre(10749, "Romance"), Genre(878, "Sci-Fi"),
            Genre(53, "Thriller"), Genre(10752, "War"), Genre(37, "Western")
        )

        val tvGenres = listOf(
            Genre(10759, "Action & Adventure"), Genre(16, "Animation"), Genre(35, "Comedy"),
            Genre(80, "Crime"), Genre(99, "Documentary"), Genre(18, "Drama"),
            Genre(10765, "Sci-Fi & Fantasy"), Genre(10762, "Kids"), Genre(9648, "Mystery"),
            Genre(10764, "Reality"), Genre(10749, "Romance"), Genre(10766, "Soap"),
            Genre(10767, "Talk"), Genre(10768, "War & Politics")
        )
    }
}

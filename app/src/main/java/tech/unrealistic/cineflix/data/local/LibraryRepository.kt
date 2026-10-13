package tech.unrealistic.cineflix.data.local

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════
// LIBRARY REPOSITORY — single source of truth for favourites / saved / likes
//
// Screens never touch the DAO directly. They observe the cached key Sets
// (instant, synchronous reads for button state) and call toggle functions.
// ═══════════════════════════════════════════════════════════════════════════

object LibraryRepository {

    private lateinit var dao: LibraryDao
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── Cached key sets: "$listType:$mediaType:$mediaId" ─────────────────────
    private val _favouriteKeys = MutableStateFlow<Set<String>>(emptySet())
    val favouriteKeys: StateFlow<Set<String>> = _favouriteKeys.asStateFlow()

    private val _savedKeys = MutableStateFlow<Set<String>>(emptySet())
    val savedKeys: StateFlow<Set<String>> = _savedKeys.asStateFlow()

    private val _likedEpisodeKeys = MutableStateFlow<Set<String>>(emptySet())
    val likedEpisodeKeys: StateFlow<Set<String>> = _likedEpisodeKeys.asStateFlow()

    /** Called once from CineflixApplication.onCreate(). */
    fun init(context: Context) {
        if (::dao.isInitialized) return
        dao = LibraryDatabase.get(context).libraryDao()

        // Mirror the DB into the cached sets so every screen sees changes instantly
        scope.launch {
            dao.observeList(LibraryItemEntity.LIST_FAVOURITE).collect { list ->
                _favouriteKeys.value = list.map { it.key }.toSet()
            }
        }
        scope.launch {
            dao.observeList(LibraryItemEntity.LIST_SAVED).collect { list ->
                _savedKeys.value = list.map { it.key }.toSet()
            }
        }
        scope.launch {
            dao.observeList(LibraryItemEntity.LIST_FAVOURITE)
                .map { list -> list.filter { it.mediaType == LibraryItemEntity.TYPE_EPISODE } }
                .collect { episodes ->
                    _likedEpisodeKeys.value = episodes.map { it.key }.toSet()
                }
        }
    }

    // ── Key helpers ──────────────────────────────────────────────────────────

    fun favouriteKey(mediaId: Int, mediaType: String) =
        "${LibraryItemEntity.LIST_FAVOURITE}:$mediaType:$mediaId"

    fun savedKey(mediaId: Int, mediaType: String) =
        "${LibraryItemEntity.LIST_SAVED}:$mediaType:$mediaId"

    // ── Observability for a single item (used by sheets/cards) ───────────────

    fun observeIsFavourite(mediaId: Int, mediaType: String): Flow<Boolean> =
        dao.observeContains(mediaId, mediaType, LibraryItemEntity.LIST_FAVOURITE)

    fun observeIsSaved(mediaId: Int, mediaType: String): Flow<Boolean> =
        dao.observeContains(mediaId, mediaType, LibraryItemEntity.LIST_SAVED)

    // ── Toggles (Create / Delete) ────────────────────────────────────────────

    /** Movies & TV shows: add to favourites if absent, remove if present. */
    fun toggleFavourite(mediaId: Int, mediaType: String) {
        val key = favouriteKey(mediaId, mediaType)
        val isFavourite = key in _favouriteKeys.value
        if (isFavourite) {
            // Optimistic removal for instant UI feedback
            _favouriteKeys.update { it - key }
            scope.launch {
                dao.delete(mediaId, mediaType, LibraryItemEntity.LIST_FAVOURITE)
            }
        } else {
            _favouriteKeys.update { it + key }
            scope.launch {
                dao.insert(
                    LibraryItemEntity(
                        mediaId = mediaId,
                        mediaType = mediaType,
                        listType = LibraryItemEntity.LIST_FAVOURITE
                    )
                )
            }
        }
    }

    /** Movies & TV shows: add to the saved (watchlist) list or remove. */
    fun toggleSaved(mediaId: Int, mediaType: String) {
        val key = savedKey(mediaId, mediaType)
        val isSaved = key in _savedKeys.value
        if (isSaved) {
            _savedKeys.update { it - key }
            scope.launch {
                dao.delete(mediaId, mediaType, LibraryItemEntity.LIST_SAVED)
            }
        } else {
            _savedKeys.update { it + key }
            scope.launch {
                dao.insert(
                    LibraryItemEntity(
                        mediaId = mediaId,
                        mediaType = mediaType,
                        listType = LibraryItemEntity.LIST_SAVED
                    )
                )
            }
        }
    }

    /** Episodes: like / unlike. Stored in the favourite list with show context. */
    fun toggleEpisodeLike(seriesId: Int, seasonNumber: Int, episodeId: Int) {
        val key =
            "${LibraryItemEntity.LIST_FAVOURITE}:${LibraryItemEntity.TYPE_EPISODE}:$episodeId"
        val isLiked = key in _likedEpisodeKeys.value
        if (isLiked) {
            _likedEpisodeKeys.update { it - key }
            scope.launch {
                dao.delete(
                    episodeId,
                    LibraryItemEntity.TYPE_EPISODE,
                    LibraryItemEntity.LIST_FAVOURITE
                )
            }
        } else {
            _likedEpisodeKeys.update { it + key }
            scope.launch {
                dao.insert(
                    LibraryItemEntity(
                        mediaId = episodeId,
                        mediaType = LibraryItemEntity.TYPE_EPISODE,
                        listType = LibraryItemEntity.LIST_FAVOURITE,
                        seriesId = seriesId,
                        seasonNumber = seasonNumber
                    )
                )
            }
        }
    }

    // ── Reads for the Favourites page ────────────────────────────────────────

    /** Snapshot of a whole list — the Favourites page resolves these ids via TMDB. */
    suspend fun entriesIn(listType: String): List<LibraryItemEntity> =
        dao.list(listType)

    /** Movie + TV favourites (episodes are excluded — they get their own row). */
    suspend fun favouriteMediaEntries(): List<LibraryItemEntity> =
        entriesIn(LibraryItemEntity.LIST_FAVOURITE)
            .filter { it.mediaType != LibraryItemEntity.TYPE_EPISODE }

    /** Liked episode entries (each carries seriesId + seasonNumber for refetch). */
    suspend fun likedEpisodeEntries(): List<LibraryItemEntity> =
        entriesIn(LibraryItemEntity.LIST_FAVOURITE)
            .filter { it.mediaType == LibraryItemEntity.TYPE_EPISODE }

    /** Saved (watchlist) movie + TV entries. */
    suspend fun savedEntries(): List<LibraryItemEntity> =
        entriesIn(LibraryItemEntity.LIST_SAVED)
}

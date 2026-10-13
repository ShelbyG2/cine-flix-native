package tech.unrealistic.cineflix.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════════════════
// LIBRARY — local store of the user's liked/saved items
//
// One table serves all three kinds of entries:
//   • movie   → mediaId = TMDB movie id
//   • tv      → mediaId = TMDB show id
//   • episode → mediaId = TMDB episode id (+ seriesId/seasonNumber needed to
//               refetch its metadata, since TMDB has no /episode/{id} endpoint)
//
// A composite unique index on (mediaId, mediaType, listType) is the primary
// key so re-liking an already-liked item can never create a duplicate row —
// insert with OnConflictStrategy.IGNORE, delete by the same triple.
// ═══════════════════════════════════════════════════════════════════════════

@Entity(
    tableName = "library_items",
    indices = [
        Index(
            value = ["mediaId", "mediaType", "listType"],
            unique = true
        )
    ]
)
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** TMDB id: movie id, tv id, or episode id (see mediaType). */
    val mediaId: Int,

    /** "movie" | "tv" | "episode" — matches MediaItem.mediaType conventions. */
    val mediaType: String,

    /** "favourite" | "saved" — which list this entry belongs to. */
    val listType: String,

    /** Episodes only: the parent show's TMDB id, needed to refetch metadata. */
    val seriesId: Int? = null,

    /** Episodes only: the season number containing this episode. */
    val seasonNumber: Int? = null,

    /** Epoch millis when the entry was created — powers "recently added" sorting. */
    val addedAt: Long = System.currentTimeMillis()
) {
    /** Stable identity used as a Set member in UI state (and as LazyList keys). */
    val key: String get() = "$listType:$mediaType:$mediaId"

    companion object {
        const val LIST_FAVOURITE = "favourite"
        const val LIST_SAVED = "saved"

        const val TYPE_MOVIE = "movie"
        const val TYPE_TV = "tv"
        const val TYPE_EPISODE = "episode"
    }
}

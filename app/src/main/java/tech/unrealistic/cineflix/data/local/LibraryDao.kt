package tech.unrealistic.cineflix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    /** Emits whenever the favourite list changes — drives button state everywhere. */
    @Query("SELECT * FROM library_items WHERE listType = :listType ORDER BY addedAt DESC")
    fun observeList(listType: String): Flow<List<LibraryItemEntity>>

    /** Snapshot read — used by the Favourites page to resolve stored ids. */
    @Query("SELECT * FROM library_items WHERE listType = :listType ORDER BY addedAt DESC")
    suspend fun list(listType: String): List<LibraryItemEntity>

    /** True if this exact (mediaId, mediaType, listType) triple exists. */
    @Query(
        "SELECT EXISTS(SELECT 1 FROM library_items " +
                "WHERE mediaId = :mediaId AND mediaType = :mediaType AND listType = :listType)"
    )
    fun observeContains(mediaId: Int, mediaType: String, listType: String): Flow<Boolean>

    /** IGNORE keeps a re-like from duplicating or resetting the addedAt order. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: LibraryItemEntity)

    /** No-op if the row doesn't exist — makes toggle semantics safe. */
    @Query(
        "DELETE FROM library_items " +
                "WHERE mediaId = :mediaId AND mediaType = :mediaType AND listType = :listType"
    )
    suspend fun delete(mediaId: Int, mediaType: String, listType: String)

    @Query("DELETE FROM library_items")
    suspend fun clearAll()
}

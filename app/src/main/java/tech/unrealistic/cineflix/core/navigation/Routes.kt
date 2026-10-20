package tech.unrealistic.cineflix

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object Home : NavKey
@Serializable
data object Discover : NavKey
@Serializable
data object Profile : NavKey
@Serializable
data object Favourites : NavKey
@Serializable
data class MediaDetailModal(val valid: Int, val type: String) : NavKey
@Serializable
data class Player(
    val id: Int,
    val type: String,
    val season: Int? = null,
    val episode: Int? = null,
    // Playback metadata supplied by the caller (nav graph / details sheet).
    // The player screen never fetches any of it itself.
    val title: String? = null,
    val backdropPath: String? = null,
    val episodeTitle: String? = null,
    val episodeIndex: Int? = null,
    val totalEpisodesInSeason: Int? = null,
)

/**
 * Everything the player needs to start a TV episode, resolved by the caller
 * from the episode list it already loaded. Optional fields stay null when a
 * screen only knows the ids — the player degrades to `S1E2` style labels.
 */
@Serializable
data class EpisodePlayRequest(
    val seriesId: Int,
    val season: Int,
    val episode: Int,
    val seriesTitle: String? = null,
    val backdropPath: String? = null,
    val episodeTitle: String? = null,
    val episodeIndex: Int? = null,
    val totalEpisodesInSeason: Int? = null,
)
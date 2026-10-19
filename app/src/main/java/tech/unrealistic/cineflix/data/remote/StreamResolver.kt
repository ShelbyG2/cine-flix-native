package tech.unrealistic.cineflix.data.remote

/**
 * Reconstructs the playable embed-wave URL from a TMDB media id.
 *
 * EmbedWave exposes embed URLs purely by TMDB id:
 *   movie: /embed/movie/{tmdbId}
 *   tv:    /embed/tv/{tmdbId}/{season}/{episode}
 *
 * This helper is used by the player resolver to build the URL that the
 * embed player should load.
 */
object StreamResolver {

    const val BASE_URL = "https://embedwave.cc"

    private const val MOVIE_PATH = "/embed/movie"
    private const val TV_PATH = "/embed/tv"

    fun movie(tmdbId: Int): String {
        require(tmdbId > 0) { "TMDB movie id must be positive, was $tmdbId" }
        return "$BASE_URL$MOVIE_PATH/$tmdbId"
    }

    fun tv(tmdbId: Int, season: Int, episode: Int): String {
        require(tmdbId > 0) { "TMDB id must be positive, was $tmdbId" }
        require(season >= 1) { "Season must be >=1, was $season" }
        require(episode >= 1) { "Episode must be >=1, was $episode" }
        return "$BASE_URL$TV_PATH/$tmdbId/$season/$episode"
    }

    /** Pick the movie or tv URL from a TMDB media type. */
    fun resolveUrl(tmdbId: Int, mediaType: String, season: Int?, episode: Int?): String =
        when (mediaType) {
            "movie" -> movie(tmdbId)
            "tv" -> tv(tmdbId, season ?: 1, episode ?: 1)
            else -> throw IllegalArgumentException("Unsupported media type: $mediaType")
        }
}

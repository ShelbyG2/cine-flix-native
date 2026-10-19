package tech.unrealistic.cineflix.data.remote

/**
 * Builds player URLs for the EmbedWave embed API (https://embedwave.cc/docs).
 *
 * The API is addressed purely by TMDB id — there is no key, signup or token:
 *  - movie: `https://embedwave.cc/embed/movie/{tmdbId}`
 *  - tv:    `https://embedwave.cc/embed/tv/{tmdbId}/{season}/{episode}`
 *
 * Season and episode numbers are 1-indexed. `autoplay=1` is forwarded to the
 * upstream player; the browser may still block it, which is why the flag is
 * only a request and never treated as a guarantee.
 */
object EmbedWave {

    const val BASE_URL: String = "https://embedwave.cc"

    private const val MOVIE_PATH = "/embed/movie"
    private const val TV_PATH = "/embed/tv"

    const val MEDIA_TYPE_MOVIE = "movie"
    const val MEDIA_TYPE_TV = "tv"

    fun movie(tmdbId: Int, autoplay: Boolean = true): String {
        require(tmdbId > 0) { "TMDB movie id must be positive, was $tmdbId" }
        return withAutoplay("$BASE_URL$MOVIE_PATH/$tmdbId", autoplay)
    }

    fun tv(
        tmdbId: Int,
        season: Int,
        episode: Int,
        autoplay: Boolean = true,
    ): String {
        require(tmdbId > 0) { "TMDB series id must be positive, was $tmdbId" }
        require(season >= 1) { "Season must be 1-indexed, was $season" }
        require(episode >= 1) { "Episode must be 1-indexed, was $episode" }
        return withAutoplay("$BASE_URL$TV_PATH/$tmdbId/$season/$episode", autoplay)
    }

    /**
     * Picks the movie or series URL from a TMDB media type. Missing season or
     * episode numbers start a series at its first episode.
     */
    fun embedUrl(
        tmdbId: Int,
        mediaType: String,
        season: Int? = null,
        episode: Int? = null,
        autoplay: Boolean = true,
    ): String = when (mediaType) {
        MEDIA_TYPE_MOVIE -> movie(tmdbId, autoplay)
        MEDIA_TYPE_TV -> tv(tmdbId, season ?: 1, episode ?: 1, autoplay)
        else -> throw IllegalArgumentException("Unsupported media type: $mediaType")
    }

    private fun withAutoplay(url: String, autoplay: Boolean): String =
        if (autoplay) "$url?autoplay=1" else url
}

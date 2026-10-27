package tech.unrealistic.cineflix.data.remote

/**
 * Embed pages the extractor scrapes for a direct stream, in priority order.
 *
 * Each provider is addressed purely by TMDB id. Season and episode numbers are
 * 1-indexed. The player tries them in order and plays the first manifest it can
 * extract; the embed page itself is only shown when every provider fails.
 */
interface EmbedProvider {
    val name: String
    fun movie(tmdbId: Int): String
    fun tv(tmdbId: Int, season: Int, episode: Int): String
}

object VidSrc : EmbedProvider {
    override val name: String = "VidSrc"
    private const val BASE_URL = "https://vidsrc.me"

    override fun movie(tmdbId: Int): String {
        require(tmdbId > 0) { "TMDB movie id must be positive, was $tmdbId" }
        return "$BASE_URL/embed/movie/$tmdbId"
    }

    override fun tv(tmdbId: Int, season: Int, episode: Int): String {
        require(tmdbId > 0) { "TMDB series id must be positive, was $tmdbId" }
        require(season >= 1) { "Season must be 1-indexed, was $season" }
        require(episode >= 1) { "Episode must be 1-indexed, was $episode" }
        return "$BASE_URL/embed/tv/$tmdbId/$season/$episode"
    }
}

object VidLink : EmbedProvider {
    override val name: String = "VidLink"
    private const val BASE_URL = "https://vidlink.pro"

    override fun movie(tmdbId: Int): String {
        require(tmdbId > 0) { "TMDB movie id must be positive, was $tmdbId" }
        return "$BASE_URL/movie/$tmdbId"
    }

    override fun tv(tmdbId: Int, season: Int, episode: Int): String {
        require(tmdbId > 0) { "TMDB series id must be positive, was $tmdbId" }
        require(season >= 1) { "Season must be 1-indexed, was $season" }
        require(episode >= 1) { "Episode must be 1-indexed, was $episode" }
        return "$BASE_URL/tv/$tmdbId/$season/$episode"
    }
}

/** Providers the player tries, first one wins. */
val streamProviders: List<EmbedProvider> = listOf(VidSrc, VidLink)

/** Builds the embed URL for a title from one provider. */
fun EmbedProvider.embedUrl(
    tmdbId: Int,
    mediaType: String,
    season: Int? = null,
    episode: Int? = null,
): String = when (mediaType) {
    "movie" -> movie(tmdbId)
    "tv" -> tv(tmdbId, season ?: 1, episode ?: 1)
    else -> throw IllegalArgumentException("Unsupported media type: $mediaType")
}

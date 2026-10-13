package tech.unrealistic.cineflix.data.remote


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.Season
import tech.unrealistic.cineflix.data.remote.models.TmdbResponse
import tech.unrealistic.cineflix.data.remote.models.Tv


interface TmdbService {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<Movie>

    @GET("trending/tv/week")
    suspend fun getTrendingShows(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<Tv>

    @GET("trending/all/day")
    suspend fun getTrendingMixed(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<MediaItem>


    @GET("movie/top_rated")
    suspend fun getRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<MediaItem>

    @GET("tv/top_rated")
    suspend fun getRatedTv(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<MediaItem>


    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
    ): Movie

    @GET("tv/{id}")
    suspend fun getTvDetail(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): Tv

    @GET("movie/{id}/similar")
    suspend fun getSimilarMovie(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse<MediaItem>

    @GET("tv/{id}/similar")
    suspend fun getSimilarTv(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbResponse <MediaItem>

    @GET("tv/{id}/season/{season_number}")
    suspend fun getSeason(
        @Path("id") seriesId: Int,
        @Path("season_number") seasonNumber:Int,

    ): Season

    // ── Discover tab ─────────────────────────────────────────────────────
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") withGenres: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("vote_count.gte") voteCountGte: Int? = null
    ): TmdbResponse<Movie>

    @GET("discover/tv")
    suspend fun discoverTvShows(
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") withGenres: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("vote_count.gte") voteCountGte: Int? = null
    ): TmdbResponse<Tv>

    // Separate movie/tv search endpoints (instead of search/multi) because
    // multi can return "person" results the MediaItem serializer can't decode
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbResponse<Movie>

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbResponse<Tv>

}
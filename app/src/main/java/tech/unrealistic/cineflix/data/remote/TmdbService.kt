package tech.unrealistic.cineflix.data.remote


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
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
    ): MediaItem

    @GET("tv/{id}")
    suspend fun getTvDetail(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): MediaItem

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

}
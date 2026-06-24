package tech.unrealistic.cineflix.data.remote


import retrofit2.http.GET
import retrofit2.http.Query
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.data.remote.models.TmdbResponse
import tech.unrealistic.cineflix.data.remote.models.Tv

interface TmdbService {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int =1
    ) : TmdbResponse<Movie>
    @GET("trending/tv/week")
    suspend fun getTrendingShows(
        @Query("language") language: String= "en-US",
        @Query("page") page: Int =1
    ) : TmdbResponse<Tv>

    @GET("popular/all/day")
    suspend fun getPopularShows(
        @Query("language") language: String= "en-US",
        @Query("page") page: Int =1
    ) : TmdbResponse<MediaItem>
}
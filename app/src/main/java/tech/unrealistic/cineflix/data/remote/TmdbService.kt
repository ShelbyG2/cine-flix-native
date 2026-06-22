package tech.unrealistic.cineflix.data.remote


import retrofit2.http.GET
import retrofit2.http.Query
import tech.unrealistic.cineflix.data.remote.models.TmdbResponse

interface TmdbService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int =1
    ) : TmdbResponse
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("language") language: String= "en-US",
        @Query("page") page: Int =1
    ) : TmdbResponse
}
package tech.unrealistic.cineflix.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbResponse (
    val page: Int,
    val  results : List<Movie>
)

@Serializable
data class Movie (
    val id : Int,
    val title: String,
    val  overview: String,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    val popularity: Double,
    val video : Boolean = false,


)



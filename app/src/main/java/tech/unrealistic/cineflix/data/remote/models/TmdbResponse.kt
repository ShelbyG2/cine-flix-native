package tech.unrealistic.cineflix.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class TmdbResponse<T> (

    val page: Int,
    val  results : List<T>
)

@Serializable(with = MediaSerializer::class)
sealed interface  MediaItem {
    val id : Int
    val overview : String?
    val posterPath : String?
    val backdropPath : String?
    val  voteAverage : Double
    val voteCount : Int
    val genreIds : List<Int>
    val originalLanguage : String
    val popularity : Double

}

@Serializable
@SerialName("movie")
data class Movie (
    override val id : Int,
    val title: String,
   override val  overview: String,
    @SerialName("poster_path") override val posterPath: String?,
    @SerialName("backdrop_path") override val backdropPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") override val voteAverage: Double,
    @SerialName("vote_count") override val voteCount: Int,
    @SerialName("genre_ids") override val genreIds: List<Int>,
    @SerialName("original_language") override val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    override val popularity: Double,
    val video : Boolean = false,


) : MediaItem


@Serializable
@SerialName("tv")
data class Tv(
    override val id: Int,
    @SerialName("name") val name: String, // TMDB uses "name" for TV shows
    override val overview: String,
    @SerialName("poster_path") override val posterPath: String?,
    @SerialName("backdrop_path") override val backdropPath: String?,
    @SerialName("first_air_date") val releaseDate: String? = null, // TMDB uses "first_air_date"
    @SerialName("vote_average") override val voteAverage: Double,
    @SerialName("vote_count") override val voteCount: Int,
    @SerialName("genre_ids") override val genreIds: List<Int>,
    @SerialName("original_language") override val originalLanguage: String,
    @SerialName("original_name") val originalTitle: String,
    override val popularity: Double
)  : MediaItem



object MediaSerializer :  JsonContentPolymorphicSerializer <MediaItem>(MediaItem::class){
    override fun selectDeserializer(element: JsonElement) = when {
        //Include the  "media_type" field ("movie" or "tv" )

         element.jsonObject["media_type"]?.jsonPrimitive?.content == "tv" -> Tv.serializer()
        element.jsonObject["media_type"]?.jsonPrimitive?.content == "movie" -> Movie.serializer()

        // Safety fallback if "media_type" is missing

        "name" in element.jsonObject -> Tv.serializer()
        else -> Movie.serializer()
    }
}
val MediaItem.displayTitle: String
    get() = when (this) {
        is Movie -> this.title
        is Tv -> this.name
    }
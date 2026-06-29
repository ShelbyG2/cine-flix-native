package tech.unrealistic.cineflix.data.remote.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class TmdbResponse<T>(

    val page: Int,
    val results: List<T>
)

@Serializable(with = MediaSerializer::class)
sealed interface MediaItem {
    val id: Int
    val mediaType: String
    val overview: String?
    val posterPath: String?
    val backdropPath: String?
    val voteAverage: Double
    val voteCount: Int
    val genreIds: List<Int>
    val originalLanguage: String
    val popularity: Double

}

@Serializable
@SerialName("movie")
data class Movie(
    override val id: Int,
    override val mediaType: String = "movie",
    val title: String,
    override val overview: String,
    @SerialName("poster_path") override val posterPath: String? = null,
    @SerialName("backdrop_path") override val backdropPath: String?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") override val voteAverage: Double,
    @SerialName("vote_count") override val voteCount: Int,
    @SerialName("genre_ids") override val genreIds: List<Int> = emptyList(),
    @SerialName("original_language") override val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    override val popularity: Double,
    val video: Boolean = false,


    ) : MediaItem


@Serializable
@SerialName("tv")
data class Tv(
    override val id: Int,
    override val mediaType: String = "tv",
    @SerialName("name") val name: String, // TMDB uses "name" for TV shows
    override val overview: String,
    @SerialName("poster_path") override val posterPath: String?,
    @SerialName("backdrop_path") override val backdropPath: String?,
    @SerialName("first_air_date") val releaseDate: String? = null, // TMDB uses "first_air_date"
    @SerialName("vote_average") override val voteAverage: Double,
    @SerialName("vote_count") override val voteCount: Int,
    @SerialName("genre_ids") override val genreIds: List<Int> = emptyList(),
    @SerialName("original_language") override val originalLanguage: String,
    @SerialName("original_name") val originalTitle: String,
    override val popularity: Double
) : MediaItem


object MediaSerializer : JsonContentPolymorphicSerializer<MediaItem>(MediaItem::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out MediaItem>{
        //Include the  "media_type" field ("movie" or "tv" )

        val jsonObject = element.jsonObject
        //First check using the standard media_type

        val mediaType = jsonObject["media_type"]?.jsonPrimitive?.content
        if( mediaType == "tv") return  Tv.serializer()
        if (mediaType == "movie") return Movie.serializer()
        return when {
            "name" in jsonObject || "first_air_date" in jsonObject -> Tv.serializer()
            "title" in jsonObject || "release_date" in jsonObject -> Movie.serializer()

            else -> Movie.serializer()
        }


    }
}

val MediaItem.displayTitle: String
    get() = when (this) {
        is Movie -> this.title
        is Tv -> this.name
    }


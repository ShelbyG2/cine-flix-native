package tech.unrealistic.cineflix.helpers

import tech.unrealistic.cineflix.data.remote.models.Genre
import tech.unrealistic.cineflix.data.remote.models.MediaItem


object GenreHelper {

    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Sci-Fi",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western",
        10759 to "Action & Adventure",
        10762 to "Kids",
        10763 to "News",
        10764 to "Reality",
        10765 to "Sci-Fi & Fantasy",
        10766 to "Soap",
        10767 to "Talk",
        10768 to "War & Politics",
    )

    //from endpoints that have genreIds ass an array
    fun fromIds (ids: List<Int>?): List<String> =
        ids?.mapNotNull { genreMap[it] }?: emptyList()

    fun topThree (ids: List<Int>): List<String> =
        fromIds(ids).take(3)
    //from endpoints that return genres as an object
    fun fromGenres (genres: List<Genre>?): List<String> =
        genres?.map {  it.name} ?. take(3) ?: emptyList()

    fun nameOf (id:Int): String? = genreMap[id]




}
// Now your UI just asks for .genreNames and this extension does all the heavy lifting!
val MediaItem.genreNames: List<String>
    get() {

        if (!genres.isNullOrEmpty()) {
            return GenreHelper.fromGenres(genres)
        }


        return GenreHelper.topThree(genreIds)
    }
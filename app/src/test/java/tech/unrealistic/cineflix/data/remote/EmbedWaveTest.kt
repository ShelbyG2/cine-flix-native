package tech.unrealistic.cineflix.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EmbedWaveTest {

    @Test
    fun `movie url addresses the tmdb id`() {
        assertEquals(
            "https://embedwave.cc/embed/movie/533535?autoplay=1",
            EmbedWave.movie(533535)
        )
    }

    @Test
    fun `movie url can skip autoplay`() {
        assertEquals(
            "https://embedwave.cc/embed/movie/533535",
            EmbedWave.movie(533535, autoplay = false)
        )
    }

    @Test
    fun `tv url includes one indexed season and episode`() {
        assertEquals(
            "https://embedwave.cc/embed/tv/1399/2/5?autoplay=1",
            EmbedWave.tv(tmdbId = 1399, season = 2, episode = 5)
        )
    }

    @Test
    fun `embed url picks the path from the media type`() {
        assertEquals(
            "https://embedwave.cc/embed/movie/27205",
            EmbedWave.embedUrl(27205, "movie", autoplay = false)
        )
        assertEquals(
            "https://embedwave.cc/embed/tv/1399/1/1",
            EmbedWave.embedUrl(1399, "tv", autoplay = false)
        )
    }

    @Test
    fun `tv embed defaults to the first episode`() {
        assertEquals(
            "https://embedwave.cc/embed/tv/1399/1/1",
            EmbedWave.embedUrl(1399, "tv", season = null, episode = null, autoplay = false)
        )
    }

    @Test
    fun `invalid ids and zero indexed episodes are rejected`() {
        assertThrows(IllegalArgumentException::class.java) { EmbedWave.movie(0) }
        assertThrows(IllegalArgumentException::class.java) {
            EmbedWave.tv(tmdbId = 1399, season = 0, episode = 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            EmbedWave.tv(tmdbId = 1399, season = 1, episode = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            EmbedWave.embedUrl(1399, "anime")
        }
    }
}

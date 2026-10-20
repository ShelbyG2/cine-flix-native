package tech.unrealistic.cineflix.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StreamProvidersTest {

    @Test
    fun `vidsrc movie url addresses the tmdb id`() {
        assertEquals(
            "https://vidsrc.me/embed/movie/533535",
            VidSrc.movie(533535)
        )
    }

    @Test
    fun `vidsrc tv url includes season and episode`() {
        assertEquals(
            "https://vidsrc.me/embed/tv/1399/2/5",
            VidSrc.tv(tmdbId = 1399, season = 2, episode = 5)
        )
    }

    @Test
    fun `vidlink movie and tv urls`() {
        assertEquals("https://vidlink.pro/movie/533535", VidLink.movie(533535))
        assertEquals(
            "https://vidlink.pro/tv/1399/2/5",
            VidLink.tv(tmdbId = 1399, season = 2, episode = 5)
        )
    }

    @Test
    fun `embed url picks the path from the media type`() {
        assertEquals(
            "https://vidsrc.me/embed/movie/27205",
            VidSrc.embedUrl(27205, "movie")
        )
        assertEquals(
            "https://vidsrc.me/embed/tv/1399/1/1",
            VidSrc.embedUrl(1399, "tv")
        )
    }

    @Test
    fun `tv embed defaults to the first episode`() {
        assertEquals(
            "https://vidlink.pro/tv/1399/1/1",
            VidLink.embedUrl(1399, "tv", season = null, episode = null)
        )
    }

    @Test
    fun `providers are tried in priority order`() {
        assertEquals(listOf("VidSrc", "VidLink"), streamProviders.map { it.name })
    }

    @Test
    fun `invalid ids and zero indexed episodes are rejected`() {
        assertThrows(IllegalArgumentException::class.java) { VidSrc.movie(0) }
        assertThrows(IllegalArgumentException::class.java) {
            VidSrc.tv(tmdbId = 1399, season = 0, episode = 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            VidLink.tv(tmdbId = 1399, season = 1, episode = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            VidSrc.embedUrl(1399, "anime")
        }
    }
}

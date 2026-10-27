package tech.unrealistic.cineflix.feature.player

import androidx.media3.common.Format
import androidx.media3.common.TrackGroup
import androidx.media3.common.Tracks
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the track sheets: how the options are built from the player's tracks
 * and the rule that a sheet highlights exactly one option at a time.
 */
class TrackOptionsTest {

    private fun group(
        id: String,
        formats: List<Format>,
        selected: Int = -1,
    ): Tracks.Group = Tracks.Group(
        TrackGroup(id, *formats.toTypedArray()),
        true,
        IntArray(formats.size) { 3 },
        BooleanArray(formats.size) { it == selected },
    )

    private fun videoFormat(width: Int, height: Int) = Format.Builder()
        .setSampleMimeType("video/mp4")
        .setWidth(width)
        .setHeight(height)
        .build()

    private fun textFormat(language: String) = Format.Builder()
        .setSampleMimeType("text/vtt")
        .setLanguage(language)
        .build()

    private fun audioFormat(language: String, channelCount: Int) = Format.Builder()
        .setSampleMimeType("audio/mp4")
        .setLanguage(language)
        .setChannelCount(channelCount)
        .build()

    @Test
    fun `video options carry a quality label and a unique key`() {
        val options = videoTrackOptions(
            listOf(
                group(
                    "video",
                    listOf(videoFormat(1920, 1080), videoFormat(1280, 720)),
                )
            )
        )

        assertEquals(listOf("1080p", "720p"), options.map { it.label })
        assertEquals(1920, options[0].width)
        assertEquals(720, options[1].height)
        assertEquals(2, options.map { it.key }.distinct().size)
    }

    @Test
    fun `text options are labelled with the language name`() {
        // Every text track is its own group, the way the players report them.
        val options = textTrackOptions(
            listOf(
                group("text-en", listOf(textFormat("en"))),
                group("text-es", listOf(textFormat("es"))),
            )
        )

        assertEquals(Locale.forLanguageTag("en").displayLanguage, options[0].label)
        assertEquals(Locale.forLanguageTag("es").displayLanguage, options[1].label)
        assertEquals(listOf("en", "es"), options.map { it.language })
    }

    @Test
    fun `audio options carry the channel layout`() {
        val options = audioTrackOptions(
            listOf(group("audio", listOf(audioFormat("en", 6), audioFormat("en", 2))))
        )

        assertEquals("${Locale.forLanguageTag("en").displayLanguage} (5.1)", options[0].label)
        assertEquals("${Locale.forLanguageTag("en").displayLanguage} (Stereo)", options[1].label)
    }

    @Test
    fun `the selected track of the player is found`() {
        val groups = listOf(
            group("text-en", listOf(textFormat("en"))),
            group("text-es", listOf(textFormat("es")), selected = 0),
        )

        assertEquals("ttext-es-0", selectedTrackKey(groups, "t"))
        assertNull(selectedTrackKey(listOf(group("text-en", listOf(textFormat("en")))), "t"))
    }

    @Test
    fun `the quality sheet highlights exactly one option`() {
        val options = videoTrackOptions(
            listOf(group("video", listOf(videoFormat(1920, 1080), videoFormat(1280, 720))))
        )

        val choices = listOf(
            QualityChoice.Auto,
            QualityChoice.Manual(options[0].key),
            QualityChoice.Manual(options[1].key),
        )

        choices.forEach { choice ->
            val selected = selectedRows(options, qualitySelectedKey(choice), autoRow = "Auto")
            assertEquals("choice $choice must highlight one row", 1, selected.size)
        }
    }

    @Test
    fun `the subtitle sheet highlights exactly one option`() {
        val options = textTrackOptions(
            listOf(
                group("text-en", listOf(textFormat("en"))),
                group("text-es", listOf(textFormat("es"))),
            )
        )

        val choices = listOf(
            SubtitleChoice.Off,
            SubtitleChoice.Track(options[0].key),
            SubtitleChoice.Track(options[1].key),
        )

        choices.forEach { choice ->
            val selected = selectedRows(options, subtitleSelectedKey(choice), autoRow = "Off")
            assertEquals("choice $choice must highlight one row", 1, selected.size)
        }
    }

    /**
     * Mirrors how the sheet renders its radios: the "Auto"/"Off" row is lit
     * while no specific track is selected, otherwise the matching track row is.
     */
    private fun selectedRows(
        options: List<TrackOption>,
        selectedKey: String?,
        autoRow: String,
    ): List<String> {
        val rows = mutableListOf<String>()
        if (selectedKey == null) rows += autoRow
        options.filter { it.key == selectedKey }.forEach { rows += it.label }
        assertTrue(rows.size <= 1)
        return rows
    }
}

package tech.unrealistic.cineflix.feature.player

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import java.util.Locale

/**
 * One selectable entry inside a track sheet (§4).
 *
 * [key] identifies the entry across rebuilds of the track list, so a choice
 * the user made survives the player reporting a new set of tracks.
 */
internal data class TrackOption(
    val key: String,
    val label: String,
    val language: String?,
    val width: Int = 0,
    val height: Int = 0,
)

/**
 * Quality selection (§4.2). [Auto] is the default and stays selected until the
 * user pins a rendition.
 */
internal sealed interface QualityChoice {
    data object Auto : QualityChoice
    data class Manual(val key: String) : QualityChoice
}

/** Subtitle selection (§4.3). */
internal sealed interface SubtitleChoice {
    data object Off : SubtitleChoice
    data class Track(val key: String) : SubtitleChoice
}

/** Video renditions offered by the quality sheet (§4.2). */
@OptIn(UnstableApi::class)
internal fun videoTrackOptions(groups: List<Tracks.Group>): List<TrackOption> =
    groups.flatMap { group ->
        (0 until group.length).map { index ->
            val format = group.getTrackFormat(index)
            TrackOption(
                key = "v${group.mediaTrackGroup.id}-$index",
                label = qualityLabel(format),
                language = format.language,
                width = format.width,
                height = format.height,
            )
        }
    }

/** Text tracks offered by the subtitle sheet (§4.3). */
@OptIn(UnstableApi::class)
internal fun textTrackOptions(groups: List<Tracks.Group>): List<TrackOption> =
    groups.flatMap { group ->
        (0 until group.length).map { index ->
            val format = group.getTrackFormat(index)
            TrackOption(
                key = "t${group.mediaTrackGroup.id}-$index",
                label = languageLabel(format),
                language = format.language,
            )
        }
    }

/** Audio tracks offered by the audio sheet (§4.4). */
@OptIn(UnstableApi::class)
internal fun audioTrackOptions(groups: List<Tracks.Group>): List<TrackOption> =
    groups.flatMap { group ->
        (0 until group.length).map { index ->
            val format = group.getTrackFormat(index)
            TrackOption(
                key = "a${group.mediaTrackGroup.id}-$index",
                label = audioLabel(format),
                language = format.language,
            )
        }
    }

/** Key of the track the player currently has selected for [groups]. */
@OptIn(UnstableApi::class)
internal fun selectedTrackKey(groups: List<Tracks.Group>, prefix: String): String? {
    groups.forEach { group ->
        for (index in 0 until group.length) {
            if (group.isTrackSelected(index)) return "$prefix${group.mediaTrackGroup.id}-$index"
        }
    }
    return null
}

/**
 * Key of the option the quality sheet highlights, or null while "Auto" is
 * selected. Exactly one row of the sheet can match it.
 */
internal fun qualitySelectedKey(choice: QualityChoice): String? = when (choice) {
    is QualityChoice.Auto -> null
    is QualityChoice.Manual -> choice.key
}

/**
 * Key of the option the subtitle sheet highlights, or null while subtitles
 * are off. Exactly one row of the sheet can match it.
 */
internal fun subtitleSelectedKey(choice: SubtitleChoice): String? = when (choice) {
    is SubtitleChoice.Off -> null
    is SubtitleChoice.Track -> choice.key
}

/** "1080p", "4K", … for a video rendition (§4.2). */
internal fun qualityLabel(format: Format): String = when {
    format.height >= 2160 -> "4K"
    format.height >= 1080 -> "1080p"
    format.height >= 720 -> "720p"
    format.height >= 480 -> "480p"
    format.height > 0 -> "${format.height}p"
    else -> format.label ?: "Unknown"
}

/** Human-readable language name, falling back to the track label (§4.3). */
internal fun languageLabel(format: Format): String {
    val language = format.language
    val label = format.label
    return when {
        !language.isNullOrBlank() ->
            Locale.forLanguageTag(language).displayLanguage.takeIf { it.isNotBlank() } ?: language
        !label.isNullOrBlank() -> label
        else -> "Unknown"
    }
}

/** Language name plus the channel layout when the stream reports one (§4.4). */
internal fun audioLabel(format: Format): String {
    val base = languageLabel(format)
    return when (format.channelCount) {
        6 -> "$base (5.1)"
        2 -> "$base (Stereo)"
        1 -> "$base (Mono)"
        in 3..Int.MAX_VALUE -> "$base (${format.channelCount}ch)"
        else -> base
    }
}

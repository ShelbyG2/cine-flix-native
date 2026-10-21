package tech.unrealistic.cineflix.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Poster-derived tint, deliberately clamped into the monochrome range.
 *
 * A title's dominant colour may tint the **details hero gradient** — nowhere
 * else. Artwork is often saturated, so the colour is desaturated and its
 * luminance capped: white body text over the resulting gradient keeps a
 * comfortable AA margin (the tint never gets lighter than ~#383838).
 */
object PosterPalette {

    /** Highest luminance a hero tint may reach so `onImage` text stays AA. */
    private const val MAX_TINT_LUMINANCE = 0.22f

    /**
     * Desaturates [source] to a neutral ink and clamps its luminance.
     * Returns pure black at or below [MAX_TINT_LUMINANCE].
     */
    fun clampForReadableText(source: Color): Color {
        val neutral = (source.red + source.green + source.blue) / 3f
        val clamped = neutral.coerceIn(0f, MAX_TINT_LUMINANCE)
        return Color(clamped, clamped, clamped, source.alpha)
    }
}

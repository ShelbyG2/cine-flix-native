package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.ui.graphics.Color

/**
 * One-off artwork values for the onboarding illustrations.
 *
 * These used to live in the global theme, which made the design system carry
 * values only three screens ever read. Keeping them here keeps the monochrome
 * grammar (`Color.Black` / `Color.White` at low alpha) without leaking
 * onboarding-specific colour into `ui/theme`.
 */
internal object OnboardingArt {
    /** Inactive progress dot — white at 30% over imagery. */
    val progressDotInactive = Color.White.copy(alpha = 0.30f)

    /** Shade inside the glass sphere, bottom-right. */
    val sphereShade = Color.Black.copy(alpha = 0.25f)

    /** Ring opacity for the two pulsing glow rings. */
    const val pulseRingAlpha1 = 0.8f
    const val pulseRingAlpha2 = 0.6f
}

package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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

    // ── Progress dots ───────────────────────────────────────────────────────
    val dotActiveW   = 24.dp
    val dotInactiveW = 8.dp
    val dotH         = 8.dp

    // ── Glass sphere artwork ────────────────────────────────────────────────
    val sphereBox      = 220.dp
    val sphereRing     = 200.dp
    val sphereOrb      = 170.dp
    val githubMarkLarge = 74.dp
    val sphereShadowW  = 150.dp
    val sphereShadowH  = 20.dp
    val specularW      = 70.dp
    val specularH      = 40.dp
    val rimSliverW     = 46.dp
    val rimSliverH     = 16.dp
    val ringBorderWide = 2.dp
    val ringBorderThin = 1.5.dp

    // ── 3D folder artwork ───────────────────────────────────────────────────
    val folderTabW    = 96.dp
    val folderTabH    = 26.dp
    val folderBodyW   = 200.dp
    val folderBodyH   = 150.dp
    val fileLineH     = 8.dp
    val fileLineWidths = listOf(120.dp, 88.dp, 104.dp)
    val downloadDisc  = 40.dp
    val folderShadowW = 170.dp
    val folderShadowH = 22.dp

    /** Folder artwork corners — they belong to the illustration, not the system. */
    val folderTabShape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp,
    )
    val folderBodyShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp,
    )
}

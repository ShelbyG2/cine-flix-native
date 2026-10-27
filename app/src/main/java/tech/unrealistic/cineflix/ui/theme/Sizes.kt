package tech.unrealistic.cineflix.ui.theme

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SIZE TOKENS
// Icon glyphs, one touch target, strokes and component dimensions.
// Composables must never declare raw `.size(N.dp)` for these — use
// AppTheme.sizes.*. Touch targets are never below 48dp.
// Card widths adapt to the window size class instead of being fixed.
// ═══════════════════════════════════════════════════════════════════════════

data class AppSizes(
    // ── Icon glyphs (visual only — hit area comes from touchTarget) ─────────
    val iconXs:   Dp = 14.dp,   // star / tiny glyphs
    val iconSm:   Dp = 16.dp,   // inline icons inside chips & buttons
    val iconCompact: Dp = 18.dp, // close / like glyphs inside 48dp targets
    val iconMd:   Dp = 20.dp,   // standard icon
    val iconLg:   Dp = 24.dp,   // large icon
    val iconXl:   Dp = 32.dp,   // icon inside round action buttons

    // ── Touch targets (interactive) ─────────────────────────────────────────
    /** The one accessible minimum — top bar, chips, cards, sheets, player. */
    val touchTarget: Dp = 48.dp,

    // ── Strokes ─────────────────────────────────────────────────────────────
    val hairline:         Dp = 0.5.dp,  // pills, dividers
    val glassBorderWidth: Dp = 0.5.dp,  // glass rim
    val borderWidth:      Dp = 1.dp,    // standard border

    // ── Component sizes ─────────────────────────────────────────────────────
    val actionButton:    Dp = 52.dp,  // play / favourite / share round buttons
    /** Blur radius for the single glass material (top bar / nav bar). */
    val glassBlurRadius:  Dp = 28.dp,
    /** Content height of the iOS-style bottom tab bar (before nav-bar inset). */
    val navBarHeight:     Dp = 56.dp,
    /** Content height of the floating top bar (before status-bar inset). */
    val topBarHeight:     Dp = 56.dp,
    /** Visible disc of a close button inside its 48dp target. */
    val closeButtonVisual: Dp = 32.dp,
    /** Player: centre transport button and scrubber thumb. */
    val playerPlayButton: Dp = 64.dp,
    val playerThumb:      Dp = 16.dp,
    /** Player: wavy-bars artwork box. */
    val wavyBarsWidth:    Dp = 40.dp,
    val wavyBarsHeight:   Dp = 28.dp,
    val dragHandleWidth:  Dp = 36.dp,
    val dragHandleHeight: Dp = 4.dp,
    /** List entrance rise (fade-up). */
    val listRise:         Dp = 12.dp,

    // ── Card / poster dimensions (adaptive) ─────────────────────────────────
    /** Width of a poster in a horizontal row — grows with the window. */
    val mediaCardWidth:    Dp = CompactMediaCardWidth,
    /** Width of a wide 16:9 library card — grows with the window. */
    val libraryCardW:      Dp = CompactLibraryCardWidth,
    val mediaCardPosterRatio: Float = 1.5f,   // height = width × ratio (2:3)
    val overlayButtonPad:  Dp = 6.dp,         // inset of corner buttons on posters
    /** Hero carousel height as a fraction of the available screen height. */
    val heroHeightFraction: Float = 0.4f,
    /** Minimum column width for adaptive poster grids. */
    val gridCellMin:       Dp = 110.dp,

    // ── State views (empty / error / offline) ───────────────────────────────
    val stateIcon:      Dp = 40.dp,
    val stateIconLg:    Dp = 56.dp,
    val spinner:        Dp = 28.dp,

    // ── Details sheet ───────────────────────────────────────────────────────
    val detailsHeroFadeH: Dp = 140.dp,
    val episodeStillW:    Dp = 130.dp,

    // ── Skeleton widths (text blocks have no fixed production size — their
    //    heights derive from typography/spacing at the call site) ────────────
    val skeletonChipW:  Dp = 45.dp,
    val skeletonGenreW: Dp = 75.dp,
    val skeletonTitleW: Dp = 160.dp,
) {
    companion object {
        // Compact is the base; medium and expanded widen instead of stretching.
        private val CompactMediaCardWidth: Dp = 140.dp
        private val MediumMediaCardWidth: Dp = 160.dp
        private val ExpandedMediaCardWidth: Dp = 180.dp

        private val CompactLibraryCardWidth: Dp = 220.dp
        private val MediumLibraryCardWidth: Dp = 240.dp
        private val ExpandedLibraryCardWidth: Dp = 280.dp

        /**
         * Card metrics for the current window width. The expanded layout adds
         * columns (see `GridCells.Adaptive`) and scales single-row cards; it
         * never introduces new elements.
         */
        fun forWindowWidth(widthSizeClass: WindowWidthSizeClass?): AppSizes =
            when (widthSizeClass) {
                WindowWidthSizeClass.Medium -> AppSizes(
                    mediaCardWidth = MediumMediaCardWidth,
                    libraryCardW = MediumLibraryCardWidth,
                    gridCellMin = 120.dp,
                )
                WindowWidthSizeClass.Expanded -> AppSizes(
                    mediaCardWidth = ExpandedMediaCardWidth,
                    libraryCardW = ExpandedLibraryCardWidth,
                    gridCellMin = 140.dp,
                )
                else -> AppSizes()
            }
    }
}

val LocalAppSizes = staticCompositionLocalOf { AppSizes() }

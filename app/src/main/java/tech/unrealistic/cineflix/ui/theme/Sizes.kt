package tech.unrealistic.cineflix.ui.theme


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SIZE TOKENS
// Named sizes for icons, avatars, controls and tap targets.
// Composables must never declare raw `.size(N.dp)` for these — use
// AppTheme.sizes.* (see AppTheme in Theme.kt).
// Touch targets are never below 48dp (accessibility minimum).
// ═══════════════════════════════════════════════════════════════════════════

data class AppSizes(
    // ── Icon glyphs (visual only — hit area comes from touchTarget) ─────────
    val iconXs:   Dp = 13.dp,   // star / tiny glyphs
    val iconSm:   Dp = 16.dp,   // inline icons inside chips & buttons
    val iconMd:   Dp = 20.dp,   // standard icon
    val iconLg:   Dp = 24.dp,   // large icon
    val iconXl:   Dp = 32.dp,   // icon inside round action buttons

    // ── Touch targets (interactive) ─────────────────────────────────────────
    val touchTarget:    Dp = 48.dp,  // absolute minimum tappable area
    val topBarControl:  Dp = 48.dp,  // search pill / top bar buttons
    val cardAction:     Dp = 48.dp,  // favourite / save on cards
    val closeButton:    Dp = 48.dp,  // sheet close button
    val chipTouchArea:  Dp = 48.dp,  // FilterChip / GenreChip hit height

    // ── Component sizes ─────────────────────────────────────────────────────
    // ── Stroke widths ───────────────────────────────────────────────────
    val hairline:         Dp = 0.5.dp,  // pills, dividers
    val glassBorderWidth: Dp = 0.8.dp,  // glass rim
    val borderWidth:      Dp = 1.dp,    // standard border

    val actionButton:   Dp = 52.dp,  // play / favourite / share round buttons
    val roundAction:    Dp = 30.dp,  // visible round icon buttons on cards
    val likeButton:     Dp = 48.dp,  // episode like button (hit target ≥48)
    val dragHandleWidth:  Dp = 36.dp,
    val dragHandleHeight: Dp = 4.dp,
    val searchBarHeight:  Dp = 48.dp,
    val avatar:         Dp = 56.dp,  // profile / empty-state icon well

    // ── Card / poster dimensions (content grid) ─────────────────────────────
    val mediaCardWidth:    Dp = 140.dp,
    val mediaCardPosterRatio: Float = 1.5f,   // height = width × ratio (2:3)
    val overlayButtonPad:  Dp = 6.dp,   // inset of corner buttons on posters
    /** Hero carousel height as a fraction of the available screen height. */
    val heroHeightFraction: Float = 0.4f,

    // ── State views (empty / error / offline) ─────────────────────────────
    val stateIcon:      Dp = 40.dp,  // icon in error/empty states
    val stateIconLg:    Dp = 56.dp,  // icon in full-screen error/empty states
    val spinner:        Dp = 28.dp,  // inline loading indicator
    val gridCellMin:    Dp = 110.dp, // adaptive poster grid cell minimum

    // ── Splash ──────────────────────────────────────────────────────────────
    val splashRingCanvas: Dp = 120.dp,
    val playButton:       Dp = 76.dp,
    val playGlyph:        Dp = 30.dp,

    // ── Progress dots (onboarding) ─────────────────────────────────────────
    val dotActiveW:   Dp = 24.dp,
    val dotInactiveW: Dp = 8.dp,
    val dotH:         Dp = 8.dp,

    // ── Glass sphere artwork (onboarding) ──────────────────────────────────
    val sphereBox:      Dp = 220.dp,
    val sphereRing:     Dp = 200.dp,
    val sphereOrb:      Dp = 170.dp,
    val githubMarkLarge: Dp = 74.dp,
    val sphereShadowW:  Dp = 150.dp,
    val sphereShadowH:  Dp = 20.dp,
    val specularW:      Dp = 70.dp,
    val specularH:      Dp = 40.dp,
    val rimSliverW:     Dp = 46.dp,
    val rimSliverH:     Dp = 16.dp,
    val ringBorderWide: Dp = 2.dp,
    val ringBorderThin: Dp = 1.5.dp,

    // ── 3D folder artwork (onboarding) ────────────────────────────────────
    val folderTabW:    Dp = 96.dp,
    val folderTabH:    Dp = 26.dp,
    val folderBodyW:   Dp = 200.dp,
    val folderBodyH:   Dp = 150.dp,
    val fileLineH:     Dp = 8.dp,
    val fileLineWidths: List<Dp> = listOf(120.dp, 88.dp, 104.dp),
    val downloadDisc:  Dp = 40.dp,
    val folderShadowW: Dp = 170.dp,
    val folderShadowH: Dp = 22.dp,

    // ── Details sheet ───────────────────────────────────────────────────
    val detailsHeroHeight: Dp = 300.dp,  // backdrop height
    val detailsHeroFadeH:  Dp = 140.dp,  // backdrop→surface fade height
    val iconCompact:       Dp = 18.dp,   // close / like glyphs inside 48dp targets
    val episodeStillW:     Dp = 130.dp,  // episode thumbnail width
    val libraryCardW:      Dp = 220.dp,  // liked-episode card width

    // ── Skeleton widths (text blocks have no production fixed size — their
    //    heights are derived from typography/spacing at the call site) ──────
    val skeletonChipW:      Dp = 45.dp,
    val skeletonGenreW:     Dp = 75.dp,
    val skeletonTitleW:     Dp = 160.dp,
)

val LocalAppSizes = staticCompositionLocalOf { AppSizes() }

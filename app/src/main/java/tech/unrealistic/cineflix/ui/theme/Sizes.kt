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
    val actionButton:   Dp = 52.dp,  // play / favourite / share round buttons
    val roundAction:    Dp = 30.dp,  // visible round icon buttons on cards
    val likeButton:     Dp = 32.dp,  // episode like button (visual)
    val dragHandleWidth:  Dp = 36.dp,
    val dragHandleHeight: Dp = 4.dp,
    val searchBarHeight:  Dp = 48.dp,
    val avatar:         Dp = 56.dp,  // profile / empty-state icon well
    val skeletonAction: Dp = 52.dp,  // shimmer placeholder for action buttons

    // ── Card / poster dimensions (content grid) ─────────────────────────────
    val mediaCardWidth:    Dp = 120.dp,
    val mediaCardPosterRatio: Float = 1.5f,   // height = width × ratio
    val heroCardWidth:     Dp = 300.dp,
    val heroCardHeight:    Dp = 180.dp,
)

val LocalAppSizes = staticCompositionLocalOf { AppSizes() }

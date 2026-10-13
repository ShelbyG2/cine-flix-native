package tech.unrealistic.cineflix.ui.theme


import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SHAPE TOKENS (resolved from JSON)
//   borderRadius.sm = 4dp
//   borderRadius.lg = 8dp
//   borderRadius.xl = 16dp
//
// Note: borderRadius.multi-value ({sm} {lg} = 4dp 8dp) is used for
// asymmetric shapes — see BottomSheetShape below.
// ═══════════════════════════════════════════════════════════════════════════

val AppShapes = Shapes(
    // Tooltip, snackbar, small tags → borderRadius.sm
    extraSmall = RoundedCornerShape(4.dp),

    // Input fields, chips, small buttons → borderRadius.lg
    small = RoundedCornerShape(8.dp),

    // Cards, dialogs → between lg and xl
    medium = RoundedCornerShape(12.dp),

    // Large cards, modals → borderRadius.xl
    large = RoundedCornerShape(16.dp),

    // FAB, bottom sheets → beyond xl
    extraLarge = RoundedCornerShape(24.dp),
)

// ── Named shape constants for semantic use ─────────────────────────────────
// Use these instead of magic numbers in composables

/** 4dp — from borderRadius.sm */
val ShapeSmall = RoundedCornerShape(4.dp)

/** 8dp — from borderRadius.lg */
val ShapeMedium = RoundedCornerShape(8.dp)

/** 16dp — from borderRadius.xl */
val ShapeLarge = RoundedCornerShape(16.dp)

/** multi-value: top=4dp bottom=8dp — from borderRadius.multi-value ({sm} {lg}) */
val ShapeAsymmetric = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 4.dp,
    bottomStart = 8.dp,
    bottomEnd = 8.dp
)

/** Bottom sheet — 24dp top corners, flat bottom */
val BottomSheetShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/** Player / cover full screen — no rounding */
val FullScreenShape = RoundedCornerShape(0.dp)

/** Pill — fully rounded ends (chips, glass pills). Use instead of RoundedCornerShape(50). */
val ShapePill = RoundedCornerShape(50)

/** Circle — round icon buttons, avatars. Use instead of raw CircleShape in composables. */
val ShapeCircle = CircleShape

/** 50dp radius — large glass orbs / spheres. */
val ShapeRound = RoundedCornerShape(50.dp)

/** 14dp radius — onboarding CTA buttons. */
val ShapeButton = RoundedCornerShape(14.dp)

/** Folder tab — top 18dp, bottom 6dp (onboarding 3D folder artwork). */
val ShapeTab = RoundedCornerShape(
    topStart = 18.dp,
    topEnd = 18.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

/** Folder body — top 8dp, bottom 18dp (onboarding 3D folder artwork). */
val ShapeFolderBody = RoundedCornerShape(
    topStart = 8.dp,
    topEnd = 8.dp,
    bottomStart = 18.dp,
    bottomEnd = 18.dp
)
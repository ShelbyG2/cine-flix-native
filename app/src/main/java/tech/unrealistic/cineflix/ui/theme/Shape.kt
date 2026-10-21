package tech.unrealistic.cineflix.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SHAPE SYSTEM — 8 / 16 / 28 dp plus pill. Nothing else.
// Each named shape equals its same-named M3 slot, so there is one meaning per
// radius across the app.
// ═══════════════════════════════════════════════════════════════════════════

val AppShapes = Shapes(
    // Tooltips / snackbars only — the one place a 4dp radius is still allowed.
    extraSmall = RoundedCornerShape(4.dp),

    // Inputs, chips, small tiles.
    small = RoundedCornerShape(8.dp),

    // Cards, dialogs, buttons.
    medium = RoundedCornerShape(16.dp),

    // Large cards and full-bleed media blocks.
    large = RoundedCornerShape(16.dp),

    // Sheets and other large containers.
    extraLarge = RoundedCornerShape(28.dp),
)

/** 8dp — posters, episode stills, small tiles. */
val ShapeSmall = RoundedCornerShape(8.dp)

/** 16dp — cards, dialogs, buttons. */
val ShapeMedium = RoundedCornerShape(16.dp)

/** 28dp — sheets and other large containers. */
val ShapeLarge = RoundedCornerShape(28.dp)

/** Pill — chips and fully rounded actions. */
val ShapePill = RoundedCornerShape(50)

/** Circle — round icon buttons and avatars. */
val ShapeCircle = CircleShape

/** Bottom sheet — 28dp top corners, flat bottom. */
val BottomSheetShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp,
)

/** Player / full-screen surfaces — no rounding. */
val FullScreenShape = RoundedCornerShape(0.dp)

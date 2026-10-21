package tech.unrealistic.cineflix.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SPACING — one 4-point grid: 4 / 8 / 12 / 16 / 24 / 32 / 48.
// Spacing owns gaps and padding; Sizes owns icon and control dimensions.
// ═══════════════════════════════════════════════════════════════════════════

data class AppSpacing(
    // ── Grid ────────────────────────────────────────────────────────────────
    val xs:   Dp = 4.dp,
    val sm:   Dp = 8.dp,
    val md:   Dp = 12.dp,
    val lg:   Dp = 16.dp,
    val xl:   Dp = 24.dp,
    val xxl:  Dp = 32.dp,
    val xxxl: Dp = 48.dp,

    // ── Semantic aliases ────────────────────────────────────────────────────
    val screenPadding: Dp = 16.dp,  // horizontal screen margin
    val sectionGap:    Dp = 32.dp,  // between sections
    val itemGap:       Dp = 8.dp,   // between list items
    val inlineGap:     Dp = 4.dp,   // between inline elements

    // ── Component spacing (all on the grid) ─────────────────────────────────
    val topBarPadding:      Dp = 8.dp,   // floating top bar inner padding
    val chipPaddingH:       Dp = 12.dp,  // chip horizontal padding
    val chipPaddingV:       Dp = 8.dp,   // chip vertical padding
    val chipRowGap:         Dp = 4.dp,   // rating chip icon ↔ label
    val actionRowH:         Dp = 16.dp,  // action row horizontal
    val actionRowV:         Dp = 16.dp,  // action row vertical
    val buttonPaddingH:     Dp = 24.dp,  // primary button content padding
    val buttonPaddingV:     Dp = 12.dp,
    val buttonGap:          Dp = 8.dp,   // icon ↔ label inside buttons
    val sectionPaddingBottom: Dp = 24.dp,
    val heroTopInset:       Dp = 48.dp,  // carousel inset under the top bar
    val titleOverlap:       Dp = 24.dp,  // details title overlap onto backdrop
)

// staticCompositionLocalOf — spacing never changes at runtime
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

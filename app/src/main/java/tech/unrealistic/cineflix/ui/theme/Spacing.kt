package tech.unrealistic.cineflix.ui.theme



import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// SPACING TOKENS (resolved from JSON)
//
//   dimension.scale = 2
//   dimension.xs    = 4dp
//   dimension.sm    = xs × scale   =  4 × 2 =  8dp
//   dimension.md    = sm × scale   =  8 × 2 = 16dp
//   dimension.lg    = md × scale   = 16 × 2 = 32dp
//   dimension.xl    = lg × scale   = 32 × 2 = 64dp
//
//   spacing tokens mirror dimension tokens 1:1
//   spacing.multi-value = sm + xl = 8dp + 64dp (horizontal + vertical)
// ═══════════════════════════════════════════════════════════════════════════

data class AppSpacing(
    // ── Base scale ───────────────────────────────────────────────────────
    val xs:  Dp = 4.dp,    // dimension.xs
    val sm:  Dp = 8.dp,    // dimension.sm  = xs × 2
    val md:  Dp = 16.dp,   // dimension.md  = sm × 2
    val lg:  Dp = 32.dp,   // dimension.lg  = md × 2
    val xl:  Dp = 64.dp,   // dimension.xl  = lg × 2

    // ── Semantic aliases ──────────────────────────────────────────────────
    // Maps the token names to intent-based names for use in composables
    val iconSize:          Dp = 20.dp,  // standard icon
    val iconSizeLg:        Dp = 24.dp,  // large icon
    val touchTarget:       Dp = 48.dp,  // minimum accessible tap target
    val cardPadding:       Dp = 16.dp,  // = md
    val screenPadding:     Dp = 16.dp,  // = md, horizontal screen margin
    val sectionGap:        Dp = 32.dp,  // = lg, between sections
    val itemGap:           Dp = 8.dp,   // = sm, between list items
    val inlineGap:         Dp = 4.dp,   // = xs, between inline elements

    // ── multi-value spacing token (spacing.multi-value = sm xl = 8dp 64dp)
    // Interpreted as: vertical=8dp, horizontal=64dp
    val multiValueVertical:   Dp = 8.dp,
    val multiValueHorizontal: Dp = 64.dp,
)

// staticCompositionLocalOf — spacing never changes at runtime
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
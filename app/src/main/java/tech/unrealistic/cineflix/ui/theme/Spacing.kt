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

    // ── Sub-scale steps (used where the ×2 scale has no step) ───────────────
    val gap2:   Dp = 2.dp,
    val gap3:   Dp = 3.dp,
    val gap6:   Dp = 6.dp,
    val gap10:  Dp = 10.dp,
    val gap12:  Dp = 12.dp,
    val gap20:  Dp = 20.dp,
    val gap24:  Dp = 24.dp,
    val gap40:  Dp = 40.dp,

    // ── Semantic component spacing ──────────────────────────────────────────
    val topBarPadding:   Dp = 10.dp,  // FloatingTopBar inner padding
    val chipPaddingH:    Dp = 10.dp,  // genre chip horizontal padding
    val chipPaddingV:    Dp = 4.dp,   // genre chip vertical padding
    val chipRowGap:      Dp = 3.dp,   // rating chip icon↔label gap
    val actionRowH:      Dp = 20.dp,  // ActionButtons row horizontal
    val actionRowV:      Dp = 16.dp,  // ActionButtons row vertical
    val buttonPaddingH:  Dp = 24.dp,  // Play button content padding
    val buttonPaddingV:  Dp = 12.dp,
    val buttonGap:       Dp = 6.dp,   // icon↔label inside buttons
    val sectionPaddingBottom: Dp = 28.dp,  // below each MediaSection
    val heroTopInset:    Dp = 56.dp,  // carousel inset under the floating top bar
)

// staticCompositionLocalOf — spacing never changes at runtime
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
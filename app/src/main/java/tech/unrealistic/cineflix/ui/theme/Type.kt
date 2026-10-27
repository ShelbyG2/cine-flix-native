package tech.unrealistic.cineflix.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import tech.unrealistic.cineflix.R

// ═══════════════════════════════════════════════════════════════════════════
// TYPEFACE — Inter, bundled offline (OFL, see licenses/Inter-OFL.txt).
// One family; premium comes from weight contrast and tight display tracking,
// never from a second face. The family declares 400 / 500 / 600 / 700, so
// nothing in the app may ask for a weight outside that set.
// ═══════════════════════════════════════════════════════════════════════════
val AppFontFamily = FontFamily(
    Font(R.font.inter_regular,  FontWeight.Normal),
    Font(R.font.inter_medium,   FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold,     FontWeight.Bold),
)

// ═══════════════════════════════════════════════════════════════════════════
// SCALE — six separated steps mapped onto the M3 slots.
//
//   step      size  weight     M3 slots
//   caption    12   Regular    bodySmall
//   body       14   Regular    bodyMedium
//   bodyLarge  16   Regular    bodyLarge
//   title      22   SemiBold   titleLarge (section + card titles)
//   headline   32   Bold       headlineLarge (screen + detail titles)
//   display    48   Bold       displayLarge (hero / splash)
//
// Tracking: display/headline -0.02em, titles -0.01em, body 0, labels +0.01em.
// Line height: headings 1.1–1.15×, body 1.4–1.5×. No text below 11sp.
// ═══════════════════════════════════════════════════════════════════════════

private fun text(
    size: Int,
    weight: FontWeight,
    lineHeightMultiplier: Float,
    trackingEm: Float = 0f,
): TextStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * lineHeightMultiplier).sp,
    letterSpacing = trackingEm.em,
)

val AppTypography = Typography(

    // ── Display — hero / splash only ────────────────────────────────────────
    displayLarge  = text(48, FontWeight.Bold,     1.08f, -0.02f),
    displayMedium = text(40, FontWeight.Bold,     1.10f, -0.02f),
    displaySmall  = text(36, FontWeight.SemiBold, 1.10f, -0.02f),

    // ── Headline — screen titles, detail titles ─────────────────────────────
    headlineLarge  = text(32, FontWeight.Bold,     1.12f, -0.02f),
    headlineMedium = text(28, FontWeight.Bold,     1.14f, -0.02f),
    headlineSmall  = text(24, FontWeight.SemiBold, 1.15f, -0.02f),

    // ── Title — section headers, card titles ────────────────────────────────
    titleLarge  = text(22, FontWeight.SemiBold, 1.15f, -0.01f),
    titleMedium = text(17, FontWeight.SemiBold, 1.25f, -0.01f),
    titleSmall  = text(15, FontWeight.Medium,   1.30f, -0.01f),

    // ── Body ────────────────────────────────────────────────────────────────
    bodyLarge  = text(16, FontWeight.Normal, 1.45f),
    bodyMedium = text(14, FontWeight.Normal, 1.45f),
    bodySmall  = text(12, FontWeight.Normal, 1.45f),

    // ── Labels — buttons, chips, nav ────────────────────────────────────────
    labelLarge  = text(14, FontWeight.Medium, 1.30f, 0.01f),
    labelMedium = text(12, FontWeight.Medium, 1.30f, 0.01f),
    labelSmall  = text(11, FontWeight.Medium, 1.30f, 0.01f),
)

// ═══════════════════════════════════════════════════════════════════════════
// NAMED STYLES — the only survivors beyond the M3 slots.
// ═══════════════════════════════════════════════════════════════════════════

/** CINEFLIX wordmark: Bold, wide tracking, rendered from all-caps copy. */
val WordmarkStyle = TextStyle(
    fontFamily    = AppFontFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 16.sp,
    lineHeight    = 18.sp,
    letterSpacing = 0.12.em,
)

/** Splash wordmark — same voice, display size for the entrance. */
val SplashWordmarkStyle = TextStyle(
    fontFamily    = AppFontFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 32.sp,
    lineHeight    = 36.sp,
    letterSpacing = 0.12.em,
)

/** Extra leading factor for long-form body text (ExpandableText). */
const val BodyLineHeightBoost = 1.2f

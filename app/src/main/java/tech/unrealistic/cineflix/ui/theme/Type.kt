package tech.unrealistic.cineflix.ui.theme



import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import tech.unrealistic.cineflix.R


// ═══════════════════════════════════════════════════════════════════════════
// LEXEND FONT FAMILY
// Sourced from tokens: fontFamilies.heading / fontFamilies.body
// User spec: Light-16, Regular-18, Medium-22, SemiBold-24
// ═══════════════════════════════════════════════════════════════════════════

val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val LexendFont = GoogleFont("Lexend")

val LexendFamily = FontFamily(
    Font(
        googleFont   = LexendFont,
        fontProvider = GoogleFontsProvider,
        weight       = FontWeight.Light       // 300 — body 16sp
    ),
    Font(
        googleFont   = LexendFont,
        fontProvider = GoogleFontsProvider,
        weight       = FontWeight.Normal      // 400 — body 18sp
    ),
    Font(
        googleFont   = LexendFont,
        fontProvider = GoogleFontsProvider,
        weight       = FontWeight.Medium      // 500 — title 22sp
    ),
    Font(
        googleFont   = LexendFont,
        fontProvider = GoogleFontsProvider,
        weight       = FontWeight.SemiBold    // 600 — headline 24sp
    ),
    Font(
        googleFont   = LexendFont,
        fontProvider = GoogleFontsProvider,
        weight       = FontWeight.Bold        // 700 — display/h1
    ),
)

// ═══════════════════════════════════════════════════════════════════════════
// FONT SIZE TOKENS (resolved from JSON expressions, body = 16sp)
//   xs   = 16 × 0.65          = 10sp
//   sm   = 16 × 0.85          = 14sp
//   body = 16sp
//   h6   = 16sp
//   h5   = round(16 × 1.25¹)  = 20sp
//   h4   = round(16 × 1.25²)  = 25sp
//   h3   = round(16 × 1.25³)  = 31sp
//   h2   = round(16 × 1.25⁴)  = 39sp
//   h1   = round(16 × 1.25⁵)  = 49sp
// ═══════════════════════════════════════════════════════════════════════════

private val FontSizeXs   = 10.sp
private val FontSizeSm   = 14.sp
private val FontSizeBody = 16.sp   // Lexend Light  — user spec
private val FontSizeH6   = 16.sp
private val FontSizeH5   = 20.sp
private val FontSizeH4   = 25.sp
private val FontSizeH3   = 31.sp
private val FontSizeH2   = 39.sp
private val FontSizeH1   = 49.sp

// Spec sizes (direct from user):
private val FontSizeSpec16 = 16.sp   // Lexend Light    16
private val FontSizeSpec18 = 18.sp   // Lexend Regular  18
private val FontSizeSpec22 = 22.sp   // Lexend Medium   22
private val FontSizeSpec24 = 24.sp   // Lexend SemiBold 24

// ═══════════════════════════════════════════════════════════════════════════
// LINE HEIGHTS (from JSON)
//   heading: 110%
//   body:    140%
// ═══════════════════════════════════════════════════════════════════════════

private fun lineHeightFor(fontSize: Float, multiplier: Float) =
    (fontSize * multiplier).sp

// ═══════════════════════════════════════════════════════════════════════════
// TYPOGRAPHY SCALE
//
// M3 slot            → User spec mapping
// ─────────────────────────────────────────────────────────────────────────
// displayLarge       → h1  49sp / Bold        / heading lineHeight (110%)
// displayMedium      → h2  39sp / Bold        / heading lineHeight
// displaySmall       → h3  31sp / SemiBold    / heading lineHeight
// headlineLarge      → h3  31sp / SemiBold    / heading lineHeight
// headlineMedium     → h4  25sp / SemiBold    / heading lineHeight
// headlineSmall      → Lexend SemiBold 24sp   ← USER SPEC
// titleLarge         → Lexend Medium   22sp   ← USER SPEC
// titleMedium        → h5  20sp / Medium      / heading lineHeight
// titleSmall         → h6  16sp / Medium      / body lineHeight
// bodyLarge          → Lexend Light    16sp   ← USER SPEC (primary body)
// bodyMedium         → Lexend Regular  18sp   ← USER SPEC (secondary body)
// bodySmall          → sm  14sp / Light       / body lineHeight
// labelLarge         → sm  14sp / Medium      / body lineHeight (buttons)
// labelMedium        → xs  10sp / Medium      / body lineHeight
// labelSmall         → xs  10sp / Normal      / body lineHeight
// ═══════════════════════════════════════════════════════════════════════════

val AppTypography = Typography(

    // ── Display — h1, h2, hero text ─────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = FontSizeH1,                          // 49sp
        lineHeight    = lineHeightFor(49f, 1.10f),           // 110% = 53.9sp
        letterSpacing = (-49f * 0.05f / 100f).sp            // -5% (decreased)
    ),
    displayMedium = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = FontSizeH2,                          // 39sp
        lineHeight    = lineHeightFor(39f, 1.10f),           // 42.9sp
        letterSpacing = (-39f * 0.05f / 100f).sp
    ),
    displaySmall = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = FontSizeH3,                          // 31sp
        lineHeight    = lineHeightFor(31f, 1.10f),           // 34.1sp
        letterSpacing = 0.sp
    ),

    // ── Headline ─────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = FontSizeH3,                          // 31sp
        lineHeight    = lineHeightFor(31f, 1.10f),
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = FontSizeH4,                          // 25sp
        lineHeight    = lineHeightFor(25f, 1.10f),           // 27.5sp
        letterSpacing = 0.sp
    ),

    // ── USER SPEC: Lexend SemiBold 24sp ──────────────────────────────────
    headlineSmall = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.SemiBold,                 // 600
        fontSize      = FontSizeSpec24,                      // 24sp ← spec
        lineHeight    = lineHeightFor(24f, 1.10f),           // 26.4sp
        letterSpacing = 0.sp
    ),

    // ── USER SPEC: Lexend Medium 22sp ────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Medium,                   // 500
        fontSize      = FontSizeSpec22,                      // 22sp ← spec
        lineHeight    = lineHeightFor(22f, 1.10f),           // 24.2sp
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = FontSizeH5,                          // 20sp
        lineHeight    = lineHeightFor(20f, 1.10f),           // 22sp
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = FontSizeH6,                          // 16sp
        lineHeight    = lineHeightFor(16f, 1.40f),           // 22.4sp
        letterSpacing = 0.sp
    ),

    // ── USER SPEC: Lexend Light 16sp ─────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Light,                    // 300
        fontSize      = FontSizeSpec16,                      // 16sp ← spec
        lineHeight    = lineHeightFor(16f, 1.40f),           // 22.4sp (body 140%)
        letterSpacing = 0.sp
    ),

    // ── USER SPEC: Lexend Regular 18sp ───────────────────────────────────
    bodyMedium = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Normal,                   // 400
        fontSize      = FontSizeSpec18,                      // 18sp ← spec
        lineHeight    = lineHeightFor(18f, 1.40f),           // 25.2sp
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Light,
        fontSize      = FontSizeSm,                          // 14sp
        lineHeight    = lineHeightFor(14f, 1.40f),           // 19.6sp
        letterSpacing = 0.sp
    ),

    // ── Label — buttons, chips, badges ───────────────────────────────────
    labelLarge = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Medium,                   // button text
        fontSize      = FontSizeSm,                         // 14sp
        lineHeight    = lineHeightFor(14f, 1.40f),
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = lineHeightFor(12f, 1.40f),
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = LexendFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = FontSizeXs,                         // 10sp
        lineHeight    = lineHeightFor(10f, 1.40f),
        letterSpacing = 0.sp
    ),
)
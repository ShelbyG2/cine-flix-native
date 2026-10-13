package tech.unrealistic.cineflix.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Theme-internal helper: same color, new alpha.
 * Lets theme files build translucent variants without `.copy(alpha)` in composables.
 */
fun Color.withAlpha(alpha: Float): Color = Color(red, green, blue, alpha)


// ═══════════════════════════════════════════════════════════════════════════
// AUTO-GENERATED FROM TOKENS STUDIO JSON
// Raw palette — never reference these directly in composables.
// Use MaterialTheme.colorScheme.* or AppTheme.colors.* instead.
// ═══════════════════════════════════════════════════════════════════════════

// ── Absolute ──────────────────────────────────────────────────────────────
internal val ColorBlack        = Color(0xFF000000)
internal val ColorWhite        = Color(0xFFFFFFFF)

// ── Gray scale ────────────────────────────────────────────────────────────
internal val ColorGray100      = Color(0xFFF7FAFC)
internal val ColorGray200      = Color(0xFFEDF2F7)
internal val ColorGray300      = Color(0xFFE2E8F0)
internal val ColorGray400      = Color(0xFFCBD5E0)
internal val ColorGray500      = Color(0xFFA0AEC0)
internal val ColorGray600      = Color(0xFF718096)
internal val ColorGray700      = Color(0xFF4A5568)
internal val ColorGray800      = Color(0xFF2D3748)
internal val ColorGray900      = Color(0xFF1A202C)

// ── Red ───────────────────────────────────────────────────────────────────
internal val ColorRed100       = Color(0xFFFFF5F5)
internal val ColorRed200       = Color(0xFFFED7D7)
internal val ColorRed300       = Color(0xFFFEB2B2)
internal val ColorRed400       = Color(0xFFFC8181)
internal val ColorRed500       = Color(0xFFF56565)
internal val ColorRed600       = Color(0xFFE53E3E)
internal val ColorRed700       = Color(0xFFC53030)
internal val ColorRed800       = Color(0xFF9B2C2C)
internal val ColorRed900       = Color(0xFF742A2A)

// ── Orange ────────────────────────────────────────────────────────────────
internal val ColorOrange100    = Color(0xFFFFFAF0)
internal val ColorOrange200    = Color(0xFFFEEBC8)
internal val ColorOrange300    = Color(0xFFFBD38D)
internal val ColorOrange400    = Color(0xFFF6AD55)
internal val ColorOrange500    = Color(0xFFED8936)
internal val ColorOrange600    = Color(0xFFDD6B20)
internal val ColorOrange700    = Color(0xFFC05621)
internal val ColorOrange800    = Color(0xFF9C4221)
internal val ColorOrange900    = Color(0xFF7B341E)

// ── Green ─────────────────────────────────────────────────────────────────
internal val ColorGreen100     = Color(0xFFF0FFF4)
internal val ColorGreen200     = Color(0xFFC6F6D5)
internal val ColorGreen300     = Color(0xFF9AE6B4)
internal val ColorGreen400     = Color(0xFF68D391)
internal val ColorGreen500     = Color(0xFF48BB78)
internal val ColorGreen600     = Color(0xFF38A169)
internal val ColorGreen700     = Color(0xFF2F855A)
internal val ColorGreen800     = Color(0xFF276749)
internal val ColorGreen900     = Color(0xFF22543D)

// ── Teal (primary accent) ──────────────────────────────────────────────────
internal val ColorTeal100      = Color(0xFFE6FFFA)
internal val ColorTeal200      = Color(0xFFB2F5EA)
internal val ColorTeal300      = Color(0xFF81E6D9)
internal val ColorTeal400      = Color(0xFF4FD1C5)
internal val ColorTeal500      = Color(0xFF38B2AC)
internal val ColorTeal600      = Color(0xFF319795)
internal val ColorTeal700      = Color(0xFF2C7A7B)
internal val ColorTeal800      = Color(0xFF285E61)
internal val ColorTeal900      = Color(0xFF234E52)

// ── Blue ──────────────────────────────────────────────────────────────────
internal val ColorBlue100      = Color(0xFFEBF8FF)
internal val ColorBlue200      = Color(0xFFBEE3F8)
internal val ColorBlue300      = Color(0xFF90CDF4)
internal val ColorBlue400      = Color(0xFF63B3ED)
internal val ColorBlue500      = Color(0xFF4299E1)
internal val ColorBlue600      = Color(0xFF3182CE)
internal val ColorBlue700      = Color(0xFF2B6CB0)
internal val ColorBlue800      = Color(0xFF2C5282)
internal val ColorBlue900      = Color(0xFF2A4365)

// ── Indigo ────────────────────────────────────────────────────────────────
internal val ColorIndigo100    = Color(0xFFEBF4FF)
internal val ColorIndigo500    = Color(0xFF667EEA)
internal val ColorIndigo600    = Color(0xFF5A67D8)
internal val ColorIndigo700    = Color(0xFF4C51BF)
internal val ColorIndigo800    = Color(0xFF434190)
internal val ColorIndigo900    = Color(0xFF3C366B)

// ── Purple ────────────────────────────────────────────────────────────────
internal val ColorPurple100    = Color(0xFFFAF5FF)
internal val ColorPurple400    = Color(0xFFB794F4)
internal val ColorPurple500    = Color(0xFF9F7AEA)
internal val ColorPurple600    = Color(0xFF805AD5)
internal val ColorPurple700    = Color(0xFF6B46C1)
internal val ColorPurple800    = Color(0xFF553C9A)
internal val ColorPurple900    = Color(0xFF44337A)

// ── Pink ──────────────────────────────────────────────────────────────────
internal val ColorPink100      = Color(0xFFFFF5F7)
internal val ColorPink500      = Color(0xFFED64A6)
internal val ColorPink600      = Color(0xFFD53F8C)
internal val ColorPink700      = Color(0xFFB83280)

// ═══════════════════════════════════════════════════════════════════════════
// SEMANTIC / BRAND TOKENS
// Mapped directly from the top-level JSON keys
// ═══════════════════════════════════════════════════════════════════════════

internal val ColorNavbar           = Color(0xFF1B1B1B)  // Navbar
internal val ColorCover            = Color(0xFF131313)  // Cover / Home / Player
internal val ColorPrimaryBrand     = Color(0xFF131313)  // Primary
internal val ColorText             = Color(0xFF040404)  // Text
internal val ColorLightBackground  = Color(0xFF848484)  // Light Background (legacy)
internal val ColorStroke           = Color(0xFF969696)  // Stroke

// AA-corrected light-mode semantics (WCAG 4.5:1 on white)
internal val ColorTextSecondary    = Color(0xFF6B6B6B)  // 5.3:1 on #FFF
internal val ColorOutlineLight     = Color(0xFF787878)  // 4.4:1 on #FFF
internal val ColorSurfaceLight     = Color(0xFFF7FAFC)  // near-white app background
internal val ColorSurfaceLightAlt  = Color(0xFFEDF2F7)  // cover / navbar / player
internal val ColorSecondaryBrand   = Color(0xFFB8B8B8)  // Secondary Color
internal val ColorSubText          = Color(0xFF878787)  // Sub Text
internal val ColorBlack2B          = Color(0xFF2B2B2B)  // Black #2b2b2b

// ═══════════════════════════════════════════════════════════════════════════
// LIGHT COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════════
internal val md_light_primary              = ColorTeal700   // Teal600 was 3.5:1 on white — Teal700 = 5.0:1
internal val md_light_onPrimary            = ColorWhite
internal val md_light_primaryContainer     = ColorTeal100
internal val md_light_onPrimaryContainer   = ColorTeal900
internal val md_light_secondary            = ColorBlue600
internal val md_light_onSecondary          = ColorWhite
internal val md_light_secondaryContainer   = ColorBlue100
internal val md_light_onSecondaryContainer = ColorBlue900
internal val md_light_tertiary             = ColorPurple600
internal val md_light_onTertiary           = ColorWhite
internal val md_light_tertiaryContainer    = ColorPurple100
internal val md_light_onTertiaryContainer  = ColorPurple900
internal val md_light_error                = ColorRed700    // Red600 was 4.1:1 — Red700 = 5.5:1
internal val md_light_onError              = ColorWhite
internal val md_light_errorContainer       = ColorRed100
internal val md_light_onErrorContainer     = ColorRed800
internal val md_light_background           = ColorSurfaceLight     // legible light app background
internal val md_light_onBackground         = ColorText             // #040404
internal val md_light_surface              = ColorWhite
internal val md_light_onSurface            = ColorText
internal val md_light_surfaceVariant       = ColorGray100
internal val md_light_onSurfaceVariant     = ColorTextSecondary     // #878787 was 3.6:1 — now 5.3:1
internal val md_light_outline              = ColorOutlineLight      // #969696 was 2.96:1 — now 4.4:1
internal val md_light_outlineVariant       = ColorGray300
internal val md_light_inverseSurface       = ColorGray800
internal val md_light_inverseOnSurface     = ColorGray100
internal val md_light_inversePrimary       = ColorTeal300
internal val md_light_surfaceTint          = ColorTeal600
internal val md_light_scrim                = ColorBlack

// ═══════════════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════════
internal val md_dark_primary              = ColorTeal300
internal val md_dark_onPrimary            = ColorTeal900
internal val md_dark_primaryContainer     = ColorTeal800
internal val md_dark_onPrimaryContainer   = ColorTeal100
internal val md_dark_secondary            = ColorBlue300
internal val md_dark_onSecondary          = ColorBlue900
internal val md_dark_secondaryContainer   = ColorBlue800
internal val md_dark_onSecondaryContainer = ColorBlue100
internal val md_dark_tertiary             = ColorPurple400
internal val md_dark_onTertiary           = ColorPurple900
internal val md_dark_tertiaryContainer    = ColorPurple800
internal val md_dark_onTertiaryContainer  = ColorPurple100
internal val md_dark_error                = ColorRed400
internal val md_dark_onError              = ColorRed900
internal val md_dark_errorContainer       = ColorRed800
internal val md_dark_onErrorContainer     = ColorRed100
internal val md_dark_background           = ColorCover             // #131313
internal val md_dark_onBackground         = ColorWhite
internal val md_dark_surface              = ColorNavbar            // #1b1b1b
internal val md_dark_onSurface            = ColorGray300
internal val md_dark_surfaceVariant       = ColorBlack2B           // #2b2b2b
internal val md_dark_onSurfaceVariant     = ColorSecondaryBrand    // #b8b8b8
internal val md_dark_outline              = ColorStroke            // #969696
internal val md_dark_outlineVariant       = ColorGray700
internal val md_dark_inverseSurface       = ColorLightBackground
internal val md_dark_inverseOnSurface     = ColorCover
internal val md_dark_inversePrimary       = ColorTeal600
internal val md_dark_surfaceTint          = ColorTeal300
internal val md_dark_scrim                = ColorBlack

// ═══════════════════════════════════════════════════════════════════════════
// EXTENDED SEMANTIC COLORS
// Extra roles that don't exist in the M3 spec — accessed via AppTheme.colors
// ═══════════════════════════════════════════════════════════════════════════
data class AppExtendedColors(
    val success:            Color,
    val onSuccess:          Color,
    val successContainer:   Color,
    val onSuccessContainer: Color,
    val warning:            Color,
    val onWarning:          Color,
    val warningContainer:   Color,
    val onWarningContainer: Color,
    // Screen-specific surfaces from the brand tokens
    val navbar:             Color,
    val cover:              Color,
    val player:             Color,
    val subText:            Color,
    val stroke:             Color,

    // ── Glass system (tuned per mode — light glass needs a stronger tint/border)
    val glassSurface:       Color,   // frosted base fill (alpha baked in)
    val glassBorder:        Color,   // rim border tint (alpha baked in)
    val glassGloss:         Color,   // specular gloss / streak tint
    val glassNoise:         Color,   // noise grain dot color
    val glassNoiseAlpha:    Float,   // grain strength, tuned per mode
    val glassBorderWidth:   Dp,      // hairline rim width
    val hairline:           Dp,      // 0.5dp hairline (pills, dividers)
    val borderWidth:        Dp,      // standard 1dp border

    // ── On-image roles (ALWAYS light-on-dark in BOTH modes — images are darkened
    //    by imageScrim before text is drawn over them)
    val onImage:            Color,
    val onImageMuted:       Color,
    val onImageInverse:     Color,   // dark text on white buttons over images
    val imageScrimStart:    Color,   // gradient start (transparent)
    val imageScrimEnd:      Color,   // gradient end (dark)
    val scrimStrong:        Color,   // modal / focus dimming
    val scrimSoft:          Color,   // light dimming (cards, pills)

    // ── Accents used over imagery (identical in both modes)
    val ratingStar:         Color,
    val favouriteActive:    Color,
    val savedActive:        Color,

    // ── Navigation chrome
    val navSelectedIcon:    Color,
    val navUnselectedIcon:  Color,

    // ── Shimmer / skeleton
    val skeletonBase:       Color,
    val skeletonHighlight:  Color,
)

internal val LightExtendedColors = AppExtendedColors(
    success            = ColorGreen600,
    onSuccess          = ColorWhite,
    successContainer   = ColorGreen100,
    onSuccessContainer = ColorGreen900,
    warning            = ColorOrange500,
    onWarning          = ColorWhite,
    warningContainer   = ColorOrange100,
    onWarningContainer = ColorOrange900,
    navbar             = ColorSurfaceLightAlt,
    cover              = ColorSurfaceLightAlt,
    player             = ColorSurfaceLight,
    subText            = ColorTextSecondary,
    stroke             = ColorOutlineLight,

    glassSurface       = Color(0x99FFFFFF),  // 60% white — milky, visible over images
    glassBorder        = Color(0x40000000),  // 25% black — visible on light surfaces
    glassGloss         = Color(0x80FFFFFF),  // 50% white specular
    glassNoise         = Color(0x14000000),  // dark grain (visible on light glass)
    glassNoiseAlpha    = 0.05f,
    glassBorderWidth   = 0.8.dp,
    hairline           = 0.5.dp,
    borderWidth        = 1.dp,

    onImage            = ColorWhite,
    onImageMuted       = Color(0xD9FFFFFF),
    onImageInverse     = Color(0xFF0A0A0A),
    imageScrimStart    = Color(0x00000000),
    imageScrimEnd      = Color(0xC0000000),
    scrimStrong        = Color(0xCC000000),
    scrimSoft          = Color(0x66000000),

    ratingStar         = Color(0xFFF5B301),
    favouriteActive    = Color(0xFFFF6B6B),
    savedActive        = Color(0xFFFFC107),

    navSelectedIcon    = ColorTeal700,       // 5.0:1 on light nav surface
    navUnselectedIcon  = ColorTextSecondary, // 5.3:1

    skeletonBase       = Color(0xFFE9EDF2),
    skeletonHighlight  = Color(0xFFFAFCFE),
)

internal val DarkExtendedColors = AppExtendedColors(
    success            = ColorGreen400,
    onSuccess          = ColorGreen900,
    successContainer   = ColorGreen800,
    onSuccessContainer = ColorGreen100,
    warning            = ColorOrange400,
    onWarning          = ColorOrange900,
    warningContainer   = ColorOrange800,
    onWarningContainer = ColorOrange100,
    navbar             = ColorNavbar,          // #1b1b1b
    cover              = ColorCover,           // #131313
    player             = ColorCover,           // #131313
    subText            = ColorSecondaryBrand,  // #b8b8b8 — 7.2:1 on #1b1b1b
    stroke             = ColorStroke,          // #969696

    glassSurface       = Color(0x14FFFFFF),  // 8% white over dark imagery
    glassBorder        = Color(0x4DFFFFFF),  // 30% white rim
    glassGloss         = Color(0x33FFFFFF),  // 20% white specular
    glassNoise         = Color(0x14FFFFFF),  // light grain
    glassNoiseAlpha    = 0.03f,
    glassBorderWidth   = 0.8.dp,
    hairline           = 0.5.dp,
    borderWidth        = 1.dp,

    onImage            = ColorWhite,
    onImageMuted       = Color(0xD9FFFFFF),
    onImageInverse     = Color(0xFF0A0A0A),
    imageScrimStart    = Color(0x00000000),
    imageScrimEnd      = Color(0xC0000000),
    scrimStrong        = Color(0xCC000000),
    scrimSoft          = Color(0x66000000),

    ratingStar         = Color(0xFFF5B301),
    favouriteActive    = Color(0xFFFF6B6B),
    savedActive        = Color(0xFFFFC107),

    navSelectedIcon    = ColorWhite,          // 17.4:1 on #1b1b1b
    navUnselectedIcon  = Color(0xFF9E9E9E),   // 5.9:1

    skeletonBase       = Color(0xFF2B2B2B),
    skeletonHighlight  = Color(0xFF3D3D3D),
)

// ── CompositionLocal — extended colors, provided by CineflixTheme ───────────
val LocalAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }
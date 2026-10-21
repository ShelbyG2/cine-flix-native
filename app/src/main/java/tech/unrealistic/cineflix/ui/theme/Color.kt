package tech.unrealistic.cineflix.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Theme-internal helper: same color, new alpha. */
fun Color.withAlpha(alpha: Float): Color = Color(red, green, blue, alpha)

// ═══════════════════════════════════════════════════════════════════════════
// MONOCHROME PALETTE — black & white, Apple-style system grays.
// No brand hue. Color appears only for status (error/success/warning) and the
// favourite heart. Never reference these directly in composables.
// ═══════════════════════════════════════════════════════════════════════════
internal val ColorBlack      = Color(0xFF000000)
internal val ColorWhite      = Color(0xFFFFFFFF)
internal val ColorInk        = Color(0xFF0B0B0C)   // light-mode primary / text (19:1 on white)

// Light neutrals
internal val ColorGray50     = Color(0xFFFAFAFC)
internal val ColorGray100    = Color(0xFFF2F2F7)   // grouped background
internal val ColorGray150    = Color(0xFFEBEBF0)
internal val ColorGray200    = Color(0xFFE5E5EA)   // hairlines
internal val ColorGray400    = Color(0xFF8E8E93)   // outlines / inactive icons (3.3:1 on white)
internal val ColorGray600    = Color(0xFF6C6C70)   // secondary text (5.0:1 on white)
internal val ColorGray700    = Color(0xFF3A3A3C)

// Dark neutrals
internal val ColorD050       = Color(0xFF0B0B0C)
internal val ColorD100       = Color(0xFF111113)
internal val ColorD200       = Color(0xFF1C1C1E)   // cards / sheets
internal val ColorD300       = Color(0xFF2C2C2E)   // raised / variant
internal val ColorD400       = Color(0xFF3A3A3C)
internal val ColorD500       = Color(0xFF48484A)   // dark hairlines
internal val ColorD600       = Color(0xFF7C7C80)   // dark outlines (4.4:1 on black)
internal val ColorD700       = Color(0xFF98989D)   // dark secondary text (7:1 on black)
internal val ColorD900       = Color(0xFFF2F2F7)   // dark-mode text

// Functional status only (the only chromatic colors in the app)
internal val ColorErrorLight = Color(0xFFD70015)   // 5.6:1 on white
internal val ColorErrorDark  = Color(0xFFFF453A)
internal val ColorHeart      = Color(0xFFFF453A)
internal val ColorSuccessLight = Color(0xFF248A3D)
internal val ColorSuccessDark  = Color(0xFF30D158)
internal val ColorWarningLight = Color(0xFFB25000)
internal val ColorWarningDark  = Color(0xFFFF9F0A)

// ═══════════════════════════════════════════════════════════════════════════
// LIGHT — ink on white
// ═══════════════════════════════════════════════════════════════════════════
internal val md_light_primary              = ColorInk
internal val md_light_onPrimary            = ColorWhite
internal val md_light_primaryContainer     = ColorGray100
internal val md_light_onPrimaryContainer   = ColorInk
internal val md_light_secondary            = ColorGray700
internal val md_light_onSecondary          = ColorWhite
internal val md_light_secondaryContainer   = ColorGray200
internal val md_light_onSecondaryContainer = ColorInk
internal val md_light_tertiary             = ColorGray600
internal val md_light_onTertiary           = ColorWhite
internal val md_light_tertiaryContainer    = ColorGray100
internal val md_light_onTertiaryContainer  = ColorInk
internal val md_light_error                = ColorErrorLight
internal val md_light_onError              = ColorWhite
internal val md_light_errorContainer       = Color(0xFFFFE5E3)
internal val md_light_onErrorContainer     = Color(0xFF5C0009)
internal val md_light_background           = ColorWhite
internal val md_light_onBackground         = ColorInk
internal val md_light_surface              = ColorWhite
internal val md_light_onSurface            = ColorInk
internal val md_light_surfaceVariant       = ColorGray100
internal val md_light_onSurfaceVariant     = ColorGray600
internal val md_light_outline              = ColorGray400
internal val md_light_outlineVariant       = ColorGray200
internal val md_light_inverseSurface       = ColorD200
internal val md_light_inverseOnSurface     = ColorGray100
internal val md_light_inversePrimary       = ColorWhite
internal val md_light_surfaceTint          = ColorInk
internal val md_light_scrim                = ColorBlack
// Surface ladder (pure neutral — no lavender)
internal val md_light_surfaceBright           = ColorWhite
internal val md_light_surfaceDim              = Color(0xFFDCDCE0)
internal val md_light_surfaceContainerLowest  = ColorWhite
internal val md_light_surfaceContainerLow     = ColorGray50
internal val md_light_surfaceContainer        = ColorGray100
internal val md_light_surfaceContainerHigh    = ColorGray150
internal val md_light_surfaceContainerHighest = ColorGray200

// ═══════════════════════════════════════════════════════════════════════════
// DARK — true black, white ink (OLED)
// ═══════════════════════════════════════════════════════════════════════════
internal val md_dark_primary              = ColorWhite
internal val md_dark_onPrimary            = ColorBlack
internal val md_dark_primaryContainer     = ColorD300
internal val md_dark_onPrimaryContainer   = ColorWhite
internal val md_dark_secondary            = ColorGray200
internal val md_dark_onSecondary          = ColorD200
internal val md_dark_secondaryContainer   = ColorD300
internal val md_dark_onSecondaryContainer = ColorD900
internal val md_dark_tertiary             = Color(0xFFAEAEB2)
internal val md_dark_onTertiary           = ColorD200
internal val md_dark_tertiaryContainer    = ColorD400
internal val md_dark_onTertiaryContainer  = ColorD900
internal val md_dark_error                = ColorErrorDark
internal val md_dark_onError              = ColorBlack
internal val md_dark_errorContainer       = Color(0xFF4A1512)
internal val md_dark_onErrorContainer     = Color(0xFFFFD9D6)
internal val md_dark_background           = ColorBlack
internal val md_dark_onBackground         = ColorWhite
internal val md_dark_surface              = ColorD200
internal val md_dark_onSurface            = ColorD900
internal val md_dark_surfaceVariant       = ColorD300
internal val md_dark_onSurfaceVariant     = ColorD700
internal val md_dark_outline              = ColorD600
internal val md_dark_outlineVariant       = ColorD500
internal val md_dark_inverseSurface       = ColorD900
internal val md_dark_inverseOnSurface     = ColorD200
internal val md_dark_inversePrimary       = ColorBlack
internal val md_dark_surfaceTint          = ColorWhite
internal val md_dark_scrim                = ColorBlack
internal val md_dark_surfaceBright           = ColorD300
internal val md_dark_surfaceDim              = ColorBlack
internal val md_dark_surfaceContainerLowest  = ColorBlack
internal val md_dark_surfaceContainerLow     = ColorD050
internal val md_dark_surfaceContainer        = ColorD200
internal val md_dark_surfaceContainerHigh    = ColorD300
internal val md_dark_surfaceContainerHighest = ColorD400

// ═══════════════════════════════════════════════════════════════════════════
// EXTENDED COLORS (structure unchanged so call sites keep compiling)
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
    val navbar:             Color,
    val cover:              Color,
    val player:             Color,
    val subText:            Color,
    val stroke:             Color,

    val glassSurface:       Color,
    val glassBorder:        Color,
    val glassGloss:         Color,
    val glassNoise:         Color,
    val glassNoiseAlpha:    Float,

    val onImage:            Color,
    val onImageMuted:       Color,
    val onImageInverse:     Color,
    val imageScrimStart:    Color,
    val imageScrimEnd:      Color,
    val scrimStrong:        Color,
    val scrimSoft:          Color,
    val imageTopScrim:      Color,
    val imageVignette:      Color,
    val progressDotInactive: Color,

    val ratingStar:         Color,
    val favouriteActive:    Color,
    val savedActive:        Color,

    val imageChipSurface:   Color,
    val onImageChipSurface: Color,
    val onImageChipBorder:  Color,
    val pillBorder:         Color,
    val ratingChipFillAlpha:  Float,
    val ratingChipBorderAlpha: Float,
    val chipUnselectedAlpha:  Float,
    val chipSelectedAlpha:    Float,
    val posterFadeAlpha:      Float,
    val heroTopAlpha:       Float,
    val heroMidAlpha:       Float,
    val heroMutedAlpha:     Float,
    val glassNoiseDensity:  Int,
    val sphereShade:        Color,
    val playerBackground:   Color,
    val pulseRingAlpha1:    Float,
    val pulseRingAlpha2:    Float,

    val navSelectedIcon:    Color,
    val navUnselectedIcon:  Color,

    val skeletonBase:       Color,
    val skeletonHighlight:  Color,
)

internal val LightExtendedColors = AppExtendedColors(
    success            = ColorSuccessLight,
    onSuccess          = ColorWhite,
    successContainer   = Color(0xFFE3F5E8),
    onSuccessContainer = Color(0xFF0B3D1C),
    warning            = ColorWarningLight,
    onWarning          = ColorWhite,
    warningContainer   = Color(0xFFFFF1E0),
    onWarningContainer = Color(0xFF4A2200),
    navbar             = ColorGray50,
    cover              = ColorGray100,
    player             = ColorWhite,
    subText            = ColorGray600,
    stroke             = ColorGray400,

    // Apple-style thin material: white, hairline rim
    glassSurface       = Color(0xB3FFFFFF),
    glassBorder        = Color(0x1F000000),
    glassGloss         = Color(0xB3FFFFFF),
    glassNoise         = Color(0x0F000000),
    glassNoiseAlpha    = 0.03f,

    onImage            = ColorWhite,
    onImageMuted       = Color(0xD9FFFFFF),
    onImageInverse     = ColorInk,
    imageScrimStart    = Color(0x00000000),
    imageScrimEnd      = Color(0xCC000000),
    scrimStrong        = Color(0xCC000000),
    scrimSoft          = Color(0xA6000000),
    imageTopScrim      = Color(0xA6000000),
    imageVignette      = Color(0x73000000),
    progressDotInactive = Color(0x4DFFFFFF),

    // Monochrome: stars and saved are white over imagery; only the heart is red
    ratingStar         = ColorWhite,
    favouriteActive    = ColorHeart,
    savedActive        = ColorWhite,

    imageChipSurface   = Color(0x8C000000),
    onImageChipSurface = Color(0x1FFFFFFF),
    onImageChipBorder  = Color(0x40FFFFFF),
    pillBorder         = Color(0x40FFFFFF),
    ratingChipFillAlpha  = 0.35f,
    ratingChipBorderAlpha = 0.50f,
    chipUnselectedAlpha  = 0.5f,
    chipSelectedAlpha    = 0.25f,
    posterFadeAlpha      = 0.95f,
    heroTopAlpha       = 0.85f,
    heroMidAlpha       = 0.60f,
    heroMutedAlpha     = 0.75f,
    glassNoiseDensity  = 400,
    sphereShade        = Color(0x40000000),
    pulseRingAlpha1    = 0.8f,
    pulseRingAlpha2    = 0.6f,
    playerBackground   = ColorBlack,

    navSelectedIcon    = ColorInk,
    navUnselectedIcon  = ColorGray400,

    skeletonBase       = ColorGray200,
    skeletonHighlight  = ColorGray100,
)

internal val DarkExtendedColors = AppExtendedColors(
    success            = ColorSuccessDark,
    onSuccess          = Color(0xFF0A2E15),
    successContainer   = Color(0xFF12351F),
    onSuccessContainer = Color(0xFFD6F5DF),
    warning            = ColorWarningDark,
    onWarning          = Color(0xFF2B1700),
    warningContainer   = Color(0xFF4A2C00),
    onWarningContainer = Color(0xFFFFE2B8),
    navbar             = ColorD100,
    cover              = ColorBlack,
    player             = ColorBlack,
    subText            = ColorD700,
    stroke             = ColorD600,

    glassSurface       = Color(0x1FFFFFFF),
    glassBorder        = Color(0x33FFFFFF),
    glassGloss         = Color(0x26FFFFFF),
    glassNoise         = Color(0x14FFFFFF),
    glassNoiseAlpha    = 0.03f,

    onImage            = ColorWhite,
    onImageMuted       = Color(0xD9FFFFFF),
    onImageInverse     = ColorInk,
    imageScrimStart    = Color(0x00000000),
    imageScrimEnd      = Color(0xCC000000),
    scrimStrong        = Color(0xCC000000),
    scrimSoft          = Color(0x66000000),
    imageTopScrim      = Color(0xA6000000),
    imageVignette      = Color(0x73000000),
    progressDotInactive = Color(0x4DFFFFFF),

    ratingStar         = ColorWhite,
    favouriteActive    = ColorHeart,
    savedActive        = ColorWhite,

    imageChipSurface   = Color(0x8C000000),
    onImageChipSurface = Color(0x1FFFFFFF),
    onImageChipBorder  = Color(0x40FFFFFF),
    pillBorder         = Color(0x26FFFFFF),
    ratingChipFillAlpha  = 0.35f,
    ratingChipBorderAlpha = 0.50f,
    chipUnselectedAlpha  = 0.5f,
    chipSelectedAlpha    = 0.25f,
    posterFadeAlpha      = 0.95f,
    heroTopAlpha       = 0.85f,
    heroMidAlpha       = 0.60f,
    heroMutedAlpha     = 0.75f,
    glassNoiseDensity  = 400,
    sphereShade        = Color(0x40000000),
    pulseRingAlpha1    = 0.8f,
    pulseRingAlpha2    = 0.6f,
    playerBackground   = ColorBlack,

    navSelectedIcon    = ColorWhite,
    navUnselectedIcon  = ColorGray400,

    skeletonBase       = ColorD200,
    skeletonHighlight  = ColorD300,
)

val LocalAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }
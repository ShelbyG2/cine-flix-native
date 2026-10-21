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
//   body text  #0B0B0C on #FFFFFF   19.0:1  AA
//   secondary  #6C6C70 on #FFFFFF    5.0:1  AA
//   outline    #8E8E93 on #FFFFFF    3.3:1  AA (icons / large text)
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
//   body text  #F2F2F7 on #000000  18.9:1  AA
//   secondary  #98989D on #000000   7.0:1  AA
//   outline    #7C7C80 on #000000   4.4:1  AA (icons / large text)
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
// EXTENDED COLORS — grouped by purpose instead of one flat 60-field bag.
// ═══════════════════════════════════════════════════════════════════════════

/** Status colors: the only chromatic colors besides the favourite heart. */
data class StatusColors(
    val success:            Color,
    val onSuccess:          Color,
    val successContainer:   Color,
    val onSuccessContainer: Color,
    val warning:            Color,
    val onWarning:          Color,
    val warningContainer:   Color,
    val onWarningContainer: Color,
)

/** The single translucent material used by the top bar and the nav bar. */
data class GlassColors(
    /** Translucent tint drawn over the blurred backdrop. */
    val surface: Color,
    /** Hairline on the bar's own edge. */
    val border:  Color,
    /** Solid, slightly more opaque material for API < 31 (no blur). */
    val fallback: Color,
)

/** Scrims, text and chip fills that sit over poster/backdrop imagery. */
data class ImageOverlayColors(
    val onImage:        Color,
    val onImageMuted:   Color,
    val onImageInverse: Color,
    val scrimStart:     Color,
    val scrimStrong:    Color,
    val scrimSoft:      Color,
    val topScrim:       Color,
    val vignette:       Color,
    val chipSurface:    Color,
    val onChipSurface:  Color,
    val chipBorder:     Color,
    val pillBorder:     Color,
)

/** Rating / favourite / saved marks. Stars and saved are white; the heart is red. */
data class AccentColors(
    val rating:    Color,
    val favourite: Color,
    val saved:     Color,
)

/** Bottom navigation icon roles. */
data class NavColors(
    val selectedIcon:   Color,
    val unselectedIcon: Color,
)

/** Skeleton shimmer base + highlight. */
data class SkeletonColors(
    val base:      Color,
    val highlight: Color,
)

/**
 * Non-color numeric specs that used to live inside the colour bag:
 * the alphas of image overlays and the details hero gradient.
 */
data class EffectSpec(
    val ratingChipFillAlpha:   Float = 0.35f,
    val ratingChipBorderAlpha: Float = 0.50f,
    val chipUnselectedAlpha:   Float = 0.5f,
    val chipSelectedAlpha:     Float = 0.25f,
    val posterFadeAlpha:       Float = 0.95f,
    val heroMidAlpha:          Float = 0.60f,
    val heroMutedAlpha:        Float = 0.75f,
)

data class AppExtendedColors(
    val status:    StatusColors,
    val glass:     GlassColors,
    val image:     ImageOverlayColors,
    val accent:    AccentColors,
    val nav:       NavColors,
    val skeleton:  SkeletonColors,
    /** True-black playback background in both modes. */
    val playerBackground: Color,
)

internal val LightExtendedColors = AppExtendedColors(
    status = StatusColors(
        success            = ColorSuccessLight,
        onSuccess          = ColorWhite,
        successContainer   = Color(0xFFE3F5E8),
        onSuccessContainer = Color(0xFF0B3D1C),
        warning            = ColorWarningLight,
        onWarning          = ColorWhite,
        warningContainer   = Color(0xFFFFF1E0),
        onWarningContainer = Color(0xFF4A2200),
    ),
    // Apple-style thin material: white, hairline rim
    glass = GlassColors(
        surface  = Color(0xB3FFFFFF),   // white at ~70%
        border   = Color(0x1F000000),   // 12% ink hairline
        fallback = Color(0xF0F2F2F7),   // surfaceContainer at ~94%
    ),
    image = ImageOverlayColors(
        onImage        = ColorWhite,
        onImageMuted   = Color(0xD9FFFFFF),
        onImageInverse = ColorInk,
        scrimStart     = Color(0x00000000),
        scrimStrong    = Color(0xCC000000),
        scrimSoft      = Color(0xA6000000),
        topScrim       = Color(0xA6000000),
        vignette       = Color(0x73000000),
        chipSurface    = Color(0x8C000000),
        onChipSurface  = Color(0x1FFFFFFF),
        chipBorder     = Color(0x40FFFFFF),
        pillBorder     = Color(0x40FFFFFF),
    ),
    // Monochrome: stars and saved are white over imagery; only the heart is red
    accent = AccentColors(
        rating    = ColorWhite,
        favourite = ColorHeart,
        saved     = ColorWhite,
    ),
    nav = NavColors(
        selectedIcon   = ColorInk,
        unselectedIcon = ColorGray400,
    ),
    skeleton = SkeletonColors(
        base      = ColorGray200,
        highlight = ColorGray100,
    ),
    playerBackground = ColorBlack,
)

internal val DarkExtendedColors = AppExtendedColors(
    status = StatusColors(
        success            = ColorSuccessDark,
        onSuccess          = Color(0xFF0A2E15),
        successContainer   = Color(0xFF12351F),
        onSuccessContainer = Color(0xFFD6F5DF),
        warning            = ColorWarningDark,
        onWarning          = Color(0xFF2B1700),
        warningContainer   = Color(0xFF4A2C00),
        onWarningContainer = Color(0xFFFFE2B8),
    ),
    glass = GlassColors(
        surface  = Color(0xB31C1C1E),   // #1C1C1E at ~70%
        border   = Color(0x33FFFFFF),   // 20% white hairline
        fallback = Color(0xF0111113),   // #111113 at ~94%
    ),
    image = ImageOverlayColors(
        onImage        = ColorWhite,
        onImageMuted   = Color(0xD9FFFFFF),
        onImageInverse = ColorInk,
        scrimStart     = Color(0x00000000),
        scrimStrong    = Color(0xCC000000),
        scrimSoft      = Color(0x66000000),
        topScrim       = Color(0xA6000000),
        vignette       = Color(0x73000000),
        chipSurface    = Color(0x8C000000),
        onChipSurface  = Color(0x1FFFFFFF),
        chipBorder     = Color(0x40FFFFFF),
        pillBorder     = Color(0x26FFFFFF),
    ),
    accent = AccentColors(
        rating    = ColorWhite,
        favourite = ColorHeart,
        saved     = ColorWhite,
    ),
    nav = NavColors(
        selectedIcon   = ColorWhite,
        unselectedIcon = ColorGray400,
    ),
    skeleton = SkeletonColors(
        base      = ColorD200,
        highlight = ColorD300,
    ),
    playerBackground = ColorBlack,
)

val LocalAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/** Effect alphas / densities are mode-independent. */
val LocalAppEffects = staticCompositionLocalOf { EffectSpec() }

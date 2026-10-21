package tech.unrealistic.cineflix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary              = md_dark_primary,
    onPrimary            = md_dark_onPrimary,
    primaryContainer     = md_dark_primaryContainer,
    onPrimaryContainer   = md_dark_onPrimaryContainer,
    secondary            = md_dark_secondary,
    onSecondary          = md_dark_onSecondary,
    secondaryContainer   = md_dark_secondaryContainer,
    onSecondaryContainer = md_dark_onSecondaryContainer,
    tertiary             = md_dark_tertiary,
    onTertiary           = md_dark_onTertiary,
    tertiaryContainer    = md_dark_tertiaryContainer,
    onTertiaryContainer  = md_dark_onTertiaryContainer,
    error                = md_dark_error,
    onError              = md_dark_onError,
    errorContainer       = md_dark_errorContainer,
    onErrorContainer     = md_dark_onErrorContainer,
    background           = md_dark_background,
    onBackground         = md_dark_onBackground,
    surface              = md_dark_surface,
    onSurface            = md_dark_onSurface,
    surfaceVariant       = md_dark_surfaceVariant,
    onSurfaceVariant     = md_dark_onSurfaceVariant,
    outline              = md_dark_outline,
    outlineVariant       = md_dark_outlineVariant,
    inverseSurface       = md_dark_inverseSurface,
    inverseOnSurface     = md_dark_inverseOnSurface,
    inversePrimary       = md_dark_inversePrimary,
    surfaceTint          = md_dark_surfaceTint,
    scrim                = md_dark_scrim,
    surfaceBright           = md_dark_surfaceBright,
    surfaceDim              = md_dark_surfaceDim,
    surfaceContainerLowest  = md_dark_surfaceContainerLowest,
    surfaceContainerLow     = md_dark_surfaceContainerLow,
    surfaceContainer        = md_dark_surfaceContainer,
    surfaceContainerHigh    = md_dark_surfaceContainerHigh,
    surfaceContainerHighest = md_dark_surfaceContainerHighest,
)

private val LightColorScheme = lightColorScheme(
    primary              = md_light_primary,
    onPrimary            = md_light_onPrimary,
    primaryContainer     = md_light_primaryContainer,
    onPrimaryContainer   = md_light_onPrimaryContainer,
    secondary            = md_light_secondary,
    onSecondary          = md_light_onSecondary,
    secondaryContainer   = md_light_secondaryContainer,
    onSecondaryContainer = md_light_onSecondaryContainer,
    tertiary             = md_light_tertiary,
    onTertiary           = md_light_onTertiary,
    tertiaryContainer    = md_light_tertiaryContainer,
    onTertiaryContainer  = md_light_onTertiaryContainer,
    error                = md_light_error,
    onError              = md_light_onError,
    errorContainer       = md_light_errorContainer,
    onErrorContainer     = md_light_onErrorContainer,
    background           = md_light_background,
    onBackground         = md_light_onBackground,
    surface              = md_light_surface,
    onSurface            = md_light_onSurface,
    surfaceVariant       = md_light_surfaceVariant,
    onSurfaceVariant     = md_light_onSurfaceVariant,
    outline              = md_light_outline,
    outlineVariant       = md_light_outlineVariant,
    inverseSurface       = md_light_inverseSurface,
    inverseOnSurface     = md_light_inverseOnSurface,
    inversePrimary       = md_light_inversePrimary,
    surfaceTint          = md_light_surfaceTint,
    scrim                = md_light_scrim,
    surfaceBright           = md_light_surfaceBright,
    surfaceDim              = md_light_surfaceDim,
    surfaceContainerLowest  = md_light_surfaceContainerLowest,
    surfaceContainerLow     = md_light_surfaceContainerLow,
    surfaceContainer        = md_light_surfaceContainer,
    surfaceContainerHigh    = md_light_surfaceContainerHigh,
    surfaceContainerHighest = md_light_surfaceContainerHighest,
)

/**
 * Forces light system-bar icons for as long as the composable is on screen.
 * Full-bleed dark imagery (hero, details backdrop, the player) needs pale icons
 * even in light theme, where the theme default is dark icons.
 * Restores the previous values on dispose so the next screen is unaffected.
 */
@Composable
fun ForceLightSystemBarIcons(enabled: Boolean = true) {
    val view = LocalView.current
    if (view.isInEditMode) return

    DisposableEffect(view, enabled) {
        val controller = (view.context as? Activity)?.window
            ?.let { window -> WindowCompat.getInsetsController(window, view) }
        val previousStatus = controller?.isAppearanceLightStatusBars
        val previousNav = controller?.isAppearanceLightNavigationBars
        if (enabled) {
            controller?.isAppearanceLightStatusBars = false
            controller?.isAppearanceLightNavigationBars = false
        }
        onDispose {
            if (enabled && controller != null) {
                previousStatus?.let { controller.isAppearanceLightStatusBars = it }
                previousNav?.let { controller.isAppearanceLightNavigationBars = it }
            }
        }
    }
}

@Composable
fun CineflixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Status bar / nav bar icon colour follows the theme (light mode → dark icons)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAppExtendedColors provides if (darkTheme) DarkExtendedColors else LightExtendedColors,
        LocalAppEffects provides EffectSpec(),
        LocalAppSpacing provides AppSpacing(),
        LocalAppSizes provides AppSizes(),
        LocalAppMotion provides AppMotion(reduceMotion = rememberReduceMotion()),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// AppTheme — single access point for every design token.
// Composables use MaterialTheme.* for M3 roles and AppTheme.* for the rest.
// ═══════════════════════════════════════════════════════════════════════════
object AppTheme {
    val colors:  AppExtendedColors @Composable get() = LocalAppExtendedColors.current
    val effects: EffectSpec        @Composable get() = LocalAppEffects.current
    val spacing: AppSpacing        @Composable get() = LocalAppSpacing.current
    val sizes:   AppSizes          @Composable get() = LocalAppSizes.current
    val motion:  AppMotion         @Composable get() = LocalAppMotion.current
}
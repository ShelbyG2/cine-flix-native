package tech.unrealistic.cineflix.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// ═══════════════════════════════════════════════════════════════════════════
// MOTION TOKENS
// Fast, springy, physical. Every duration, easing and spring used by a
// composable comes from here — no raw tween/spring specs in UI code.
//
// Reduce motion: every [spec] collapses to `snap()` and decorative infinite
// animations should be skipped (callers already gate on `reduceMotion`).
// ═══════════════════════════════════════════════════════════════════════════

data class AppMotion(
    /** True when the user disabled animations system-wide (reduce motion). */
    val reduceMotion: Boolean = false,

    // ── Durations (ms) ──────────────────────────────────────────────────────
    val shortMs:     Int = 150,   // taps, icon swaps
    val mediumMs:    Int = 300,   // sheets, page transitions
    val longMs:      Int = 500,   // hero crossfades
    val longFadeMs:  Int = 800,   // slow image / colour crossfades
    val shimmerMs:   Int = 1200,  // skeleton sweep
    val crossfadeMs: Int = 180,   // tab switches
    val staggerMs:   Int = 30,    // list fade-up stagger (max 8 items)

    // ── Press feedback ──────────────────────────────────────────────────────
    val pressScale:    Float = 0.97f,  // cards and buttons
    val navPressScale: Float = 0.92f,  // bottom navigation tabs

    // ── Curves ──────────────────────────────────────────────────────────────
    val emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
    val standard:   Easing = FastOutSlowInEasing,
    val decelerate: Easing = LinearOutSlowInEasing,
    val accelerate: Easing = FastOutLinearInEasing,
) {
    /** Spring used for press feedback. */
    val pressSpring: SpringSpec<Float> = spring(dampingRatio = 0.7f, stiffness = 400f)

    /**
     * The one animation spec factory. Returns `snap()` under reduce motion so
     * nothing moves, and a token-driven tween otherwise.
     */
    fun <T> spec(
        durationMs: Int = mediumMs,
        easing: Easing = standard,
    ): FiniteAnimationSpec<T> =
        if (reduceMotion) snap<T>() else tween(durationMillis = durationMs, easing = easing)

    /** Press spring, or `snap()` when motion is reduced. */
    val pressSpec: FiniteAnimationSpec<Float>
        get() = if (reduceMotion) snap<Float>() else pressSpring

    /** Decorative loop (Ken Burns, pulses, shimmer). Callers gate on reduceMotion. */
    fun infinite(
        durationMs: Int,
        easing: Easing = standard,
        repeatMode: RepeatMode = RepeatMode.Restart,
        delayMs: Int = 0,
    ): InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis = durationMs, easing = easing, delayMillis = delayMs),
        repeatMode = repeatMode,
    )
}

val LocalAppMotion = staticCompositionLocalOf { AppMotion() }

/**
 * Reads the system animator duration scale once per composition entry.
 * 0 means animations are off (reduce motion). Keyed on the context so a
 * configuration change re-reads the setting, but recomposition does not.
 */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }
}

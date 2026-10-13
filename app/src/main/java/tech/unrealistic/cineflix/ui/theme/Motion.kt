package tech.unrealistic.cineflix.ui.theme


import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// ═══════════════════════════════════════════════════════════════════════════
// MOTION TOKENS
// Decorative motion (Ken Burns, pulse rings, carousel scaling, shimmer travel)
// must respect the system animator duration scale (reduce motion).
// ═══════════════════════════════════════════════════════════════════════════

data class AppMotion(
    /** True when the user has disabled animations system-wide (reduce motion). */
    val reduceMotion: Boolean = false,
    /** Duration tokens, ms — replaces magic tween(...) values in composables. */
    val shortMs:  Int = 150,
    val mediumMs: Int = 300,
    val longMs:   Int = 500,
    val shimmerMs: Int = 1200,  // skeleton sweep — static when reduceMotion
)

val LocalAppMotion = staticCompositionLocalOf { AppMotion() }

/** Reads the system animator duration scale; 0 means animations are off. */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return try {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    } catch (e: Exception) {
        false
    }
}

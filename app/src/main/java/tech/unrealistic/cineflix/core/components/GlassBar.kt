package tech.unrealistic.cineflix.core.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * The app's one glass material, shared by the floating top bar and the bottom
 * navigation bar.
 *
 * - API 31+: real backdrop blur through Haze, tinted with [glass.surface].
 * - Below API 31: a more opaque solid surface with the same hairline, so the
 *   bar reads as intentional rather than half-broken.
 *
 * Only the top edge gets a hairline; the sides and bottom stay unframed.
 */
/** Which edge carries the 0.5dp hairline. */
enum class HairlineEdge { Top, Bottom, None }

@Composable
fun Modifier.glassBar(
    hazeState: HazeState,
    shape: Shape = RectangleShape,
    hairlineEdge: HairlineEdge = HairlineEdge.Top,
): Modifier {
    val glass = AppTheme.colors.glass
    val sizes = AppTheme.sizes
    val blurSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val style = HazeStyle(
        backgroundColor = Color.Transparent,
        tints = listOf(HazeTint(glass.surface)),
        blurRadius = sizes.glassBlurRadius,
        noiseFactor = 0f,
        fallbackTint = HazeTint(glass.fallback),
    )

    return this
        .clip(shape)
        .then(
            if (blurSupported) {
                Modifier.hazeEffect(state = hazeState, style = style)
            } else {
                // Intentional solid fallback — same footprint, same hairline.
                Modifier.background(glass.fallback)
            }
        )
        .then(
            if (hairlineEdge == HairlineEdge.None) {
                Modifier
            } else {
                Modifier.drawWithContent {
                    drawContent()
                    val stroke = sizes.hairline.toPx()
                    val y = if (hairlineEdge == HairlineEdge.Top) {
                        stroke / 2f
                    } else {
                        size.height - stroke / 2f
                    }
                    drawLine(
                        color = glass.border,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = stroke,
                    )
                }
            }
        )
}

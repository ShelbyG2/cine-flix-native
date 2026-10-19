package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeMedium

/**
 * Skeleton placeholder used by every loading state.
 * The sweep is drawn with theme skeleton tokens and stays static when the
 * system reduce-motion setting is on.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeMedium,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerEffect()
    )
}


fun Modifier.shimmerEffect(): Modifier = composed {

    val colors = AppTheme.colors
    val motion = AppTheme.motion

    //Reduce motion → static skeleton block, no travelling gradient
    if (motion.reduceMotion) {
        return@composed this.background(colors.skeletonBase)
    }

    //Keep track of the pixel bounds of the target composable
    var size by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
//endless loop translation
    val translateAnimation by transition.animateFloat(
        initialValue = -2f * size.width.toFloat(),
        targetValue = 2f * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = motion.shimmerMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_translation"
    )

    //construct linear gradient  brush followin the translated animation values
    val shimmerBrush = if (size.width > 0) {
        Brush.linearGradient(
            colors = listOf(colors.skeletonBase, colors.skeletonHighlight, colors.skeletonBase),
            start = Offset(translateAnimation, 0f),
            end = Offset(translateAnimation + size.width.toFloat(), size.height.toFloat())
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }
    //paint background surface and capture layout sizing hooks dynamically
    this
        .onGloballyPositioned { coordinates ->
            size = coordinates.size
        }
        .background(shimmerBrush)
}

package tech.unrealistic.cineflix.feature.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// ── Glassmorphism layer ───────────────────────────────────────────────────────
// Achieves glass with four stacked layers:
//   1. Frosted base     — semi-transparent white fill
//   2. Specular gloss   — vertical gradient from bright top to transparent bottom
//   3. Edge light       — thin border simulating light hitting the glass rim
//   4. Noise texture    — subtle grain drawn on canvas to break the digital flatness

@Composable
fun GlassMorphOverlay(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    frostedAlpha: Float = 0.08f,    // 0.05–0.15 sweet spot for dark backgrounds
    glossAlpha: Float = 0.20f,    // top-edge specular brightness
    borderAlpha: Float = 0.30f,    // rim light intensity
    tintColor: Color = Color.White,
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)

            // ── Layer 1: Frosted base ─────────────────────────────────
        .background(tintColor.copy(alpha = frostedAlpha))

            // ── Layer 2 + 4: Specular gloss + noise via single draw pass
        .drawWithCache {
            // Pre-build the gloss brush once — reused across frames
            val glossBrush = Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = glossAlpha),
                    tintColor.copy(alpha = glossAlpha * 0.5f),
                    Color.Transparent,
                ), startY = 0f, endY = size.height * 0.55f   // gloss covers top 55%
            )

            // Diagonal specular streak — like light catching a glass edge
            val streakBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    tintColor.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width * 0.6f, size.height * 0.4f),
                tileMode = TileMode.Clamp
            )

            onDrawWithContent {
                // Draw the actual composable content first (card image etc.)
                drawContent()
                // Gloss on top
                drawRect(glossBrush)
                // Diagonal streak
                drawRect(streakBrush)
                // Noise grain — draws 400 tiny random dots for texture
                drawNoise(tintColor, alpha = 0.025f, density = 400)
            }
        }

            // ── Layer 3: Edge / rim light ─────────────────────────────
        .border(
            width = 0.8.dp, brush = Brush.linearGradient(
                colors = listOf(
                    tintColor.copy(alpha = borderAlpha),
                    tintColor.copy(alpha = borderAlpha * 0.3f),
                    Color.Transparent,
                    tintColor.copy(alpha = borderAlpha * 0.1f),
                )
            ), shape = shape
        ))
}

// Canvas extension — draws random noise dots for grain texture
private fun DrawScope.drawNoise(color: Color, alpha: Float, density: Int) {
    val random = Random(42)   // fixed seed = stable across recompositions
    repeat(density) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        drawCircle(
            color = color.copy(alpha = alpha * random.nextFloat()),
            radius = random.nextFloat() * 1.2f,
            center = Offset(x, y)
        )
    }
}
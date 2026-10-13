package tech.unrealistic.cineflix.feature.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.material3.MaterialTheme
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.withAlpha
import kotlin.random.Random

// ── Glassmorphism layer ───────────────────────────────────────────────────────
// Achieves glass with four stacked layers:
//   1. Frosted base     — semi-transparent fill
//   2. Specular gloss   — vertical gradient from bright top to transparent bottom
//   3. Edge light       — thin border simulating light hitting the glass rim
//   4. Noise texture    — subtle grain drawn on canvas to break the digital flatness
// All colours/alphas come from AppTheme.colors — tuned per mode (light glass
// needs a stronger tint/border to read on light surfaces).

@Composable
fun GlassMorphOverlay(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .clip(shape)

            // ── Layer 1: Frosted base ─────────────────────────────────
            .background(colors.glassSurface)

            // ── Layer 2 + 4: Specular gloss + noise via single draw pass
            .drawWithCache {
                // Pre-build the gloss brush once — reused across frames
                val glossBrush = Brush.verticalGradient(
                    colors = listOf(
                        colors.glassGloss,
                        colors.glassGloss.withAlpha(colors.glassGloss.alpha * 0.5f),
                        colors.glassSurface.withAlpha(0f),
                    ), startY = 0f, endY = size.height * 0.55f   // gloss covers top 55%
                )

                // Diagonal specular streak — like light catching a glass edge
                val streakBrush = Brush.linearGradient(
                    colors = listOf(
                        colors.glassGloss.withAlpha(0f),
                        colors.glassGloss.withAlpha(colors.glassGloss.alpha * 0.6f),
                        colors.glassGloss.withAlpha(0f),
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
                    // Noise grain — seeded random dots for texture
                    drawNoise(
                        colors.glassNoise,
                        alpha = colors.glassNoiseAlpha,
                        density = colors.glassNoiseDensity
                    )
                }
            }

            // ── Layer 3: Edge / rim light ─────────────────────────────────
            .border(
                width = AppTheme.sizes.glassBorderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        colors.glassBorder,
                        colors.glassBorder.withAlpha(colors.glassBorder.alpha * 0.3f),
                        colors.glassBorder.withAlpha(0f),
                        colors.glassBorder.withAlpha(colors.glassBorder.alpha * 0.1f),
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
            color = color.withAlpha(alpha * random.nextFloat()),
            radius = random.nextFloat() * 1.2f,
            center = Offset(x, y)
        )
    }
}

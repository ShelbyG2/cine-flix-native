package tech.unrealistic.cineflix.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tech.unrealistic.cineflix.ui.theme.LexendFamily
import kotlin.math.sin

/**
 * Splash shown on every cold start.
 * Centered play button with a glowing pulse ring + animated loading dots,
 * "CINEFLIX" wordmark in Lexend with staggered letter fade-in.
 * Auto-dismisses after 2.8s.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after 2.8 seconds
    LaunchedEffect(Unit) {
        delay(2_800)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // ── Play button with glowing pulse ring ──────────────────────────
            Box(contentAlignment = Alignment.Center) {

                // Glow pulse ring — expands + fades out repeatedly
                val ringTransition = rememberInfiniteTransition(label = "splash_ring")
                val ringProgress by ringTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1_800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "splash_ring_progress"
                )
                // Second ring offset in phase so one is always visible
                val ring2Progress by ringTransition.animateFloat(
                    initialValue = 0.35f,
                    targetValue = 1.35f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1_800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "splash_ring_progress_2"
                )

                Canvas(modifier = Modifier.size(120.dp)) {
                    val maxSize = size.minDimension / 2f
                    listOf(ringProgress, ring2Progress).forEach { progress ->
                        val clamped = progress.coerceIn(0f, 1f)
                        val radius = maxSize * (0.55f + 0.45f * clamped)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.28f * (1f - clamped)),
                            radius = radius,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // The play button itself — rounded square, glass edge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color.White.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(22.dp)
                        )
                ) {
                    Canvas(modifier = Modifier.size(30.dp)) {
                        val triangleWidth = size.width * 0.62f
                        val triangleHeight = size.height * 0.72f
                        val startX = (size.width - triangleWidth) / 2f + size.width * 0.06f
                        val centerY = size.height / 2f

                        drawPath(
                            path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(startX, centerY - triangleHeight / 2f)
                                lineTo(startX + triangleWidth, centerY)
                                lineTo(startX, centerY + triangleHeight / 2f)
                                close()
                            },
                            color = Color(0xFF131313)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── CINEFLIX wordmark — staggered fade-in per letter ────────────
            Row(horizontalArrangement = Arrangement.Center) {
                "CINEFLIX".forEachIndexed { index, letter ->
                    LetterFadeIn(
                        letter = letter.toString(),
                        startDelayMillis = index * 90L
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Animated loading dots ────────────────────────────────────────
            val dotsTransition = rememberInfiniteTransition(label = "splash_dots")
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                repeat(3) { index ->
                    val phase by dotsTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = (2f * Math.PI).toFloat(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "splash_dot_$index"
                    )
                    // Sine wave gives each dot a bobbing fade, offset per index
                    val offset = sin(phase - index * 0.7f)
                    val dotAlpha = (0.25f + 0.75f * ((offset + 1f) / 2f)).coerceIn(0.15f, 1f)

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer {
                                alpha = dotAlpha
                                scaleX = 0.7f + 0.3f * dotAlpha
                                scaleY = 0.7f + 0.3f * dotAlpha
                            }
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

/** Single letter of the wordmark that fades + slides in after [startDelayMillis]. */
@Composable
private fun LetterFadeIn(letter: String, startDelayMillis: Long) {
    val animated = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(startDelayMillis)
        animated.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Text(
        text = letter.toString(),
        style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = LexendFamily,
            fontWeight = FontWeight.Black,
            letterSpacing = 6.sp
        ),
        color = Color.White,
        modifier = Modifier.graphicsLayer {
            alpha = animated.value
            translationY = (1f - animated.value) * 24f
        }
    )
}

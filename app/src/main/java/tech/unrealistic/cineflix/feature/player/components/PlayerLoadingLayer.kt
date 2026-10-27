package tech.unrealistic.cineflix.feature.player.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.ui.theme.AppTheme

/** Scrim end stop: themed background at 85% over the backdrop (§2.1). */
private const val SCRIM_BOTTOM_ALPHA = 0.85f

/** Wavy-bar loop timing — decorative, player-local. */
private const val BAR_CYCLE_MS = 600
private const val BAR_STAGGER_MS = 80

/**
 * Full-screen playback loading state: TMDB backdrop, gradient scrim, the title
 * being started, the wavy bars animation and the real buffered percentage.
 *
 * Deliberately no circular spinner — buffering is communicated by the wavy bars
 * plus the linear buffered indicator (§2 of the customisation instructions).
 */
@Composable
fun PlayerLoadingLayer(
    displayTitle: String,
    bufferedPercent: Int,
    backdropPath: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading playback" }
    ) {
        if (!backdropPath.isNullOrBlank()) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w1280$backdropPath",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Vertical scrim: transparent at the top, themed background at the bottom.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppTheme.colors.image.scrimStart,
                            MaterialTheme.colorScheme.background.copy(alpha = SCRIM_BOTTOM_ALPHA),
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.xxl)
                .padding(bottom = AppTheme.spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
        ) {
            Text(
                text = "Playing",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            WavyBarsAnimation()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { bufferedPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(AppTheme.spacing.xs),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    drawStopIndicator = {},
                )
                Text(
                    text = "$bufferedPercent%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Five vertical bars whose heights animate independently with staggered phases
 * so they never all peak at the same moment (§2.3).
 */
@Composable
fun WavyBarsAnimation(modifier: Modifier = Modifier) {
    val motion = AppTheme.motion
    val transition = rememberInfiniteTransition(label = "wavy")
    val barCount = 5
    val phases = remember { List(barCount) { it * 72f } }   // 72° apart

    val animatedFractions = phases.mapIndexed { i, phase ->
        transition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = motion.infinite(
                durationMs = BAR_CYCLE_MS,
                easing = motion.standard,
                repeatMode = RepeatMode.Reverse,
                delayMs = i * BAR_STAGGER_MS
            ),
            label = "bar_$i"
        )
    }

    val barColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .width(AppTheme.sizes.wavyBarsWidth)
            .height(AppTheme.sizes.wavyBarsHeight)
    ) {
        val barWidth = size.width / (barCount * 2f - 1f)
        val gap = barWidth
        animatedFractions.forEachIndexed { i, fraction ->
            // Static mid-height bars under reduce motion — no decorative loop.
            val value = if (motion.reduceMotion) 0.6f else fraction.value
            val barHeight = size.height * value
            val left = i * (barWidth + gap)
            val top = (size.height - barHeight) / 2f
            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f)
            )
        }
    }
}

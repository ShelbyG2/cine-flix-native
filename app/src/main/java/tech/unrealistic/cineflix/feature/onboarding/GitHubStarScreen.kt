package tech.unrealistic.cineflix.feature.onboarding

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.feature.home.components.GlassMorphOverlay
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.LabelStrongStyle
import tech.unrealistic.cineflix.ui.theme.ShapeButton
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.ShapeRound
import tech.unrealistic.cineflix.ui.theme.withAlpha

private const val GITHUB_REPO_URL = "https://github.com/ShelbyG2/cine-flix-native"

/**
 * Screen 3 — GitHub Star.
 * A 3D glass sphere with two-layer specular highlights, dual pulsing glow
 * rings, a ground shadow, and the GitHub mark inside with a drop shadow.
 * A liquid-glass social-proof pill, a "Star on GitHub" CTA (opens the repo)
 * and a "Maybe Later" text link — both complete onboarding.
 */
@Composable
fun GitHubStarScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = spacing.gap24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(spacing.lg))

        // ── Glass sphere with the GitHub mark ──────────────────────────────
        GitHubSphere(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(spacing.sectionPaddingBottom))

        // ── Copy ───────────────────────────────────────────────────────────
        Text(
            text = "Like CINEFLIX?",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.gap10))
        Text(
            text = "A star on GitHub takes two seconds and helps more people discover the app. It means a lot to an indie project!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.md))

        // ── Social proof pill (liquid glass) ───────────────────────────────
        Box {
            GlassMorphOverlay(
                modifier = Modifier.matchParentSize(),
                shape = ShapeRound
            )
            Row(
                modifier = Modifier.padding(
                    horizontal = spacing.md,
                    vertical = spacing.gap10
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = colors.ratingStar,
                    modifier = Modifier.size(sizes.iconSm)
                )
                Spacer(modifier = Modifier.width(spacing.buttonGap))
                Text(
                    text = "Starred by 2k+ developers",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.gap22))

        // ── Star on GitHub CTA ─────────────────────────────────────────────
        Button(
            onClick = {
                // Open the repo, then continue into the app
                runCatching {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPO_URL))
                    )
                }
                onComplete()
            },
            shape = ShapeButton,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(
                horizontal = spacing.buttonPaddingH,
                vertical = spacing.buttonPaddingVTall
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mini GitHub mark on the button
            GitHubMark(
                size = sizes.iconCompact,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(spacing.gap10))
            Text(text = "Star on GitHub", style = LabelStrongStyle)
        }

        Spacer(modifier = Modifier.height(spacing.buttonGap))

        // ── Maybe Later text link — 48dp target ────────────────────────────
        Box(
            modifier = Modifier
                .height(sizes.touchTarget)
                .clip(ShapePill)
                .clickable(onClick = onComplete)
                .padding(horizontal = spacing.gap20),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Maybe Later",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(spacing.sm))
    }
}

/**
 * The 3D glass sphere: dual pulsing glow rings, radial glass body, a primary
 * specular ellipse, a secondary rim highlight and a ground shadow. The GitHub
 * mark sits in the centre with a soft drop shadow.
 * Pulse rings are frozen mid-glow under reduce-motion.
 */
@Composable
private fun GitHubSphere(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val motion = AppTheme.motion

    // Dual pulsing glow rings — scale + opacity keyframes
    val pulse = rememberInfiniteTransition(label = "sphere_pulse")
    val animRing1Scale by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_scale"
    )
    val animRing1Alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_alpha"
    )
    // Second ring runs on an offset phase so one is always glowing
    val animRing2Scale by pulse.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, delayMillis = 1_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_scale"
    )
    val animRing2Alpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, delayMillis = 1_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_alpha"
    )

    // Reduce motion → rings rest at a static mid-glow instead of pulsing
    val ring1Scale = if (motion.reduceMotion) 1.075f else animRing1Scale
    val ring1Alpha = if (motion.reduceMotion) 0.225f else animRing1Alpha
    val ring2Scale = if (motion.reduceMotion) 1.25f else animRing2Scale
    val ring2Alpha = if (motion.reduceMotion) 0.15f else animRing2Alpha

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Sphere + rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(sizes.sphereBox)
            ) {
                val sphereColor = MaterialTheme.colorScheme.primary

                // Glow ring 1
                Box(
                    modifier = Modifier
                        .size(sizes.sphereRing)
                        .graphicsLayer {
                            scaleX = ring1Scale
                            scaleY = ring1Scale
                            alpha = ring1Alpha
                        }
                        .border(
                            width = sizes.ringBorderWide,
                            color = sphereColor.withAlpha(colors.pulseRingAlpha1),
                            shape = ShapePill
                        )
                )
                // Glow ring 2
                Box(
                    modifier = Modifier
                        .size(sizes.sphereRing)
                        .graphicsLayer {
                            scaleX = ring2Scale
                            scaleY = ring2Scale
                            alpha = ring2Alpha
                        }
                        .border(
                            width = sizes.ringBorderThin,
                            color = sphereColor.withAlpha(colors.pulseRingAlpha2),
                            shape = ShapePill
                        )
                )

                // Glass body + highlights + mark
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(sizes.sphereOrb)
                        .clip(ShapePill)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.glassGloss,
                                    colors.glassSurface,
                                    colors.sphereShade
                                ),
                                center = Offset(90f, 60f),
                                radius = 260f
                            )
                        )
                        .border(
                            width = sizes.borderWidth,
                            color = colors.glassBorder,
                            shape = ShapePill
                        )
                ) {
                    // Primary specular highlight (top-left ellipse)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = spacing.gap24, top = spacing.gap18)
                            .width(sizes.specularW)
                            .height(sizes.specularH)
                            .graphicsLayer { rotationZ = -24f }
                            .clip(ShapePill)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        colors.glassGloss,
                                        colors.glassGloss.withAlpha(
                                            colors.glassGloss.alpha * 0.1f
                                        )
                                    )
                                )
                            )
                    )
                    // Secondary rim highlight (bottom-right arc sliver)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = spacing.gap20, bottom = spacing.gap26)
                            .width(sizes.rimSliverW)
                            .height(sizes.rimSliverH)
                            .graphicsLayer { rotationZ = -30f }
                            .clip(ShapePill)
                            .background(colors.glassBorder)
                    )

                    // GitHub mark with drop shadow — themed so it reads on
                    // light glass in light mode too
                    GitHubMark(
                        size = sizes.githubMarkLarge,
                        tint = MaterialTheme.colorScheme.onSurface,
                        drawShadow = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Ground shadow ellipse
            Box(
                modifier = Modifier
                    .width(sizes.sphereShadowW)
                    .height(sizes.sphereShadowH)
                    .clip(ShapePill)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.imageChipSurface,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

/**
 * The GitHub octocat mark (vector drawable). When [drawShadow] is set, a dark
 * offset copy is painted underneath to fake a drop-shadow filter.
 */
@Composable
private fun GitHubMark(
    size: Dp,
    tint: Color,
    drawShadow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        if (drawShadow) {
            // Fake drop shadow — dark offset copy (45% black token)
            Icon(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = null,
                tint = AppTheme.colors.imageVignette,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = 2f
                        translationY = 2f
                    }
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_github),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.matchParentSize()
        )
    }
}

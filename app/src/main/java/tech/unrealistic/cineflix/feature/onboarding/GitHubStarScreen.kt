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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.feature.home.components.GlassMorphOverlay

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Glass sphere with the GitHub mark ──────────────────────────────
        GitHubSphere(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(28.dp))

        // ── Copy ───────────────────────────────────────────────────────────
        Text(
            text = "Like CINEFLIX?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "A star on GitHub takes two seconds and helps more people discover the app. It means a lot to an indie project!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.72f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Social proof pill (liquid glass) ───────────────────────────────
        Box {
            GlassMorphOverlay(
                modifier = Modifier.matchParentSize(),
                shape = tech.unrealistic.cineflix.ui.theme.ShapeRound
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Starred by 2k+ developers",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

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
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF131313)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mini GitHub mark on the button
            GitHubMark(
                size = 18.dp,
                tint = Color(0xFF131313)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Star on GitHub",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Maybe Later text link ──────────────────────────────────────────
        Text(
            text = "Maybe Later",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.55f),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onComplete)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * The 3D glass sphere: dual pulsing glow rings, radial glass body, a primary
 * specular ellipse, a secondary rim highlight and a ground shadow. The GitHub
 * mark sits in the centre with a soft drop shadow.
 */
@Composable
private fun GitHubSphere(modifier: Modifier = Modifier) {
    // Dual pulsing glow rings — scale + opacity keyframes
    val pulse = rememberInfiniteTransition(label = "sphere_pulse")
    val ring1Scale by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_scale"
    )
    val ring1Alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_alpha"
    )
    // Second ring runs on an offset phase so one is always glowing
    val ring2Scale by pulse.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, delayMillis = 1_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_scale"
    )
    val ring2Alpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_200, delayMillis = 1_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_alpha"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Sphere + rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                val sphereColor = MaterialTheme.colorScheme.primary

                // Glow ring 1
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = ring1Scale
                            scaleY = ring1Scale
                            alpha = ring1Alpha
                        }
                        .border(
                            width = 2.dp,
                            color = sphereColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(50)
                        )
                )
                // Glow ring 2
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = ring2Scale
                            scaleY = ring2Scale
                            alpha = ring2Alpha
                        }
                        .border(
                            width = 1.5.dp,
                            color = sphereColor.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(50)
                        )
                )

                // Glass body + highlights + mark
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color.White.copy(alpha = 0.06f),
                                    Color.Black.copy(alpha = 0.25f)
                                ),
                                center = Offset(90f, 60f),
                                radius = 260f
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.30f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    // Primary specular highlight (top-left ellipse)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 24.dp, top = 18.dp)
                            .width(70.dp)
                            .height(40.dp)
                            .graphicsLayer { rotationZ = -24f }
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.55f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    )
                    // Secondary rim highlight (bottom-right arc sliver)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 26.dp)
                            .width(46.dp)
                            .height(16.dp)
                            .graphicsLayer { rotationZ = -30f }
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.20f))
                    )

                    // GitHub mark with drop shadow
                    GitHubMark(
                        size = 74.dp,
                        tint = Color.White,
                        drawShadow = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ground shadow ellipse
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0f)
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
            // Fake drop shadow — dark offset copy
            Icon(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.45f),
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

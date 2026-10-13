package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.feature.home.components.GlassMorphOverlay

// ── Slide content ────────────────────────────────────────────────────────────

private data class IntroSlide(
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val chips: List<String>,
)

private val introSlides = listOf(
    IntroSlide(
        imageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1080&q=80",
        title = "Your Cinema, Everywhere",
        subtitle = "Stream blockbusters, series and hidden gems in stunning quality — anytime, anywhere.",
        chips = listOf("Trending Now", "4K Ultra HD")
    ),
    IntroSlide(
        imageUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=1080&q=80",
        title = "Download & Go",
        subtitle = "Save titles for offline nights, flights and anywhere the signal drops.",
        chips = listOf("Offline Mode", "Smart Downloads")
    ),
    IntroSlide(
        imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=1080&q=80",
        title = "Made For You",
        subtitle = "Personalised picks that learn your taste, so the next great watch is always one tap away.",
        chips = listOf("Personalised", "No Ads")
    ),
)

/**
 * Screen 1 — Cinematic Intro.
 * Full-bleed Unsplash images with a Ken Burns zoom, layered cinematic gradients,
 * a depth-of-field vignette, floating liquid-glass feature card, chip tags,
 * progress dots (active = expanded white pill) and a Next / Get Started button.
 */
@Composable
fun CinematicIntroScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var page by remember { mutableIntStateOf(0) }
    val pageCount = introSlides.size
    val slide = introSlides[page]
    val isLastSlide = page == pageCount - 1

    // Ken Burns — slow continuous zoom on the current image
    val kenBurns = rememberInfiniteTransition(label = "ken_burns")
    val kenBurnsScale by kenBurns.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_scale"
    )

    Box(modifier = modifier.fillMaxSize()) {

        // ── Full-bleed image with Ken Burns + crossfade between slides ──────
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                (fadeIn(tween(700)) + slideInVertically { it / 8 }) togetherWith
                        (fadeOut(tween(700)) + slideOutVertically { -it / 8 })
            },
            label = "intro_slide"
        ) { currentPage ->
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = introSlides[currentPage].imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = kenBurnsScale
                            scaleY = kenBurnsScale
                        }
                )
            }
        }

        // ── Cinematic gradient layer 1 — top scrim ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ── Cinematic gradient layer 2 — bottom scrim for legibility ───────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.35f to Color.Transparent,
                            0.70f to MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                            1.00f to MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        // ── Depth-of-field radial vignette (lens blur simulation) ──────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        ),
                        radius = 1_200f
                    )
                )
        )

        // ── Top bar: wordmark + Skip pill ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CINEFLIX",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = Color.White
            )

            // Skip pill — liquid glass / frosted
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable(onClick = onSkip)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // ── Bottom content: chips + glass feature card + dots + button ─────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {

            // Feature card + chips swap together with an AnimatePresence-style
            // slide-in-from-below + fade
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (fadeIn(tween(450, easing = FastOutSlowInEasing)) +
                            slideInVertically(tween(450, easing = FastOutSlowInEasing)) { it }) togetherWith
                            (fadeOut(tween(250)) +
                                    slideOutVertically(tween(250)) { -it / 2 })
                },
                label = "intro_card"
            ) { currentPage ->
                val currentSlide = introSlides[currentPage]

                Column {
                    // Chip tags above the card
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentSlide.chips.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .border(
                                        width = 0.8.dp,
                                        color = Color.White.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = chip,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Floating liquid glass feature card
                    Box {
                        // Glass layers (frosted base, gloss, rim light, noise)
                        GlassMorphOverlay(
                            modifier = Modifier.matchParentSize(),
                            shape = MaterialTheme.shapes.extraLarge
                        )

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = currentSlide.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentSlide.subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress dots — active expands into a white pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val isActive = index == page
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) 24.dp else 8.dp,
                        animationSpec = tween(350, easing = FastOutSlowInEasing),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isActive) Color.White
                                else Color.White.copy(alpha = 0.30f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Next / Get Started
            Button(
                onClick = {
                    if (isLastSlide) onNext() else page++
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF131313)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 24.dp,
                    vertical = 14.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLastSlide) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

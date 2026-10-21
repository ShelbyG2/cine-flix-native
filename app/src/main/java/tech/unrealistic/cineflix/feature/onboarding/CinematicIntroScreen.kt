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
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.feature.home.components.GlassMorphOverlay
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.LabelStrongStyle
import tech.unrealistic.cineflix.ui.theme.ShapeButton
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.WordmarkStyle
import tech.unrealistic.cineflix.ui.theme.withAlpha

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

    val motion = AppTheme.motion
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    // Ken Burns — slow continuous zoom on the current image
    // Decorative: frozen at 1× under reduce-motion
    val kenBurns = rememberInfiniteTransition(label = "ken_burns")
    val animatedKenBurns by kenBurns.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_scale"
    )
    val kenBurnsScale = if (motion.reduceMotion) 1f else animatedKenBurns

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
                            colors.image.topScrim,
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
                            0.70f to MaterialTheme.colorScheme.background
                                .withAlpha(AppTheme.effects.heroMutedAlpha),
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
                            colors.image.vignette
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
                .padding(horizontal = spacing.gap20, vertical = spacing.gap14),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CINEFLIX",
                style = WordmarkStyle,
                color = colors.image.onImage
            )

            // Skip pill — liquid glass / frosted, 48dp target
            Box(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(colors.image.onChipSurface)
                    .border(
                        width = sizes.hairline,
                        color = colors.image.chipBorder,
                        shape = ShapePill
                    )
                    .clickable(onClick = onSkip)
                    .height(sizes.touchTarget)
                    .padding(horizontal = spacing.gap18),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.image.onImageMuted
                )
            }
        }

        // ── Bottom content: chips + glass feature card + dots + button ─────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = spacing.gap20, vertical = spacing.gap18)
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
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        currentSlide.chips.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(ShapePill)
                                    .background(colors.image.onChipSurface)
                                    .border(
                                        width = sizes.glassBorderWidth,
                                        color = colors.image.chipBorder,
                                        shape = ShapePill
                                    )
                                    .padding(
                                        horizontal = spacing.gap12,
                                        vertical = spacing.gap5
                                    )
                            ) {
                                Text(
                                    text = chip,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.image.onImageMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.gap12))

                    // Floating liquid glass feature card
                    Box {
                        // Glass layers (frosted base, gloss, rim light, noise)
                        GlassMorphOverlay(
                            modifier = Modifier.matchParentSize(),
                            shape = MaterialTheme.shapes.extraLarge
                        )

                        Column(modifier = Modifier.padding(spacing.gap20)) {
                            Text(
                                text = currentSlide.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = colors.image.onImage
                            )
                            Spacer(modifier = Modifier.height(spacing.sm))
                            Text(
                                text = currentSlide.subtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.image.onImageMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.gap20))

            // Progress dots — active expands into a pill (instant under reduce-motion)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val isActive = index == page
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) sizes.dotActiveW else sizes.dotInactiveW,
                        animationSpec = tween(
                            if (motion.reduceMotion) 0 else 350,
                            easing = FastOutSlowInEasing
                        ),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = spacing.xs)
                            .height(sizes.dotH)
                            .width(dotWidth)
                            .clip(ShapePill)
                            .background(
                                if (isActive) colors.image.onImage
                                else OnboardingArt.progressDotInactive
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.gap18))

            // Next / Get Started
            Button(
                onClick = {
                    if (isLastSlide) onNext() else page++
                },
                shape = ShapeButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.image.onImage,
                    contentColor = colors.image.onImageInverse
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = spacing.buttonPaddingH,
                    vertical = spacing.buttonPaddingVTall
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLastSlide) "Get Started" else "Next",
                    style = LabelStrongStyle
                )
                Spacer(modifier = Modifier.width(spacing.sm))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(sizes.iconCompact)
                )
            }
        }
    }
}

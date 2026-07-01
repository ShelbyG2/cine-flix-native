package tech.unrealistic.cineflix.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.core.components.FloatingTopBar
import tech.unrealistic.cineflix.core.components.shimmerEffect
import tech.unrealistic.cineflix.feature.home.components.GlassHeroBackground


@Composable
fun HomeScreenSkeleton(modifier: Modifier = Modifier) {
    // 1. Setup density and top bar tracking state to match production exactly
    var topBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val calculatedHeroHeight = maxHeight * 0.5f

        // 2. ROOT LAYER Stacking Box
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ── 1. Hero Section Skeleton ──
                HeroSectionSkeleton(
                    // Pass the tracked topBarHeight dynamically to avoid gaps
                    topPadding = topBarHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(calculatedHeroHeight)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── 2. Horizontal Rows Skeletons ──
                MediaSectionSkeleton(itemCount = 5)
                MediaSectionSkeleton(itemCount = 5)
            }

            // ── 3. FLOATING APP BAR SKELETON LAYER ──
            // Stays static, matches the transparent layout footprint of production perfectly
            FloatingTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        topBarHeight = with(density) { coordinates.size.height.toDp() }
                    },
                onSearchClick = {},
                isScrolled = false // Keep it false so it stays transparent while loading
            )
        }
    }
}

@Composable
fun HeroSectionSkeleton(
    modifier: Modifier = Modifier,
    topPadding: Dp,
) {
    val skeletonVibrant = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val skeletonMuted = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)

    GlassHeroBackground(
        vibrantColor = skeletonVibrant,
        mutedColor = skeletonMuted,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── 1. Carousel Skeleton Track Block ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // 💡 Uses the exact topPadding dynamic token passed from HomeScreenSkeleton
                    .padding(top = topPadding)
                    .height(340.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
            }

            // ── 2. Info Panel Skeleton ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Mock Chips Row (Rating + Genres)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 45.dp, height = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(width = 75.dp, height = 24.dp)
                                .clip(RoundedCornerShape(50))
                                .shimmerEffect()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mock Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun MediaSectionSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5 // Shows 5 visible shimmering items by default
) {
    Column(modifier = modifier.padding(bottom = 28.dp)) {

        // ── 1. Mock Section Title Header ─────────────────────────────────────
        // Replicates the padding bounds of the text title line
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(160.dp) // Fixed width block representing a text title row
                .height(24.dp) // Matches the typographic spatial height of titleLarge
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect()
        )

        // ── 2. Mock Media Item Row Track ──────────────────────────────────────
        // Keeps the exact contentPadding and arrangement metrics as your production row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false // Prevents the user from flicking incomplete rows
        ) {
            items(itemCount) {
                // Mock Card Item
                // Replace widths and heights here with your MediaCard default design specifications
                // (Assuming standard non-hero poster dimensions are roughly 120.dp width by 180.dp height)
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)) // Mirror the MediaCard custom corner shapes
                        .shimmerEffect()
                )
            }
        }
    }
}
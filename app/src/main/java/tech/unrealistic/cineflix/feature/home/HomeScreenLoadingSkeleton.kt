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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.core.components.shimmerEffect
import tech.unrealistic.cineflix.feature.home.components.GlassHeroBackground


@Composable
fun HomeScreenSkeleton (modifier: Modifier = Modifier) {

// Wrap in BoxWithConstraints to safely access maxHeight without warnings
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val calculatedHeroHeight = maxHeight * 0.5f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── 1. Hero Section Skeleton ──
            HeroSectionSkeleton(
                topPadding = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calculatedHeroHeight)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── 2. Horizontal Rows Skeletons ──
            // Mimics "Trending Movies"
            MediaSectionSkeleton(itemCount = 5)

            // Mimics "Trending TV Shows"
            MediaSectionSkeleton(itemCount = 5)
        }
    }
}

@Composable 
fun HeroSectionSkeleton(modifier: Modifier = Modifier,
                                topPadding: Dp,) {


        // Standard static fallback backdrop colors for the glass system panel while fetching data
        val skeletonVibrant = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        val skeletonMuted = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)

        GlassHeroBackground(
            vibrantColor = skeletonVibrant,
            mutedColor = skeletonMuted,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── 1. Carousel Skeleton Track Block ────────────────────────────
                // Replicates the structural space allocation footprint of your 340dp Carousel layer box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = topPadding)
                        .height(340.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Mimics the exact main prominent featured horizontal item sheet element bounds
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.65f) // Matches HorizontalCenteredHeroCarousel display ratio
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )
                }

                // ── 2. Info Panel Skeleton ──────────────────────────────────────
                // Replicates padding metrics inside HeroInfoPanel explicitly
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Mock Title Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mock Chips Row (Rating + Genres)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Rating block shape
                        Box(
                            modifier = Modifier
                                .size(width = 45.dp, height = 24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        // Genre Pill shapes
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
                        // Play Button placeholder tracking weight(1f) block
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Proportional button baseline spec
                                .clip(RoundedCornerShape(12.dp))
                                .shimmerEffect()
                        )

                        // Favorite IconButton circular placeholder tracking size(52.dp)
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
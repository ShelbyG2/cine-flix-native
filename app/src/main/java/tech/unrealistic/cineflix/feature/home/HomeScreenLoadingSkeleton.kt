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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.core.components.FloatingTopBar
import tech.unrealistic.cineflix.core.components.shimmerEffect
import tech.unrealistic.cineflix.feature.home.components.GlassHeroBackground
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill


@Composable
fun HomeScreenSkeleton(modifier: Modifier = Modifier) {
    // 1. Setup density and top bar tracking state to match production exactly
    var topBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading…" }
    ) {
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

                Spacer(modifier = Modifier.height(AppTheme.spacing.md))

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
    val skeletonVibrant = AppTheme.colors.skeletonBase
    val skeletonMuted = AppTheme.colors.skeletonHighlight

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
                    .height(AppTheme.sizes.skeletonHeroTrackH),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .padding(
                            horizontal = AppTheme.spacing.md,
                            vertical = AppTheme.spacing.gap12
                        )
                        .clip(MaterialTheme.shapes.large)
                        .shimmerEffect()
                )
            }

            // ── 2. Info Panel Skeleton ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.spacing.gap20,
                        vertical = AppTheme.spacing.md
                    )
            ) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.gap12))

                // Mock Chips Row (Rating + Genres)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = AppTheme.sizes.skeletonChipW,
                                height = AppTheme.sizes.skeletonChipH
                            )
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = AppTheme.sizes.skeletonGenreW,
                                    height = AppTheme.sizes.skeletonChipH
                                )
                                .clip(ShapePill)
                                .shimmerEffect()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.gap20))

                // Mock Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppTheme.sizes.touchTarget)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(AppTheme.sizes.skeletonAction)
                            .clip(ShapeCircle)
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
    Column(modifier = modifier.padding(bottom = AppTheme.spacing.sectionPaddingBottom)) {

        // ── 1. Mock Section Title Header ─────────────────────────────────────
        // Replicates the padding bounds of the text title line
        Box(
            modifier = Modifier
                .padding(
                    horizontal = AppTheme.spacing.md,
                    vertical = AppTheme.spacing.sm
                )
                .width(AppTheme.sizes.skeletonTitleW) // Fixed width block representing a text title row
                .height(AppTheme.sizes.skeletonTitleH) // Matches the typographic spatial height of titleLarge
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        // ── 2. Mock Media Item Row Track ──────────────────────────────────────
        // Keeps the exact contentPadding and arrangement metrics as your production row
        LazyRow(
            contentPadding = PaddingValues(horizontal = AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gap12),
            userScrollEnabled = false // Prevents the user from flicking incomplete rows
        ) {
            items(itemCount) {
                // Mock Card Item
                // Mirrors the MediaCard poster dimensions from AppSizes
                Box(
                    modifier = Modifier
                        .width(AppTheme.sizes.skeletonPosterW)
                        .height(AppTheme.sizes.skeletonPosterH)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }
        }
    }
}
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.rememberHazeState
import tech.unrealistic.cineflix.core.components.FloatingTopBar
import tech.unrealistic.cineflix.core.navigation.LocalBottomBarInset
import tech.unrealistic.cineflix.core.components.ShimmerBox
import tech.unrealistic.cineflix.feature.home.components.HeroBackdrop
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill


@Composable
fun HomeScreenSkeleton(modifier: Modifier = Modifier) {
    // 1. Setup density and top bar tracking state to match production exactly
    var topBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val topBarHaze = rememberHazeState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading…" }
    ) {
        // Same fraction the loaded HomeScreen uses — skeletons match content.
        val calculatedHeroHeight = maxHeight * AppTheme.sizes.heroHeightFraction

        // 2. ROOT LAYER Stacking Box
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = LocalBottomBarInset.current)
                    .haze(topBarHaze)
            ) {
                // ── 1. Hero Section Skeleton ──
                HeroSectionSkeleton(
                    // Pass the tracked topBarHeight dynamically to avoid gaps
                    topPadding = topBarHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(calculatedHeroHeight)
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.lg))

                // ── 2. Horizontal Rows Skeletons ──
                MediaSectionSkeleton(itemCount = 5)
                MediaSectionSkeleton(itemCount = 5)
            }

            // ── 3. FLOATING APP BAR SKELETON LAYER ──
            // Stays static, matches the transparent layout footprint of production perfectly
            FloatingTopBar(
                hazeState = topBarHaze,
                onSearchClick = {},
                // Skeleton mirrors the top of the screen — still over the hero.
                isScrolled = false,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        topBarHeight = with(density) { coordinates.size.height.toDp() }
                    },
            )
        }
    }
}

@Composable
fun HeroSectionSkeleton(
    modifier: Modifier = Modifier,
    topPadding: Dp,
) {
    // Heights derive from the production type scale, not separate literals.
    val density = LocalDensity.current
    val chipHeight = with(density) {
        MaterialTheme.typography.labelLarge.lineHeight.toDp()
    } + AppTheme.spacing.chipPaddingV * 2

    HeroBackdrop(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── 1. Carousel Skeleton Track Block ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    // Uses the exact topPadding dynamic token passed from HomeScreenSkeleton
                    .padding(top = topPadding),
                contentAlignment = Alignment.Center
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.65f)
                        .padding(
                            horizontal = AppTheme.spacing.lg,
                            vertical = AppTheme.spacing.md
                        ),
                    shape = MaterialTheme.shapes.large
                )
            }

            // ── 2. Info Panel Skeleton ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.spacing.xl,
                        vertical = AppTheme.spacing.lg
                    )
            ) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                // Mock Chips Row (Rating + Genres)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(
                            width = AppTheme.sizes.skeletonChipW,
                            height = chipHeight
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                    repeat(2) {
                        ShimmerBox(
                            modifier = Modifier.size(
                                width = AppTheme.sizes.skeletonGenreW,
                                height = chipHeight
                            ),
                            shape = ShapePill
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.xl))

                // Mock Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppTheme.sizes.touchTarget),
                        shape = MaterialTheme.shapes.medium
                    )
                    ShimmerBox(
                        modifier = Modifier.size(AppTheme.sizes.actionButton),
                        shape = ShapeCircle
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
    val density = LocalDensity.current
    val titleHeight = with(density) {
        MaterialTheme.typography.titleLarge.lineHeight.toDp()
    }

    Column(modifier = modifier.padding(bottom = AppTheme.spacing.sectionPaddingBottom)) {

        // ── 1. Mock Section Title Header ─────────────────────────────────────
        // Replicates the padding bounds and the titleLarge line height
        ShimmerBox(
            modifier = Modifier
                .padding(
                    horizontal = AppTheme.spacing.lg,
                    vertical = AppTheme.spacing.sm
                )
                .width(AppTheme.sizes.skeletonTitleW)
                .height(titleHeight),
            shape = MaterialTheme.shapes.small
        )

        // ── 2. Mock Media Item Row Track ──────────────────────────────────────
        // Keeps the exact contentPadding and arrangement metrics as your production row
        LazyRow(
            contentPadding = PaddingValues(horizontal = AppTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            userScrollEnabled = false // Prevents the user from flicking incomplete rows
        ) {
            items(itemCount) {
                // Mock Card Item — same width and 2:3 poster ratio as MediaCard
                ShimmerBox(
                    modifier = Modifier
                        .width(AppTheme.sizes.mediaCardWidth)
                        .height(
                            AppTheme.sizes.mediaCardWidth *
                                    AppTheme.sizes.mediaCardPosterRatio
                        ),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    }
}
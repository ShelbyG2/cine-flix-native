package tech.unrealistic.cineflix.feature.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import tech.unrealistic.cineflix.core.components.ShimmerBox
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * Skeleton mirroring the details sheet: 16:9 backdrop, genre chips, title,
 * metadata row, action buttons and three overview lines.
 */
@Composable
fun DetailShimmerLayout(modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    // Placeholder heights come from the production type scale + chip padding.
    val density = LocalDensity.current
    val titleHeight = with(density) {
        MaterialTheme.typography.headlineMedium.lineHeight.toDp()
    }
    val chipHeight = with(density) {
        MaterialTheme.typography.labelLarge.lineHeight.toDp()
    } + spacing.chipPaddingV * 2

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading…" }
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            shape = RectangleShape
        )

        Column(modifier = Modifier.padding(spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.gap6)) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(sizes.skeletonGenreW)
                            .height(chipHeight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(titleHeight)
            )

            Spacer(modifier = Modifier.height(spacing.md))

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(sizes.skeletonChipW)
                            .height(spacing.gap12)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizes.actionButton),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.gap12)
                )
                Spacer(modifier = Modifier.height(spacing.sm))
            }
        }
    }
}

/**
 * Three episode-row placeholders shown while a season is switching.
 */
@Composable
fun EpisodeShimmerList(
    modifier: Modifier = Modifier,
    rowCount: Int = 3,
) {
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading episodes…" }
    ) {
        repeat(rowCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md, vertical = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.gap12)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(sizes.episodeStillW)
                        .aspectRatio(16f / 9f),
                    shape = MaterialTheme.shapes.small
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(spacing.gap12)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.gap12)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(spacing.gap12)
                    )
                }
            }
        }
    }
}

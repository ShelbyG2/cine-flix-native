package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import dev.chrisbanes.haze.HazeState
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.WordmarkStyle

/**
 * Floating top bar — the original scroll-gated design on the one glass
 * material:
 *
 * - **Over the hero** ([isScrolled] false): no bar at all — a bare row with a
 *   white wordmark and the search control on its dark glass pill, floating
 *   directly on the artwork.
 * - **Past the hero** ([isScrolled] true): the glass material fades in behind
 *   the row (same Haze material as the bottom nav, hairline on the bottom
 *   edge) and the wordmark switches to the theme ink colour.
 *
 * Content never hides underneath — the row always reserves the same height,
 * so the layout does not jump when the bar materializes.
 */
@Composable
fun FloatingTopBar(
    hazeState: HazeState,
    onSearchClick: () -> Unit,
    isScrolled: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val motion = AppTheme.motion
    val image = AppTheme.colors.image

    // The glass material fades in once the list has scrolled past the hero.
    val appear by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = motion.spec(motion.mediumMs, motion.standard),
        label = "top_bar_appear"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.onSurface else image.onImage,
        animationSpec = motion.spec(motion.mediumMs, motion.standard),
        label = "top_bar_content"
    )

    Box(modifier = modifier.fillMaxWidth()) {

        // Glass bar — only materialized past the hero; nothing over the artwork.
        if (appear > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = appear }
                    .glassBar(hazeState, hairlineEdge = HairlineEdge.Bottom)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(sizes.topBarHeight)
                .padding(horizontal = spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "CINEFLIX",
                style = WordmarkStyle,
                color = contentColor,
            )

            // Dark glass pill — keeps the search icon legible over any artwork,
            // in both themes, without relying on the bar behind it.
            Row(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(image.scrimStrong)
                    .border(
                        width = sizes.hairline,
                        color = image.pillBorder,
                        shape = ShapePill
                    )
                    .padding(spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(sizes.touchTarget)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = image.onImage,
                        modifier = Modifier.size(sizes.iconMd)
                    )
                }
            }
        }
    }
}

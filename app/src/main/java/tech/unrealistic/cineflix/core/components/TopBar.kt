package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.chrisbanes.haze.HazeState
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.WordmarkStyle

/**
 * Floating top bar over the same glass material as the bottom navigation:
 * wordmark on the left, the existing search control on the right, and a
 * 0.5dp hairline along the bottom edge.
 */
@Composable
fun FloatingTopBar(
    hazeState: HazeState,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    Row(
        modifier = modifier
            .fillMaxWidth()
            .glassBar(hazeState, hairlineEdge = HairlineEdge.Bottom)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(sizes.topBarHeight)
            .padding(horizontal = spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CINEFLIX",
            style = WordmarkStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(sizes.touchTarget),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(sizes.iconMd),
            )
        }
    }
}

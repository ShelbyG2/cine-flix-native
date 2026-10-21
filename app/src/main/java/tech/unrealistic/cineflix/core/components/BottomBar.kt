package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import dev.chrisbanes.haze.HazeState
import tech.unrealistic.cineflix.AppDestinations
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * iOS-style bottom tab bar: icon above a short label, evenly distributed,
 * 56dp of content plus the system navigation-bar inset, over the shared glass
 * material. Same destinations, same order, same labels as before.
 *
 * Selection is signalled three ways so it never relies on colour alone:
 * icon tint, icon size and label size.
 */
@Composable
fun CineflixBottomBar(
    selected: AppDestinations,
    onSelect: (AppDestinations) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val sizes = AppTheme.sizes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassBar(hazeState)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.navBarHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppDestinations.entries.forEach { destination ->
                NavTab(
                    destination = destination,
                    isSelected = destination == selected,
                    onClick = { onSelect(destination) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavTab(
    destination: AppDestinations,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val sizes = AppTheme.sizes
    val navColors = AppTheme.colors.nav
    val motion = AppTheme.motion
    val haptics = LocalHapticFeedback.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    // Brief scale on press; snaps back instantly under reduce-motion.
    val scale by animateFloatAsState(
        targetValue = if (isPressed) motion.navPressScale else 1f,
        animationSpec = motion.pressSpec,
        label = "nav_tab_press",
    )

    val tint = if (isSelected) navColors.selectedIcon else navColors.unselectedIcon

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .sizeIn(minWidth = sizes.touchTarget, minHeight = sizes.touchTarget)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .selectable(
                selected = isSelected,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
        ) {
            Icon(
                painter = painterResource(destination.icon),
                // The visible label below names the tab.
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(if (isSelected) sizes.iconLg else sizes.iconMd),
            )
            Text(
                text = destination.label,
                style = if (isSelected) {
                    MaterialTheme.typography.labelMedium
                } else {
                    MaterialTheme.typography.labelSmall
                },
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

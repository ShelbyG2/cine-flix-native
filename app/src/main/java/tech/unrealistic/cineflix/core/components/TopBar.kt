package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import tech.unrealistic.cineflix.R
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.WordmarkStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingTopBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    isScrolled: Boolean

) {
    val motion = AppTheme.motion
    val colors = AppTheme.colors

    //Animate background color change
    val backgroundColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(durationMillis = motion.mediumMs),
        label = "bar_bg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(AppTheme.spacing.topBarPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The Logo — legible on imagery (light) and on the bar (theme role)
        Text(
            text = "CINEFLIX",
            style = WordmarkStyle,
            color = if (isScrolled) MaterialTheme.colorScheme.onSurface else colors.image.onImage
        )

        // The Floating Glass Panel (Pill Shape)
        Row(
            modifier = Modifier
                .clip(ShapePill)
                .background(colors.image.scrimStrong) // dark pill keeps the icon legible in both modes
                .border(
                    width = AppTheme.sizes.hairline,
                    color = colors.image.pillBorder,
                    shape = ShapePill
                )
                .padding(AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
        ) {

            // Search Icon inside the panel — 48dp touch target
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(AppTheme.sizes.touchTarget)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = colors.image.onImage,
                    modifier = Modifier.size(AppTheme.sizes.iconMd)
                )
            }


        }
    }
}

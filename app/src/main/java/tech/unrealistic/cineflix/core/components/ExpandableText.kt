package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.BodyLineHeightBoost
import tech.unrealistic.cineflix.ui.theme.LabelStrongStyle

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3
) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }

    // animateContentSize() gives it that premium "Netflix-style" smooth slide down
    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = text,
            // If expanded, remove the line limit entirely
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            // Dedicated colour role (AA in both modes) instead of alpha-dimmed text
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * BodyLineHeightBoost,
            onTextLayout = { textLayoutResult ->
                // Check if the text was clamped by the maxLines constraint.
                // We only calculate this when it's NOT expanded so it doesn't glitch when closing.
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    hasVisualOverflow = true
                }
            }
        )

        // Only show the toggle affordance if the text was actually too long
        if (hasVisualOverflow) {
            Box(
                modifier = Modifier
                    .height(AppTheme.sizes.touchTarget)
                    .clickable { isExpanded = !isExpanded },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (isExpanded) "Show Less" else "Read More",
                    style = LabelStrongStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

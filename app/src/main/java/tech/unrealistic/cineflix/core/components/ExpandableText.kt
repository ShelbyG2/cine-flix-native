package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            // Keeping your improved line height from the previous refactor
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f,
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
            Text(
                text = if (isExpanded) "Show Less" else "Read More",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }
}
package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.twotone.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * Centred error state with an optional retry affordance.
 * Offline failures get the Wi-Fi-off icon; other failures get the error icon.
 * Theme tokens only — legible in light and dark mode.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
    icon: ImageVector? = null,
    onRetry: (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val isOffline = message.contains("internet", ignoreCase = true) ||
            message.contains("Wi-Fi", ignoreCase = true)
    val resolvedIcon = icon ?: if (isOffline) Icons.TwoTone.WifiOff
    else Icons.Outlined.ErrorOutline

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        Icon(
            imageVector = resolvedIcon,
            contentDescription = if (isOffline) "Offline" else "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(AppTheme.sizes.stateIcon)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Button(
                onClick = onRetry,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = spacing.buttonPaddingH,
                    vertical = spacing.buttonPaddingV
                ),
                modifier = Modifier.defaultMinSize(minHeight = AppTheme.sizes.touchTarget)
            ) {
                Text(text = "Retry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Centred empty state — icon, title, optional supporting message and action.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: Pair<String, () -> Unit>? = null,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(AppTheme.sizes.stateIconLg)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        subtitle?.takeIf { it.isNotBlank() }?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        action?.let { (label, onClick) ->
            TextButton(
                onClick = onClick,
                modifier = Modifier.defaultMinSize(minHeight = AppTheme.sizes.touchTarget)
            ) {
                Text(text = label, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

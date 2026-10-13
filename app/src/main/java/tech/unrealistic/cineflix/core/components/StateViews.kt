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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onRetry: (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val isOffline = message.contains("internet", ignoreCase = true) ||
            message.contains("Wi-Fi", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        Icon(
            imageVector = if (isOffline) Icons.TwoTone.WifiOff else Icons.Outlined.ErrorOutline,
            contentDescription = if (isOffline) "Offline" else "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(AppTheme.sizes.stateIcon)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
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
                Text(text = "Try Again", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Centred empty state — icon, title, supporting message.
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

package tech.unrealistic.cineflix.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tech.unrealistic.cineflix.ui.theme.AppTheme

/** Profile destination — placeholder copy only, unchanged. */
@Composable
fun ProfileScreen(onNavigate: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppTheme.spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "This is Profile",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

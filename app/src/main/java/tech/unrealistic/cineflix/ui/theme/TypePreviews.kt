package tech.unrealistic.cineflix.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/** Every M3 type slot in one column, so weights and tracking can be compared. */
@Composable
private fun TypeScaleSample() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(AppTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
    ) {
        Text("Display large", style = MaterialTheme.typography.displayLarge)
        Text("Display medium", style = MaterialTheme.typography.displayMedium)
        Text("Display small", style = MaterialTheme.typography.displaySmall)
        Text("Headline large", style = MaterialTheme.typography.headlineLarge)
        Text("Headline medium", style = MaterialTheme.typography.headlineMedium)
        Text("Headline small", style = MaterialTheme.typography.headlineSmall)
        Text("Title large", style = MaterialTheme.typography.titleLarge)
        Text("Title medium", style = MaterialTheme.typography.titleMedium)
        Text("Title small", style = MaterialTheme.typography.titleSmall)
        Text("Body large", style = MaterialTheme.typography.bodyLarge)
        Text("Body medium", style = MaterialTheme.typography.bodyMedium)
        Text("Body small", style = MaterialTheme.typography.bodySmall)
        Text("Label large", style = MaterialTheme.typography.labelLarge)
        Text("Label medium", style = MaterialTheme.typography.labelMedium)
        Text("Label small", style = MaterialTheme.typography.labelSmall)
        Text("CINEFLIX", style = WordmarkStyle)
    }
}

@Preview(name = "Type — light", showBackground = true, widthDp = 360)
@Composable
private fun TypeScaleLightPreview() {
    CineflixTheme(darkTheme = false) { TypeScaleSample() }
}

@Preview(name = "Type — dark", showBackground = true, widthDp = 360)
@Composable
private fun TypeScaleDarkPreview() {
    CineflixTheme(darkTheme = true) { TypeScaleSample() }
}

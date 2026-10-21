package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tech.unrealistic.cineflix.data.remote.models.Movie
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.CineflixTheme

private val PreviewMovie = Movie(
    id = 1,
    title = "A Very Long Film Title That Ellipsises",
    overview = "",
    posterPath = null,
    backdropPath = null,
    releaseDate = "2026-01-01",
    voteAverage = 8.4,
    voteCount = 1200,
    originalLanguage = "en",
    originalTitle = "A Very Long Film Title That Ellipsises",
    popularity = 12.0,
)

@Composable
private fun ComponentSample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            MediaCard(
                media = PreviewMovie,
                isSaved = true,
                onSaveToggle = {},
                isFavourite = false,
                onFavouriteToggle = {},
            )
        }

        ActionButtons(
            item = PreviewMovie,
            isFavourite = false,
            onPlay = {},
            onMediaClick = { _, _ -> },
            onFavourite = {},
            isSaved = true,
            onSave = {},
            onMediaShare = {},
        )

        EmptyState(
            icon = Icons.Outlined.FavoriteBorder,
            title = "Your library is empty",
            subtitle = "Tap the heart on any title to add it here.",
        )

        ErrorState(
            title = "Something went wrong",
            message = "Check your internet connection.",
            onRetry = {},
        )

        CardLabel()
    }
}

@Composable
private fun CardLabel() {
    // Section-header sample so hierarchy can be compared next to the cards.
    androidx.compose.material3.Text(
        text = "Trending Movies",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Preview(name = "Components — light phone", showBackground = true, widthDp = 360)
@Composable
private fun ComponentsLightPhone() {
    CineflixTheme(darkTheme = false) { SurfaceBackground { ComponentSample() } }
}

@Preview(name = "Components — dark phone", showBackground = true, widthDp = 360)
@Composable
private fun ComponentsDarkPhone() {
    CineflixTheme(darkTheme = true) { SurfaceBackground { ComponentSample() } }
}

@Preview(name = "Components — light tablet", showBackground = true, widthDp = 800)
@Composable
private fun ComponentsLightTablet() {
    CineflixTheme(darkTheme = false) { SurfaceBackground { ComponentSample() } }
}

@Composable
private fun SurfaceBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(AppTheme.spacing.sm)) { content() }
}

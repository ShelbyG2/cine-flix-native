package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.ui.theme.AppTheme

@Composable
fun MediaSection(
    title: String,
    items: List<MediaItem>,          // unified type — no more movies/tvShows split
    modifier: Modifier = Modifier,
    onMediaClick:((Int, String) -> Unit),
    savedIds: Set<Int> = emptySet(),
    onSaveToggle: ((MediaItem) -> Unit)? = null,
) {
    if (items.isEmpty()) return           // don't render an empty section at all

    Column(modifier = modifier.padding(bottom = AppTheme.spacing.sectionPaddingBottom)) {

        Text(
            text     = title,
            style    = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color    = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.lg,
                vertical = AppTheme.spacing.sm
            )
        )

        // LazyRow instead of LazyHorizontalGrid(Fixed(1)) —
        // Grid for a single row adds unnecessary overhead
        LazyRow(
            state = rememberLazyListState(),
            contentPadding      = PaddingValues(horizontal = AppTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            items(
                items = items,
                key   = { it.id }       // stable keys prevent unnecessary recompositions
            ) { item ->
                MediaCard(
                    media      = item,
                    isSaved = savedIds.contains(item.id),
                    onSaveToggle = if (onSaveToggle != null) {{ onSaveToggle(item) }} else null,
                    modifier = Modifier.clickable {
                        onMediaClick(item.id, item.mediaType) }

                )
            }
        }
    }
}
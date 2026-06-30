package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.data.remote.models.MediaItem

@Composable
fun MediaSection(
    title:      String,
    items:      List<MediaItem>,          // unified type — no more movies/tvShows split
    modifier:   Modifier = Modifier,
    onMediaClick: ((Int, String) -> Unit),
) {
    if (items.isEmpty()) return           // don't render an empty section at all

    Column(modifier = modifier.padding(bottom = 28.dp)) {

        Text(
            text     = title,
            style    = MaterialTheme.typography.titleLarge,
            color    = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // LazyRow instead of LazyHorizontalGrid(Fixed(1)) —
        // Grid for a single row adds unnecessary overhead
        LazyRow(
            contentPadding      = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = items,
                key   = { it.id }       // stable keys prevent unnecessary recompositions
            ) { item ->
                MediaCard(
                    media      = item,
                    modifier = Modifier.clickable { onMediaClick(item.id, item.mediaType) }

                )
            }
        }
    }
}
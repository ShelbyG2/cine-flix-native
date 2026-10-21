package tech.unrealistic.cineflix.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.unrealistic.cineflix.core.components.MediaCard
import tech.unrealistic.cineflix.core.components.enterFadeUp
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * A titled horizontal poster shelf. Section headers use `titleLarge`; the row
 * scrolls with 16dp screen padding and 8dp between posters.
 */
@Composable
fun MediaSection(
    title: String,
    items: List<MediaItem>,          // unified type — no more movies/tvShows split
    modifier: Modifier = Modifier,
    onMediaClick: ((Int, String) -> Unit),
    savedIds: Set<Int> = emptySet(),
    onSaveToggle: ((MediaItem) -> Unit)? = null,
) {
    if (items.isEmpty()) return           // don't render an empty section at all

    val spacing = AppTheme.spacing

    Column(modifier = modifier.padding(bottom = spacing.sectionPaddingBottom)) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                start = spacing.screenPadding,
                end = spacing.screenPadding,
                top = spacing.sm,
                bottom = spacing.md
            )
        )

        LazyRow(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(horizontal = spacing.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing.itemGap),
        ) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.id }   // stable keys prevent extra recompositions
            ) { index, item ->
                MediaCard(
                    media = item,
                    isSaved = savedIds.contains(item.id),
                    onSaveToggle = if (onSaveToggle != null) {{ onSaveToggle(item) }} else null,
                    modifier = Modifier
                        .enterFadeUp(index)
                        .clickable { onMediaClick(item.id, item.mediaType) }
                )
            }
        }
    }
}

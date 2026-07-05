package tech.unrealistic.cineflix.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import tech.unrealistic.cineflix.core.components.ActionButtons
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle
import tech.unrealistic.cineflix.feature.home.components.MediaSection
import tech.unrealistic.cineflix.helpers.genreNames


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    mediaId: Int,
    mediaType: String,
    sheetState: SheetState,
    onPlayClick: (MediaItem) -> Unit = {},
    onFavourite: (MediaItem) -> Unit = {},
    onMediaShare: (MediaItem)-> Unit = {}


    ) {
    if (showBottomSheet) {


        ModalBottomSheet(
            onDismissRequest,
            modifier = modifier,
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets.navigationBars },
            dragHandle = {}
        ) {
            val sheetViewModel: DetailsSheetViewModel = viewModel()
            val uiState by sheetViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(mediaId, mediaType) {
                val id = mediaId ?: return@LaunchedEffect
                val type = mediaType ?: return@LaunchedEffect

                sheetViewModel.fetchSheetDetails(type, id)
            }

            Column(

                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .statusBarsPadding(),
            ) {
                when (val state = uiState) {
                    is DetailsSheetState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is DetailsSheetState.Success -> {
                        val mediaItem = state.selectedShow
                        val similarShows = state.similarShows
                        val posterUrl = "https://image.tmdb.org/t/p/w500${mediaItem.backdropPath}"
                        fun  onMediaClick  ( id:Int, type: String ) {
                            sheetViewModel.fetchSheetDetails(type, id)
                        }
                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        )
                        {

                            AsyncImage(
                                model = posterUrl,
                                contentDescription = mediaItem.displayTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop

                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    0.85f
                                                ),
                                                MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    0.95f
                                                )

                                            ),

                                            )

                                    )
                                    .align(Alignment.BottomCenter)
                            )
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 16.dp)
                                    .clip(CircleShape)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(0.5f))
                                    .border(
                                        width = 0.8.dp,
                                        MaterialTheme.colorScheme.primary.copy(0.25f),
                                        shape = CircleShape
                                    )


                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Details",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                            ) {
                                Row {
                                    mediaItem.genreNames.forEach { genre ->
                                        Surface(
                                            shape = RoundedCornerShape(50),    // pill
                                            color = Color.White.copy(alpha = 0.12f),
                                            modifier = Modifier
                                                .border(
                                                    0.8.dp,
                                                    Color.White.copy(alpha = 0.25f),
                                                    RoundedCornerShape(50)
                                                )
                                        ) {
                                            Text(
                                                text     = genre,
                                                style    = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = mediaItem.displayTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )


                            }
                        }

                        ActionButtons(
                            item = mediaItem,
                            onMediaShare = { onMediaShare(mediaItem) },
                            isFavourite = false,
                            onFavourite = { onFavourite(mediaItem) },
                            onPlay = { mediaItem?.let { onPlayClick(it) } },
                        )
                        mediaItem.overview?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier= Modifier.padding(10.dp)
                            )
                        }

                        MediaSection(
                            title = "More Like this ",
                            items = similarShows,
                            onMediaClick = {
                                id:Int, type: String ->
                                sheetViewModel.fetchSheetDetails(type, id)

                            }
                        )

                    }

                    is DetailsSheetState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }


            }
        }
    }
}


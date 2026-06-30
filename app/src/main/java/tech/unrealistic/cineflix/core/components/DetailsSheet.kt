package tech.unrealistic.cineflix.core.components

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.RetrofitClient
import tech.unrealistic.cineflix.data.remote.genreNames
import tech.unrealistic.cineflix.data.remote.models.MediaItem
import tech.unrealistic.cineflix.data.remote.models.displayTitle

sealed interface DetailsSheetState {
    object Loading : DetailsSheetState
    data class Success(
        val selectedShow: MediaItem
    ) : DetailsSheetState

    data class Error(
        val message: String
    ) : DetailsSheetState
}


class DetailsSheetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsSheetState>(DetailsSheetState.Loading)
    val uiState: StateFlow<DetailsSheetState> = _uiState.asStateFlow()


    fun fetchSheetDetails(mediaType: String, mediaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsSheetState.Loading
            try {
                val mediaDetails =if (mediaType=="movie") {
                    RetrofitClient.tmdbService.getMovieDetail(id = mediaId)
                }else RetrofitClient.tmdbService.getTvDetail(mediaId)
                _uiState.value = DetailsSheetState.Success(
                    mediaDetails
                )
            } catch (e: Exception) {
                _uiState.value = DetailsSheetState.Error("An error occurred ${e.message}")
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    mediaId: Int,
    mediaType: String,
    sheetState: SheetState,

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
                        val posterUrl = "https://image.tmdb.org/t/p/w500${mediaItem.backdropPath}"
                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        ) {

                            AsyncImage(
                                model = posterUrl,
                                contentDescription = mediaItem.displayTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop

                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surfaceContainer.copy(0.85f),
                                                MaterialTheme.colorScheme.surfaceContainer.copy(0.9f)

                                            ),
                                            0f, 250F
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


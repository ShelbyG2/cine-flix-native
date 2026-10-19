package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.unrealistic.cineflix.data.remote.EmbedWave

sealed class PlayerUiState {
    object Loading : PlayerUiState()
    data class Ready(val streamUrl: String) : PlayerUiState()
    data class Error(val message: String) : PlayerUiState()
    object NoStream : PlayerUiState() // fallback to embedded WebView
}

class PlayerViewModel @JvmOverloads constructor(
    context: Context,
    private val onStreamResolved: (String) -> Unit = {},
    private val onPlayReady: () -> Unit = {},
    private val onPlayError: (String) -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _playbackState = MutableStateFlow(false)
    val playbackState: StateFlow<Boolean> = _playbackState.asStateFlow()

    fun preparePlayer(
        tmdbId: Int,
        mediaType: String,
        season: Int? = null,
        episode: Int? = null,
        onFail: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading

            try {
                val embedUrl = EmbedWave.embedUrl(
                    tmdbId = tmdbId,
                    mediaType = mediaType,
                    season = season,
                    episode = episode,
                    autoplay = false
                )

                val streamUrl = try {
                    resolveStreamUrl(embedUrl, tmdbId, mediaType, season, episode)
                } catch (e: Exception) {
                    null
                }

                if (streamUrl != null) {
                    _uiState.value = PlayerUiState.Ready(streamUrl)
                    onStreamResolved(streamUrl)
                    playStream(streamUrl, onFail)
                } else {
                    _uiState.value = PlayerUiState.NoStream
                    onFail()
                }
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.Error(e.message ?: "Unknown error")
                onFail()
            }
        }
    }

    private suspend fun resolveStreamUrl(
        embedUrl: String,
        tmdbId: Int,
        mediaType: String,
        season: Int?,
        episode: Int?
    ): String? {
        // Placeholder: return null to fall back to the embedded WebView.
        return null
    }

    private fun playStream(streamUrl: String, onFail: () -> Unit) {
        try {
            val mediaItem = MediaItem.fromUri(streamUrl)
            _player.value?.setMediaItem(mediaItem)
            _player.value?.prepare()
            _player.value?.play()
            _playbackState.value = true
            onPlayReady()
        } catch (e: Exception) {
            _playbackState.value = false
            onPlayError(e.message ?: "Playback failed")
            onFail()
        }
    }

    fun isPlaying(): Boolean = _playbackState.value

    fun releasePlayer() {
        viewModelScope.launch {
            _player.value?.release()
            _player.value = null
            _playbackState.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        _player.value?.release()
        _player.value = null
    }
}
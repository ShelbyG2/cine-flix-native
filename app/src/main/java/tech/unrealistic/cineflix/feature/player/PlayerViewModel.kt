package tech.unrealistic.cineflix.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

/**
 * What the player screen should render. `NoStream` means the embed page never
 * exposed a playable manifest, so the screen falls back to hosting the provider
 * player in a WebView.
 */
/** A playable manifest plus the page it was discovered on (sent as Referer). */
data class ResolvedStream(val url: String, val referrer: String)

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(val stream: ResolvedStream) : PlayerUiState
    data object NoStream : PlayerUiState
}

/**
 * Drives stream resolution for the player screen.
 *
 * Extraction is injected as [resolve] by the screen: the view model stays free
 * of Android types, so `viewModel()` can build it, and the resolution source
 * (on-device extractor today, HTTP backend tomorrow) can be swapped without
 * touching this class.
 */
class PlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    /**
     * Tries each embed page in order and stops at the first playable manifest.
     * [resolve] is supplied by the screen because extraction needs an Android
     * WebView; swapping it for a backend call later needs no change here.
     */
    fun preparePlayer(embedUrls: List<String>, resolve: suspend (String) -> ResolvedStream?) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            for (embedUrl in embedUrls) {
                val stream = withTimeoutOrNull(RESOLVE_TIMEOUT_MS.milliseconds) {
                    runCatching { resolve(embedUrl) }.getOrNull()
                }
                if (stream != null && stream.url.isNotBlank()) {
                    _uiState.value = PlayerUiState.Ready(stream)
                    return@launch
                }
            }
            _uiState.value = PlayerUiState.NoStream
        }
    }

    private companion object {
        /** A provider either exposes a manifest quickly or never does. */
        const val RESOLVE_TIMEOUT_MS = 15_000L
    }
}

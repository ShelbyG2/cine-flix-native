package tech.unrealistic.cineflix.feature.player

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerViewModelTest {

    @Test
    fun `initial state is Loading`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        assertEquals(PlayerUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `player is initially null`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        assertTrue(viewModel.player.value == null)
    }

    @Test
    fun `playback state is initially false`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        assertFalse(viewModel.playbackState.value)
    }

    @Test
    fun `isPlaying returns false initially`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        assertFalse(viewModel.isPlaying())
    }
}

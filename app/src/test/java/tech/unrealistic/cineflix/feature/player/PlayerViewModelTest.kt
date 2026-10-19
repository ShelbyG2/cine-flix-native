package tech.unrealistic.cineflix.feature.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
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
        org.junit.Assert.assertEquals(PlayerUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `player is initially null`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        org.junit.Assert.assertTrue(viewModel.player.value == null)
    }

    @Test
    fun `playback state is initially false`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        org.junit.Assert.assertFalse(viewModel.playbackState.value)
    }

    @Test
    fun `isPlaying returns false initially`() {
        val viewModel = PlayerViewModel(
            context = null as Context,
            onStreamResolved = {},
            onPlayReady = {},
            onPlayError = {}
        )
        org.junit.Assert.assertFalse(viewModel.isPlaying())
    }
}
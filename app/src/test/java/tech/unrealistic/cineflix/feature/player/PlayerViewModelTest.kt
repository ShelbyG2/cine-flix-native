package tech.unrealistic.cineflix.feature.player

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerViewModelTest {

    @Test
    fun `initial state is Loading`() {
        val viewModel = PlayerViewModel()

        assertEquals(PlayerUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `view model is creatable without dependencies`() {
        // The screen obtains it with viewModel(), which needs a no-arg constructor.
        val viewModel = PlayerViewModel()

        assertEquals(PlayerUiState.Loading, viewModel.uiState.value)
    }
}

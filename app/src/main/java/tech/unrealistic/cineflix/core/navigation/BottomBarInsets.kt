package tech.unrealistic.cineflix.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Height of the glass bottom bar including the system navigation-bar inset.
 *
 * Screens add this as the bottom padding of their scrollable content, so the
 * last item can always scroll clear of the translucent bar while the content
 * itself still passes underneath it.
 */
val LocalBottomBarInset = staticCompositionLocalOf<Dp> { 0.dp }

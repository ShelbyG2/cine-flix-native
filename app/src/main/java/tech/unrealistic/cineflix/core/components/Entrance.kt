package tech.unrealistic.cineflix.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import tech.unrealistic.cineflix.ui.theme.AppTheme

/**
 * One-time fade-up for list items: translateY 12dp → 0 and alpha 0 → 1, with
 * at most a 30ms stagger and a cap after 8 items.
 *
 * The "already played" flag is saveable, so scrolling an item out and back does
 * not replay the animation. Under reduce motion the item is simply visible.
 */
@Composable
fun Modifier.enterFadeUp(index: Int): Modifier {
    val motion = AppTheme.motion
    val risePx = with(LocalDensity.current) { AppTheme.sizes.listRise.toPx() }
    var played by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(if (motion.reduceMotion || played) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (played || motion.reduceMotion) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        delay(index.coerceAtMost(MAX_STAGGERED_ITEMS) * motion.staggerMs.toLong())
        progress.animateTo(1f, motion.spec(motion.mediumMs, motion.emphasized))
        played = true
    }

    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * risePx
    }
}

/** Items past this index animate together, so long lists stay calm. */
private const val MAX_STAGGERED_ITEMS = 8

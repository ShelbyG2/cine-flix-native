package tech.unrealistic.cineflix.feature.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import tech.unrealistic.cineflix.ui.theme.AppTheme

/** Scrim behind the controls — the instruction's 45% black over themed surface. */
private val ControlsScrimAlpha = 0.45f

/** Play/pause size from the customisation instructions (§3.1). */
private val PlayButtonSize = 64.dp

/** Scrubber thumb size (§3.1: 16 dp thumb). */
private val ThumbSize = 16.dp

/** Ignore sub-pixel drift when mirroring playback into the scrubber. */
private const val POSITION_SYNC_EPSILON = 0.001f

/**
 * The player's own controls: top bar (close + track pickers), centre transport
 * row and bottom scrubber. Drawn entirely in Compose on top of the disabled
 * `PlayerView` so every colour comes from `colorScheme` tokens (§3).
 *
 * The layer is shown/hidden by the caller; [onInteract] must be invoked by every
 * element that should restart the 3-second auto-hide timer.
 */
@Composable
fun PlayerControlsLayer(
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onQuality: () -> Unit,
    onSubtitles: () -> Unit,
    onAudio: () -> Unit,
    onLayout: (() -> Unit)? = null,
    onSeek: (Long) -> Unit,
    onExit: () -> Unit,
    onInteract: () -> Unit,
    modifier: Modifier = Modifier,
    showPrev: Boolean = false,
    showNext: Boolean = false,
    qualityVisible: Boolean = true,
    qualityEnabled: Boolean = true,
    subtitleVisible: Boolean = true,
    subtitleEnabled: Boolean = true,
    audioVisible: Boolean = true,
    audioEnabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = ControlsScrimAlpha))
    ) {
        // ── Top bar ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = {
                    onInteract()
                    onExit()
                },
                modifier = Modifier.size(AppTheme.sizes.touchTarget),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close player",
                    modifier = Modifier.size(AppTheme.sizes.iconMd)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                if (qualityVisible) {
                    PlayerLabelButton(
                        label = "Quality",
                        icon = Icons.Outlined.HighQuality,
                        enabled = qualityEnabled,
                        onClick = {
                            onInteract()
                            onQuality()
                        }
                    )
                }
                if (subtitleVisible) {
                    PlayerLabelButton(
                        label = "Subtitles",
                        icon = Icons.Outlined.Subtitles,
                        enabled = subtitleEnabled,
                        onClick = {
                            onInteract()
                            onSubtitles()
                        }
                    )
                }
                if (audioVisible) {
                    PlayerLabelButton(
                        label = "Audio",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        enabled = audioEnabled,
                        onClick = {
                            onInteract()
                            onAudio()
                        }
                    )
                }
                onLayout?.let {
                    PlayerLabelButton(
                        label = "Layout",
                        icon = Icons.Outlined.AspectRatio,
                        enabled = true,
                        onClick = {
                            onInteract()
                            it()
                        }
                    )
                }
            }
        }

        // ── Centre transport row ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                // Hidden buttons leave a 48dp slot behind so play/pause stays
                // centred (§3.2).
                AnimatedVisibility(visible = showPrev) {
                    PlayerPlainButton(
                        icon = Icons.Filled.SkipPrevious,
                        description = "Previous episode",
                        onClick = {
                            onInteract()
                            onPrevious()
                        }
                    )
                }
                if (!showPrev) Spacer(Modifier.size(AppTheme.sizes.touchTarget))

                FilledIconButton(
                    onClick = {
                        onInteract()
                        onTogglePlay()
                    },
                    modifier = Modifier.size(PlayButtonSize),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(AppTheme.sizes.iconXl)
                    )
                }

                AnimatedVisibility(visible = showNext) {
                    PlayerPlainButton(
                        icon = Icons.Filled.SkipNext,
                        description = "Next episode",
                        onClick = {
                            onInteract()
                            onNext()
                        }
                    )
                }
                if (!showNext) Spacer(Modifier.size(AppTheme.sizes.touchTarget))
            }
        }

        // ── Bottom scrubber ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeLabel(formatPosition(currentPositionMs))
            Scrubber(
                fraction = if (durationMs > 0) {
                    (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f)
                } else {
                    0f
                },
                durationMs = durationMs,
                onSeek = { position ->
                    onInteract()
                    onSeek(position)
                },
                modifier = Modifier.weight(1f),
            )
            TimeLabel(formatPosition(durationMs))
        }
    }
}

/**
 * Scrubber built on the slot-based `Slider` so the thumb can be sized to the
 * 16 dp the instructions specify. External position updates only move the thumb
 * while it is not being dragged, which keeps seeking and playback polling from
 * fighting each other.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Scrubber(
    fraction: Float,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val sliderState = rememberSliderState(
        value = fraction,
        valueRange = 0f..1f,
    )

    // User-driven value changes (drag, tap, arrow keys) through Material3's Slider.
    // onValueChange is called for every user change: drag, tap, and arrow keys.
    // We store the new value and seek playback directly.
    sliderState.onValueChange = { newValue ->
        sliderState.value = newValue
        val duration = durationMs
        if (duration > 0) {
            onSeek((newValue * duration).toLong())
        }
    }

    Slider(
        state = sliderState,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary
                ),
                thumbSize = DpSize(ThumbSize, ThumbSize),
            )
        },
        track = { state ->
            SliderDefaults.Track(
                sliderState = state,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            )
        },
    )
}

/** Icon button with a `labelSmall` caption underneath (§3.1 top bar). */
@Composable
private fun PlayerLabelButton(
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(AppTheme.sizes.touchTarget)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(AppTheme.sizes.iconMd)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Plain 48dp transport button (prev / next) with the onSurface tint (§3.3). */
@Composable
private fun PlayerPlainButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(AppTheme.sizes.touchTarget)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(AppTheme.sizes.iconLg)
        )
    }
}

@Composable
private fun TimeLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

/** `mm:ss`, or `hh:mm:ss` once the runtime passes an hour. */
internal fun formatPosition(millis: Long): String {
    if (millis <= 0L) return "00:00"
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import tech.unrealistic.cineflix.ui.theme.AppTheme
import tech.unrealistic.cineflix.ui.theme.ShapeCircle
import tech.unrealistic.cineflix.ui.theme.ShapePill
import tech.unrealistic.cineflix.ui.theme.withAlpha

/** Decorative folder tilt loop length. */
private const val FOLDER_TILT_MS = 2_600

/**
 * Screen 2 — File Access Permission.
 * 3D tilted folder (tab + body layers, file lines, download icon,
 * ground shadow) above a system-style permission card.
 * Both buttons advance the flow.
 */
@Composable
fun FileAccessScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(spacing.xl))

        // ── 3D folder icon ─────────────────────────────────────────────────
        FolderIcon3D(modifier = Modifier.weight(1f))

        // ── Permission card — solid raised surface with a hairline rim ──────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = sizes.hairline,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.extraLarge
                )
        ) {
            Column(modifier = Modifier.padding(spacing.xl)) {
                Text(
                    text = "Allow access to your files?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                Text(
                    text = "CINEFLIX needs storage access so you can save movies and episodes for offline watching.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.md))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(spacing.sm)
                            .clip(ShapeCircle)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = "Your downloads stay on this device — used for offline playback only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // ── Actions ────────────────────────────────────────────────────────
        Button(
            onClick = onContinue,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(
                horizontal = spacing.buttonPaddingH,
                vertical = spacing.buttonPaddingV
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Allow Access", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(spacing.sm))

        // Ghost border button — also advances the flow
        Button(
            onClick = onContinue,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = sizes.borderWidth,
                color = MaterialTheme.colorScheme.outline
            ),
            contentPadding = PaddingValues(
                horizontal = spacing.buttonPaddingH,
                vertical = spacing.buttonPaddingV
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Not Now",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(spacing.sm))
    }
}

/**
 * Folder built from two raised surfaces (tab + body) tilted back on the X axis
 * with a perspective camera, file lines and a download glyph inside, and a soft
 * ground shadow ellipse underneath for depth.
 */
@Composable
private fun FolderIcon3D(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val sizes = AppTheme.sizes
    val motion = AppTheme.motion

    // Gentle floating tilt — keeps the 3D perspective alive
    // Decorative: frozen mid-tilt under reduce-motion
    val tilt = rememberInfiniteTransition(label = "folder_tilt")
    val animatedTiltX by tilt.animateFloat(
        initialValue = 14f,
        targetValue = 22f,
        animationSpec = motion.infinite(
            durationMs = FOLDER_TILT_MS,
            easing = motion.standard,
            repeatMode = RepeatMode.Reverse
        ),
        label = "folder_tilt_x"
    )
    val tiltX = if (motion.reduceMotion) 18f else animatedTiltX

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(contentAlignment = Alignment.Center) {

                // Folder body + tab group, tilted with perspective
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            // Perspective camera so the tilt reads as 3D
                            cameraDistance = 12f * density
                            rotationX = tiltX
                            transformOrigin = TransformOrigin.Center
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // ── Tab layer (behind, peeking above the body) ────
                        Box(
                            modifier = Modifier
                                .width(OnboardingArt.folderTabW)
                                .height(OnboardingArt.folderTabH)
                                .clip(OnboardingArt.folderTabShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(
                                    width = sizes.borderWidth,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = OnboardingArt.folderTabShape
                                )
                        )

                        // ── Body layer — raised surface, holds the content ─
                        Box(
                            modifier = Modifier
                                .width(OnboardingArt.folderBodyW)
                                .height(OnboardingArt.folderBodyH)
                                .clip(OnboardingArt.folderBodyShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(
                                    width = sizes.borderWidth,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = OnboardingArt.folderBodyShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Soft top highlight
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                // File lines
                                OnboardingArt.fileLineWidths.forEach { lineWidth ->
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = spacing.xs)
                                            .width(lineWidth)
                                            .height(OnboardingArt.fileLineH)
                                            .clip(ShapePill)
                                            .background(MaterialTheme.colorScheme.outline)
                                    )
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                // Download glyph in a small raised disc
                                Box(
                                    modifier = Modifier
                                        .size(OnboardingArt.downloadDisc)
                                        .clip(ShapeCircle)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .border(
                                            width = sizes.borderWidth,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = ShapeCircle
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.FileDownload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(sizes.iconMd)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // ── Ground shadow ellipse ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(OnboardingArt.folderShadowW)
                    .height(OnboardingArt.folderShadowH)
                    .clip(ShapePill)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.image.chipSurface,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

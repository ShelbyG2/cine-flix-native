package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.unrealistic.cineflix.feature.home.components.GlassMorphOverlay

/**
 * Screen 2 — File Access Permission.
 * 3D tilted frosted-glass folder (tab + body layers, file lines, download icon,
 * ground shadow) above a system-style glass permission card.
 * Both buttons advance the flow.
 */
@Composable
fun FileAccessScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── 3D folder icon ─────────────────────────────────────────────────
        FolderIcon3D(modifier = Modifier.weight(1f))

        // ── System-style permission card ───────────────────────────────────
        Box {
            GlassMorphOverlay(
                modifier = Modifier.matchParentSize(),
                shape = MaterialTheme.shapes.extraLarge
            )

            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Allow access to your files?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CINEFLIX needs storage access so you can save movies and episodes for offline watching.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your downloads stay on this device — used for offline playback only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Actions ────────────────────────────────────────────────────────
        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF131313)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Allow Access",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ghost border button — also advances the flow
        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White.copy(alpha = 0.85f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Not Now",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Folder built from two frosted-glass layers (tab + body) tilted back on the
 * X axis with a perspective camera, file lines and a download glyph inside,
 * and a soft ground shadow ellipse underneath for depth.
 */
@Composable
private fun FolderIcon3D(modifier: Modifier = Modifier) {
    // Gentle floating tilt — keeps the 3D perspective alive
    val tilt = rememberInfiniteTransition(label = "folder_tilt")
    val tiltX by tilt.animateFloat(
        initialValue = 14f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "folder_tilt_x"
    )

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
                                .width(96.dp)
                                .height(26.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = 6.dp,
                                        bottomEnd = 6.dp
                                    )
                                )
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart = 6.dp,
                                        bottomEnd = 6.dp
                                    )
                                )
                        )

                        // ── Body layer — frosted glass, holds the content ─
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(150.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.20f),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Gloss streak on the glass
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.14f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                // File lines
                                listOf(120.dp, 88.dp, 104.dp).forEach { lineWidth ->
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 5.dp)
                                            .width(lineWidth)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.White.copy(alpha = 0.28f))
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Download glyph in a small glass disc
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.White.copy(alpha = 0.16f))
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.28f),
                                            shape = RoundedCornerShape(50)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.FileDownload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── Ground shadow ellipse ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
    }
}

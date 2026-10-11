package tech.unrealistic.cineflix.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * First-launch onboarding host.
 * Steps: 1) Cinematic intro (3 slides) → 2) File access permission → 3) GitHub star.
 * [onFinished] is invoked when the user completes (or skips) the whole flow.
 */
@Composable
fun OnboardingFlow(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn(tween(400))) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut(tween(300)))
                } else {
                    (slideInHorizontally { -it } + fadeIn(tween(400))) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut(tween(300)))
                }
            },
            label = "onboarding_step"
        ) { currentStep ->
            when (currentStep) {
                1 -> CinematicIntroScreen(
                    onNext = { step = 2 },
                    onSkip = onFinished
                )

                2 -> FileAccessScreen(
                    onContinue = { step = 3 }
                )

                else -> GitHubStarScreen(onComplete = onFinished)
            }
        }
    }
}

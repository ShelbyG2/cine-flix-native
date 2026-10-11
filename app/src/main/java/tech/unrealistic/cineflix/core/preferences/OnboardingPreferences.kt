package tech.unrealistic.cineflix.core.preferences

import android.content.Context

/**
 * Persists the "onboarding completed" flag so the first-launch flow
 * (Cinematic Intro → File Access → GitHub Star) only ever shows once.
 */
object OnboardingPreferences {

    private const val PREFS_NAME = "cineflix_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    fun hasCompletedOnboarding(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)

    fun markOnboardingComplete(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }
}

package tech.unrealistic.cineflix

import android.app.Application
import tech.unrealistic.cineflix.data.local.LibraryRepository

/**
 * Initializes app-wide singletons that need a Context.
 * Registered in AndroidManifest as android:name=".CineflixApplication".
 */
class CineflixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LibraryRepository.init(this)
    }
}

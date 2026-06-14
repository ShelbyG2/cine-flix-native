package tech.unrealistic.cineflix

import androidx.navigation3.runtime.NavKey


enum class AppDestinations(
    val label: String,
    val icon: Int,
    val  route: NavKey,
) {
    HOME("Home", R.drawable.ic_home_, Home),
    DISCOVER("Discover", R.drawable.ic_explore, Discover),
    FAVOURITES("Favourites", R.drawable.ic_bookmark, Favourites),
    PROFILE("Profile", R.drawable.ic_profile, Profile),
}



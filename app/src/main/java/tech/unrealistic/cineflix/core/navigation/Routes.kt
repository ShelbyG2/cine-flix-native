package tech.unrealistic.cineflix

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable data object Home : NavKey
@Serializable data object Discover : NavKey
@Serializable data object Profile : NavKey
@Serializable data object Favourites: NavKey
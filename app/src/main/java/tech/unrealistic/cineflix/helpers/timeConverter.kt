package tech.unrealistic.cineflix.helpers

fun formatTime (t: Int): String {
    val minutes = t% 60
    val hours= t/60
    return "${hours}h ${minutes}m"
}
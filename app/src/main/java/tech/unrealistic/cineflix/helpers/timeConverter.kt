package tech.unrealistic.cineflix.helpers

public fun formatTime(t: Int): String {
    val minutes = t % 60
    val hours = t / 60
    if (hours > 0) {
        return "${hours}h ${minutes}m"
    }
    return "${minutes}m"
}
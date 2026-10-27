package tech.unrealistic.cineflix.ui.theme

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the monochrome palette.
 *
 * Every literal in `Color.kt` must be a neutral gray (channel spread ≤ 8)
 * unless its name marks it as a status colour (error / success / warning) or
 * the favourite heart. This fails the build the moment a brand hue creeps back.
 */
class MonochromePaletteTest {

    private val colorSource = File(
        "src/main/java/tech/unrealistic/cineflix/ui/theme/Color.kt"
    )

    private val literal = Regex("""val\s+(\w+)\s*=\s*Color\(0x([0-9A-Fa-f]{8})\)""")

    @Test
    fun `every colour is neutral unless it is a status colour`() {
        assertTrue(
            "Color.kt not found at ${colorSource.absolutePath}",
            colorSource.isFile
        )

        val violations = mutableListOf<String>()
        colorSource.readLines().forEach { line ->
            literal.findAll(line).forEach { match ->
                val name = match.groupValues[1]
                // Drop the alpha byte; compare RGB only.
                val rgb = match.groupValues[2].substring(2)
                val channels = (0 until 3).map { i ->
                    rgb.substring(i * 2, i * 2 + 2).toInt(16)
                }
                val spread = channels.max() - channels.min()
                val allowedChroma = STATUS_NAMES.any { name.contains(it, ignoreCase = true) }
                if (spread > MAX_NEUTRAL_SPREAD && !allowedChroma) {
                    violations += "$name = #$rgb (channel spread $spread)"
                }
            }
        }

        assertTrue(
            "Chromatic colours found in the monochrome palette:\n" +
                violations.joinToString("\n"),
            violations.isEmpty()
        )
    }

    @Test
    fun `legacy brand tokens are removed`() {
        assertTrue(
            "Color.kt not found at ${colorSource.absolutePath}",
            colorSource.isFile
        )
        val source = colorSource.readText()
        val legacyTokens = listOf(
            "ColorTeal", "ColorNavbar", "ColorCover", "ColorPrimaryBrand",
            "ColorLightBackground", "ColorSubText", "ColorStroke", "ColorGold",
            "ColorBlue", "ColorPurple"
        )
        val found = legacyTokens.filter { source.contains(it) }
        assertTrue("Legacy brand tokens still present: $found", found.isEmpty())
    }

    private companion object {
        /** Allowed chromatic names (plus their containers / on* variants). */
        val STATUS_NAMES = listOf("error", "success", "warning", "heart")

        /** Max difference between the R, G and B channels for a neutral gray. */
        const val MAX_NEUTRAL_SPREAD = 8
    }
}

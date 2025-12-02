package org.jetbrains.plugins.template.quicklinks.model

/**
 * Represents a quick link configuration.
 *
 * @property name The display name of the link (used in "Open <name>" command)
 * @property url The URL template that may contain placeholders like {USER}, {REPO}
 */
data class QuickLink(
    var name: String = "",
    var url: String = ""
) {
    /**
     * Extracts placeholder names from the URL.
     * Placeholders are enclosed in curly braces, e.g., {USER}, {REPO}
     */
    fun getPlaceholders(): Set<String> {
        val regex = Regex("\\{([^}]+)}")
        return regex.findAll(url).map { it.groupValues[1] }.toSet()
    }

    /**
     * Resolves the URL by replacing placeholders with their values.
     *
     * @param placeholderValues Map of placeholder names to their values
     * @return The resolved URL with all placeholders replaced
     */
    fun resolveUrl(placeholderValues: Map<String, String>): String {
        var resolvedUrl = url
        for ((placeholder, value) in placeholderValues) {
            resolvedUrl = resolvedUrl.replace("{$placeholder}", value)
        }
        return resolvedUrl
    }
}

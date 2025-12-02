package org.jetbrains.plugins.template.quicklinks

import org.jetbrains.plugins.template.quicklinks.model.QuickLink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickLinkTest {

    @Test
    fun testGetPlaceholders() {
        val link = QuickLink("GitHub", "https://github.com/{USER}/{REPO}")
        val placeholders = link.getPlaceholders()
        
        assertEquals(2, placeholders.size)
        assertTrue(placeholders.contains("USER"))
        assertTrue(placeholders.contains("REPO"))
    }

    @Test
    fun testGetPlaceholdersEmpty() {
        val link = QuickLink("Google", "https://google.com")
        val placeholders = link.getPlaceholders()
        
        assertTrue(placeholders.isEmpty())
    }

    @Test
    fun testResolveUrl() {
        val link = QuickLink("GitHub", "https://github.com/{USER}/{REPO}")
        val placeholderValues = mapOf(
            "USER" to "myuser",
            "REPO" to "myrepo"
        )
        
        val resolvedUrl = link.resolveUrl(placeholderValues)
        
        assertEquals("https://github.com/myuser/myrepo", resolvedUrl)
    }

    @Test
    fun testResolveUrlPartialPlaceholders() {
        val link = QuickLink("GitHub", "https://github.com/{USER}/{REPO}")
        val placeholderValues = mapOf(
            "USER" to "myuser"
        )
        
        val resolvedUrl = link.resolveUrl(placeholderValues)
        
        assertEquals("https://github.com/myuser/{REPO}", resolvedUrl)
    }

    @Test
    fun testResolveUrlNoPlaceholders() {
        val link = QuickLink("Google", "https://google.com")
        val placeholderValues = mapOf(
            "USER" to "myuser"
        )
        
        val resolvedUrl = link.resolveUrl(placeholderValues)
        
        assertEquals("https://google.com", resolvedUrl)
    }

    @Test
    fun testCopy() {
        val original = QuickLink("GitHub", "https://github.com/{USER}/{REPO}")
        val copy = original.copy()
        
        assertEquals(original.name, copy.name)
        assertEquals(original.url, copy.url)
        
        // Verify they are different objects
        copy.name = "Modified"
        assertEquals("GitHub", original.name)
        assertEquals("Modified", copy.name)
    }

    @Test
    fun testMultiplePlaceholdersSameName() {
        val link = QuickLink("Test", "https://example.com/{USER}/profile/{USER}/settings")
        val placeholders = link.getPlaceholders()
        
        // Should only contain unique placeholders
        assertEquals(1, placeholders.size)
        assertTrue(placeholders.contains("USER"))
        
        val resolved = link.resolveUrl(mapOf("USER" to "john"))
        assertEquals("https://example.com/john/profile/john/settings", resolved)
    }
}

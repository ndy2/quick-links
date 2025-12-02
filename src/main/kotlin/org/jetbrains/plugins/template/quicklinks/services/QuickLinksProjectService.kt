package org.jetbrains.plugins.template.quicklinks.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.template.quicklinks.model.QuickLink

/**
 * Project-level service for managing project-specific quick links and placeholder values.
 */
@Service(Service.Level.PROJECT)
@State(
    name = "QuickLinksProjectSettings",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class QuickLinksProjectService(private val project: Project) : PersistentStateComponent<QuickLinksProjectService.State> {

    data class State(
        var links: MutableList<QuickLink> = mutableListOf(),
        var placeholderValues: MutableMap<String, String> = mutableMapOf()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    /**
     * Returns all project-specific quick links.
     */
    fun getLinks(): List<QuickLink> = myState.links.toList()

    /**
     * Sets the project-specific quick links.
     */
    fun setLinks(links: List<QuickLink>) {
        myState.links = links.map { it.copy() }.toMutableList()
    }

    /**
     * Adds a new project-specific quick link.
     */
    fun addLink(link: QuickLink) {
        myState.links.add(link.copy())
    }

    /**
     * Removes a project-specific quick link by name.
     */
    fun removeLink(name: String) {
        myState.links.removeIf { it.name == name }
    }

    /**
     * Returns all placeholder values for this project.
     */
    fun getPlaceholderValues(): Map<String, String> = myState.placeholderValues.toMap()

    /**
     * Sets placeholder values for this project.
     */
    fun setPlaceholderValues(values: Map<String, String>) {
        myState.placeholderValues = values.toMutableMap()
    }

    /**
     * Gets a specific placeholder value.
     */
    fun getPlaceholderValue(name: String): String? = myState.placeholderValues[name]

    /**
     * Sets a specific placeholder value.
     */
    fun setPlaceholderValue(name: String, value: String) {
        myState.placeholderValues[name] = value
    }

    /**
     * Returns all links (both global and project-specific) with placeholders resolved
     * using project placeholder values.
     */
    fun getAllLinksResolved(): List<Pair<QuickLink, String>> {
        val globalLinks = QuickLinksApplicationService.getInstance().getLinks()
        val projectLinks = getLinks()
        val allLinks = globalLinks + projectLinks
        val placeholderValues = getPlaceholderValues()
        
        return allLinks.map { link ->
            link to link.resolveUrl(placeholderValues)
        }
    }

    /**
     * Returns all links (both global and project-specific).
     */
    fun getAllLinks(): List<QuickLink> {
        val globalLinks = QuickLinksApplicationService.getInstance().getLinks()
        val projectLinks = getLinks()
        return globalLinks + projectLinks
    }

    /**
     * Collects all placeholders from all links (global and project-specific).
     */
    fun getAllPlaceholders(): Set<String> {
        return getAllLinks().flatMap { it.getPlaceholders() }.toSet()
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): QuickLinksProjectService =
            project.getService(QuickLinksProjectService::class.java)
    }
}

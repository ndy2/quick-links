package org.jetbrains.plugins.template.quicklinks.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.plugins.template.quicklinks.model.QuickLink

/**
 * Application-level service for managing global quick links.
 * These links are shared across all projects.
 */
@Service(Service.Level.APP)
@State(
    name = "QuickLinksApplicationSettings",
    storages = [Storage("quickLinks.xml")]
)
class QuickLinksApplicationService : PersistentStateComponent<QuickLinksApplicationService.State> {

    data class State(
        var links: MutableList<QuickLink> = mutableListOf()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    /**
     * Returns all global quick links.
     */
    fun getLinks(): List<QuickLink> = myState.links.toList()

    /**
     * Sets the global quick links.
     */
    fun setLinks(links: List<QuickLink>) {
        myState.links = links.map { it.copy() }.toMutableList()
    }

    /**
     * Adds a new global quick link.
     */
    fun addLink(link: QuickLink) {
        myState.links.add(link.copy())
    }

    /**
     * Removes a global quick link by name.
     */
    fun removeLink(name: String) {
        myState.links.removeIf { it.name == name }
    }

    companion object {
        @JvmStatic
        fun getInstance(): QuickLinksApplicationService =
            ApplicationManager.getApplication().getService(QuickLinksApplicationService::class.java)
    }
}

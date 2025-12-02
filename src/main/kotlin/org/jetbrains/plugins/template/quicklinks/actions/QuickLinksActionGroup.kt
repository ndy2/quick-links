package org.jetbrains.plugins.template.quicklinks.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.plugins.template.quicklinks.services.QuickLinksApplicationService
import org.jetbrains.plugins.template.quicklinks.services.QuickLinksProjectService

/**
 * Dynamic action group that creates "Open <name>" actions for each configured quick link.
 * The group is populated with actions for both global and project-specific links.
 */
class QuickLinksActionGroup : ActionGroup("Quick Links", "Open quick links", null) {

    init {
        isPopup = true
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (e == null) return emptyArray()
        
        val project = e.project ?: return emptyArray()
        
        val globalLinks = QuickLinksApplicationService.getInstance().getLinks()
        val projectLinks = QuickLinksProjectService.getInstance(project).getLinks()
        
        val actions = mutableListOf<AnAction>()
        
        // Add global links
        if (globalLinks.isNotEmpty()) {
            for (link in globalLinks) {
                actions.add(OpenQuickLinkAction(link))
            }
        }
        
        // Add separator if both global and project links exist
        if (globalLinks.isNotEmpty() && projectLinks.isNotEmpty()) {
            actions.add(com.intellij.openapi.actionSystem.Separator.create("Project Links"))
        }
        
        // Add project-specific links
        for (link in projectLinks) {
            actions.add(OpenQuickLinkAction(link))
        }
        
        return actions.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        
        val globalLinks = QuickLinksApplicationService.getInstance().getLinks()
        val projectLinks = QuickLinksProjectService.getInstance(project).getLinks()
        
        e.presentation.isEnabledAndVisible = globalLinks.isNotEmpty() || projectLinks.isNotEmpty()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}

package org.jetbrains.plugins.template.quicklinks.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.jetbrains.plugins.template.quicklinks.model.QuickLink
import org.jetbrains.plugins.template.quicklinks.services.QuickLinksProjectService

/**
 * Action that opens a quick link in the browser.
 * Placeholders in the URL are resolved using project-specific placeholder values.
 */
class OpenQuickLinkAction(
    private val link: QuickLink
) : AnAction("Open ${link.name}", "Open ${link.name} in browser", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val projectService = QuickLinksProjectService.getInstance(project)
        val placeholderValues = projectService.getPlaceholderValues()
        
        // Check for missing placeholders
        val missingPlaceholders = link.getPlaceholders().filter { !placeholderValues.containsKey(it) || placeholderValues[it].isNullOrEmpty() }
        
        if (missingPlaceholders.isNotEmpty()) {
            Messages.showWarningDialog(
                project,
                "Missing placeholder values: ${missingPlaceholders.joinToString(", ")}.\n\nPlease configure them in Settings > Tools > Quick Links.",
                "Missing Placeholder Values"
            )
            return
        }
        
        val resolvedUrl = link.resolveUrl(placeholderValues)
        BrowserUtil.browse(resolvedUrl)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}

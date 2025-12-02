package org.jetbrains.plugins.template.quicklinks.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import org.jetbrains.plugins.template.quicklinks.model.QuickLink
import org.jetbrains.plugins.template.quicklinks.services.QuickLinksProjectService
import javax.swing.JComponent
import javax.swing.table.DefaultTableModel

/**
 * Project-level settings configurable for managing project-specific quick links
 * and placeholder values.
 */
class QuickLinksProjectConfigurable(private val project: Project) : Configurable {

    private var panel: DialogPanel? = null
    private var linksTableModel: DefaultTableModel? = null
    private var placeholdersTableModel: DefaultTableModel? = null
    private val linksData: MutableList<QuickLink> = mutableListOf()
    private val placeholderData: MutableMap<String, String> = mutableMapOf()

    override fun getDisplayName(): String = "Quick Links"

    override fun createComponent(): JComponent {
        val service = QuickLinksProjectService.getInstance(project)
        
        // Load links data
        linksData.clear()
        linksData.addAll(service.getLinks().map { it.copy() })
        
        // Load placeholder values
        placeholderData.clear()
        placeholderData.putAll(service.getPlaceholderValues())

        // Create links table
        linksTableModel = object : DefaultTableModel(arrayOf("Name", "URL"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = true
        }

        for (link in linksData) {
            linksTableModel!!.addRow(arrayOf(link.name, link.url))
        }

        val linksTable = JBTable(linksTableModel)
        linksTable.setShowGrid(true)
        linksTable.columnModel.getColumn(0).preferredWidth = 150
        linksTable.columnModel.getColumn(1).preferredWidth = 400

        linksTableModel!!.addTableModelListener { e ->
            if (e.firstRow >= 0 && e.firstRow < linksData.size) {
                val row = e.firstRow
                when (e.column) {
                    0 -> linksData[row].name = linksTableModel!!.getValueAt(row, 0) as String
                    1 -> linksData[row].url = linksTableModel!!.getValueAt(row, 1) as String
                }
            }
        }

        val linksDecorator = ToolbarDecorator.createDecorator(linksTable)
            .setAddAction {
                val newLink = QuickLink("", "")
                linksData.add(newLink)
                linksTableModel!!.addRow(arrayOf("", ""))
                linksTable.editCellAt(linksTableModel!!.rowCount - 1, 0)
            }
            .setRemoveAction {
                val selectedRow = linksTable.selectedRow
                if (selectedRow >= 0) {
                    linksData.removeAt(selectedRow)
                    linksTableModel!!.removeRow(selectedRow)
                }
            }
            .disableUpDownActions()

        // Create placeholders table
        placeholdersTableModel = object : DefaultTableModel(arrayOf("Placeholder", "Value"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = column == 1
        }

        // Populate placeholders from all links (global + project)
        val allPlaceholders = service.getAllPlaceholders()
        for (placeholder in allPlaceholders.sorted()) {
            placeholdersTableModel!!.addRow(arrayOf(placeholder, placeholderData[placeholder] ?: ""))
        }

        val placeholdersTable = JBTable(placeholdersTableModel)
        placeholdersTable.setShowGrid(true)
        placeholdersTable.columnModel.getColumn(0).preferredWidth = 150
        placeholdersTable.columnModel.getColumn(1).preferredWidth = 400

        placeholdersTableModel!!.addTableModelListener { e ->
            if (e.firstRow >= 0 && e.column == 1) {
                val placeholder = placeholdersTableModel!!.getValueAt(e.firstRow, 0) as String
                val value = placeholdersTableModel!!.getValueAt(e.firstRow, 1) as String
                placeholderData[placeholder] = value
            }
        }

        val placeholdersDecorator = ToolbarDecorator.createDecorator(placeholdersTable)
            .setAddAction {
                placeholdersTableModel!!.addRow(arrayOf("", ""))
                placeholdersTable.editCellAt(placeholdersTableModel!!.rowCount - 1, 0)
            }
            .setRemoveAction {
                val selectedRow = placeholdersTable.selectedRow
                if (selectedRow >= 0) {
                    val placeholder = placeholdersTableModel!!.getValueAt(selectedRow, 0) as String
                    placeholderData.remove(placeholder)
                    placeholdersTableModel!!.removeRow(selectedRow)
                }
            }
            .disableUpDownActions()

        // Make the placeholder name editable for custom placeholders
        placeholdersTableModel = object : DefaultTableModel(arrayOf("Placeholder", "Value"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = true
        }

        // Repopulate
        for (placeholder in allPlaceholders.sorted()) {
            placeholdersTableModel!!.addRow(arrayOf(placeholder, placeholderData[placeholder] ?: ""))
        }
        // Also add placeholders that might not be from links
        for ((placeholder, value) in placeholderData) {
            if (placeholder !in allPlaceholders) {
                placeholdersTableModel!!.addRow(arrayOf(placeholder, value))
            }
        }

        val placeholdersTable2 = JBTable(placeholdersTableModel)
        placeholdersTable2.setShowGrid(true)
        placeholdersTable2.columnModel.getColumn(0).preferredWidth = 150
        placeholdersTable2.columnModel.getColumn(1).preferredWidth = 400

        placeholdersTableModel!!.addTableModelListener { e ->
            if (e.firstRow >= 0 && e.firstRow < placeholdersTableModel!!.rowCount) {
                val row = e.firstRow
                val placeholder = placeholdersTableModel!!.getValueAt(row, 0) as String
                val value = placeholdersTableModel!!.getValueAt(row, 1) as String
                if (placeholder.isNotBlank()) {
                    placeholderData[placeholder] = value
                }
            }
        }

        val placeholdersDecorator2 = ToolbarDecorator.createDecorator(placeholdersTable2)
            .setAddAction {
                placeholdersTableModel!!.addRow(arrayOf("", ""))
                placeholdersTable2.editCellAt(placeholdersTableModel!!.rowCount - 1, 0)
            }
            .setRemoveAction {
                val selectedRow = placeholdersTable2.selectedRow
                if (selectedRow >= 0) {
                    val placeholder = placeholdersTableModel!!.getValueAt(selectedRow, 0) as String
                    placeholderData.remove(placeholder)
                    placeholdersTableModel!!.removeRow(selectedRow)
                }
            }
            .disableUpDownActions()

        panel = panel {
            group("Project Quick Links") {
                row {
                    label("Configure quick links specific to this project.")
                }
                row {
                    label("URLs can contain placeholders like {USER}, {REPO}.")
                }
                row {
                    cell(JBScrollPane(linksDecorator.createPanel()))
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
            group("Placeholder Values") {
                row {
                    label("Set placeholder values for this project. These apply to both global and project links.")
                }
                row {
                    cell(JBScrollPane(placeholdersDecorator2.createPanel()))
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }

        return panel!!
    }

    override fun isModified(): Boolean {
        val service = QuickLinksProjectService.getInstance(project)
        
        val currentLinks = service.getLinks()
        val currentPlaceholders = service.getPlaceholderValues()
        
        if (currentLinks.size != linksData.size) return true
        if (currentPlaceholders != placeholderData.filter { it.value.isNotBlank() }) return true
        
        for (i in currentLinks.indices) {
            if (currentLinks[i].name != linksData[i].name ||
                currentLinks[i].url != linksData[i].url) {
                return true
            }
        }
        
        return false
    }

    override fun apply() {
        val service = QuickLinksProjectService.getInstance(project)
        
        // Filter out empty links
        val validLinks = linksData.filter { it.name.isNotBlank() && it.url.isNotBlank() }
        service.setLinks(validLinks)
        
        // Filter out empty placeholder values
        val validPlaceholders = placeholderData.filter { it.key.isNotBlank() }
        service.setPlaceholderValues(validPlaceholders)
    }

    override fun reset() {
        val service = QuickLinksProjectService.getInstance(project)
        
        linksData.clear()
        linksData.addAll(service.getLinks().map { it.copy() })
        
        placeholderData.clear()
        placeholderData.putAll(service.getPlaceholderValues())
        
        linksTableModel?.let { model ->
            while (model.rowCount > 0) {
                model.removeRow(0)
            }
            for (link in linksData) {
                model.addRow(arrayOf(link.name, link.url))
            }
        }
        
        placeholdersTableModel?.let { model ->
            while (model.rowCount > 0) {
                model.removeRow(0)
            }
            val allPlaceholders = service.getAllPlaceholders()
            for (placeholder in allPlaceholders.sorted()) {
                model.addRow(arrayOf(placeholder, placeholderData[placeholder] ?: ""))
            }
        }
    }

    override fun disposeUIResources() {
        panel = null
        linksTableModel = null
        placeholdersTableModel = null
    }
}

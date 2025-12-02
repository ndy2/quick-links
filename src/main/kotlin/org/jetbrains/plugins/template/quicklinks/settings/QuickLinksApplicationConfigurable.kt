package org.jetbrains.plugins.template.quicklinks.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import org.jetbrains.plugins.template.quicklinks.model.QuickLink
import org.jetbrains.plugins.template.quicklinks.services.QuickLinksApplicationService
import javax.swing.JComponent
import javax.swing.table.DefaultTableModel

/**
 * Application-level settings configurable for managing global quick links.
 */
class QuickLinksApplicationConfigurable : Configurable {

    private var panel: DialogPanel? = null
    private var linksTableModel: DefaultTableModel? = null
    private val linksData: MutableList<QuickLink> = mutableListOf()

    override fun getDisplayName(): String = "Quick Links (Global)"

    override fun createComponent(): JComponent {
        val service = QuickLinksApplicationService.getInstance()
        linksData.clear()
        linksData.addAll(service.getLinks().map { it.copy() })

        linksTableModel = object : DefaultTableModel(arrayOf("Name", "URL"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = true
        }

        // Load existing data
        for (link in linksData) {
            linksTableModel!!.addRow(arrayOf(link.name, link.url))
        }

        val linksTable = JBTable(linksTableModel)
        linksTable.setShowGrid(true)
        linksTable.columnModel.getColumn(0).preferredWidth = 150
        linksTable.columnModel.getColumn(1).preferredWidth = 400

        // Add listener to sync table edits with linksData
        linksTableModel!!.addTableModelListener { e ->
            if (e.firstRow >= 0 && e.firstRow < linksData.size) {
                val row = e.firstRow
                when (e.column) {
                    0 -> linksData[row].name = linksTableModel!!.getValueAt(row, 0) as String
                    1 -> linksData[row].url = linksTableModel!!.getValueAt(row, 1) as String
                }
            }
        }

        val decorator = ToolbarDecorator.createDecorator(linksTable)
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

        panel = panel {
            group("Global Quick Links") {
                row {
                    label("Configure quick links that are available across all projects.")
                }
                row {
                    label("URLs can contain placeholders like {USER}, {REPO} which are resolved per project.")
                }
                row {
                    cell(JBScrollPane(decorator.createPanel()))
                        .align(Align.FILL)
                        .resizableColumn()
                }.resizableRow()
            }
        }

        return panel!!
    }

    override fun isModified(): Boolean {
        val service = QuickLinksApplicationService.getInstance()
        val currentLinks = service.getLinks()
        
        if (currentLinks.size != linksData.size) return true
        
        for (i in currentLinks.indices) {
            if (currentLinks[i].name != linksData[i].name ||
                currentLinks[i].url != linksData[i].url) {
                return true
            }
        }
        return false
    }

    override fun apply() {
        val service = QuickLinksApplicationService.getInstance()
        // Filter out empty links
        val validLinks = linksData.filter { it.name.isNotBlank() && it.url.isNotBlank() }
        service.setLinks(validLinks)
    }

    override fun reset() {
        val service = QuickLinksApplicationService.getInstance()
        linksData.clear()
        linksData.addAll(service.getLinks().map { it.copy() })
        
        linksTableModel?.let { model ->
            while (model.rowCount > 0) {
                model.removeRow(0)
            }
            for (link in linksData) {
                model.addRow(arrayOf(link.name, link.url))
            }
        }
    }

    override fun disposeUIResources() {
        panel = null
        linksTableModel = null
    }
}

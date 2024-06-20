package androidstudio.tools.missed.features.customcommand.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.features.customcommand.presenter.confirmationdialog.ConfirmationDialog
import androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog.CustomCommandDialog
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.colored
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import icons.CollaborationToolsIcons
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jdesktop.swingx.HorizontalLayout
import org.jetbrains.annotations.VisibleForTesting
import org.jetbrains.kotlin.idea.caches.project.NotUnderContentRootModuleInfo.project
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class CustomCommandView(
    override val viewModel: CustomCommandViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<CustomCommandViewModel>(viewModel) {

    private val itemsPanel: JPanel = JPanel()
    private lateinit var addButton: JButton

    init {
        setContent(
            title = resourceManager.string("customCommandTitle"),
            mainView(),
            itemsPanel
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
    }

    @VisibleForTesting
    fun mainView(): JPanel {
        return panel {
            row {
                text(resourceManager.string("customCommandDescription")).gap(RightGap.SMALL)
                browserLink(
                    resourceManager.string("help"),
                    "https://github.com/rasoulmiri/AndroidStudioToolsMissed/wiki/Custom-Command-Demo"
                )
            }

            row {
                addButton = button(resourceManager.string("add")) {
                    showDialog(CustomCommand.EMPTY)
                }.component
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    @Suppress("MagicNumber")
    private fun createItemView(customCommand: CustomCommand): JPanel {
        val iconSize = 30
        val itemPanel = JPanel(BorderLayout())
        val textLabel = JLabel(" ${customCommand.name}", SwingConstants.LEFT)

        val buttonsPanel = JPanel(HorizontalLayout())
        val runButton = JButton().apply {
            icon = AllIcons.Actions.Execute
            preferredSize = Dimension(iconSize, iconSize)
            addActionListener {
                viewModel.executeCommand(customCommand)
            }
        }
        val editButton = JButton().apply {
            icon = AllIcons.Actions.Edit
            preferredSize = Dimension(iconSize, iconSize)
            addActionListener {
                showDialog(customCommand)
            }
        }
        val deleteButton = JButton().apply {
            icon = CollaborationToolsIcons.Delete.colored(Color.WHITE)
            preferredSize = Dimension(iconSize, iconSize)
            addActionListener {
                val dialog = ConfirmationDialog(
                    project,
                    resourceManager.string("customCommandDeleteDialogTitle"),
                    resourceManager.string("customCommandDeleteDialogDescription", customCommand.name ?: ""),
                    resourceManager.string("customCommandDeleteDialogDeleteLabel"),
                    resourceManager.string("customCommandCancelDialogCancelLabel")
                )
                if (dialog.showAndGet()) {
                    viewModel.deleteById(customCommand.id)
                }
            }
        }

        buttonsPanel.add(runButton)
        buttonsPanel.add(editButton)
        buttonsPanel.add(deleteButton)

        itemPanel.add(buttonsPanel, BorderLayout.WEST)
        itemPanel.add(textLabel, BorderLayout.EAST)

        return itemPanel
    }

    private fun showDialog(customCommand: CustomCommand) {
        val customCommandDialog = CustomCommandDialog(resourceManager)
        customCommandDialog.show(customCommand)
        when (customCommandDialog.exitCode) {
            DialogWrapper.OK_EXIT_CODE -> {
                viewModel.updateData()
            }
        }
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }

        viewScope.launch {
            viewModel.customCommandsStateFlow.collect {
                itemsPanel.removeAll()
                itemsPanel.add(
                    panel {
                        it.forEachIndexed { _, commandData ->
                            row {
                                cell(createItemView(commandData))
                            }
                        }
                    }
                )
                itemsPanel.updateUI()
            }
        }
    }
}

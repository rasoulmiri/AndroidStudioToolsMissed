package androidstudio.tools.missed.features.dozeandstandby.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.features.dozeandstandby.presenter.whitelistdialog.WhiteListDialog
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.FONT_COMMENT_SIZE
import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JButton
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JPanel

class DozeAndStandbyView(
    override val viewModel: DozeAndStandbyViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<DozeAndStandbyViewModel>(viewModel) {

    // Doze
    private val dozeStateTextLabel = JLabel(resourceManager.string("deviceStateStatus"))
    private val dozeButton = OnOffButton()
    private val dozeButtonItemListener = ItemListener { ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            viewModel.setStateDozeMode(isActive = true)
        } else if (ev.stateChange == ItemEvent.DESELECTED) {
            viewModel.setStateDozeMode(isActive = false)
        }
    }

    // Standby
    private lateinit var standbyPackageIdLabel: Cell<JEditorPane>
    private val standbyButton = OnOffButton()
    private val standbyButtonItemListener = ItemListener { ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            viewModel.setStateStandbyMode(isActive = true)
        } else if (ev.stateChange == ItemEvent.DESELECTED) {
            viewModel.setStateStandbyMode(isActive = false)
        }
    }

    // WhiteList
    private lateinit var whiteListPackageIdLabel: Cell<JEditorPane>
    private lateinit var whiteListAddButton: JButton
    private lateinit var whiteListRemoveButton: JButton

    init {
        setContent(
            title = resourceManager.string("dozeAndStandbyTitle"),
            dozeView(),
            standbyView(),
            whiteListView()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
        viewModel.getStateDozeMode()
        viewModel.getStateStandbyMode()
        viewModel.fetchAllWhiteList()
    }

    private fun dozeView(): JPanel {
        return panel {
            group(resourceManager.string("doze")) {
                row {
                    label(resourceManager.string("dozeDescription"))
                    browserLink(resourceManager.string("moreInformation"), "").component
                    browserLink(resourceManager.string("demo"), "").component
                }

                row {
                    label(resourceManager.string("dozeMode"))
                    dozeButton.addItemListener(dozeButtonItemListener)
                    cell(dozeButton)
                }.layout(RowLayout.PARENT_GRID)

                row {
                    dozeStateTextLabel.font =
                        Font(dozeStateTextLabel.font.name, dozeStateTextLabel.font.style, FONT_COMMENT_SIZE)
                    dozeStateTextLabel.foreground = JBColor.GRAY
                    cell(dozeStateTextLabel)
                }
            }
        }
    }

    private fun standbyView(): JPanel {
        return panel {
            group(resourceManager.string("standby")) {
                row {
                    label(resourceManager.string("standbyDescription"))
                    browserLink(resourceManager.string("moreInformation"), "").component
                    browserLink(resourceManager.string("demo"), "").component
                }

                row {
                    standbyPackageIdLabel = comment(resourceManager.string("packageId"))
                    cell(standbyPackageIdLabel.component)
                }

                row {
                    label(resourceManager.string("standby"))
                    standbyButton.addItemListener(standbyButtonItemListener)
                    cell(standbyButton)
                }.layout(RowLayout.PARENT_GRID)
            }
        }
    }

    private fun whiteListView(): JPanel {
        return panel {
            group(resourceManager.string("whiteListTitle")) {
                row {
                    label(resourceManager.string("whiteListDescription"))
                    browserLink(resourceManager.string("moreInformation"), "").component
                    browserLink(resourceManager.string("demo"), "").component
                }

                row {
                    whiteListPackageIdLabel = comment(resourceManager.string("packageId"))
                    cell(whiteListPackageIdLabel.component)
                }

                row {
                    // Add
                    whiteListAddButton = button(resourceManager.string("whiteListAdd")) {
                        viewModel.addToWhiteList()
                    }.component.apply {
                        icon = AllIcons.General.Add
                    }

                    // Remove
                    whiteListRemoveButton = button(resourceManager.string("whiteListRemove")) {
                        viewModel.removeFromWhiteList()
                    }.component.apply {
                        icon = AllIcons.General.Remove
                    }
                }.layout(RowLayout.PARENT_GRID)

                row {
                    button(resourceManager.string("whiteListShowAll")) {
                        viewModel.fetchAllWhiteList()
                        WhiteListDialog(viewModel.whiteListPackageIds).show()
                    }.component
                }
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
            viewModel.dozeStateFlow.collectLatest { stateText ->
                dozeStateTextLabel.text = resourceManager.string("deviceStateStatus", stateText)
            }
        }

        viewScope.launch {
            viewModel.dozeStateStateFlow.collectLatest { isActive ->
                dozeButton.removeItemListener(dozeButtonItemListener)
                dozeButton.isSelected = isActive
                dozeButton.addItemListener(dozeButtonItemListener)
            }
        }

        viewScope.launch {
            viewModel.standbyStateStateFlow.collectLatest { isActive ->
                standbyButton.removeItemListener(standbyButtonItemListener)
                standbyButton.isSelected = isActive
                standbyButton.addItemListener(standbyButtonItemListener)
            }
        }

        viewScope.launch {
            viewModel.packageIdSelectedStateFlow.collectLatest { packageId ->
                val text = resourceManager.string("packageId") + packageId
                standbyPackageIdLabel.component.text = text
                whiteListPackageIdLabel.component.text = text
                viewModel.getStateStandbyMode()
                viewModel.fetchAllWhiteList()
            }
        }

        viewScope.launch {
            viewModel.packageIdInWhiteListStateFlow.collectLatest { packageIdInWhiteList ->
                whiteListAddButton.isEnabled = !packageIdInWhiteList
                whiteListRemoveButton.isEnabled = packageIdInWhiteList
            }
        }
    }
}

package androidstudio.tools.missed.features.battery.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.debounce
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.event.ChangeListener
import javax.swing.text.NumberFormatter

private const val BATTERY_INITIAL_VALUE = 0
private const val BATTERY_MIN_VALUE = 0
private const val BATTERY_MAX_VALUE = 100
private const val BATTERY_STEP_SIZE = 1

class BatteryView(
    override val viewModel: BatteryViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<BatteryViewModel>(viewModel) {

    // ChargerConnection
    private val chargerConnectionLabel = JLabel(resourceManager.string("chargerConnection"))
    private lateinit var chargerConnectionComment: Cell<JEditorPane>
    private val chargerConnectionButton = OnOffButton()
    private val chargerConnectionButtonItemListener = ItemListener { ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            viewModel.setStateChargerConnection(isConnect = true)
        } else if (ev.stateChange == ItemEvent.DESELECTED) {
            viewModel.setStateChargerConnection(isConnect = false)
        }
    }

    // Battery Level
    private val batteryLevelSpinner =
        JBIntSpinner(BATTERY_INITIAL_VALUE, BATTERY_MIN_VALUE, BATTERY_MAX_VALUE, BATTERY_STEP_SIZE)
    private val debounceBatteryLevelChange =
        debounce(scope = viewModel.viewModelScope, destinationFunction = viewModel::setBatteryLevel)
    private val batteryLevelSpinnerChangeListener = ChangeListener {
        debounceBatteryLevelChange(((it.source as JSpinner).value) as Int)
    }

    // PowerSavingMode
    private val powerSavingModeLabel = JLabel(resourceManager.string("powerSavingMode"))
    private lateinit var powerSavingModeComment: Cell<JEditorPane>
    private val powerSavingModeButton = OnOffButton()
    private val powerSavingModeButtonItemListener = ItemListener { ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            viewModel.setStatePowerSavingMode(isActive = true)
        } else if (ev.stateChange == ItemEvent.DESELECTED) {
            viewModel.setStatePowerSavingMode(isActive = false)
        }
    }

    init {
        setContent(
            title = resourceManager.string("batteryTitle"),
            batteryDetailView(),
            powerSavingModeView()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
        viewModel.getBatteryState()
    }

    private fun batteryDetailView(): JPanel {
        return panel {
            row {
                cell(chargerConnectionLabel).gap(RightGap.SMALL)
                chargerConnectionComment = comment("")
                cell(chargerConnectionButton).gap(RightGap.COLUMNS)
                chargerConnectionButton.addItemListener(chargerConnectionButtonItemListener)
            }.layout(RowLayout.PARENT_GRID)

            row {
                label(resourceManager.string("batteryLevel"))
                val txt = (batteryLevelSpinner.editor as JSpinner.NumberEditor).textField
                (txt.formatter as NumberFormatter).allowsInvalid = false
                batteryLevelSpinner.addChangeListener(batteryLevelSpinnerChangeListener)
                cell(batteryLevelSpinner)
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    private fun powerSavingModeView(): JPanel {
        return panel {
                row {
                    cell(powerSavingModeLabel).gap(RightGap.SMALL)
                    powerSavingModeComment = comment("")
                    cell(powerSavingModeButton)
                    powerSavingModeButton.addItemListener(powerSavingModeButtonItemListener)
                }

                row {
                    button(resourceManager.string("resetBatterySetting")) {
                        viewModel.resetBatterySetting()
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
            viewModel.chargerConnectionStateFlow.collectLatest { isConnected ->

                chargerConnectionButton.removeItemListener(chargerConnectionButtonItemListener)
                chargerConnectionButton.isSelected = isConnected
                chargerConnectionButton.addItemListener(chargerConnectionButtonItemListener)

                chargerConnectionComment.component.text = if (isConnected) {
                    resourceManager.string("connected")
                } else {
                    resourceManager.string("disconnected")
                }
            }
        }

        viewScope.launch {
            viewModel.batteryLevelStateFlow.collectLatest { level ->
                batteryLevelSpinner.removeChangeListener(batteryLevelSpinnerChangeListener)
                batteryLevelSpinner.number = level
                batteryLevelSpinner.addChangeListener(batteryLevelSpinnerChangeListener)
            }
        }

        viewScope.launch {
            viewModel.powerSavingModeActiveStateFlow.collectLatest { isActive ->

                powerSavingModeButton.removeItemListener(powerSavingModeButtonItemListener)
                powerSavingModeButton.isSelected = isActive
                powerSavingModeButton.addItemListener(powerSavingModeButtonItemListener)

                powerSavingModeComment.component.text = if (isActive) {
                    resourceManager.string("active")
                } else {
                    resourceManager.string("deactive")
                }
            }
        }
    }
}

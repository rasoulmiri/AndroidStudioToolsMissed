package androidstudio.tools.missed.features.internetconnection.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JPanel

class InternetConnectionView(
    override val viewModel: InternetConnectionViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<InternetConnectionViewModel>(viewModel) {

    // AirplaneMode
    private val airplaneModeButton = OnOffButton()
    private val airplaneModeButtonItemListener = ItemListener { event ->
        when (event.stateChange) {
            ItemEvent.SELECTED -> viewModel.setStateAirplaneMode(isOn = true)
            ItemEvent.DESELECTED -> viewModel.setStateAirplaneMode(isOn = false)
        }
    }

    // Mobile Data
    private val mobileDataButton = OnOffButton()
    private val mobileDataButtonItemListener = ItemListener { event ->
        when (event.stateChange) {
            ItemEvent.SELECTED -> viewModel.setStateMobileData(isOn = true)
            ItemEvent.DESELECTED -> viewModel.setStateMobileData(isOn = false)
        }
    }

    // Wifi
    private val wifiButton = OnOffButton()
    private val wifiButtonItemListener = ItemListener { event ->
        when (event.stateChange) {
            ItemEvent.SELECTED -> viewModel.setStateWifi(isOn = true)
            ItemEvent.DESELECTED -> viewModel.setStateWifi(isOn = false)
        }
    }

    // Bluetooth
    private val bluetoothButton = OnOffButton()
    private val bluetoothButtonItemListener = ItemListener { event ->
        when (event.stateChange) {
            ItemEvent.SELECTED -> viewModel.setStateBluetooth(isOn = true)
            ItemEvent.DESELECTED -> viewModel.setStateBluetooth(isOn = false)
        }
    }

    init {
        setContent(
            title = resourceManager.string("internetConnectionTitle"),
            actionButtons()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
        viewModel.updateStates()
    }

    private fun actionButtons(): JPanel {
        return panel {
            row {
                label(resourceManager.string("airplaneModeTitle"))
                cell(airplaneModeButton).component.apply {
                    addItemListener(airplaneModeButtonItemListener)
                }
            }.layout(RowLayout.PARENT_GRID)

            row {
                label(resourceManager.string("mobileDataConnectionTitle"))
                cell(mobileDataButton).component.apply {
                    addItemListener(mobileDataButtonItemListener)
                }
            }.layout(RowLayout.PARENT_GRID)

            row {
                label(resourceManager.string("wifiConnectionTitle"))
                cell(wifiButton).component.apply {
                    addItemListener(wifiButtonItemListener)
                }
            }.layout(RowLayout.PARENT_GRID)

            row {
                label(resourceManager.string("bluetoothConnectionTitle"))
                cell(bluetoothButton).component.apply {
                    addItemListener(bluetoothButtonItemListener)
                }
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }

        viewScope.launch {
            viewModel.airplaneModeStateFlow.collectLatest { isActive ->
                airplaneModeButton.removeItemListener(airplaneModeButtonItemListener)
                airplaneModeButton.isSelected = isActive
                airplaneModeButton.addItemListener(airplaneModeButtonItemListener)
            }
        }
        viewScope.launch {
            viewModel.mobileDataStateFlow.collectLatest { isActive ->
                mobileDataButton.removeItemListener(mobileDataButtonItemListener)
                mobileDataButton.isSelected = isActive
                mobileDataButton.addItemListener(mobileDataButtonItemListener)
            }
        }
        viewScope.launch {
            viewModel.wifiStateFlow.collectLatest { isActive ->
                wifiButton.removeItemListener(wifiButtonItemListener)
                wifiButton.isSelected = isActive
                wifiButton.addItemListener(wifiButtonItemListener)
            }
        }
        viewScope.launch {
            viewModel.bluetoothStateFlow.collectLatest { isActive ->
                bluetoothButton.removeItemListener(bluetoothButtonItemListener)
                bluetoothButton.isSelected = isActive
                bluetoothButton.addItemListener(bluetoothButtonItemListener)
            }
        }
    }
}

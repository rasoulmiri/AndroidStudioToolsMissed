package androidstudio.tools.missed.features.deviceAndpackageid.presenter

import androidstudio.tools.missed.base.ViewMaster
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.model.DevicesComboBoxModel
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.model.DevicesComboBoxRenderer
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.model.PackageId
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.model.PackageIdComboBoxModel
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.model.PackageIdsComboBoxRenderer
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DIMENSION_8
import com.intellij.openapi.ui.ComboBox
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JPanel

class DevicesAndPackageIdView(
    override val viewModel: DevicesAndPackageIdsViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : ViewMaster<DevicesAndPackageIdsViewModel>(viewModel) {

    // Device
    private lateinit var devicesComboBox: JComboBox<DeviceInformation?>
    private var devicesComboBoxModel = DevicesComboBoxModel()

    // packageIds
    private lateinit var packageIdsComboBox: JComboBox<PackageId?>
    private var packageIdsComboBoxModel = PackageIdComboBoxModel()

    // Show all packageIds
    private lateinit var showAllPackageIdsCheckBox: JCheckBox

    init {
        devicesView()
        packageIdsOfDeviceView()
        initialObserve()
    }

    private fun devicesView() {
        // Devices combobox
        devicesComboBox = ComboBox(devicesComboBoxModel)
        devicesComboBox.renderer =
            DevicesComboBoxRenderer(resourceManager.string("connectAnAndroidDevice"))
        devicesComboBox.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                viewModel.setSelectedDevice(devicesComboBoxModel.selection)
            }
        }
        this.add(devicesComboBox)
    }

    private fun packageIdsOfDeviceView() {
        val packageIdsPanel = JPanel()
        packageIdsPanel.layout = BoxLayout(packageIdsPanel, BoxLayout.X_AXIS)

        packageIdsComboBox = ComboBox(packageIdsComboBoxModel)
        packageIdsComboBox.preferredSize = Dimension(DIMENSION_8, packageIdsComboBox.preferredSize.height)
        packageIdsComboBox.renderer =
            PackageIdsComboBoxRenderer(resourceManager.string("connectAnAndroidDevice"))
        packageIdsComboBox.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                viewModel.selectedPackageId((packageIdsComboBox.selectedItem as PackageId).title)
            }
        }
        packageIdsPanel.add(packageIdsComboBox)

        // Show all packageIds checkbox
        showAllPackageIdsCheckBox = JCheckBox(resourceManager.string("showAllApps"))
        showAllPackageIdsCheckBox.addItemListener {
            viewModel.showAllPackageIdsChangeEvent(it.stateChange == ItemEvent.SELECTED)
        }
        packageIdsPanel.add(showAllPackageIdsCheckBox)

        this.add(packageIdsPanel)
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }

        viewScope.launch {
            viewModel.devicesStateFlow.collect { devices ->
                devicesComboBoxModel.items.clear()
                devicesComboBoxModel.items.addAll(devices)
                devicesComboBoxModel.selectedItem = devicesComboBoxModel.items.getOrNull(0)
                devicesComboBox.updateUI()
            }
        }

        viewScope.launch {
            viewModel.packageIdsStateFlow.collect { packageIds ->
                packageIdsComboBoxModel.items.clear()
                packageIdsComboBoxModel.items.addAll(packageIds.map(::PackageId))
                packageIdsComboBoxModel.selectedItem = packageIdsComboBoxModel.items.getOrNull(0)
                packageIdsComboBox.updateUI()
            }
        }
    }
}

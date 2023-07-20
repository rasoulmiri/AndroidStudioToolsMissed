package androidstudio.tools.missed.features.deviceAndpackageid.presenter.model

import androidstudio.tools.missed.manager.device.model.DeviceInformation
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

internal class DevicesComboBoxModel : ComboBoxModel<DeviceInformation?>, AbstractListModel<DeviceInformation?>() {

    var items: ArrayList<DeviceInformation?> = arrayListOf()
    var selection: DeviceInformation? = null

    override fun getElementAt(index: Int): DeviceInformation? = items[index]

    override fun getSize() = items.size

    override fun setSelectedItem(item: Any?) {
        selection = (item as? DeviceInformation)
    }

    override fun getSelectedItem(): Any? = selection
}

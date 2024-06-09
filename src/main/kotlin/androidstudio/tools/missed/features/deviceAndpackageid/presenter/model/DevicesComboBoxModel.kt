package androidstudio.tools.missed.features.deviceAndpackageid.presenter.model

import androidstudio.tools.missed.manager.device.model.Device
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

internal class DevicesComboBoxModel : ComboBoxModel<Device?>, AbstractListModel<Device?>() {

    var items: ArrayList<Device?> = arrayListOf()
    var selection: Device? = null

    override fun getElementAt(index: Int): Device? = items[index]

    override fun getSize() = items.size

    override fun setSelectedItem(item: Any?) {
        selection = (item as? Device)
    }

    override fun getSelectedItem(): Any? = selection
}

package androidstudio.tools.missed.features.deviceAndpackageid.presenter.model

import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

internal class PackageIdComboBoxModel : ComboBoxModel<PackageId?>, AbstractListModel<PackageId?>() {

    var items: ArrayList<PackageId?> = arrayListOf()
    private var selection: PackageId? = null

    override fun getElementAt(index: Int): PackageId? = items[index]

    override fun getSize(): Int = items.size

    override fun setSelectedItem(item: Any?) {
        selection = item as? PackageId
    }

    override fun getSelectedItem(): Any? = selection
}

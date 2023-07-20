package androidstudio.tools.missed.features.permission.presenter.model

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

class PermissionComboBoxModel : ComboBoxModel<PermissionStateModel?>, AbstractListModel<PermissionStateModel?>() {

    var items = arrayListOf<PermissionStateModel?>()
    var selection: PermissionStateModel? = null

    override fun getElementAt(index: Int): PermissionStateModel? = items[index]

    override fun getSize(): Int = items.size

    override fun setSelectedItem(anItem: Any) {
        selection = anItem as PermissionStateModel
    }

    override fun getSelectedItem(): Any? = selection?.name
}

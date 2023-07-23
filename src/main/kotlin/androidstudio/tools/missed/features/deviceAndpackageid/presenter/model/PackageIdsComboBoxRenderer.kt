package androidstudio.tools.missed.features.deviceAndpackageid.presenter.model

import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

internal class PackageIdsComboBoxRenderer(private val placeHolder: String) : JLabel(), ListCellRenderer<Any?> {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        hasFocus: Boolean
    ): Component {
        text = if (index == -1 && value == null) {
            placeHolder
        } else {
            if ((value as? PackageId)?.title != null) {
                (value as? PackageId)?.title
            } else {
                value.toString()
            }
        }
        return this
    }
}

package androidstudio.tools.missed.features.permission.presenter.model

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class PermissionComboBoxRenderer(private var placeHolder: String) : JLabel(), ListCellRenderer<Any?> {

    fun setPlaceHolder(placeHolder: String) {
        this.placeHolder = placeHolder
    }

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        hasFocus: Boolean
    ): JComponent {
        val label = JLabel()

        label.text = if (index == -1 && value == null) {
            placeHolder
        } else {
            if (value is PermissionStateModel) {
                label.toolTipText = value.name

                val grantedStatus = if (value.isGranted) {
                    "<font color=\"green\">Granted</font>"
                } else {
                    "<font color=\"red\">Ungranted</font>"
                }
                val type = if (value.isRuntime) "Runtime" else "Install-time"
                val stateText = "$grantedStatus - $type"

                "<html>${value.name}<body><br/><font size=\"3\"color=\"gray\">$stateText</font></body></html>"
            } else {
                label.toolTipText = value.toString()
                value.toString()
            }
        }

        return label
    }
}

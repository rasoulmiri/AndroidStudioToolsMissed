package androidstudio.tools.missed.features.limitationmodes.presenter.whitelistdialog

import androidstudio.tools.missed.utils.DIMENSION_10
import androidstudio.tools.missed.utils.DIMENSION_15
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class WhiteListDialog(private val whiteList: ArrayList<String>?) : DialogWrapper(false) {

    init {
        init()
    }

    override fun isOK(): Boolean = false

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BorderLayout()

        val textArea = createAndConfigureTextArea()
        val scroll = createAndConfigureScrollPane(textArea)

        panel.add(scroll)
        return panel
    }

    private fun createAndConfigureTextArea(): JTextArea {
        val textArea = JTextArea()
        val strBuilder = StringBuilder()

        var group = ""

        val whiteListSorted = whiteList?.sortedWith(compareBy { it })
        whiteListSorted?.forEach {
            val splitText = it.split(",")
            if (group != splitText.first()) {
                group = splitText.first()
                strBuilder.appendLine().appendLine("${splitText.firstOrNull()?.uppercase()}:")
            }
            strBuilder.appendLine("     ${splitText.getOrNull(1)}")
        }
        textArea.text = strBuilder.toString().trim()
        textArea.wrapStyleWord = true
        textArea.lineWrap = true
        textArea.isOpaque = false
        textArea.isEditable = false
        textArea.isFocusable = false
        return textArea
    }

    private fun createAndConfigureScrollPane(textArea: JTextArea): JBScrollPane {
        val scroll = JBScrollPane(
            textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        )
        scroll.minimumSize = Dimension(DIMENSION_10, DIMENSION_15)
        return scroll
    }
}

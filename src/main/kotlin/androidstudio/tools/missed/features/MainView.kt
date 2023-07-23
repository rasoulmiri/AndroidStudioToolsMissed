package androidstudio.tools.missed.features

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.base.ViewMaster
import androidstudio.tools.missed.utils.GAP_0
import androidstudio.tools.missed.utils.GAP_1
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSeparator

class MainView(
    private val stickyHeaderView: ViewMaster<*>,
    private val views: List<CollapsibleGroupView<*>>
) : JPanel(BorderLayout(0, 0)) {

    init {
        stickHeaderView()
        scrollableView()
    }

    private fun stickHeaderView() {
        val jPanel = JPanel(
            VerticalFlowLayout(VerticalFlowLayout.TOP and VerticalFlowLayout.LEFT, GAP_1, GAP_0, true, true)
        ).apply {
            add(stickyHeaderView)
            add(JSeparator())
        }

        this.add(jPanel, BorderLayout.PAGE_START)
    }

    private fun scrollableView() {
        val jPanel = JPanel(
            VerticalFlowLayout(VerticalFlowLayout.TOP and VerticalFlowLayout.LEFT, GAP_1, GAP_0, true, true)
        ).apply {
            views.forEach(::add)
        }

        val scrollPane = JBScrollPane(jPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }

        this.add(scrollPane, BorderLayout.CENTER)
    }
}

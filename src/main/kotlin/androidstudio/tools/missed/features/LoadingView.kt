package androidstudio.tools.missed.features

import com.intellij.ui.AnimatedIcon
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class LoadingView : JPanel(BorderLayout()) {
    init {
        add(JLabel("Loading...", AnimatedIcon.Default(), SwingConstants.CENTER), BorderLayout.CENTER)
    }
}

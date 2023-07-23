package androidstudio.tools.missed.features

import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.AnimatedIcon
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class NeedToConnectDeviceView(
    private val resourceManager: ResourceManager,
    private val onRetryClickListener: () -> Unit
) :
    JPanel(BorderLayout()) {
    init {
        val panel = JPanel(VerticalFlowLayout(VerticalFlowLayout.CENTER, true, false))

        // label
        val descriptionLabel = JLabel(
            resourceManager.string("needToConnectDeviceDescription"),
            AnimatedIcon.Blinking(IconLoader.getIcon("/icons/androidDevice/androidDevice.svg", javaClass)),
            SwingConstants.CENTER
        )
        panel.add(descriptionLabel)

        // button
        val buttonPanel = JPanel()
        val retryButton = JButton(resourceManager.string("retry"), AllIcons.Actions.Refresh)
        retryButton.addActionListener {
            onRetryClickListener()
        }
        buttonPanel.add(retryButton)
        panel.add(buttonPanel)

        this.add(panel, BorderLayout.CENTER)
    }
}

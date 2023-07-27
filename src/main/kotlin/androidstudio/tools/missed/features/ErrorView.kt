package androidstudio.tools.missed.features

import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_MEDIUM
import androidstudio.tools.missed.utils.DisabledIcon
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.AnimatedIcon
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ErrorView(private val resourceManager: ResourceManager, private val onRetryClickListener: () -> Unit) :
    JPanel(BorderLayout()) {
    init {
        val panel = JPanel(VerticalFlowLayout(VerticalFlowLayout.CENTER, true, false))

        // label
        val descriptionLabel = JLabel(
            resourceManager.string("InitialPluginError"),
            AnimatedIcon(
                DELAY_MEDIUM.toInt(),
                AllIcons.General.BalloonError,
                DisabledIcon(AllIcons.General.BalloonError)
            ),
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

package androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.coroutines.exception.coroutineExceptionHandler
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.koin.java.KoinJavaComponent.inject
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class CustomCommandDialog(
    private val resourceManager: ResourceManager
) : DialogWrapper(true) {

    val viewScope = CoroutineScope(Dispatchers.Swing + SupervisorJob() + coroutineExceptionHandler)

    private lateinit var nameTextField: ExtendableTextField
    private lateinit var descriptionTextField: ExtendableTextField
    private lateinit var commandTextArea: JTextArea
    private lateinit var resultArea: JTextArea

    private val viewModel: CustomDialogViewModel by inject(CustomDialogViewModel::class.java)

    private val inputTextFieldErrorExtension = object : ExtendableTextComponent.Extension {
        private val icon: Icon = AllIcons.Ide.FatalError
        override fun getIcon(hovered: Boolean): Icon = icon
        override fun getTooltip(): String = resourceManager.string("inputTextIsEmptyError")
    }

    fun show(customCommand: CustomCommand) {
        initialObserve()
        viewModel.setup(customCommand)
        init()
        show()
    }

    override fun dispose() {
        viewScope.cancel()
        super.dispose()
    }

    @Suppress("MagicNumber", "LongMethod")
    override fun createCenterPanel(): JPanel {
        val subMainPanel = JPanel().apply {
            preferredSize = Dimension(600, 300)
            layout = GridBagLayout()
        }

        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        gbc.weighty = 1.0
        val largeDescriptionText = JTextArea(resourceManager.string("customCommandHelp")).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            background = subMainPanel.background
        }
        subMainPanel.add(largeDescriptionText, gbc)

        val titleLabel = JLabel(resourceManager.string("customCommandNameLabel"))
        gbc.gridwidth = 1
        gbc.gridx = 0
        gbc.gridy = 1
        subMainPanel.add(titleLabel, gbc)

        nameTextField = ExtendableTextField(20)
        gbc.gridx = 1
        gbc.gridy = 1
        subMainPanel.add(nameTextField, gbc)

        val descriptionLabel = JLabel(resourceManager.string("customCommandDescriptionLabel"))
        gbc.gridx = 0
        gbc.gridy = 2
        subMainPanel.add(descriptionLabel, gbc)

        descriptionTextField = ExtendableTextField(20)
        gbc.gridx = 1
        gbc.gridy = 2
        subMainPanel.add(descriptionTextField, gbc)

        val commandLabel = JLabel(resourceManager.string("customCommandCommandLabel"))
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.anchor = GridBagConstraints.NORTH
        subMainPanel.add(commandLabel, gbc)

        commandTextArea = JTextArea(5, 20)
        commandTextArea.lineWrap = true
        commandTextArea.wrapStyleWord = true
        val scrollPane = JScrollPane(commandTextArea)

        gbc.gridx = 1
        gbc.gridy = 3
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        subMainPanel.add(scrollPane, gbc)

        val resultLabel = JLabel(resourceManager.string("customCommandResultLabel"))
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.weightx = 0.0
        gbc.weighty = 0.0
        gbc.anchor = GridBagConstraints.NORTH
        subMainPanel.add(resultLabel, gbc)

        resultArea = JTextArea(3, 20)
        resultArea.lineWrap = true
        resultArea.wrapStyleWord = true
        resultArea.isEditable = false
        resultArea.background = subMainPanel.background
        val resultScrollPane = JScrollPane(resultArea)
        gbc.gridx = 1
        gbc.gridy = 4
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        subMainPanel.add(resultScrollPane, gbc)
        return subMainPanel
    }

    override fun createActions(): Array<Action> {
        val okAction = object : DialogWrapperAction(resourceManager.string("customCommandSave")) {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                if (nameTextField.text.trim().isEmpty()) {
                    nameTextField.setExtensions(inputTextFieldErrorExtension)
                    nameTextField.updateUI()
                } else if (commandTextArea.text.trim().isEmpty()) {
                    commandTextArea.border = BorderFactory.createLineBorder(java.awt.Color.RED)
                    commandTextArea.updateUI()
                } else {
                    viewModel.save(
                        name = nameTextField.text.trim(),
                        description = descriptionTextField.text.trim(),
                        command = commandTextArea.text.trim()
                    )
                    close(OK_EXIT_CODE)
                }
            }
        }

        val cancelAction =
            object : DialogWrapperAction(resourceManager.string("customCommandCancelDialogCancelLabel")) {
                override fun doAction(e: java.awt.event.ActionEvent?) {
                    close(CANCEL_EXIT_CODE)
                }
            }

        val runAction = object : AbstractAction(
            resourceManager.string("customCommandRun")
        ) {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                viewModel.executeCommand(
                    nameTextField.text.trim(),
                    descriptionTextField.text.trim(),
                    commandTextArea.text.trim()
                )
            }
        }
        runAction.putValue(Action.SMALL_ICON, AllIcons.Actions.Execute)

        return arrayOf(okAction, cancelAction, runAction)
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.commandStateFlow.collect {
                title = if (it.id == null) {
                    resourceManager.string("customCommandNew")
                } else {
                    resourceManager.string("customCommandEdit")
                }
                if (::nameTextField.isInitialized) {
                    nameTextField.text = it.name
                    descriptionTextField.text = it.description
                    commandTextArea.text = it.command
                }
            }
        }

        viewScope.launch {
            viewModel.resultStateFlow.collect {
                resultArea.text = it
            }
        }
    }
}

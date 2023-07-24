package androidstudio.tools.missed.features.input.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.features.input.model.EventKey
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.icons.AllIcons
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class InputTextView(
    override val viewModel: InputTextViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<InputTextViewModel>(viewModel) {

    private lateinit var inputTextField: ExtendableTextField
    private val inputTextFieldErrorExtension = object : ExtendableTextComponent.Extension {
        private val icon: Icon = AllIcons.Ide.FatalError
        override fun getIcon(hovered: Boolean): Icon = icon
        override fun getTooltip(): String = resourceManager.string("inputTextIsEmptyError")
    }
    private lateinit var sendButton: JButton
    private lateinit var clearAndSendButton: JButton
    init {
        setContent(
            title = resourceManager.string("inputTextTitle"),
            textField(),
            actionButtons()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
    }

    @VisibleForTesting
    fun textField(): JPanel {
        return panel {
            row {
                text(resourceManager.string("inputTextDescription")).gap(RightGap.SMALL)
                browserLink(resourceManager.string("help"), "https://github.com/rasoulmiri/AndroidStudioToolsMissed/wiki/Input-Tools")
            }

            row {
                label(resourceManager.string("text"))
                inputTextField = ExtendableTextField()
                inputTextField.document.addDocumentListener(object : DocumentAdapter() {
                    override fun textChanged(e: DocumentEvent) {
                        if (inputTextField.extensions.size != 0) {
                            inputTextField.removeExtension(inputTextFieldErrorExtension)
                        }
                    }
                })
                cell(inputTextField).columns(COLUMNS_LARGE)
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    @VisibleForTesting
    fun actionButtons(): JPanel {
        return panel {
            row {
                sendButton = button(resourceManager.string("send")) {
                    if (inputTextField.text.trim().isEmpty()) {
                        inputTextField.setExtensions(inputTextFieldErrorExtension)
                        inputTextField.updateUI()
                    } else {
                        viewModel.sendTextToDevice(inputTextField.text)
                    }
                }.component

                clearAndSendButton = button(resourceManager.string("clearAndSend")) {
                    if (inputTextField.text.trim().isEmpty()) {
                        inputTextField.setExtensions(inputTextFieldErrorExtension)
                        inputTextField.updateUI()
                    } else {
                        viewModel.clearAndSendTextToDevice(inputTextField.text)
                    }
                }.component

                button(resourceManager.string("clear")) {
                    viewModel.clearTextInEditText()
                }
            }

            row {
                button(resourceManager.string("done")) {
                    viewModel.sendEventToDevice(EventKey.DONE)
                }
                button(resourceManager.string("next")) {
                    viewModel.sendEventToDevice(EventKey.NEXT)
                }
            }
        }
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }
    }
}

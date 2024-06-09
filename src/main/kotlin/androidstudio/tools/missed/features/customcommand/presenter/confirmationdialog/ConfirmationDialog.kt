package androidstudio.tools.missed.features.customcommand.presenter.confirmationdialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import javax.swing.Action
import javax.swing.JPanel

class ConfirmationDialog(
    project: Project?,
    private val title: String,
    private val description: String,
    private val okLabel: String,
    private val cancelLabel: String
) : DialogWrapper(project) {

    init {
        init()
        setTitle(title)
    }

    override fun createCenterPanel(): JPanel {
        return panel {
            row {
                label(description)
            }
        }
    }

    override fun createActions(): Array<Action> {
        val okAction = object : DialogWrapperAction(okLabel) {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                close(OK_EXIT_CODE)
            }
        }

        val cancelAction = object : DialogWrapperAction(cancelLabel) {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                close(CANCEL_EXIT_CODE)
            }
        }

        return arrayOf(okAction, cancelAction)
    }
}

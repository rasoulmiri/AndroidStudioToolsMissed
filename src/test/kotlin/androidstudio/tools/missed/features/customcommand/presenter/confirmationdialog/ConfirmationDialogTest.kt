package androidstudio.tools.missed.features.customcommand.presenter.confirmationdialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.Test

import java.lang.reflect.Method
import javax.swing.Action
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

class ConfirmationDialogTest {

    private fun <T> runOnEdtAndWait(callable: () -> T): T {
        var result: T? = null
        var exception: Throwable? = null
        val latch = java.util.concurrent.CountDownLatch(1)

        SwingUtilities.invokeAndWait {
            try {
                result = callable()
            } catch (e: Throwable) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.await()
        exception?.let { throw it }
        return result!!
    }

    @Test
    fun `should create dialog with given title and description`() {
        runOnEdtAndWait {
            // Given
            val project = mockk<Project>(relaxed = true)
            val title = "Test Title"
            val description = "Test Description"
            val okLabel = "OK"
            val cancelLabel = "Cancel"

            // When
            val dialog = ConfirmationDialog(project, title, description, okLabel, cancelLabel)

            // Use reflection to access the protected createCenterPanel method
            val createCenterPanelMethod: Method = ConfirmationDialog::class.java.getDeclaredMethod("createCenterPanel")
            createCenterPanelMethod.isAccessible = true
            val centerPanel = createCenterPanelMethod.invoke(dialog) as JPanel

            // Then
            assertEquals(title, dialog.title)

            assertTrue(centerPanel is JPanel)

            val label = centerPanel.getComponent(0) as JLabel
            assertEquals(description, label.text)
        }
    }

    @Test
    fun `should create actions with given labels`() {
        runOnEdtAndWait {
            // Given
            val project = mockk<Project>(relaxed = true)
            val title = "Test Title"
            val description = "Test Description"
            val okLabel = "OK"
            val cancelLabel = "Cancel"

            // When
            val dialog = ConfirmationDialog(project, title, description, okLabel, cancelLabel)

            // Use reflection to access the protected createActions method
            val createActionsMethod: Method = ConfirmationDialog::class.java.getDeclaredMethod("createActions")
            createActionsMethod.isAccessible = true
            val actions = createActionsMethod.invoke(dialog) as Array<Action>

            // Then
            assertEquals(2, actions.size)

            val okAction = actions[0]
            val cancelAction = actions[1]

            assertEquals(okLabel, okAction.getValue(Action.NAME))
            assertEquals(cancelLabel, cancelAction.getValue(Action.NAME))
        }
    }

    @Test
    fun `okAction should close dialog with OK_EXIT_CODE`() {
        runOnEdtAndWait {
            // Given
            val project = mockk<Project>(relaxed = true)
            val title = "Test Title"
            val description = "Test Description"
            val okLabel = "OK"
            val cancelLabel = "Cancel"
            val dialog = ConfirmationDialog(project, title, description, okLabel, cancelLabel)

            // Use reflection to access the protected createActions method
            val createActionsMethod: Method = ConfirmationDialog::class.java.getDeclaredMethod("createActions")
            createActionsMethod.isAccessible = true
            val actions = createActionsMethod.invoke(dialog) as Array<Action>

            // When
            val okAction = actions[0]
            okAction.actionPerformed(null)

            // Then
            assertEquals(DialogWrapper.OK_EXIT_CODE, dialog.exitCode)
        }
    }

    @Test
    fun `cancelAction should close dialog with CANCEL_EXIT_CODE`() {
        runOnEdtAndWait {
            // Given
            val project = mockk<Project>(relaxed = true)
            val title = "Test Title"
            val description = "Test Description"
            val okLabel = "OK"
            val cancelLabel = "Cancel"
            val dialog = ConfirmationDialog(project, title, description, okLabel, cancelLabel)

            // Use reflection to access the protected createActions method
            val createActionsMethod: Method = ConfirmationDialog::class.java.getDeclaredMethod("createActions")
            createActionsMethod.isAccessible = true
            val actions = createActionsMethod.invoke(dialog) as Array<Action>

            // When
            val cancelAction = actions[1]
            cancelAction.actionPerformed(null)

            // Then
            assertEquals(DialogWrapper.CANCEL_EXIT_CODE, dialog.exitCode)
        }
    }
}

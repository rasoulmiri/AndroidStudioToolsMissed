import androidstudio.tools.missed.manager.notification.NotificationManagerImpl
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import io.mockk.*
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.awt.event.InputEvent
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class NotificationManagerImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockProject: Project = mockk()
    private val mockNotification: Notification = mockk(relaxed = true)
    private val testScope = TestScope(UnconfinedTestDispatcher())
    private lateinit var notificationManager: NotificationManagerImpl

    @Before
    fun setUp() {
        notificationManager = NotificationManagerImpl(testScope, mockResourceManager)
        mockkStatic(ProjectManager::class)
        every { ProjectManager.getInstance() } returns mockk {
            every { openProjects } returns arrayOf(mockProject)
        }
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `showBalloon() should create and show a notification with given message`() = testScope.runTest {
        // Arrange
        val title = "Test Title"
        val content = "Test Content"
        val type = NotificationType.INFORMATION

        val message = BalloonNotificationModel(
            title = title,
            content = content,
            type = type,
            fadeoutTime = 5000
        )

        val mockNotificationGroup: NotificationGroup = mockk(relaxed = true)
        mockkStatic(NotificationGroupManager::class)
        every { NotificationGroupManager.getInstance() } returns mockk {
            every { getNotificationGroup(any()) } returns mockNotificationGroup
        }

        coEvery {
            mockNotificationGroup.createNotification(
                title = title,
                content = content,
                type = type
            )
        } returns mockNotification

        // Act
        notificationManager.showBalloon(message)

        // Assert
        coVerify { mockNotification.notify(mockProject) }
        advanceUntilIdle()
        coVerify { mockNotification.hideBalloon() }
    }

    @Test
    fun `showBalloonWithButton() should create and show a notification with a button`() = testScope.runTest {
        // Arrange
        val title = "Test Title"
        val content = "Test Content"
        val type = NotificationType.INFORMATION

        val message = BalloonNotificationModel(
            title = title,
            content = content,
            type = type,
            fadeoutTime = 5000
        )

        every { mockResourceManager.string("browseAPK") } returns "Browse APK"
        val listener: (InputEvent?) -> Unit = mockk()
        val mockNotificationGroup: NotificationGroup = mockk(relaxed = true)
        mockkStatic(NotificationGroupManager::class)
        every { NotificationGroupManager.getInstance() } returns mockk {
            every { getNotificationGroup(any()) } returns mockNotificationGroup
        }

        // Mock the behavior of addAction and make it return the mockAction
        every { mockNotification.addAction(any()) } answers {
            val actionArg = args.first() as NotificationAction
            assertEquals("Browse APK", actionArg.templateText) // Ensure that the templateText is correct
            mockNotification
        }

        coEvery {
            mockNotificationGroup.createNotification(
                title = title,
                content = content,
                type = type
            )
        } returns mockNotification

        // Act
        notificationManager.showBalloonWithButton(message, listener)

        // Assert
        coVerify { mockNotification.notify(mockProject) }

        // Perform action on the notification button
        val mockAction = mockk<NotificationAction>()
        val mockAnActionEvent: AnActionEvent = mockk()
        val mockInputEvent: InputEvent = mockk()
        coEvery { mockAnActionEvent.inputEvent } returns mockInputEvent
        coEvery { listener(any()) } returns Unit
        coEvery { mockNotification.actions } returns listOf(mockAction)
        coEvery { mockAction.actionPerformed(mockAnActionEvent) } coAnswers {
            listener(mockInputEvent)
        }

        val actions = mockNotification.actions // Retrieve the array of actions
        assertTrue(actions.isNotEmpty())

        val action = actions.first() // Get the first action
        assertEquals(mockAction, action)
        action.actionPerformed(mockAnActionEvent)
        coVerify { listener(any()) }
        advanceUntilIdle()
        coVerify { mockNotification.hideBalloon() }
    }
}

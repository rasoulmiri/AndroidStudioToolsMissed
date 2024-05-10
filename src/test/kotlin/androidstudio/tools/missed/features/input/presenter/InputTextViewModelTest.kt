package androidstudio.tools.missed.features.input.presenter

import androidstudio.tools.missed.features.input.domain.usecase.cleartext.ClearTextUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendevent.SendEventUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendtext.SendTextUseCase
import androidstudio.tools.missed.features.input.model.EventKey
import androidstudio.tools.missed.manager.resource.ResourceManager
import app.cash.turbine.test
import com.intellij.notification.NotificationType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InputTextViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockResourceManager = mockk<ResourceManager>()
    private val mockSendTextUseCase = mockk<SendTextUseCase>()
    private val mockSendEventUseCase = mockk<SendEventUseCase>()
    private val mockClearTextUseCase = mockk<ClearTextUseCase>()

    private lateinit var viewModel: InputTextViewModel

    @Before
    fun setup() {
        viewModel = InputTextViewModel(
            testDispatcher,
            mockResourceManager,
            mockSendTextUseCase,
            mockSendEventUseCase,
            mockClearTextUseCase
        )
    }

    @Test
    fun `sendTextToDevice should emit information notification when text is empty`() = runTest {
        // Arrange
        val emptyText = ""
        val errorMessageResourceId = "inputTextIsEmptyError"
        val errorMessage = "Text is empty"
        coEvery { mockResourceManager.string(errorMessageResourceId) } returns errorMessage

        viewModel.messageSharedFlow.test {
            // Act
            viewModel.sendTextToDevice(emptyText)

            // Assert
            val notification = awaitItem()
            assertEquals(NotificationType.INFORMATION, notification.type)
            assertEquals(errorMessage, notification.content)
        }
        coVerify { mockResourceManager.string(errorMessageResourceId) }
    }

    @Test
    fun `sendTextToDevice should emit success notification on successful text send`() = runTest(testDispatcher) {

        // Arrange
        val inputText = "Hello"
        val successContentResourceId = "successSendText"
        val expectedNotificationContent = "$inputText successfulMessage"
        coEvery { mockSendTextUseCase.invoke(inputText) } coAnswers { flowOf(Result.success(Unit)) }
        coEvery {
            mockResourceManager.string(
                successContentResourceId,
                inputText
            )
        } coAnswers { expectedNotificationContent }

        viewModel.messageSharedFlow.test {

            // Act
            viewModel.sendTextToDevice(inputText)

            // Assert
            val notification = awaitItem()
            assertEquals(NotificationType.INFORMATION, notification.type)
            assertEquals(expectedNotificationContent, notification.content)

            coVerify { mockSendTextUseCase.invoke(inputText) }
            coVerify { mockResourceManager.string(successContentResourceId, inputText) }
        }

    }

    @Test
    fun `sendTextToDevice should emit error notification on failed text send`() = runTest {

        // Arrange
        val inputText = "Hello"
        val errorMessage = "errorMessage"
        val errorStringResourceId = "errorSendTextTitle"
        val error = Throwable(errorMessage)
        coEvery { mockSendTextUseCase.invoke(inputText) } returns flowOf(Result.failure(error))
        coEvery { mockResourceManager.string(errorStringResourceId) } returns "string"
        val expectedNotificationContent = "${mockResourceManager.string(errorStringResourceId)} ${error.message}"

        viewModel.messageSharedFlow.test {

            // Act
            viewModel.sendTextToDevice(inputText)

            // Assert
            val notification = awaitItem()
            assertEquals(NotificationType.ERROR, notification.type)
            assertEquals(expectedNotificationContent, notification.content)

            coVerify { mockSendTextUseCase.invoke(inputText) }
            coVerify { mockResourceManager.string(errorStringResourceId) }
        }
    }

    @Test
    fun `clearAndSendTextToDevice should invoke clearTextUseCase and sendTextToDevice`() = runTest(testDispatcher) {
        // Arrange
        val inputText = "Hello"
        val successStringId = "successSendText"
        val expectedNotificationContent = "successfulMessage $inputText"
        coEvery { mockClearTextUseCase.invoke() } coAnswers { flowOf(Result.success(Unit)) }
        coEvery { mockSendTextUseCase.invoke(inputText) } coAnswers { flowOf(Result.success(Unit)) }
        coEvery { mockResourceManager.string(successStringId, inputText) } coAnswers { expectedNotificationContent }

        // Act
        viewModel.clearAndSendTextToDevice(inputText)
        advanceUntilIdle()

        // Assert
        coVerify { mockClearTextUseCase.invoke() }
        coVerify { mockSendTextUseCase.invoke(inputText) }

    }

    @Test
    fun `sendEventToDevice should invoke sendEventUseCase`() = runTest {
        // Arrange
        val event = EventKey.DONE
        coEvery { mockSendEventUseCase.invoke(event.value) } returns flowOf(Result.success(Unit))

        // Act
        viewModel.sendEventToDevice(event)

        // Assert
        coVerify { mockSendEventUseCase.invoke(event.value) }
    }

    @Test
    fun `sendEventToDevice should emit error notification on failure`() = runTest {

        // Arrange
        val errorTitle = "errorTitle"
        val errorMessage = "Failed to send event"
        val errorSendTitleResourceId = "errorSendEventTitle"
        val failureResult = Result.failure<Unit>(RuntimeException(errorMessage))
        coEvery { mockSendEventUseCase.invoke(any()) } coAnswers { flowOf(failureResult) }
        coEvery { mockResourceManager.string(errorSendTitleResourceId) } returns errorTitle

        viewModel.messageSharedFlow.test {

            // Act
            viewModel.sendEventToDevice(EventKey.DONE)

            // Assert
            val notification = awaitItem()
            assertEquals(NotificationType.ERROR, notification.type)
            assertEquals(errorTitle, notification.title)
            assertEquals(errorMessage, notification.content)

            coVerify { mockSendEventUseCase.invoke(EventKey.DONE.value) }
            coVerify { mockResourceManager.string(errorSendTitleResourceId) }
        }

    }

    @Test
    fun `clearTextInEditText should invoke clearTextUseCase`() = runTest {
        // Arrange
        val result = Result.success(Unit)
        coEvery { mockClearTextUseCase.invoke() } returns flowOf(result)

        // Act
        viewModel.clearTextInEditText()

        // Assert
        coVerify { mockClearTextUseCase.invoke() }
    }


    @Test
    fun `clearTextInEditText should emit error notification on failure`() = runTest {

        // Arrange
        val errorTitle = "errorTitle"
        val errorMessage = "errorMessage"
        val errorTitleResourceId = "errorClearTextTitle"
        val result = Result.failure<Unit>(RuntimeException(errorMessage))
        coEvery { mockClearTextUseCase.invoke() } coAnswers { flowOf(result) }
        coEvery { mockResourceManager.string(errorTitleResourceId) } coAnswers { errorTitle }

        viewModel.messageSharedFlow.test {

            // Act
            viewModel.clearTextInEditText()

            // Assert
            val notification = awaitItem()
            assertEquals(NotificationType.ERROR, notification.type)
            assertEquals(errorTitle, notification.title)
            assertEquals(errorMessage, notification.content)

            coVerify { mockClearTextUseCase.invoke() }
            coVerify { mockResourceManager.string(errorTitleResourceId) }
        }

    }
}

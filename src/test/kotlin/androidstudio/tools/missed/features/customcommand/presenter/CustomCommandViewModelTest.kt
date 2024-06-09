package androidstudio.tools.missed.features.customcommand.presenter

import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import app.cash.turbine.test
import com.intellij.notification.NotificationType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomCommandViewModelTest {

    private lateinit var viewModel: CustomCommandViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val resourceManager = mockk<ResourceManager>(relaxed = true)
    private val customCommandUseCase = mockk<CustomCommandUseCase>(relaxed = true)
    private val repository = mockk<CustomCommandRepository>(relaxed = true)
    private val customCommand =
        CustomCommand(id = 1, index = 0, name = "Test", description = "Description", command = "Command")

    @Before
    fun setUp() {
        viewModel = CustomCommandViewModel(testDispatcher, resourceManager, customCommandUseCase, repository)
    }

    @Test
    fun `test updateData`() = runTest {
        val customCommandList = listOf(customCommand)
        coEvery { repository.loadAll() } returns customCommandList

        viewModel.updateData()

        assertEquals(customCommandList, viewModel.customCommandsStateFlow.value)
    }

    @Test
    fun `test executeCommand onSuccess`() = runTest {
        val result = "Success"
        val notificationModel = BalloonNotificationModel(
            title = customCommand.name ?: "",
            content = result,
            type = NotificationType.INFORMATION,
            fadeoutTime = 3000L
        )
        coEvery { customCommandUseCase.invoke(any()) } returns flowOf(Result.success(result))

        viewModel.messageSharedFlow.test {
            viewModel.executeCommand(customCommand)
            val notification = awaitItem()
            assertEquals(notification, notificationModel)
        }

    }

    @Test
    fun `test executeCommand onFailure`() = runTest {
        val errorMessage = "Error message"
        val notificationModel = BalloonNotificationModel(
            content = errorMessage,
            type = NotificationType.ERROR
        )
        coEvery { customCommandUseCase.invoke(any()) } returns flowOf(Result.failure(Exception(errorMessage)))

        viewModel.messageSharedFlow.test {
            viewModel.executeCommand(customCommand)
            val notification = awaitItem()
            assertEquals(notification, notificationModel)
        }

    }

    @Test
    fun `test deleteById`() = runTest {
        val id = 1
        val customCommandList = emptyList<CustomCommand>()
        every { repository.deleteById(id) } answers { }
        coEvery { repository.loadAll() } returns customCommandList

        viewModel.deleteById(id)

        assertEquals(customCommandList, viewModel.customCommandsStateFlow.value)
    }
}

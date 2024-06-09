import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog.CustomDialogViewModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomDialogViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val resourceManager = mockk<ResourceManager>(relaxed = true)
    private val customCommandUseCase = mockk<CustomCommandUseCase>(relaxed = true)
    private val repository = mockk<CustomCommandRepository>(relaxed = true)
    private val viewModel =spyk(CustomDialogViewModel(testDispatcher, resourceManager, customCommandUseCase, repository))

    @Test
    fun `executeCommand is success when customCommandUseCase return success result`() = runTest {
        val result = "Success"
        coEvery { customCommandUseCase.invoke(any()) } returns flowOf(Result.success(result))

        val name = "Test"
        val description = "Description"
        val command = "Command"

        viewModel.executeCommand(name, description, command)

        assertEquals(viewModel.resultStateFlow.first(), result)
        coVerify {
            customCommandUseCase.invoke(
                CustomCommand(
                    id = 0,
                    index = 0,
                    name = name,
                    description = description,
                    command = command
                )
            )
        }
    }

    @Test
    fun `executeCommand is failure when customCommandUseCase return failure result`() = runTest {
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<String>(expectedException)
        coEvery { customCommandUseCase.invoke(any()) } returns flowOf(expectedResult)

        val name = "Test"
        val description = "Description"
        val command = "Command"

        viewModel.executeCommand(name, description, command)

        assertEquals(viewModel.resultStateFlow.first(), expectedException.message)
        coVerify {
            customCommandUseCase.invoke(
                CustomCommand(
                    id = 0,
                    index = 0,
                    name = name,
                    description =description,
                    command = command
                )
            )
        }
    }

    @Test
    fun `when save call then should repository save is called`() = runTest {
        val name = "Test"
        val description = "Description"
        val command = "Command"
        val customCommand = CustomCommand(id = 0, index = 0, name = name, description = description, command = command)

        viewModel.save(name, description, command)

        coVerify {
            (repository).save(eq(customCommand))
        }
    }
}

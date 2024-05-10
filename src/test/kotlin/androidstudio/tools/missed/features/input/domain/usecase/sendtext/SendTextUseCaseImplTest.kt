package androidstudio.tools.missed.features.input.domain.usecase.sendtext

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SendTextUseCaseImplTest {

    private val mockDeviceManager = mockk<DeviceManager>()
    private lateinit var useCase: SendTextUseCaseImpl

    @Before
    fun setUp() {
        useCase = SendTextUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed`() = runTest {

        // Arrange
        val text = "Test text"
        val expectedResult = Result.success(Unit)
        coEvery {
            mockDeviceManager.executeShellCommand(any())
        } coAnswers {
            if (args.first() is AdbCommand.InputText) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(text).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(match { it is AdbCommand.InputText && it.message == "Test\\ text" }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {

        // Arrange
        val text = "Test text"
        val errorText = "Command failed"
        val exception = RuntimeException(errorText)
        val expectedResult = Result.failure<Unit>(exception)
        coEvery {
            mockDeviceManager.executeShellCommand(any())
        } coAnswers {
            if (args.first() is AdbCommand.InputText) {
                Result.failure<String>(exception)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(text).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(match { it is AdbCommand.InputText && it.message == "Test\\ text" }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `addDoubleBackSlashForEspecialCharacters() should escape special characters`() {
        // Arrange
        val inputText = "|\"'<>;\\?`&*()~ "
        val expectedOutput = "\\|\\\"\\\'\\<\\>\\;\\\\?\\`\\&\\*\\(\\)\\~\\ "

        // Act
        val result = useCase.addDoubleBackSlashForEspecialCharacters(inputText)

        // Assert
        assertEquals(expectedOutput, result)
    }
}

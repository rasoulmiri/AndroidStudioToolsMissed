package androidstudio.tools.missed.features.input.domain.usecase.sendevent

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
class SendEventUseCaseImplTest {

    private val mockDeviceManager = mockk<DeviceManager>()
    private lateinit var useCase: SendEventUseCase

    @Before
    fun setUp() {
        useCase = SendEventUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed`() = runTest {

        // Arrange
        val event = "KEYCODE_A"
        val expectedResult = Result.success(Unit)
        coEvery {
            mockDeviceManager.executeShellCommand(any())
        } coAnswers {
            if (args.first() is AdbCommand.InputEvent) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(event).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(match { it is AdbCommand.InputEvent && it.event == event }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {

        val event = "KEYCODE_B"
        val errorText = "Command failed"
        val exception = RuntimeException(errorText)
        val expectedResult = Result.failure<Unit>(exception)
        coEvery {
            mockDeviceManager.executeShellCommand(any())
        } coAnswers {
            if (args.first() is AdbCommand.InputEvent) {
                Result.failure<String>(exception)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(event).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(match { it is AdbCommand.InputEvent && it.event == event }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(any()) }
    }
}

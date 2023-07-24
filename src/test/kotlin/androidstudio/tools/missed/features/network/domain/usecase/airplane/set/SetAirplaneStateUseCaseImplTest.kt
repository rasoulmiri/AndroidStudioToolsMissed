package androidstudio.tools.missed.features.network.domain.usecase.airplane.set

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SetAirplaneStateUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: SetAirplaneStateUseCaseImpl

    @Before
    fun setUp() {
        useCase = SetAirplaneStateUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when setting airplane mode on`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.SetAirplaneModeState) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit success result with true when setting airplane mode off`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.SetAirplaneModeState) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(false).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit failure result when shell command fails while setting airplane mode on`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.SetAirplaneModeState) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit failure result when shell command fails while setting airplane mode off`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.SetAirplaneModeState) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(false).single()

        // Assert
        assertEquals(expectedResult, result)
    }
}

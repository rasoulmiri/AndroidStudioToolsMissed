package androidstudio.tools.missed.features.network.domain.usecase.airplane.get

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAirplaneStateUseCaseImplTest {

    private val mockDeviceManager = mockk<DeviceManager>()
    private lateinit var useCase: GetAirplaneStateUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetAirplaneStateUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed and airplane mode is enabled`() = runTest {

        // Arrange
        val expectedResult = Result.success(true)
        val expectedSuccessResult = "1"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.GetAirplaneModeState) {
                Result.success(expectedSuccessResult)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit success result when shell command succeed and airplane mode is disabled`() = runTest {

        // Arrange
        val expectedResult = Result.success(false)
        val expectedSuccessResult = "0"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.GetAirplaneModeState) {
                Result.success(expectedSuccessResult)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {

        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is NetworkAdbCommands.GetAirplaneModeState) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify { mockDeviceManager.executeShellCommand(any()) }
    }
}

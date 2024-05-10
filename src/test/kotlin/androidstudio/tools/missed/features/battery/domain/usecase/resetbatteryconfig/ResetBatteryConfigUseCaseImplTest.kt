package androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
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

class ResetBatteryConfigUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: ResetBatteryConfigUseCaseImpl

    @Before
    fun setUp() {
        useCase = ResetBatteryConfigUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.ResetSetting) {
                Result.success("Success")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.ResetSetting }
            )
        }
    }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            Result.failure(expectedException)
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.ResetSetting }
            )
        }
    }
}

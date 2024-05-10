package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set

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

class SetBatteryLevelUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: SetBatteryLevelUseCaseImpl

    @Before
    fun setUp() {
        useCase = SetBatteryLevelUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed`() = runTest {
        // Arrange
        val batteryLevel = 80
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.SetLevel) {
                Result.success("Success")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(batteryLevel).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.SetLevel && it.level == batteryLevel }
            )
        }
    }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {
        // Arrange
        val batteryLevel = 50
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.SetLevel) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(batteryLevel).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.SetLevel && it.level == batteryLevel }
            )
        }
    }
}

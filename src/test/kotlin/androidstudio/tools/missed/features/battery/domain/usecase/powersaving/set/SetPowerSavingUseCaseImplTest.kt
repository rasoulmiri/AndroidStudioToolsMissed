package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set

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

class SetPowerSavingUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: SetPowerSavingUseCaseImpl

    @Before
    fun setUp() {
        useCase = SetPowerSavingUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeeds for enabling power saving mode`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.PowerSavingModeSetOn) {
                Result.success("Success")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.PowerSavingModeSetOn }
            )
        }
    }

    @Test
    fun `invoke() should emit success result when shell command succeeds for disabling power saving mode`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.PowerSavingModeSetOff) {
                Result.success("Success")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(false).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.PowerSavingModeSetOff }
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
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(
                match { it is BatteryAdbCommands.PowerSavingModeSetOn }
            )
        }
    }
}

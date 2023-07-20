package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
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

class GetPowerSavingUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GetPowerSavingUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetPowerSavingUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when power saving mode is enabled`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.PowerSavingModeGetState) {
                    Result.success("1")
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke().single()

            // Assert
            assertEquals(expectedResult, result)
        }

    @Test
    fun `invoke() should emit success result with false when power saving mode is disabled`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(false)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.PowerSavingModeGetState) {
                    Result.success("0")
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke().single()

            // Assert
            assertEquals(expectedResult, result)
        }

    @Test
    fun `invoke() should emit failure result when shell command fails`() =
        runTest {
            // Arrange
            val expectedException = RuntimeException("Command failed")
            val expectedResult = Result.failure<Boolean>(expectedException)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.PowerSavingModeGetState) {
                    Result.failure(expectedException)
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke().single()

            // Assert
            assertEquals(expectedResult, result)
        }
}

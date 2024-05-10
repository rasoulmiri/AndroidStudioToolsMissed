package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get

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

class GetBatteryLevelUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GetBatteryLevelUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetBatteryLevelUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with battery level when shell command succeed`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(80)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.GetBatteryLevel) {
                    Result.success("level: 80")
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
    fun `invoke() should emit failure result when shell command fails`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Int>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.GetBatteryLevel) {
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

    @Test
    fun `parseBatteryLevelResult() should return battery level when input contains battery level line`() {
        // Arrange
        val input = "level: 50"

        // Act
        val result = useCase.parseBatteryLevelResult(input)

        // Assert
        assertEquals(50, result)
    }

    @Test
    fun `parseBatteryLevelResult() should return 0 when input does not contain battery level line`() {
        // Arrange
        val input = "No battery information"

        // Act
        val result = useCase.parseBatteryLevelResult(input)

        // Assert
        assertEquals(0, result)
    }
}

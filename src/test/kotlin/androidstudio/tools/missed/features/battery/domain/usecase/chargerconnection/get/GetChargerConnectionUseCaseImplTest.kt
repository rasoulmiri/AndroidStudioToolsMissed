package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get

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

class GetChargerConnectionUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GetChargerConnectionUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetChargerConnectionUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with charger connection when shell command succeeds`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.GetBatteryLevel) {
                    Result.success("AC powered: true\nUSB powered: false\nWireless powered: false")
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
    fun `invoke() should emit success result with no charger connection when shell command succeeds`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(false)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.GetBatteryLevel) {
                    Result.success("AC powered: false\nUSB powered: false\nWireless powered: false")
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
        val expectedResult = Result.failure<Boolean>(expectedException)
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
}

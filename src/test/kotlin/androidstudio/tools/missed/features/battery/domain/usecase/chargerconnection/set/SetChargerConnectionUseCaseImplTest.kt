package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set

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

class SetChargerConnectionUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: SetChargerConnectionUseCaseImpl

    @Before
    fun setUp() {
        useCase = SetChargerConnectionUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeed for charger connection`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.ChargerSetConnect) {
                    Result.success("Success")
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
    fun `invoke() should emit success result when shell command succeed for charger disconnection`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is BatteryAdbCommands.ChargerSetDisconnect) {
                    Result.success("Success")
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
    fun `invoke() should emit failure result when shell command fails`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is BatteryAdbCommands.ChargerSetConnect ||
                args.first() is BatteryAdbCommands.ChargerSetDisconnect
            ) {
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
}

package androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.get

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
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

class GetWifiStateUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GetWifiStateUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetWifiStateUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when shell command succeeds and WiFi is enabled`() = runTest {

        // Arrange
        val expectedResult = Result.success(true)
        val expectedSuccessResult = "1"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.GetWifiState) {
                Result.success(expectedSuccessResult)
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
    fun `invoke() should emit success result when shell command succeeds and WiFi is disabled`() = runTest {

        // Arrange
        val expectedResult = Result.success(false)
        val expectedSuccessResult = "0"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.GetWifiState) {
                Result.success(expectedSuccessResult)
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
            if (args.first() is InternetAdbConnectionCommands.GetWifiState) {
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

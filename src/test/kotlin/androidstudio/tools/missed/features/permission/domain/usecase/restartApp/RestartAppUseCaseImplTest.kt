package androidstudio.tools.missed.features.permission.domain.usecase.restartApp

import androidstudio.tools.missed.manager.adb.command.ApplicationAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RestartAppUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: RestartAppUseCaseImpl

    @Before
    fun setUp() {
        useCase = RestartAppUseCaseImpl(mockDeviceManager)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow("com.example.package")
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when app restarts successfully`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) } returns Result.success(
            "App closed successfully"
        )
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) } returns Result.success(
            "App opened successfully"
        )


        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(Result.success("App opened successfully"), result)

        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) }

    }

    @Test
    fun `invoke() should emit failure result when closing app fails`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val errorMessage = "Failed to close app"
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) } returns Result.failure(
            Throwable(errorMessage)
        )

        // Act
        val actualErrorMessage = useCase.invoke().single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) }
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) }
    }

    @Test
    fun `invoke() should emit failure result when opening app fails`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val errorMessage = "Failed to open app"

        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) } returns Result.success(
            "App closed successfully"
        )
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) } returns Result.failure(
            Throwable(errorMessage)
        )

        // Act
        val actualErrorMessage = useCase.invoke().single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) }
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) }

    }

    @Test
    fun `invoke() should emit failure result when both closing and opening app fail`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val errorMessageClose = "Failed to close app"
        val errorMessageOpen = "Failed to open app"
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) } returns Result.failure(
            Throwable(errorMessageClose)
        )
        coEvery { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) } returns Result.failure(
            Throwable(errorMessageOpen)
        )

        // Act
        val actualErrorMessage = useCase.invoke().single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessageClose, actualErrorMessage)
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Close> { it.packageId == packageId }) }
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(match<ApplicationAdbCommands.Open> { it.packageId == packageId }) }

    }
}

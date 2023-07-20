package androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase

import androidstudio.tools.missed.manager.adb.command.DeviceAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetPackageIdsInstalledInDeviceUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GetPackageIdsInstalledInDeviceUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetPackageIdsInstalledInDeviceUseCaseImpl(mockDeviceManager)
    }

    @Test
    fun `invoke() should return list of package IDs when selectedShowAllPackageIds is true`() = runTest {
        // Arrange
        val adbCommandResult =
            "com.example.app1\ncom.example.app2\ncom.example.app3\n"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DeviceAdbCommands.AllPackageIdsInstalled) {
                Result.success(adbCommandResult)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(Result.success(arrayListOf("com.example.app1", "com.example.app2", "com.example.app3")), result)
    }

    @Test
    fun `invoke() should return list of user-installed package IDs when selectedShowAllPackageIds is false`() =
        runTest {
            // Arrange
            val adbCommandResult =
                "com.example.app1\ncom.example.app2\ncom.example.app3\n"
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is DeviceAdbCommands.AllPackageIdsUserInstalled) {
                    Result.success(adbCommandResult)
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke(false).single()

            // Assert
            assertEquals(
                Result.success(arrayListOf("com.example.app1", "com.example.app2", "com.example.app3")),
                result
            )
        }

    @Test
    fun `invoke() should return failure result when executing adb command fails`() = runTest {
        // Arrange
        val errorMessage = "Failed to execute adb command"
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DeviceAdbCommands.AllPackageIdsInstalled
                || args.first() is DeviceAdbCommands.AllPackageIdsUserInstalled
            ) {
                Result.failure(Throwable(errorMessage))
            } else {
                error("Unexpected command")
            }
        }
        // Act
        val actualErrorMessage = useCase.invoke(true).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
    }
}

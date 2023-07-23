package androidstudio.tools.missed.features.permission.domain.usecase.grant

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
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
class GrantPermissionUseCaseImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: GrantPermissionUseCaseImpl

    @Before
    fun setUp() {
        useCase = GrantPermissionUseCaseImpl(mockResourceManager, mockDeviceManager)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow("")
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with already granted text when permission is already granted`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true)
        val alreadyGrantedText = "Permission already granted"
        coEvery { mockResourceManager.string("grantedDescription", permission.name) } returns alreadyGrantedText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(alreadyGrantedText), result)
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit success result with error text when permission is not a runtime permission`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = false)
        val errorText = "Install-time permissions are not supported"
        coEvery { mockResourceManager.string("errorInstallTimePermission") } returns errorText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(errorText), result)
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit success result with success text when permission grant succeeds`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = true)
        val packageId = "com.example.package"
        val successText = "Permission granted successfully"
        val adbCommand = PermissionAdbCommands.Grant(packageId, permission.name)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is PermissionAdbCommands.Grant) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }
        coEvery { mockResourceManager.string("successGrantDescription", permission.name, packageId) } returns successText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(successText), result)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.Grant && it.packageId == packageId && it.permission == permission.name
            })
        }

    }

    @Test
    fun `invoke() should emit failure result when permission grant fails`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = true)
        val packageId = "com.example.package"
        val errorText = "Failed to grant permission"
        val adbCommand = PermissionAdbCommands.Grant(packageId, permission.name)
        val errorMessage = "Permission grant failed"
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is PermissionAdbCommands.Grant) {
                Result.failure(Throwable(errorMessage))
            } else {
                error("Unexpected command")
            }
        }
        coEvery { mockResourceManager.string("failedGrantDescription", permission.name, packageId, errorMessage) } returns errorText

        // Act
        val actualErrorMessage = useCase.invoke(permission).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.Grant && it.packageId == packageId && it.permission == adbCommand.permission
            })
        }
    }
}

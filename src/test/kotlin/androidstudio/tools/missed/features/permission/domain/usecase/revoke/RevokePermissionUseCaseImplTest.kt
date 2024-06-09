import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCaseImpl
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
class RevokePermissionUseCaseImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: RevokePermissionUseCaseImpl

    @Before
    fun setUp() {
        useCase = RevokePermissionUseCaseImpl(mockResourceManager, mockDeviceManager)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow("")
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with already revoked text when permission is already revoked`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = true)
        val alreadyRevokedText = "Permission already revoked"
        coEvery { mockResourceManager.string("revokedDescription", permission.name) } returns alreadyRevokedText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(alreadyRevokedText), result)
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit success result with error text when permission is not a runtime permission`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = false)
        val errorText = "Install-time permissions are not supported"
        coEvery { mockResourceManager.string("errorInstallTimePermission") } returns errorText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(errorText), result)
        coVerify(exactly = 0) { mockDeviceManager.executeShellCommand(any()) }
    }

    @Test
    fun `invoke() should emit success result when permission revokes successfully`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true)
        val packageId = "com.example.package"
        val successText = "Permission revoked successfully"
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is PermissionAdbCommands.Revoke) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }
        coEvery {
            mockResourceManager.string(
                "successRevokeDescription",
                permission.name,
                packageId
            )
        } returns successText

        // Act
        val result = useCase.invoke(permission).single()

        // Assert
        assertEquals(Result.success(successText), result)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.Revoke && it.packageId == packageId && it.permission == permission.name
            })
        }

    }

    @Test
    fun `invoke() should emit failure result when permission revoke fails`() = runTest {
        // Arrange
        val permission = PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true)
        val packageId = "com.example.package"
        val errorText = "Failed to revoke permission"
        val adbCommand = PermissionAdbCommands.Revoke(packageId, permission.name)
        val errorMessage = "Permission revoke failed"
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is PermissionAdbCommands.Revoke) {
                Result.failure(Throwable(errorMessage))
            } else {
                error("Unexpected command")
            }
        }
        coEvery {
            mockResourceManager.string(
                "failedRevokeDescription",
                permission.name,
                packageId,
                errorMessage
            )
        } returns errorText

        // Act
        val actualErrorMessage = useCase.invoke(permission).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.Revoke && it.packageId == packageId && it.permission == adbCommand.permission
            })
        }
    }
}

package androidstudio.tools.missed.features.permission.domain.usecase.revokeall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCase
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RevokeAllPermissionUseCaseImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockDeviceManager: DeviceManager = mockk()
    private val mockFetchAllPermissionsUseCase: FetchAllPermissionsUseCase = mockk()
    private val mockRevokePermissionUseCase: RevokePermissionUseCase = mockk()
    private lateinit var useCase: RevokeAllPermissionUseCaseImpl

    @Before
    fun setUp() {
        useCase = RevokeAllPermissionUseCaseImpl(
            mockResourceManager,
            mockDeviceManager,
            mockFetchAllPermissionsUseCase,
            mockRevokePermissionUseCase
        )
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow("")
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when all permissions are revoked successfully`() = runTest {
        // Arrange
        val permissions = arrayListOf(
            PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true),
            PermissionStateModel("android.permission.ACCESS_FINE_LOCATION", isGranted = true, isRuntime = true)
        )
        val revokeResult = Result.success("Permission revoked successfully")
        val successResultMessage = "All permissions revoked successfully"
        val successResult = Result.success(successResultMessage)
        coEvery { mockFetchAllPermissionsUseCase.invoke() } returns flow { emit(Result.success(permissions)) }
        coEvery { mockRevokePermissionUseCase.invoke(any()) } returns flow { emit(revokeResult) }
        coEvery { mockResourceManager.string(any(), any()) } answers {
            if (args[0] == "permissionsRevokeAllSuccessTitle") {
                successResultMessage
            } else {
                "Unexpected resource"
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(successResult, result)
        coVerify(exactly = permissions.size) { mockRevokePermissionUseCase.invoke(any()) }
    }

    @Test
    fun `invoke() should emit success result when there are no permissions to revoke`() = runTest {
        // Arrange
        val emptyPermissions = arrayListOf<PermissionStateModel>()
        val successResultMessage = "All permissions revoked successfully"
        val successResult = Result.success(successResultMessage)
        coEvery { mockFetchAllPermissionsUseCase.invoke() } returns flow { emit(Result.success(emptyPermissions)) }
        coEvery { mockResourceManager.string("permissionsRevokeAllSuccessTitle", any()) } returns successResultMessage

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(successResult, result)
        coVerify(exactly = 0) { mockRevokePermissionUseCase.invoke(any()) }
    }

    @Test
    fun `invoke() should emit failure result when any permission revocation fails`() = runTest {
        // Arrange
        val permissions = arrayListOf(
            PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true),
            PermissionStateModel("android.permission.ACCESS_FINE_LOCATION", isGranted = true, isRuntime = true)
        )
        val revokeMessage = "Failed to revoke permission"
        val revokeResult = Result.failure<String>(Throwable(revokeMessage))
        coEvery { mockFetchAllPermissionsUseCase.invoke() } returns flow { emit(Result.success(permissions)) }
        coEvery { mockRevokePermissionUseCase.invoke(any()) } returns flow { emit(revokeResult) }
        coEvery { mockResourceManager.string("permissionsRevokeAllErrorTitle") } returns revokeMessage

        // Act
        val actualErrorMessage = useCase.invoke().single().exceptionOrNull()?.message

        // Assert
        assertEquals(revokeMessage, actualErrorMessage)
        coVerify(exactly = 2) { mockRevokePermissionUseCase.invoke(any()) }
    }
}

package androidstudio.tools.missed.features.permission.domain.usecase.grantall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCase
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
class GrantAllPermissionUseCaseImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockDeviceManager: DeviceManager = mockk()
    private val mockFetchAllPermissionsUseCase: FetchAllPermissionsUseCase = mockk()
    private val mockGrantPermissionUseCase: GrantPermissionUseCase = mockk()

    private lateinit var useCase: GrantAllPermissionUseCaseImpl

    @Before
    fun setUp() {
        useCase = GrantAllPermissionUseCaseImpl(
            mockResourceManager,
            mockDeviceManager,
            mockFetchAllPermissionsUseCase,
            mockGrantPermissionUseCase
        )
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow("")
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result when all permissions are granted successfully`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val successTitle = "All permissions granted successfully"
        val permission1 = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = true)
        val permission2 = PermissionStateModel("android.permission.READ_CONTACTS", isGranted = false, isRuntime = true)
        val permissions = arrayListOf(permission1, permission2)
        val grantResult = Result.success("")

        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockFetchAllPermissionsUseCase.invoke() } returns flow { emit(Result.success(permissions)) }
        coEvery { mockGrantPermissionUseCase.invoke(permission1) } returns flow { emit(grantResult) }
        coEvery { mockGrantPermissionUseCase.invoke(permission2) } returns flow { emit(grantResult) }
        coEvery { mockResourceManager.string("permissionsGrantAllSuccessTitle", packageId) } returns successTitle

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(Result.success(successTitle), result)
        coVerify(exactly = 1) { mockFetchAllPermissionsUseCase.invoke() }
        coVerify(exactly = 1) { mockGrantPermissionUseCase.invoke(permission1) }
        coVerify(exactly = 1) { mockGrantPermissionUseCase.invoke(permission2) }
        coVerify(exactly = 1) { mockResourceManager.string("permissionsGrantAllSuccessTitle", packageId) }
        coVerify(exactly = 0) { mockResourceManager.string("permissionsGrantAllErrorTitle") }
    }

    @Test
    fun `invoke() should emit failure result when any permission grant fails`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val errorTitle = "Failed to grant some permissions"
        val permission1 = PermissionStateModel("android.permission.CAMERA", isGranted = false, isRuntime = true)
        val permission2 = PermissionStateModel("android.permission.READ_CONTACTS", isGranted = false, isRuntime = true)
        val permissions = arrayListOf(permission1, permission2)
        val grantSuccess = Result.success("")
        val grantFailure = Result.failure<String>(Throwable("Permission grant failed"))

        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockFetchAllPermissionsUseCase.invoke() } returns flow { emit(Result.success(permissions)) }
        coEvery { mockGrantPermissionUseCase.invoke(permission1) } returns flow { emit(grantSuccess) }
        coEvery { mockGrantPermissionUseCase.invoke(permission2) } returns flow { emit(grantFailure) }
        coEvery { mockResourceManager.string("permissionsGrantAllErrorTitle") } returns errorTitle

        // Act
        val actualErrorMessage = useCase.invoke().single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorTitle, actualErrorMessage)
        coVerify(exactly = 1) { mockFetchAllPermissionsUseCase.invoke() }
        coVerify(exactly = 1) { mockGrantPermissionUseCase.invoke(permission1) }
        coVerify(exactly = 1) { mockGrantPermissionUseCase.invoke(permission2) }
        coVerify(exactly = 0) { mockResourceManager.string("permissionsGrantAllSuccessTitle", packageId) }
        coVerify(exactly = 1) { mockResourceManager.string("permissionsGrantAllErrorTitle") }
    }
}

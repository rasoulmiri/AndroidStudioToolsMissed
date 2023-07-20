package androidstudio.tools.missed.features.permission.domain.usecase.fetchall

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
class FetchAllPermissionsUseCaseImplTest {

    private val mockResourceManager: ResourceManager = mockk()
    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: FetchAllPermissionsUseCaseImpl

    @Before
    fun setUp() {
        useCase = FetchAllPermissionsUseCaseImpl(mockResourceManager, mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with permission list when shell command succeeds`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val allRuntimePermissions = "android.permission.CAMERA\nandroid.permission.LOCATION"
        val permissionResult = """
            android.permission.CAMERA: granted=true
            android.permission.LOCATION: granted=false
        """.trimIndent()
        val expectedResult = Result.success(
            arrayListOf(
                PermissionStateModel("android.permission.LOCATION",  isGranted = false, isRuntime =true),
                PermissionStateModel("android.permission.CAMERA", isGranted =  true, isRuntime =true),
            )
        )
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            when (args.first()) {
                is PermissionAdbCommands.AllRuntimePermissionDeviceSupported -> Result.success(allRuntimePermissions)
                is PermissionAdbCommands.AllPermissionInPackageIdInstalled -> Result.success(permissionResult)
                else -> error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllRuntimePermissionDeviceSupported
            })
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllPermissionInPackageIdInstalled &&
                        it.packageId == packageId
            })
        }
    }

    @Test
    fun `invoke() should emit failure result when shell command to get runtime permissions fails`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<ArrayList<PermissionStateModel>>(expectedException)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            when (args.first()) {
                is PermissionAdbCommands.AllPermissionInPackageIdInstalled -> Result.failure(expectedException)
                is PermissionAdbCommands.AllRuntimePermissionDeviceSupported -> Result.failure(expectedException)
                else -> error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllRuntimePermissionDeviceSupported
            })
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllPermissionInPackageIdInstalled &&
                        it.packageId == packageId
            })
        }

    }

    @Test
    fun `invoke() should emit failure result when shell command to get permissions fails`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val allRuntimePermissions = "android.permission.CAMERA\nandroid.permission.LOCATION"
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<ArrayList<PermissionStateModel>>(expectedException)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns MutableStateFlow(packageId)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            when (args.first()) {
                is PermissionAdbCommands.AllRuntimePermissionDeviceSupported -> Result.success(allRuntimePermissions)
                is PermissionAdbCommands.AllPermissionInPackageIdInstalled -> Result.failure(expectedException)
                else -> error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) {
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllRuntimePermissionDeviceSupported
            })
            mockDeviceManager.executeShellCommand(match {
                it is PermissionAdbCommands.AllPermissionInPackageIdInstalled &&
                        it.packageId == packageId
            })
        }
    }
}

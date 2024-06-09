package androidstudio.tools.missed.features.permission.domain.usecase.fetchall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
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
class FetchAllPermissionsUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: FetchAllPermissionsUseCaseImpl

    @Before
    fun setUp() {
        useCase = FetchAllPermissionsUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with permission list when shell command succeed`() = runTest {
        // Arrange
        val packageId = "com.example.package"
        val allRuntimePermissions = "android.permission.CAMERA\nandroid.permission.LOCATION"
        val permissionResult = "requested permissions:\n" +
                "      android.permission.CAMERA\n" +
                "      android.permission.LOCATION\n" +
                "    install permissions:\n" +
                "      android.permission.INTERNET: granted=true\n" +
                "      android.permission.QUERY_ALL_PACKAGES: granted=true\n" +
                "    User 0: ceDataInode=155749 installed=true hidden=false suspended=false distractionFlags=0 stopped=true notLaunched=true enabled=0 \n" +
                " android.permission.POST_NOTIFICATIONS: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]\n" +
                "   android.permission.ACCESS_FINE_LOCATION: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]\n" +
                "   android.permission.BLUETOOTH_CONNECT: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]\n" +
                "   android.permission.READ_EXTERNAL_STORAGE: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED|RESTRICTION_INSTALLER_EXEMPT]\n" +
                "   android.permission.ACCESS_COARSE_LOCATION: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]\n" +
                "   android.permission.CAMERA: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]".trimIndent()


        val expectedResult = Result.success(
            arrayListOf(
                PermissionStateModel("android.permission.CAMERA", isGranted = true, isRuntime = true),
                PermissionStateModel("android.permission.LOCATION", isGranted = false, isRuntime = true),
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
//        coVerify {
//            mockDeviceManager.executeShellCommand(match {
//                it is PermissionAdbCommands.AllRuntimePermissionDeviceSupported
//            })
//            mockDeviceManager.executeShellCommand(match {
//                it is PermissionAdbCommands.AllPermissionInPackageIdInstalled &&
//                        it.packageId == packageId
//            })
//        }
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

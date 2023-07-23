import androidstudio.tools.missed.features.apk.domain.usecase.downloadapk.DownloadApkFromDeviceUseCaseImpl
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DownloadApkFromDeviceUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: DownloadApkFromDeviceUseCaseImpl

    @Before
    fun setUp() {
        useCase = DownloadApkFromDeviceUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should return success when downloading APK successfully`() = runTest {
        // Arrange
        val packageId = "com.example.app"
        val saveDirectory = "/path/to/directory"
        val apkPathOnDevice = "package:/data/app/com.example.app/base.apk"

        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (this.args.first() is AdbCommand.GetPathBaseApkInDevice) {
                Result.success(apkPathOnDevice)
            } else if (this.args.first() is AdbCommand.CopyFile) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        coEvery { mockDeviceManager.pullFile(any(), any()) } returns Result.success("")

        // Act
        val result = useCase.invoke(saveDirectory, packageId).single()

        // Assert
        assertEquals(Result.success(true), result)
        coVerify(exactly = 2) { mockDeviceManager.executeShellCommand(any()) }
        coVerify(exactly = 1) { mockDeviceManager.pullFile(any(), any()) }
    }

    @Test
    fun `invoke() should return failure when getting APK path on device fails`() = runTest {
        // Arrange
        val packageId = "com.example.app"
        val saveDirectory = "/path/to/directory"
        val errorMessage = "Failed to get APK path on device"

        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (this.args.first() is AdbCommand.GetPathBaseApkInDevice) {
                Result.failure(Exception(errorMessage))
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val actualErrorMessage = useCase.invoke(saveDirectory, packageId).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 1) { mockDeviceManager.executeShellCommand(any()) }
        coVerify(exactly = 0) { mockDeviceManager.pullFile(any(), any()) }
    }

    @Test
    fun `invoke() should return failure when copying APK to device storage fails`() = runTest {
        // Arrange
        val packageId = "com.example.app"
        val saveDirectory = "/path/to/directory"
        val errorMessage = "Failed to copy APK to device storage"

        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (this.args.first() is AdbCommand.GetPathBaseApkInDevice) {
                Result.success("package:/data/app/com.example.app/base.apk")
            } else if (this.args.first() is AdbCommand.CopyFile) {
                Result.failure(Exception(errorMessage))
            } else {
                error("Unexpected command")
            }
        }
        coEvery { mockDeviceManager.pullFile(any(), any()) } returns Result.failure(Exception("Error"))

        // Act
        val actualErrorMessage = useCase.invoke(saveDirectory, packageId).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 2) { mockDeviceManager.executeShellCommand(any()) }
        coVerify(exactly = 0) { mockDeviceManager.pullFile(any(), any()) }
    }

    @Test
    fun `invoke() should return failure when pulling APK to save directory fails`() = runTest {
        // Arrange
        val packageId = "com.example.app"
        val saveDirectory = "/path/to/directory"
        val errorMessage = "Failed to pull APK to save directory"

        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (this.args.first() is AdbCommand.GetPathBaseApkInDevice) {
                Result.success("package:/data/app/com.example.app/base.apk")
            } else if (this.args.first() is AdbCommand.CopyFile) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        coEvery { mockDeviceManager.pullFile(any(), any()) } throws Exception(errorMessage)

        // Act
        val actualErrorMessage = useCase.invoke(saveDirectory, packageId).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
        coVerify(exactly = 2) { mockDeviceManager.executeShellCommand(any()) }
        coVerify(exactly = 1) { mockDeviceManager.pullFile(any(), any()) }
    }
}

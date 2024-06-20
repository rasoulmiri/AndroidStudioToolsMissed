import androidstudio.tools.missed.features.apkmanagement.domain.usecase.installapk.InstallApkUseCaseImpl
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class InstallApkUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: InstallApkUseCaseImpl

    @Before
    fun setUp() {
        useCase = InstallApkUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should return success when installing APK successfully`() = runTest {
        // Arrange
        val packageFilePath = "/path/to/app.apk"

        coEvery { mockDeviceManager.installApk(packageFilePath) } returns Result.success("Success")
        coEvery { mockDeviceManager.executeShellCommand(any()) } returns Result.success("Success")

        // Act
        val result = useCase.invoke(packageFilePath).single()

        // Assert
        assertEquals(Result.success(true), result)
    }

    @Test
    fun `invoke() should return failure when installing APK fails`() = runTest {
        // Arrange
        val packageFilePath = "/path/to/app.apk"
        val errorMessage = "Failed to install APK"

        coEvery { mockDeviceManager.installApk(packageFilePath) } returns Result.failure(IOException(errorMessage))
        coEvery { mockDeviceManager.executeShellCommand(any()) } returns Result.failure(IOException(errorMessage))

        // Act
        val actualErrorMessage = useCase.invoke(packageFilePath).single().exceptionOrNull()?.message

        // Assert
        assertEquals(errorMessage, actualErrorMessage)
    }
}

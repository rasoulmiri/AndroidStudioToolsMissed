package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add

import androidstudio.tools.missed.manager.adb.command.WhiteListAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WhiteListAddUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private val mockPackageIdSelectedStateFlow = MutableStateFlow<String?>(null)
    private lateinit var useCase: WhiteListAddUseCaseImpl

    @Before
    fun setUp() {
        useCase = WhiteListAddUseCaseImpl(mockDeviceManager)
        mockPackageIdSelectedStateFlow.value = "com.example.package"
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when shell command succeeds`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is WhiteListAdbCommands.Add) {
                    Result.success("Success")
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke().single()

            // Assert
            assertEquals(expectedResult, result)
        }

    @Test
    fun `invoke() should emit failure result when shell command fails`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is WhiteListAdbCommands.Add) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke().single()

        // Assert
        assertEquals(expectedResult, result)
    }
}

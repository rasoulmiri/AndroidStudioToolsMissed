package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.set

import androidstudio.tools.missed.manager.adb.command.StandbyAdbCommands
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

class StandbySetUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private val mockPackageIdSelectedStateFlow = MutableStateFlow<String?>(null)
    private lateinit var useCase: StandbySetUseCaseImpl

    @Before
    fun setUp() {
        useCase = StandbySetUseCaseImpl(mockDeviceManager)
        mockPackageIdSelectedStateFlow.value = "com.example.package"
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when shell command succeeds and active is set to true`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is StandbyAdbCommands.SetActive) {
                    Result.success("Success")
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke(isActive = true).single()

            // Assert
            assertEquals(expectedResult, result)
        }

    @Test
    fun `invoke() should emit success result with true when shell command succeeds and active is set to false`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is StandbyAdbCommands.SetDeactive) {
                    Result.success("Success")
                } else {
                    error("Unexpected command")
                }
            }

            // Act
            val result = useCase.invoke(isActive = false).single()

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
            if (args.first() is StandbyAdbCommands.SetActive) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(isActive = true).single()

        // Assert
        assertEquals(expectedResult, result)
    }
}

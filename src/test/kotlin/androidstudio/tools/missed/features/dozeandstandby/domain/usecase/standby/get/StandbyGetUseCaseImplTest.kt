package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get

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

class StandbyGetUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private val mockPackageIdSelectedStateFlow = MutableStateFlow<String?>(null)
    private val mockStandbyStateFlow = MutableStateFlow<Boolean?>(null)

    private lateinit var useCase: StandbyGetUseCaseImpl

    @Before
    fun setUp() {
        useCase = StandbyGetUseCaseImpl(mockDeviceManager)
        mockPackageIdSelectedStateFlow.value = "com.example.package"
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when shell command succeed and standby state is active`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(true)
            mockStandbyStateFlow.value = true
            coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is StandbyAdbCommands.GetState) {
                    Result.success("Idle=true")
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
    fun `invoke() should emit success result with false when shell command succeed and standby state is inactive`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(false)
            mockStandbyStateFlow.value = false
            coEvery { mockDeviceManager.packageIdSelectedStateFlow } returns mockPackageIdSelectedStateFlow.asStateFlow()
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is StandbyAdbCommands.GetState) {
                    Result.success("Idle=false")
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
            if (args.first() is StandbyAdbCommands.GetState) {
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

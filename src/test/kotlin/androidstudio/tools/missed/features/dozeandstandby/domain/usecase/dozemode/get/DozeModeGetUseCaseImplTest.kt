package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get

import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.entity.DozeModeGetStateModel
import androidstudio.tools.missed.manager.adb.command.DozeAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DozeModeGetUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: DozeModeGetUseCaseImpl

    @Before
    fun setUp() {
        useCase = DozeModeGetUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with DozeModeGetStateModel when shell command succeeds`() = runTest {
        // Arrange
        val expectedOutput = "Stepped to deep: IDLE"
        val expectedResult = Result.success(DozeModeGetStateModel(true, "IDLE"))
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DozeAdbCommands.GetState) {
                Result.success(expectedOutput)
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
    fun `invoke() should emit success result with DozeModeGetStateModel when shell command succeeds with different state`() = runTest {
        // Arrange
        val expectedOutput = "Stepped to deep: IDLE_MAINTENANCE"
        val expectedResult = Result.success(DozeModeGetStateModel(true, "IDLE_MAINTENANCE"))
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DozeAdbCommands.GetState) {
                Result.success(expectedOutput)
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
    fun `invoke() should emit success result with DozeModeGetStateModel when shell command succeeds and Doze mode is inactive`() = runTest {
        // Arrange
        val expectedOutput = "Some other output"
        val expectedResult = Result.success(DozeModeGetStateModel(false, expectedOutput))
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DozeAdbCommands.GetState) {
                Result.success(expectedOutput)
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
        val expectedResult = Result.failure<DozeModeGetStateModel>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is DozeAdbCommands.GetState) {
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

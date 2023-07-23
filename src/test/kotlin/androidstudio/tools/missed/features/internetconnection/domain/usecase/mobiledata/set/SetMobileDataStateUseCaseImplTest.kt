package androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.set

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
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

class SetMobileDataStateUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: SetMobileDataStateUseCaseImpl

    @Before
    fun setUp() {
        useCase = SetMobileDataStateUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with true when setting mobile data state on`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.SetMobileDataState) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit success result with true when setting mobile data state off`() = runTest {
        // Arrange
        val expectedResult = Result.success(true)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.SetMobileDataState) {
                Result.success("")
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(false).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit failure result when shell command fails while setting mobile data state on`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.SetMobileDataState) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(true).single()

        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke() should emit failure result when shell command fails while setting mobile data state off`() = runTest {
        // Arrange
        val expectedException = RuntimeException("Command failed")
        val expectedResult = Result.failure<Boolean>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is InternetAdbConnectionCommands.SetMobileDataState) {
                Result.failure(expectedException)
            } else {
                error("Unexpected command")
            }
        }

        // Act
        val result = useCase.invoke(false).single()

        // Assert
        assertEquals(expectedResult, result)
    }
}

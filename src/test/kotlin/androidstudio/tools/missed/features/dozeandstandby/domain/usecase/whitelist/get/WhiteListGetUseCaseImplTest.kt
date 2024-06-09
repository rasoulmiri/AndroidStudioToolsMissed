package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get

import androidstudio.tools.missed.manager.adb.command.WhiteListAdbCommands
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

class WhiteListGetUseCaseImplTest {

    private val mockDeviceManager: DeviceManager = mockk()
    private lateinit var useCase: WhiteListGetUseCaseImpl

    @Before
    fun setUp() {
        useCase = WhiteListGetUseCaseImpl(mockDeviceManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `invoke() should emit success result with parsed white list when shell command succeed`() =
        runTest {
            // Arrange
            val expectedResult = Result.success(arrayListOf("com.example.app1", "com.example.app2"))
            val successResult = "com.example.app2\ncom.example.app1"
            coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
                if (args.first() is WhiteListAdbCommands.FetchAll) {
                    Result.success(successResult)
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
        val expectedResult = Result.failure<ArrayList<String>>(expectedException)
        coEvery { mockDeviceManager.executeShellCommand(any()) } coAnswers {
            if (args.first() is WhiteListAdbCommands.FetchAll) {
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

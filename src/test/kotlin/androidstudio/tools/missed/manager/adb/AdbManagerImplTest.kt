package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.device.model.Device
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import io.ktor.utils.io.core.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.android.sdk.AndroidSdkUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File

class AdbManagerImplTest {

    private val mockAdbLogger: AdbLogger = mockk(relaxed = true)
    private val mockResourceManager: ResourceManager = mockk(relaxed = true)

    private lateinit var adbManager: AdbManagerImpl

    @Before
    fun setUp() {
        adbManager = AdbManagerImpl(
            adbLogger = mockAdbLogger,
            resourceManager = mockResourceManager
        )
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `initialAdb() should return success if connected to ADB`() = runTest {
        // Arrange
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(
            File("ADB PATH"),
            emptyList<String>()
        )

        // Act
        val result = adbManager.initialAdb()

        // Assert
        assert(result.isSuccess)
        assert(result.getOrNull() == true)
    }

    @Test
    fun `initialAdb() should return failure if not found the ADB path`() = runTest {
        // Arrange
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(null, emptyList<String>())

        // Act
        val result = adbManager.initialAdb()

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("adbConnectionIssue"))
    }


    @Test
    fun `getDevices() should return success if found the ADB path`() = runTest {
        // Arrange
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(
            File("ADB PATH"),
            emptyList<String>()
        )

        // Mock Runtime class and its methods
        mockkStatic(Runtime::class)
        // Mock input stream, error stream, and process
        val inputStream = ByteArrayInputStream(
            ("List of devices attached\n" +
                    "ce0516056bcc220e05\tdevice\n" +
                    "emulator-5554\tdevice").toByteArray()
        )
        val errorStream = ByteArrayInputStream("".toByteArray())
        val process = mockk<Process>(relaxed = true)
        every { process.inputStream } returns inputStream
        every { process.errorStream } returns errorStream

        // Mock Runtime.getRuntime().exec() to return our process
        every { Runtime.getRuntime().exec(any<String>()) } returns process

        // Mock BufferedReader
        mockkConstructor(BufferedReader::class)
        every { anyConstructed<BufferedReader>().readLine() } returnsMany listOf(
            "List of devices attached",
            "ce0516056bcc220e05\tdevice",
            "emulator-5554\tdevice",
            null
        )


        // Act
        val result = adbManager.initialAdb()
        val devices = adbManager.getDevices()

        // Assert
        // Assert
        assert(result.isSuccess)
        assert(result.getOrNull() == true)
        assert(devices.isSuccess)
        assert(devices.getOrNull()?.size == 2)
    }

    @Test
    fun `getDevices() should return failure if not found the ADB path`() = runTest {
        // Arrange
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(
            null,
            emptyList<String>()
        )

        // Mock Runtime class and its methods
        mockkStatic(Runtime::class)
        // Mock input stream, error stream, and process
        val inputStream = ByteArrayInputStream(
            ("List of devices attached\n" +
                    "ce0516056bcc220e05\tdevice\n" +
                    "emulator-5554\tdevice").toByteArray()
        )
        val errorStream = ByteArrayInputStream("".toByteArray())
        val process = mockk<Process>(relaxed = true)
        every { process.inputStream } returns inputStream
        every { process.errorStream } returns errorStream

        // Mock Runtime.getRuntime().exec() to return our process
        every { Runtime.getRuntime().exec(any<String>()) } returns process

        // Mock BufferedReader
        mockkConstructor(BufferedReader::class)
        every { anyConstructed<BufferedReader>().readLine() } returnsMany listOf(
            "List of devices attached",
            "ce0516056bcc220e05\tdevice",
            "emulator-5554\tdevice",
            null
        )


        // Act
        val result = adbManager.initialAdb()
        val devices = adbManager.getDevices()

        // Assert
        assert(result.isFailure)
        assert(devices.isFailure)
    }

    @Test
    fun `test run - success`() {
        // Mock Runtime class and its methods
        mockkStatic(Runtime::class)

        // Mock input stream, error stream, and process
        val inputStream = ByteArrayInputStream("output line 1\noutput line 2\n".toByteArray())
        val errorStream = ByteArrayInputStream("".toByteArray())
        val process = mockk<Process>(relaxed = true)
        every { process.inputStream } returns inputStream
        every { process.errorStream } returns errorStream


        // Mock Runtime.getRuntime().exec() to return our process
        every { Runtime.getRuntime().exec("some_command") } returns process

        // Mock BufferedReader
        mockkConstructor(BufferedReader::class)
        every { anyConstructed<BufferedReader>().readLine() } returnsMany listOf("output line 1", "output line 2", null)

        // Call the run function with a command
        val result = adbManager.runtimeExec("some_command")

        // Verify the result
        assert(result.isSuccess)
        assert(result.getOrNull() == "output line 1\noutput line 2\n")

    }


    @Test
    fun `executeCustomCommand should call runtimeExec`() = runTest {

        // Arrange
        val command = "ls"
        val packageId = "com.example.app"
        val customCommand = CustomCommand(id = 123, index = 1, name = "customCommandTitle", description = "customCommandDescription", command = command)
        val device = Device(name = "device1", id = "deviceId1")
        val adbManagerSpy = spyk(AdbManagerImpl(mockResourceManager, mockAdbLogger))

        // Act
        adbManagerSpy.executeCustomCommand(device = device, packageId = packageId, customCommand)

        // Verify the result
        verify {
            adbManagerSpy.runtimeExec(command)
        }

    }


    @Test
    fun `executeCustomCommand should call runtimeExec with adb path`() = runTest {

        // Arrange
        val command = "\$ADB ls"
        val packageId = "com.example.app"
        val customCommand = CustomCommand(id = 123, index = 1, name = "customCommandTitle", description = "customCommandDescription", command = command)
        val device = Device(name = "device1", id = "deviceId1")
        val adbManagerSpy = spyk(AdbManagerImpl(mockResourceManager, mockAdbLogger))

        val adbPath = "/Users/User/Library/Android/sdk/platform-tools/adb"
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(
            File(adbPath),
            emptyList<String>()
        )

        // Act
        adbManagerSpy.initialAdb()
        adbManagerSpy.executeCustomCommand(device = device, packageId = packageId, customCommand)

        // Verify the result
        verify {
            adbManagerSpy.runtimeExec("/Users/User/Library/Android/sdk/platform-tools/adb -s deviceId1 ls")
        }
    }

    @Test
    fun `executeCustomCommand should call runtimeExec with app id`() = runTest {

        // Arrange
        val command = "\$ADB \$APP_ID ls"
        val packageId = "com.example.app"
        val customCommand = CustomCommand(id = 123, index = 1, name = "customCommandTitle", description = "customCommandDescription", command = command)
        val device = Device(name = "device1", id = "deviceId1")
        val adbManagerSpy = spyk(AdbManagerImpl(mockResourceManager, mockAdbLogger))

        val adbPath = "/Users/User/Library/Android/sdk/platform-tools/adb"
        mockkStatic(ProjectManager::class)
        mockkStatic(AndroidSdkUtils::class)
        val projectManager = mockk<ProjectManager>(relaxed = true)
        val projectMock = mockk<Project>(relaxed = true)
        every { ProjectManager.getInstance() } returns projectManager
        every { projectManager.openProjects } returns arrayOf(projectMock)
        every { AndroidSdkUtils.findAdb(any()) } returns AndroidSdkUtils.AdbSearchResult(
            File(adbPath),
            emptyList<String>()
        )

        // Act
        adbManagerSpy.initialAdb()
        adbManagerSpy.executeCustomCommand(device = device, packageId = packageId, customCommand)

        // Verify the result
        verify {
            adbManagerSpy.runtimeExec("/Users/User/Library/Android/sdk/platform-tools/adb -s deviceId1 $packageId ls")
        }
    }


}

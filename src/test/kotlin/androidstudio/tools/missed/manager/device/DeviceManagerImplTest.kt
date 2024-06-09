package androidstudio.tools.missed.manager.device

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device
import androidstudio.tools.missed.manager.resource.ResourceManager
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class DeviceManagerImplTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())
    private val mockAdbManager: AdbManager = mockk(relaxed = true)
    private val mockResourceManager: ResourceManager = mockk(relaxed = true)

    private lateinit var deviceManager: DeviceManagerImpl

    @Before
    fun setUp() {
        deviceManager = DeviceManagerImpl(resourceManager = mockResourceManager, adbManager = mockAdbManager)
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `configure should return success if ADB is initialized and devices are retrieved successfully`() = runTest {
        // Arrange
        val mockDeviceInformation1 = mockk<Device>(relaxed = true)
        val mockDeviceInformation2 = mockk<Device>(relaxed = true)
        val devices = listOf(mockDeviceInformation1, mockDeviceInformation2)
        coEvery { mockAdbManager.initialAdb() } returns Result.success(true)
        coEvery { mockAdbManager.getDevices() } returns Result.success(devices)

        // Act
        val result = deviceManager.configure(testScope)

        // Assert
        assert(result.isSuccess)
        assert(deviceManager.devicesStateFlow.value == devices)
        assert(deviceManager.selectedDeviceStateFlow.value == mockDeviceInformation1)
        coVerify(exactly = 1) { mockAdbManager.initialAdb() }
        coVerify(exactly = 1) { mockAdbManager.getDevices() }
    }

    @Test
    fun `configure should return failure if ADB initialization fails`() = runTest {
        // Arrange
        coEvery { mockAdbManager.initialAdb() } returns Result.failure(Throwable("ADB initialization failed"))

        // Act
        val result = deviceManager.configure(testScope)

        // Assert
        assert(result.isFailure)
        coVerify(exactly = 1) { mockAdbManager.initialAdb() }
        coVerify(exactly = 0) { mockAdbManager.getDevices() }
    }

    @Test
    fun `configure should return failure if device retrieval from ADB fails`() = runTest {
        // Arrange
        coEvery { mockAdbManager.initialAdb() } returns Result.success(true)
        coEvery { mockAdbManager.getDevices() } returns Result.failure(Throwable("Device retrieval failed"))

        // Act
        val result = deviceManager.configure(testScope)

        // Assert
        assert(result.isFailure)
        coVerify(exactly = 1) { mockAdbManager.initialAdb() }
        coVerify(exactly = 1) { mockAdbManager.getDevices() }
    }

    @Test
    fun `setSelectedDevice should update selectedDeviceStateFlow`() = runTest {
        // Arrange
        val mockDeviceInformation = mockk<Device>(relaxed = true)

        // Act
        deviceManager.setSelectedDevice(mockDeviceInformation)

        // Assert
        assert(deviceManager.selectedDeviceStateFlow.value == mockDeviceInformation)
    }

    @Test
    fun `setSelectedApplication should update applicationSelectedStateFlow`() = runTest {
        // Arrange
        val packageId = "com.example.app"

        // Act
        deviceManager.setSelectedPackageId(packageId)

        // Assert
        assert(deviceManager.packageIdSelectedStateFlow.value == packageId)
    }

    @Test
    fun `getDeviceSelectedName should return the concatenated brand and model of the selected device`() = runTest {
        // Arrange
        val mockDeviceInformation = mockk<Device>(relaxed = true)
        every { mockDeviceInformation.name } returns "Samsung Galaxy S10"
        deviceManager.setSelectedDevice(mockDeviceInformation)

        // Act
        val result = deviceManager.getDeviceSelectedName()

        // Assert
        assert(result == "Samsung Galaxy S10")
    }

    @Test
    fun `executeShellCommand should return failure if a device is not selected`() = runTest {
        // Arrange
        val adbCommand = mockk<AdbCommand>(relaxed = true)
        every { adbCommand.isNeedDevice } returns true
        deviceManager.setSelectedDevice(null)

        // Act
        val result = deviceManager.executeShellCommand(adbCommand)

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("selectADevice"))
        coVerify(exactly = 0) { mockAdbManager.executeADBShellCommand(any(), any()) }
    }

    @Test
    fun `executeShellCommand should return failure if an application is not selected`() = runTest {
        // Arrange
        val adbCommand = mockk<AdbCommand>(relaxed = true)
        every { adbCommand.isNeedDevice } returns true
        every { adbCommand.isNeedPackageId } returns true

        val mockDeviceInformation1 = mockk<Device>(relaxed = true)
        val mockDeviceInformation2 = mockk<Device>(relaxed = true)
        val devices = listOf(mockDeviceInformation1, mockDeviceInformation2)
        coEvery { mockAdbManager.initialAdb() } returns Result.success(true)
        coEvery { mockAdbManager.getDevices() } returns Result.success(devices)
        deviceManager.configure(testScope)
        deviceManager.setSelectedDevice(mockDeviceInformation1)
        deviceManager.setSelectedPackageId(null)

        // Act
        val result = deviceManager.executeShellCommand(adbCommand)

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("selectAnApplication"))
        coVerify(exactly = 0) { mockAdbManager.executeADBShellCommand(any(), any()) }
    }

    @Test
    fun `executeShellCommand should call adbManager executeADBShellCommand with correct arguments`() = runTest {
        // Arrange
        val adbCommand = mockk<AdbCommand>(relaxed = true)
        val mockDeviceInformation = mockk<Device>(relaxed = true)
        val expectedResult = Result.success("Command executed")
        deviceManager.setSelectedDevice(mockDeviceInformation)
        deviceManager.setSelectedPackageId("com.example.app")
        coEvery { mockAdbManager.executeADBShellCommand(mockDeviceInformation, adbCommand) } returns expectedResult

        // Act
        val result = deviceManager.executeShellCommand(adbCommand)

        // Assert
        assert(result == expectedResult)
        coVerify(exactly = 1) { mockAdbManager.executeADBShellCommand(mockDeviceInformation, adbCommand) }
    }

    @Test
    fun `executeCustomCommand should call adbManager executeCustomCommand with correct arguments`() = runTest {
        // Arrange
        val customCommand = CustomCommand(id = 123, index = 1, name = "Test", description = "test", command = "ls")
        val mockDeviceInformation = mockk<Device>(relaxed = true)
        val expectedResult = Result.success("Command executed")
        deviceManager.setSelectedDevice(mockDeviceInformation)
        val packageId = "com.example.app"
        deviceManager.setSelectedPackageId(packageId)
        coEvery {
            mockAdbManager.executeCustomCommand(
                device = mockDeviceInformation,
                packageId = packageId,
                command = customCommand
            )
        } returns expectedResult

        // Act
        val result = deviceManager.executeCustomCommand(customCommand)

        // Assert
        assert(result == expectedResult)
        coVerify(exactly = 1) {
            mockAdbManager.executeCustomCommand(
                device = mockDeviceInformation,
                packageId = packageId,
                command = customCommand
            )
        }
    }
}

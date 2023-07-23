package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.device.model.toDeviceInformation
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdbManagerImplTest {

    private val mockADB: AndroidDebugBridge = mockk(relaxed = true)
    private val mockAdbLogger: AdbLogger = mockk(relaxed = true)
    private val mockResourceManager: ResourceManager = mockk(relaxed = true)

    private lateinit var adbManager: AdbManager

    @Before
    fun setUp() {
        adbManager = AdbManagerImpl(
            androidDebugBridge = mockADB,
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
        every { mockADB.isConnected } returns true
        every { mockADB.hasInitialDeviceList() } returns true

        // Act
        val result = adbManager.initialAdb()

        // Assert
        assert(result.isSuccess)
        assert(result.getOrNull() == true)
        verify(exactly = 2) { mockADB.isConnected }
        verify(exactly = 2) { mockADB.hasInitialDeviceList() }
    }

    @Test
    fun `initialAdb() should return failure if not connected to ADB`() = runTest {
        // Arrange
        every { mockADB.isConnected } returns false

        // Act
        val result = adbManager.initialAdb()

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("adbConnectionIssue"))
        verify(exactly = 11) { mockADB.isConnected }
    }

    @Test
    fun `initialAdb() should return failure if initial device list retrieval fails`() = runTest {
        // Arrange
        every { mockADB.isConnected } returns true
        every { mockADB.hasInitialDeviceList() } returns false

        // Act
        val result = adbManager.initialAdb()

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("getDevicesFromAdbError"))
        verify(exactly = 2) { mockADB.isConnected }
        verify(exactly = 11) { mockADB.hasInitialDeviceList() }
    }

    @Test
    fun `getDevices() should return success with device list if ADB is initialized`() = runTest {
        // Arrange
        every { mockADB.isConnected } returns true
        every { mockADB.hasInitialDeviceList() } returns true
        val mockDevice1 = mockk<IDevice>(relaxed = true)
        val mockDevice2 = mockk<IDevice>(relaxed = true)
        val mockDevice3 = mockk<IDevice>(relaxed = true)
        every { mockDevice1.getProperty("ro.product.brand") } returns "brand1"
        every { mockDevice3.getProperty("ro.product.brand") } returns "brand3"
        every { mockDevice1.getProperty("ro.product.model") } returns "model1"
        every { mockDevice3.getProperty("ro.product.model") } returns "model3"
        every { mockADB.devices } returns arrayOf(mockDevice1, mockDevice2, mockDevice3)
        every { mockDevice1.isOnline } returns true
        every { mockDevice2.isOnline } returns false
        every { mockDevice3.isOnline } returns true
        every { mockDevice1.serialNumber } returns "1"
        every { mockDevice2.serialNumber } returns "2"
        every { mockDevice3.serialNumber } returns "3"

        // Act
        val result = adbManager.getDevices()

        // Assert
        assert(result.isSuccess)
        assert(result.getOrNull()?.size == 2)
        assert(result.getOrNull()?.get(0)?.title == "BRAND1 MODEL1 [1]")
        assert(result.getOrNull()?.get(1)?.title == "BRAND3 MODEL3 [3]")
        verify(exactly = 1) { mockADB.devices }
        verify(exactly = 1) { mockDevice1.isOnline }
        verify(exactly = 1) { mockDevice2.isOnline }
        verify(exactly = 1) { mockDevice3.isOnline }
        verify(exactly = 1) { mockDevice1.toDeviceInformation() }
        verify(exactly = 1) { mockDevice3.toDeviceInformation() }
    }

    @Test
    fun `getDevices() should return failure if ADB is not initialized`() = runTest {
        // Arrange
        every { mockADB.isConnected } returns false
        every { mockADB.hasInitialDeviceList() } returns true

        // Act
        val result = adbManager.getDevices()

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == mockResourceManager.string("adbIsNotInitialize"))
    }
}

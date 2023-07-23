package androidstudio.tools.missed.manager.device.di

import androidstudio.tools.missed.manager.adb.di.adbManagerModule
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.di.resourceManagerModule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class DeviceManagerModuleTest : KoinTest {

    private val preDependencies = resourceManagerModule + adbManagerModule
    private val testModule = preDependencies + deviceManagerModule

    private val deviceManager: DeviceManager by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun cleanup() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun `deviceManager should not be null`() {
        assertNotNull(deviceManager)
    }
}

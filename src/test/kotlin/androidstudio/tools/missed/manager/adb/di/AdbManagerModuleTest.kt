package androidstudio.tools.missed.manager.adb.di

import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
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

class AdbManagerModuleTest : KoinTest {

    private val preDependencies = resourceManagerModule
    private val testModule = preDependencies + adbManagerModule

    private val adbLogger: AdbLogger by inject()
    private val adbManager: AdbManager by inject()

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
    fun `adbLogger should not be null`() {
        assertNotNull(adbLogger)
    }

    @Test
    fun `adbManager should not be null`() {
        assertNotNull(adbManager)
    }
}

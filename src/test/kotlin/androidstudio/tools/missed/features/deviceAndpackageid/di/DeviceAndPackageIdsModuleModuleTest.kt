package androidstudio.tools.missed.features.deviceAndpackageid.di

import androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase.GetPackageIdsInstalledInDeviceUseCase
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.DevicesAndPackageIdsViewModel
import androidstudio.tools.missed.manager.adb.di.adbManagerModule
import androidstudio.tools.missed.manager.device.di.deviceManagerModule
import androidstudio.tools.missed.manager.resource.di.resourceManagerModule
import androidstudio.tools.missed.utils.coroutines.dispatcher.coroutinesDispatcherIOModule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class DeviceAndPackageIdsModuleModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + deviceAndPackageIdsModule

    private val devicesAndPackageIdsViewModel: DevicesAndPackageIdsViewModel by inject()
    private val getPackageIdsInstalledInDeviceUseCase: GetPackageIdsInstalledInDeviceUseCase by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun cleanup() {
        unmockkAll()
        GlobalContext.stopKoin()
    }

    @Test
    fun `devicesAndPackageIdsViewModel should not be null`() {
        assertNotNull(devicesAndPackageIdsViewModel)
    }

    @Test
    fun `getPackageIdsInstalledInDeviceUseCase should not be null`() {
        assertNotNull(getPackageIdsInstalledInDeviceUseCase)
    }

}

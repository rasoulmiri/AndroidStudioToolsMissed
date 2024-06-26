package androidstudio.tools.missed.features.apkmanagement.di

import androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk.DownloadApkFromDeviceUseCase
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.installapk.InstallApkUseCase
import androidstudio.tools.missed.features.apkmanagement.presenter.ApkManagementViewModel
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

class ApkManagementModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + apkManagementModule

    private val downloadApkFromDeviceUseCase: DownloadApkFromDeviceUseCase by inject()
    private val installApkUseCase: InstallApkUseCase by inject()
    private val apkManagementViewModel: ApkManagementViewModel by inject()

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
    fun `downloadApkFromDeviceUseCase should not be null`() {
        assertNotNull(downloadApkFromDeviceUseCase)
    }

    @Test
    fun `installApkUseCase should not be null`() {
        assertNotNull(installApkUseCase)
    }

    @Test
    fun `apkViewModel should not be null`() {
        assertNotNull(apkManagementViewModel)
    }

}

package androidstudio.tools.missed.features.customcommand.di

import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.presenter.CustomCommandViewModel
import androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog.CustomDialogViewModel
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

class CustomCommandModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + customCommandModule

    private val customCommandRepository: CustomCommandRepository by inject()
    private val customCommandUseCase: CustomCommandUseCase by inject()
    private val customCommandViewModel: CustomCommandViewModel by inject()
    private val customDialogViewModel: CustomDialogViewModel by inject()

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
    fun `customCommandRepository should not be null`() {
        assertNotNull(customCommandRepository)
    }

    @Test
    fun `customCommandUseCase should not be null`() {
        assertNotNull(customCommandUseCase)
    }

    @Test
    fun `customCommandViewModel should not be null`() {
        assertNotNull(customCommandViewModel)
    }

    @Test
    fun `customDialogViewModel should not be null`() {
        assertNotNull(customDialogViewModel)
    }

}

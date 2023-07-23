package androidstudio.tools.missed.features.dozeandstandby.di

import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.DozeModeGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.set.DozeModeSetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get.StandbyGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.set.StandbySetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add.WhiteListAddUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get.WhiteListGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove.WhiteListRemoveUseCase
import androidstudio.tools.missed.features.dozeandstandby.presenter.DozeAndStandbyViewModel
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

class DozeAndStandbyModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + dozeAndStandbyModule

    private val dozeModeGetUseCase: DozeModeGetUseCase by inject()
    private val dozeModeSetUseCase: DozeModeSetUseCase by inject()
    private val standbyGetUseCase: StandbyGetUseCase by inject()
    private val standbySetUseCase: StandbySetUseCase by inject()
    private val whiteListGetUseCase: WhiteListGetUseCase by inject()
    private val whiteListAddUseCase: WhiteListAddUseCase by inject()
    private val whiteListRemoveUseCase: WhiteListRemoveUseCase by inject()
    private val dozeAndStandbyViewModel: DozeAndStandbyViewModel by inject()

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
    fun `dozeModeGetUseCase should not be null`() {
        assertNotNull(dozeModeGetUseCase)
    }

    @Test
    fun `dozeModeSetUseCase should not be null`() {
        assertNotNull(dozeModeSetUseCase)
    }

    @Test
    fun `standbyGetUseCase should not be null`() {
        assertNotNull(standbyGetUseCase)
    }

    @Test
    fun `standbySetUseCase should not be null`() {
        assertNotNull(standbySetUseCase)
    }

    @Test
    fun `whiteListGetUseCase should not be null`() {
        assertNotNull(whiteListGetUseCase)
    }

    @Test
    fun `whiteListAddUseCase should not be null`() {
        assertNotNull(whiteListAddUseCase)
    }

    @Test
    fun `whiteListRemoveUseCase should not be null`() {
        assertNotNull(whiteListRemoveUseCase)
    }

    @Test
    fun `dozeAndStandbyViewModel should not be null`() {
        assertNotNull(dozeAndStandbyViewModel)
    }

}

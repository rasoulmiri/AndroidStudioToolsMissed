package androidstudio.tools.missed.features.input.di

import androidstudio.tools.missed.features.input.domain.usecase.cleartext.ClearTextUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendevent.SendEventUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendtext.SendTextUseCase
import androidstudio.tools.missed.features.input.presenter.InputTextViewModel
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

class InputModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + inputModule

    private val sendTextUseCase: SendTextUseCase by inject()
    private val sendEventUseCase: SendEventUseCase by inject()
    private val clearTextUseCase: ClearTextUseCase by inject()
    private val inputTextViewModel: InputTextViewModel by inject()

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
    fun `sendTextUseCase should not be null`() {
        assertNotNull(sendTextUseCase)
    }

    @Test
    fun `sendEventUseCase should not be null`() {
        assertNotNull(sendEventUseCase)
    }

    @Test
    fun `clearTextUseCase should not be null`() {
        assertNotNull(clearTextUseCase)
    }

    @Test
    fun `inputTextViewModel should not be null`() {
        assertNotNull(inputTextViewModel)
    }

}

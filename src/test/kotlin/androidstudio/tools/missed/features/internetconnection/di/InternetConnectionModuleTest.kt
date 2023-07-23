package androidstudio.tools.missed.features.internetconnection.di

import androidstudio.tools.missed.features.internetconnection.domain.usecase.airplane.get.GetAirplaneStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.airplane.set.SetAirplaneStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.bluetooth.get.GetBluetoothStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.bluetooth.set.SetBluetoothStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.get.GetMobileDataStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.set.SetMobileDataStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.get.GetWifiStateUseCase
import androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.set.SetWifiStateUseCase
import androidstudio.tools.missed.features.internetconnection.presenter.InternetConnectionViewModel
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

class InternetConnectionModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + internetConnectionModule

    private val getAirplaneStateUseCase: GetAirplaneStateUseCase by inject()
    private val setAirplaneStateUseCase: SetAirplaneStateUseCase by inject()
    private val getMobileDataStateUseCase: GetMobileDataStateUseCase by inject()
    private val setMobileDataStateUseCase: SetMobileDataStateUseCase by inject()
    private val getWifiStateUseCase: GetWifiStateUseCase by inject()
    private val setWifiStateUseCase: SetWifiStateUseCase by inject()
    private val getBluetoothStateUseCase: GetBluetoothStateUseCase by inject()
    private val setBluetoothStateUseCase: SetBluetoothStateUseCase by inject()
    private val internetConnectionViewModel: InternetConnectionViewModel by inject()

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
    fun `getAirplaneStateUseCase should not be null`() {
        assertNotNull(getAirplaneStateUseCase)
    }

    @Test
    fun `setAirplaneStateUseCase should not be null`() {
        assertNotNull(setAirplaneStateUseCase)
    }

    @Test
    fun `getMobileDataStateUseCase should not be null`() {
        assertNotNull(getMobileDataStateUseCase)
    }

    @Test
    fun `setMobileDataStateUseCase should not be null`() {
        assertNotNull(setMobileDataStateUseCase)
    }

    @Test
    fun `getWifiStateUseCase should not be null`() {
        assertNotNull(getWifiStateUseCase)
    }

    @Test
    fun `setWifiStateUseCase should not be null`() {
        assertNotNull(setWifiStateUseCase)
    }

    @Test
    fun `getBluetoothStateUseCase should not be null`() {
        assertNotNull(getBluetoothStateUseCase)
    }

    @Test
    fun `setBluetoothStateUseCase should not be null`() {
        assertNotNull(setBluetoothStateUseCase)
    }

    @Test
    fun `internetConnectionViewModel should not be null`() {
        assertNotNull(internetConnectionViewModel)
    }

}

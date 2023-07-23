package androidstudio.tools.missed.features.battery.di

import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get.GetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set.SetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get.GetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set.SetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get.GetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set.SetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig.ResetBatteryConfigUseCase
import androidstudio.tools.missed.features.battery.presenter.BatteryViewModel
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

class BatteryModuleTest : KoinTest {

    private val preDependencies =
        coroutinesDispatcherIOModule +
                resourceManagerModule +
                adbManagerModule +
                deviceManagerModule
    private val testModule = preDependencies + batteryModule

    private val getChargerConnectionUseCase: GetChargerConnectionUseCase by inject()
    private val setChargerConnectionUseCase: SetChargerConnectionUseCase by inject()
    private val getBatteryLevelUseCase: GetBatteryLevelUseCase by inject()
    private val setBatteryLevelUseCase: SetBatteryLevelUseCase by inject()
    private val getPowerSavingUseCase: GetPowerSavingUseCase by inject()
    private val setPowerSavingUseCase: SetPowerSavingUseCase by inject()
    private val resetBatteryConfigUseCase: ResetBatteryConfigUseCase by inject()
    private val batteryViewModel: BatteryViewModel by inject()

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
    fun `getChargerConnectionUseCase should not be null`() {
        assertNotNull(getChargerConnectionUseCase)
    }

    @Test
    fun `setChargerConnectionUseCase should not be null`() {
        assertNotNull(setChargerConnectionUseCase)
    }

    @Test
    fun `getBatteryLevelUseCase should not be null`() {
        assertNotNull(getBatteryLevelUseCase)
    }

    @Test
    fun `setBatteryLevelUseCase should not be null`() {
        assertNotNull(setBatteryLevelUseCase)
    }

    @Test
    fun `getPowerSavingUseCase should not be null`() {
        assertNotNull(getPowerSavingUseCase)
    }

    @Test
    fun `setPowerSavingUseCase should not be null`() {
        assertNotNull(setPowerSavingUseCase)
    }

    @Test
    fun `resetBatteryConfigUseCase should not be null`() {
        assertNotNull(resetBatteryConfigUseCase)
    }

    @Test
    fun `batteryViewModel should not be null`() {
        assertNotNull(batteryViewModel)
    }

}

package androidstudio.tools.missed.features.battery.di

import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get.GetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get.GetBatteryLevelUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set.SetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set.SetBatteryLevelUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get.GetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get.GetChargerConnectionUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set.SetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set.SetChargerConnectionUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get.GetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get.GetPowerSavingUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set.SetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set.SetPowerSavingUseCaseImpl
import androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig.ResetBatteryConfigUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig.ResetBatteryConfigUseCaseImpl
import androidstudio.tools.missed.features.battery.presenter.BatteryViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val batteryModule = module {
    single<GetChargerConnectionUseCase> { GetChargerConnectionUseCaseImpl(get()) }
    single<SetChargerConnectionUseCase> { SetChargerConnectionUseCaseImpl(get()) }
    single<GetBatteryLevelUseCase> { GetBatteryLevelUseCaseImpl(get()) }
    single<SetBatteryLevelUseCase> { SetBatteryLevelUseCaseImpl(get()) }
    single<GetPowerSavingUseCase> { GetPowerSavingUseCaseImpl(get()) }
    single<SetPowerSavingUseCase> { SetPowerSavingUseCaseImpl(get()) }
    single<ResetBatteryConfigUseCase> { ResetBatteryConfigUseCaseImpl(get()) }
    single { BatteryViewModel(get(named("IODispatcher")), get(), get(), get(), get(), get(), get(), get(), get()) }
}

package androidstudio.tools.missed.features.network.di

import androidstudio.tools.missed.features.network.domain.usecase.airplane.get.GetAirplaneStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.airplane.get.GetAirplaneStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.airplane.set.SetAirplaneStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.airplane.set.SetAirplaneStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.get.GetBluetoothStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.get.GetBluetoothStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.set.SetBluetoothStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.set.SetBluetoothStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.get.GetMobileDataStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.get.GetMobileDataStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.set.SetMobileDataStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.set.SetMobileDataStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.wifi.get.GetWifiStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.wifi.get.GetWifiStateUseCaseImpl
import androidstudio.tools.missed.features.network.domain.usecase.wifi.set.SetWifiStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.wifi.set.SetWifiStateUseCaseImpl
import androidstudio.tools.missed.features.network.presenter.NetworkViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single<GetAirplaneStateUseCase> { GetAirplaneStateUseCaseImpl(get()) }
    single<SetAirplaneStateUseCase> { SetAirplaneStateUseCaseImpl(get()) }
    single<GetMobileDataStateUseCase> { GetMobileDataStateUseCaseImpl(get()) }
    single<SetMobileDataStateUseCase> { SetMobileDataStateUseCaseImpl(get()) }
    single<GetWifiStateUseCase> { GetWifiStateUseCaseImpl(get()) }
    single<SetWifiStateUseCase> { SetWifiStateUseCaseImpl(get()) }
    single<GetBluetoothStateUseCase> { GetBluetoothStateUseCaseImpl(get()) }
    single<SetBluetoothStateUseCase> { SetBluetoothStateUseCaseImpl(get()) }
    single {
        NetworkViewModel(
            get(named("IODispatcher")), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
}

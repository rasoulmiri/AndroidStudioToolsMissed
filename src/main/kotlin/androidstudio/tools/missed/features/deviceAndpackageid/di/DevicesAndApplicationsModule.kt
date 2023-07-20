package androidstudio.tools.missed.features.deviceAndpackageid.di

import androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase.GetPackageIdsInstalledInDeviceUseCase
import androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase.GetPackageIdsInstalledInDeviceUseCaseImpl
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.DevicesAndPackageIdsViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val deviceAndPackageIdsModule = module {
    single { DevicesAndPackageIdsViewModel(get(named("IODispatcher")), get(), get(), get()) }
    single<GetPackageIdsInstalledInDeviceUseCase> { GetPackageIdsInstalledInDeviceUseCaseImpl(get()) }
}

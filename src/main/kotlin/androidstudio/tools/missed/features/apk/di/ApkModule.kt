package androidstudio.tools.missed.features.apk.di

import androidstudio.tools.missed.features.apk.domain.usecase.downloadapk.DownloadApkFromDeviceUseCase
import androidstudio.tools.missed.features.apk.domain.usecase.downloadapk.DownloadApkFromDeviceUseCaseImpl
import androidstudio.tools.missed.features.apk.domain.usecase.installapk.InstallApkUseCase
import androidstudio.tools.missed.features.apk.domain.usecase.installapk.InstallApkUseCaseImpl
import androidstudio.tools.missed.features.apk.presenter.ApkViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apkModule = module {
    single<DownloadApkFromDeviceUseCase> { DownloadApkFromDeviceUseCaseImpl(get()) }
    single<InstallApkUseCase> { InstallApkUseCaseImpl(get()) }
    single { ApkViewModel(get(named("IODispatcher")), get(), get(), get(), get()) }
}

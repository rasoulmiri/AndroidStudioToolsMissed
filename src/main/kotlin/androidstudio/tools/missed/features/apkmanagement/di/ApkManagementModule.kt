package androidstudio.tools.missed.features.apkmanagement.di

import androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk.DownloadApkFromDeviceUseCase
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk.DownloadApkFromDeviceUseCaseImpl
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.installapk.InstallApkUseCase
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.installapk.InstallApkUseCaseImpl
import androidstudio.tools.missed.features.apkmanagement.presenter.ApkManagementViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apkManagementModule = module {
    single<DownloadApkFromDeviceUseCase> { DownloadApkFromDeviceUseCaseImpl(get()) }
    single<InstallApkUseCase> { InstallApkUseCaseImpl(get()) }
    single { ApkManagementViewModel(get(named("IODispatcher")), get(), get(), get(), get()) }
}

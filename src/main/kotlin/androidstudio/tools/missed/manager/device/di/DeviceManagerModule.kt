package androidstudio.tools.missed.manager.device.di

import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.device.DeviceManagerImpl
import org.koin.dsl.module

val deviceManagerModule = module {
    single<DeviceManager> { DeviceManagerImpl(get(), get()) }
}

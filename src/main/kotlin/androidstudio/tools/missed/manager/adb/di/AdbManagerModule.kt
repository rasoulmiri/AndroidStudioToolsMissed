package androidstudio.tools.missed.manager.adb.di

import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.AdbManagerImpl
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.adb.logger.AdbLoggerImpl
import org.koin.dsl.module

@Suppress("MagicNumber")
val adbManagerModule = module {
    single<AdbLogger> { AdbLoggerImpl() }
    single<AdbManager> { AdbManagerImpl(get(), get()) }
}

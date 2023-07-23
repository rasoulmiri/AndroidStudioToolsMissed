package androidstudio.tools.missed.manager.adb.di

import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.AdbManagerImpl
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.adb.logger.AdbLoggerImpl
import com.android.ddmlib.AndroidDebugBridge
import org.koin.dsl.module

val adbManagerModule = module {
    single<AndroidDebugBridge> { AndroidDebugBridge.createBridge() }
    single<AdbLogger> { AdbLoggerImpl() }
    single<AdbManager> { AdbManagerImpl(get(), get(), get()) }
}

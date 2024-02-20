package androidstudio.tools.missed.manager.adb.di

import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.AdbManagerImpl
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.adb.logger.AdbLoggerImpl
import com.android.ddmlib.AndroidDebugBridge
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@Suppress("MagicNumber")
val adbManagerModule = module {
    single<AndroidDebugBridge> { AndroidDebugBridge.createBridge(10000, TimeUnit.SECONDS) }
    single<AdbLogger> { AdbLoggerImpl() }
    single<AdbManager> { AdbManagerImpl(get(), get(), get()) }
}

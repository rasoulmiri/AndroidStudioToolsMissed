package androidstudio.tools.missed.dependencymanager

import androidstudio.tools.missed.features.apk.di.apkModule
import androidstudio.tools.missed.features.battery.di.batteryModule
import androidstudio.tools.missed.features.deviceAndpackageid.di.deviceAndPackageIdsModule
import androidstudio.tools.missed.features.dozeandstandby.di.dozeAndStandbyModule
import androidstudio.tools.missed.features.input.di.inputModule
import androidstudio.tools.missed.features.internetconnection.di.internetConnectionModule
import androidstudio.tools.missed.features.permission.di.permissionModule
import androidstudio.tools.missed.manager.adb.di.adbManagerModule
import androidstudio.tools.missed.manager.device.di.deviceManagerModule
import androidstudio.tools.missed.manager.notification.di.notificationManagerModule
import androidstudio.tools.missed.manager.resource.di.resourceManagerModule
import androidstudio.tools.missed.missedtoolwindow.di.missedToolsWindowModule
import androidstudio.tools.missed.utils.coroutines.dispatcher.coroutinesDispatcherIOModule
import androidstudio.tools.missed.utils.coroutines.scope.di.applicationCoroutinesScopeModule
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin

class DependencyInjectionInjectionManagerImpl : KoinComponent, DependencyInjectionManager {

    override fun start() {
        startKoin {
            printLogger()
            modules(
                applicationCoroutinesScopeModule +
                    coroutinesDispatcherIOModule +
                    resourceManagerModule +
                    notificationManagerModule +
                    adbManagerModule +
                    missedToolsWindowModule +
                    deviceManagerModule +
                    deviceAndPackageIdsModule +
                    inputModule +
                    internetConnectionModule +
                    permissionModule +
                    apkModule +
                    batteryModule +
                    dozeAndStandbyModule
            )
        }
    }
}

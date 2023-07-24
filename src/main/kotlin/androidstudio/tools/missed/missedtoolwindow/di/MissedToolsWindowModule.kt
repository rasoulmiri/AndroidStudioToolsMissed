package androidstudio.tools.missed.missedtoolwindow.di

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.base.ViewMaster
import androidstudio.tools.missed.features.MainView
import androidstudio.tools.missed.features.apk.presenter.ApkView
import androidstudio.tools.missed.features.battery.presenter.BatteryView
import androidstudio.tools.missed.features.deviceAndpackageid.presenter.DevicesAndPackageIdView
import androidstudio.tools.missed.features.dozeandstandby.presenter.DozeAndStandbyView
import androidstudio.tools.missed.features.input.presenter.InputTextView
import androidstudio.tools.missed.features.network.presenter.NetworkView
import androidstudio.tools.missed.features.permission.presenter.PermissionView
import androidstudio.tools.missed.missedtoolwindow.presenter.MissedToolsWindowViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val missedToolsWindowModule = module {
    single { MissedToolsWindowViewModel(get(named("IODispatcher")), get()) }
    factory<CollapsibleGroupView<*>>(named("networkView")) { NetworkView(get(), get(), get()) }
    factory<CollapsibleGroupView<*>>(named("inputTextView")) { InputTextView(get(), get(), get()) }
    factory<CollapsibleGroupView<*>>(named("permissionView")) { PermissionView(get(), get(), get()) }
    factory<CollapsibleGroupView<*>>(named("apkView")) { ApkView(get(), get(), get()) }
    factory<CollapsibleGroupView<*>>(named("dozeView")) { DozeAndStandbyView(get(), get(), get()) }
    factory<CollapsibleGroupView<*>>(named("batteryView")) { BatteryView(get(), get(), get()) }
    factory<ViewMaster<*>>(named("stickyView")) { DevicesAndPackageIdView(get(), get(), get()) }
    factory<List<CollapsibleGroupView<*>>>(named("views")) {
        listOf(
            get(named("networkView")),
            get(named("inputTextView")),
            get(named("permissionView")),
            get(named("apkView")),
            get(named("dozeView")),
            get(named("batteryView"))
        )
    }
    factory { MainView(get(named("stickyView")), get(named("views"))) }
}

package androidstudio.tools.missed.manager.notification.di

import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.notification.NotificationManagerImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val notificationManagerModule = module {
    single<NotificationManager> { NotificationManagerImpl(get(named("ApplicationScope")), get()) }
}

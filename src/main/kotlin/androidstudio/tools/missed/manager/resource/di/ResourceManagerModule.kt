package androidstudio.tools.missed.manager.resource.di

import androidstudio.tools.missed.manager.resource.ResourceManager
import org.koin.dsl.module

val resourceManagerModule = module {
    single<ResourceManager> { ResourceManager() }
}

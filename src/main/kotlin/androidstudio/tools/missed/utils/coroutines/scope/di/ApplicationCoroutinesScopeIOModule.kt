package androidstudio.tools.missed.utils.coroutines.scope.di

import androidstudio.tools.missed.utils.coroutines.scope.missedToolsWindowScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationCoroutinesScopeModule = module {
    single(named("ApplicationScope")) { missedToolsWindowScope }
}

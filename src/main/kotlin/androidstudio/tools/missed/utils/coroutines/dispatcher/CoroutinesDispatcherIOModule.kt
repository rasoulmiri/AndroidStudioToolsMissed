package androidstudio.tools.missed.utils.coroutines.dispatcher

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutinesDispatcherIOModule = module {
    single(named("IODispatcher")) {
        Dispatchers.IO
    }
}
